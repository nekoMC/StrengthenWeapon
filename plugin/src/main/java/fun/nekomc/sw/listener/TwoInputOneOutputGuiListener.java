package fun.nekomc.sw.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SwBlankConfigDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.dto.SwRawConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.promote.PromotionOperation;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.MsgUtils;
import fun.nekomc.sw.utils.ServiceUtils;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 2入1出的 GUI，仅适用于强化相关场景
 * created: 2022/3/27 01:35
 *
 * @author Chiru
 */
public abstract class TwoInputOneOutputGuiListener extends AbstractComposeGui {

    private static final int BLANK_INDEX = 0;
    private static final int RAW_INDEX = 1;

    private final boolean onlyOverwrite;

    protected TwoInputOneOutputGuiListener(Supplier<InventoryType> invTypeSupplier, String title, ItemsTypeEnum blankType, ItemsTypeEnum rawType, boolean onlyOverwrite) {
        super(invTypeSupplier, title, 2);
        // 注册校验规则
        registerCheckRule(BLANK_INDEX, blankType);
        registerCheckRule(RAW_INDEX, rawType);
        this.onlyOverwrite = onlyOverwrite;
    }

    /**
     * 从 SwBlankConfigDto 中获取当前增强规则配置
     *
     * @param blankConfigDto 白板的配置
     * @return 增强规则，StrengthRule 实例
     */
    protected abstract SwBlankConfigDto.StrengthRule getStrengthRuleFromBlankConfig(SwBlankConfigDto blankConfigDto);

    /**
     * 从 attachData 中获取当前增强等级
     *
     * @param attachData 道具的附加数据
     * @return 已进行增强的等级
     */
    protected abstract int getLvlFromAttach(SwItemAttachData attachData);

    /**
     * 绷瓷增强后，如果对附加数据进行更新
     *
     * @param oldAttach 原附加数据，可以直接修改
     * @return 新的附加数据
     */
    protected abstract SwItemAttachData newAttachDataAfterLvlUp(SwItemAttachData oldAttach);

    @Override
    protected ItemStack generatePreviewItem(@NotNull WrappedInventoryClickEvent wrapped) {
        // 不存在指定配置时，原样返回
        ItemStack blank = wrapped.inventory.getItem(BLANK_INDEX);
        Optional<SwBlankConfigDto> blankConfigOpt = ItemUtils.getConfigDtoFromItem(blank, SwBlankConfigDto.class);
        if (!blankConfigOpt.isPresent()) {
            return blank;
        }
        // 生成预览物品
        SwBlankConfigDto blankConfig = blankConfigOpt.get();
        SwBlankConfigDto.StrengthRule strengthRule = getStrengthRuleFromBlankConfig(blankConfig);
        if (null == strengthRule || null == strengthRule.getPreview()) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }

