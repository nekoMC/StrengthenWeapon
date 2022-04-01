package fun.nekomc.sw.listener;

import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SwBlankConfigDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.dto.SwRefineStoneConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.promote.PromotionOperation;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * created: 2022/3/27 01:35
 *
 * @author Chiru
 */
public class RefineTableGuiListener extends AbstractComposeGui implements Listener {

    private static final int SW_WEAPON_INDEX = 0;
    private static final int SW_STONE_INDEX = 1;

    public RefineTableGuiListener() {
        super(InventoryType.FURNACE, ConfigManager.getConfigYml().getRefineTitle(), 2);
        // 注册校验规则
        registerCheckRule(SW_WEAPON_INDEX, ItemsTypeEnum.BLANK);
        registerCheckRule(SW_STONE_INDEX, ItemsTypeEnum.REFINE_STONE);
    }

    @Override
    protected ItemStack generatePreviewItem(@NotNull WrappedInventoryClickEvent wrapped) {
        // 不存在指定配置时，原样返回
        ItemStack item = wrapped.inventory.getItem(SW_WEAPON_INDEX);
        Optional<SwBlankConfigDto> blankConfigOpt = ItemUtils.getConfigDtoFromItem(item, SwBlankConfigDto.class);
        if (!blankConfigOpt.isPresent()) {
            return item;
        }
        // 生成预览物品
        SwBlankConfigDto blankConfig = blankConfigOpt.get();
        if (null == blankConfig.getRefine() || null == blankConfig.getRefine().getPreview()) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        Optional<ItemStack> previewItemOpt = ItemUtils.buildItemByConfig(blankConfig.getRefine().getPreview());
        return previewItemOpt.orElse(item);
    }

    @Override
    protected ItemStack generateStrengthItem(@NotNull WrappedInventoryClickEvent wrapped) {
        // 获取配置
        ItemStack item = wrapped.inventory.getItem(SW_WEAPON_INDEX);
        ItemStack raw = wrapped.inventory.getItem(SW_STONE_INDEX);
        Optional<SwItemAttachData> attachData = ItemUtils.getAttachData(item);
        Optional<SwBlankConfigDto> blankConfigOpt = ItemUtils.getConfigDtoFromItem(item, SwBlankConfigDto.class);
        if (!blankConfigOpt.isPresent() || !attachData.isPresent()) {
            return item;
        }
        SwItemAttachData swItemAttachData = attachData.get();
        SwBlankConfigDto.RefineRule refineRule = blankConfigOpt.get().getRefine();
        // 洗练失败时，返回配置的失败物品
        if (!isRefineSuccess(swItemAttachData, refineRule)) {
            Optional<SwItemConfigDto> brokeItem = ConfigManager.getItemConfig(refineRule.getBroke());
            MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(Constants.Msg.REFINE_FAIL));
            if (!brokeItem.isPresent()) {
                return item;
            }
            return ItemUtils.buildItemByConfig(brokeItem.get()).orElse(item);
        }
        // 执行洗练
        return doRefine(item, raw, blankConfigOpt.get(), swItemAttachData.getRefLvl());
    }

    @Override
    protected boolean recipeMatch(Inventory targetInv) {
        // 查询白板配置
        ItemStack blank = targetInv.getItem(SW_WEAPON_INDEX);
        Optional<SwBlankConfigDto> blankConfigOpt = ItemUtils.getConfigDtoFromItem(blank, SwBlankConfigDto.class);
        if (!blankConfigOpt.isPresent()) {
            return false;
        }
        // 洗练材料
        ItemStack refineRaw = targetInv.getItem(SW_STONE_INDEX);
        String refineRawName = ItemUtils.getNameFromMeta(refineRaw);
        // 校验洗练材料
        SwBlankConfigDto blankConfig = blankConfigOpt.get();
        List<String> compatible = blankConfig.getRefine().getCompatible();
        return compatible.contains(refineRawName);
    }

    // ========== private ========== //

    /**
     * 当前洗练是否成功
     */
    private boolean isRefineSuccess(SwItemAttachData swItemAttachData, SwBlankConfigDto.RefineRule refineRule) {
        // 洗练是否成功
        boolean operateSuccess;
        int newRefLvl = swItemAttachData.getRefLvl() + 1;
        if (newRefLvl > refineRule.getLimit()) {
            operateSuccess = false;
        } else {
            int chance = refineRule.getBeginRate() - swItemAttachData.getRefLvl() * refineRule.getRateLvlDown();
            operateSuccess = RandomUtil.randomInt(0, 100) <= chance;
        }
        return operateSuccess;
    }

    private ItemStack doRefine(ItemStack blank, ItemStack raw, SwBlankConfigDto blankConfig, int oldRefLvl) {
        Optional<SwRefineStoneConfigDto> refineRawConfigOpt = ItemUtils.getConfigDtoFromItem(raw, SwRefineStoneConfigDto.class);
        if (!refineRawConfigOpt.isPresent()) {
            return blank;
        }
        // 发送玩家消息
        MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(Constants.Msg.REFINE_SUCCESS));
        SwRefineStoneConfigDto refineStoneConfig = refineRawConfigOpt.get();
        // 洗练：重置属性
        ItemStack newItem = ItemUtils.buildItemByConfig(blankConfig)
                .orElseThrow(() -> new SwException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR)));
        // 洗练：随机选择提升属性
        int promoteTime = RandomUtil.randomInt(refineStoneConfig.getMinOnce(), refineStoneConfig.getMaxOnce() + 1);
        List<String> candidates = refineStoneConfig.getCandidates();
        PromotionOperation.doPromoteByCandidatesRandomly(newItem, candidates, promoteTime);
        // 洗练：修改洗练等级
        ItemMeta newItemMeta = newItem.getItemMeta();
        if (null == newItemMeta) {
            throw new SwException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        ItemUtils.updateAttachData(newItemMeta, new SwItemAttachData(oldRefLvl + 1, 0));
        newItem.setItemMeta(newItemMeta);
        return newItem;
    }
}
