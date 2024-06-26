package fun.nekomc.sw.promote;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Multimap;
import fun.nekomc.sw.domain.enumeration.PromotionTypeEnum;
import fun.nekomc.sw.skill.AbstractSwSkill;
import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.MsgUtils;
import fun.nekomc.sw.utils.ServiceUtils;
import org.bukkit.Keyed;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 描述道具提升操作的类
 * create at 2022/3/23 12:18
 *
 * @param promotion      需要提升的操作类型
 * @param target         要操作的目标对象
 * @param promotionValue 要提升的数值
 * @param rewrite        当前操作是要覆盖原（重名）属性，还是基于原属性进行追加
 * @param weight         对当前操作进行概率计算时的权重
 * @param attrSlot       属性生效的装备槽，如果为附魔类型，则本字段为 null
 * @author Chiru
 */
public record PromotionOperation(PromotionTypeEnum promotion, Keyed target, String promotionValue, boolean rewrite,
                                 int weight, EquipmentSlot attrSlot) {

    /**
     * 配置文件描述规则时，一个规则字符串需要分割成多少个部分。
     * 解析过程见 buildByConfig 方法
     */
    private static final int CONFIG_FORMAT_DATA_SECTION = 4;

    /**
     * 以配置文件中的格式构建当前对象
     *
     * @param configStr 如 ATTR_UP-HAND:ARMOR:-4:80、ENCH:ARROW_RAIN:2:10
     * @return 描述提升规则的 PromoteOperation 实例
     * @throws ConfigurationException,IllegalArgumentException 传入的字符串无法解析时抛出
     */
    public static PromotionOperation buildByConfigStr(String configStr) {
        // 校验格式
        if (CharSequenceUtil.isEmpty(configStr)) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        String[] rules = configStr.split(":");
        if (CONFIG_FORMAT_DATA_SECTION != rules.length) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
        }
        // 解析参数：强化类型、Attribute 生效的槽位，如 ATTR_UP-HAND
        String[] promoteTypeAndSlot = rules[0].split("-");
        PromotionTypeEnum promotionType = PromotionTypeEnum.valueOf(promoteTypeAndSlot[0]);
        EquipmentSlot slot = null;
        if (PromotionTypeEnum.isAttribute(promotionType)) {
            if (promoteTypeAndSlot.length > 1) {
                slot = EquipmentSlot.valueOf(promoteTypeAndSlot[1]);
            } else {
                slot = RandomUtil.randomEle(EquipmentSlot.values());
            }
        }
        // 属性重写
        boolean rewrite = PromotionTypeEnum.isRewrite(promotionType);
        // 解析参数：强化等级，如 -4
        String promoteValue = rules[2];
        // 解析参数：强化目标，如 ARMOR
        Optional<Keyed> targetOpt = checkPromoteValue(promoteValue, promotionType, rules[1]);
        Keyed targetToPromote = targetOpt.orElseThrow(() ->
                new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR)));
        // 解析参数：概率权重，如 80
        int weight = Integer.parseInt(rules[3]);
        // 构建
        return new PromotionOperation(promotionType, targetToPromote, promoteValue, rewrite, weight, slot);
    }

    /**
     * 对指定的道具使用当前规则进行强化
     *
     * @param itemStack 要强化的道具
     */
    public boolean doPromote(@NotNull ItemStack itemStack, boolean check) {
        Assert.notNull(itemStack, "itemStack cannot be null");
        // 针对 Attribute 的强化
        return switch (promotion) {
            case ATTR, ATTR_UP -> doPromoteAttribute(itemStack, check);
            case ENCH, ENCH_UP -> doPromoteEnchantment(itemStack, check);
            case SKILL, SKILL_UP -> doPromoteSkill(itemStack, check);
            default -> false;
        };
    }

    public static void doPromoteByCandidatesRandomly(@NotNull ItemStack itemStack, List<String> candidates, int repeat, boolean check) {
        Assert.notNull(itemStack, "itemStack cannot be null");
        if (CollUtil.isEmpty(candidates)) {
            return;
        }
        // 转化、计算总权重
        List<PromotionOperation> promotionList = ServiceUtils.convertList(candidates, PromotionOperation::buildByConfigStr);
        int totalWeight = 0;
        for (PromotionOperation promotionOperation : promotionList) {
            totalWeight += promotionOperation.weight();
        }
        while (repeat-- > 0) {
            doPromoteOnce(totalWeight, promotionList, itemStack, check);
        }
    }

    // ========== private ========== //

    /**
     * 根据 PromotionOperation 规则列表执行一次增强
     */
    private static void doPromoteOnce(int totalWeight, List<PromotionOperation> promotionList, ItemStack itemStack, boolean check) {
        int nowWeight = 0;
        int targetRandom = RandomUtil.randomInt(0, totalWeight);
        for (PromotionOperation nowPromotion : promotionList) {
            // 目标权重位于指定区间内
            if (nowWeight <= targetRandom && targetRandom < nowWeight + nowPromotion.weight) {
                if (nowPromotion.doPromote(itemStack, check)) {
                    String template = nowPromotion.rewrite() ? Constants.Msg.PROMOTE_RESET : Constants.Msg.PROMOTE_CHANGE;
                    String slot = null == nowPromotion.attrSlot() ? "" : nowPromotion.attrSlot().name();
                    MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(template),
                            nowPromotion.target().getKey().getKey(), slot, nowPromotion.promotionValue());
                }
                return;
            }
            nowWeight += nowPromotion.weight;
        }
    }

    /**
     * 对 Attribute 进行增强，本方法中不会进行校验，确保调用时已校验完毕
     */
    private boolean doPromoteAttribute(ItemStack itemStack, boolean check) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (null == itemMeta) {
            throw new SwException(ConfigManager.getConfiguredMsg(Constants.Msg.UNKNOWN_ITEM));
        }
        if (null == attrSlot) {
            return false;
        }
        Attribute attributeToPromote = (Attribute) this.target;
        String toPromote = promotionValue;
        // 如果不是重写，则基于原有值计算变更
        if (!rewrite) {
            Multimap<Attribute, AttributeModifier> allModifiers = itemMeta.getAttributeModifiers();
            if (null != allModifiers && !allModifiers.isEmpty()) {
                double newPromote = Double.parseDouble(toPromote);
                Collection<AttributeModifier> oldModifiers = allModifiers.get(attributeToPromote);
                for (AttributeModifier modifier : oldModifiers) {
                    if (modifier.getSlot() == attrSlot) {
                        newPromote += modifier.getAmount();
                    }
                }
                toPromote = NumberUtil.decimalFormat("#.##", newPromote);
            }
        }
        boolean isUpdated = ItemUtils.updateAttributeModifierInMeta(itemMeta, attrSlot, attributeToPromote, toPromote, check);
        if (isUpdated) {
            itemStack.setItemMeta(itemMeta);
        }
        return isUpdated;
    }

    /**
     * 对 Enchantment 进行增强，本方法中不会进行校验，确保调用时已校验完毕
     */
    private boolean doPromoteEnchantment(ItemStack itemStack, boolean check) {
        Enchantment targetEnchant = (Enchantment) this.target;
        int targetLevel = Integer.parseInt(promotionValue);
        // 如果不是重写，则基于原等级变更附魔等级
        int enchantOldLevel = ItemUtils.getEnchantLevelOnItem(itemStack, targetEnchant);
        if (check && 0 == enchantOldLevel) {
            MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(Constants.Msg.CHECK_NOT_PASS));
            return false;
        }
        if (!rewrite) {
            targetLevel += enchantOldLevel;
        }
        ItemUtils.updateItemEnchant(itemStack, targetEnchant, targetLevel);
        return true;
    }

    /**
     * 校验 promoteValue 在当前的 promotionType 下是否可以正常解析
     * 可以正常解析时，返回要增强的目标对象
     */
    private static Optional<Keyed> checkPromoteValue(String promoteValue, PromotionTypeEnum promotionType, String promotionTarget) {
        boolean noNumeric = !NumberUtil.isNumber(promoteValue);
        boolean noFloat = !NumberUtil.isDouble(promoteValue);
        try {
            switch (promotionType) {
                case ATTR:
                case ATTR_UP:
                    if (noNumeric && noFloat) {
                        throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
                    }
                    // 解析为 Attribute
                    return Optional.of(Attribute.valueOf(promotionTarget));
                case ENCH:
                case ENCH_UP:
                    if (noNumeric) {
                        throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
                    }
                    // 解析为 Enchantment
                    Optional<Enchantment> targetEnchantOpt = ItemUtils.getEnchantByName(promotionTarget.toLowerCase());
                    if (targetEnchantOpt.isEmpty()) {
                        throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
                    }
                    return Optional.of(targetEnchantOpt.get());
                case SKILL:
                case SKILL_UP:
                    if (noNumeric) {
                        throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
                    }
                    // 解析为技能
                    return SkillHelper.getByKey(promotionTarget).map(a -> a);
                default:
                    return Optional.empty();
            }
        } catch (IllegalArgumentException | ConfigurationException e) {
            MsgUtils.consoleMsg(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_CANNOT_RESOLVE),
                    e.getMessage() + "：" + promotionType.name() + ":" + promotionTarget + ":" + promoteValue);
            throw e;
        }
    }

    private boolean doPromoteSkill(ItemStack itemStack, boolean check) {
        AbstractSwSkill targetSkill = (AbstractSwSkill) this.target;
        int targetLevel = Integer.parseInt(promotionValue);
        // 如果不是重写，则基于原等级变更附魔等级
        int skillOldLevel = SkillHelper.getSkillLevelOnItem(itemStack, targetSkill);
        if (check && 0 == skillOldLevel) {
            MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(Constants.Msg.CHECK_NOT_PASS));
            return false;
        }
        if (!rewrite) {
            targetLevel += skillOldLevel;
        }
        return SkillHelper.updateItemSkill(itemStack, targetSkill, targetLevel);
    }
}