        // 获取成功率
        int chance = 0;
        ItemStack raw = wrapped.inventory.getItem(RAW_INDEX);
        Optional<SwItemAttachData> attachData = ItemUtils.getAttachData(blank);
        Optional<SwRawConfigDto> rawConfigDtoOpt = ItemUtils.getConfigDtoFromItem(raw, SwRawConfigDto.class);
        if (attachData.isPresent() && rawConfigDtoOpt.isPresent()) {
            SwRawConfigDto rawConfig = rawConfigDtoOpt.get();
            SwItemAttachData swItemAttachData = attachData.get();
            SwBlankConfigDto.StrengthRule promoteRule = getStrengthRuleFromBlankConfig(blankConfigOpt.get());
            chance = successChance(swItemAttachData, promoteRule, rawConfig.getAddition());
        }
        Map<String, Object> extMap = MapUtil.of("chance", chance);
        Optional<ItemStack> previewItemOpt = ItemUtils.buildItemByConfig(strengthRule.getPreview(), extMap);
        return previewItemOpt.orElse(blank);
    }

    @Override
    protected ItemStack generateStrengthItem(@NotNull WrappedInventoryClickEvent wrapped) {
        // 获取配置
        ItemStack item = wrapped.inventory.getItem(BLANK_INDEX);
        ItemStack raw = wrapped.inventory.getItem(RAW_INDEX);
        Optional<SwItemAttachData> attachData = ItemUtils.getAttachData(item);
        Optional<SwBlankConfigDto> blankConfigOpt = ItemUtils.getConfigDtoFromItem(item, SwBlankConfigDto.class);
        Optional<SwRawConfigDto> rawConfigDtoOpt = ItemUtils.getConfigDtoFromItem(raw, SwRawConfigDto.class);
        if (!blankConfigOpt.isPresent() || !attachData.isPresent() || !rawConfigDtoOpt.isPresent()) {
            return item;
        }
        SwRawConfigDto rawConfig = rawConfigDtoOpt.get();
        SwItemAttachData swItemAttachData = attachData.get();
        SwBlankConfigDto.StrengthRule promoteRule = getStrengthRuleFromBlankConfig(blankConfigOpt.get());
        // 操作失败时，返回配置的失败物品
        boolean promoteSuccess = RandomUtil.randomInt(0, 100) < successChance(swItemAttachData, promoteRule, rawConfig.getAddition());
        if (!promoteSuccess) {
            Optional<SwItemConfigDto> brokeItem = ConfigManager.getItemConfig(promoteRule.getBroke());
            MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(Constants.Msg.PROMOTE_FAIL));
            if (!brokeItem.isPresent()) {
                return item;
            }
            return ItemUtils.buildItemByConfig(brokeItem.get()).orElse(item);
        }
        // 执行增强
        return doPromote(item, blankConfigOpt.get(), rawConfig, swItemAttachData);
    }

    @Override
    protected boolean recipeMatch(Inventory targetInv) {
        // 查询白板配置
        ItemStack blank = targetInv.getItem(BLANK_INDEX);
        Optional<SwBlankConfigDto> blankConfigOpt = ItemUtils.getConfigDtoFromItem(blank, SwBlankConfigDto.class);
        if (!blankConfigOpt.isPresent()) {
            return false;
        }
        // 原料材料
        ItemStack rawItem = targetInv.getItem(RAW_INDEX);
        String rawItemName = ItemUtils.getNameFromMeta(rawItem);
        // 校验材料
        SwBlankConfigDto blankConfig = blankConfigOpt.get();
        List<String> compatible = getStrengthRuleFromBlankConfig(blankConfig).getCompatible();
        return CollUtil.isEmpty(compatible) || compatible.contains(rawItemName);
    }

    // ========== private ========== //

    /**
     * 当前操作的成功率
     */
    private int successChance(SwItemAttachData swItemAttachData, SwBlankConfigDto.StrengthRule rule, int addition) {
        int oldLevel =  getLvlFromAttach(swItemAttachData);
        int newLvl = oldLevel + 1;
        if (newLvl > rule.getLimit()) {
            return 0;
        }
        return rule.getBeginRate() - oldLevel * rule.getRateLvlDown() + addition;
    }

    private ItemStack doPromote(ItemStack blank, SwBlankConfigDto blankConfig, SwRawConfigDto rawConfig, SwItemAttachData oldAttach) {
        // 提升：重置属性或基于原道具提升
        ItemStack newItem = blank;
        if (!onlyOverwrite) {
            newItem = ItemUtils.buildItemByConfig(blankConfig)
                    .orElseThrow(() -> new SwException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR)));
        }
        // 提升：随机选择提升属性
        int oldLvl = getLvlFromAttach(oldAttach);
        SwRawConfigDto.RateConfig useRateConfig = ServiceUtils.randomByWeight(rawConfig.getTimes(),
                nowConfig -> nowConfig.getChance() + oldLvl * nowConfig.getRate());
        int time = null == useRateConfig ? 1 : useRateConfig.getTime();
        List<String> candidates = rawConfig.getCandidates();
        PromotionOperation.doPromoteByCandidatesRandomly(newItem, candidates, time, onlyOverwrite);
        // 提升：修改等级
        ItemMeta newItemMeta = newItem.getItemMeta();
        if (null == newItemMeta) {
            throw new SwException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        SwItemAttachData newAttach = newAttachDataAfterLvlUp(oldAttach);
        ItemUtils.updateAttachData(newItemMeta, newAttach);
        newItem.setItemMeta(newItemMeta);
        SkillHelper.updateLore(newItem);
        return newItem;
    }
}
