package fun.nekomc.sw.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对道具进行提升的操作类型
 * create at 2022/3/23 12:17
 *
 * @author Chiru
 */
@Getter
@AllArgsConstructor
public enum PromotionTypeEnum implements BaseEnum<Integer> {
    /**
     * 重写属性
     */
    ATTR(0),
    /**
     * 提升属性
     */
    ATTR_UP(1),
    /**
     * 重写附魔
     */
    ENCH(2),
    /**
     * 提升附魔
     */
    ENCH_UP(3),
    /**
     * 重写技能
     */
    SKILL(4),
    /**
     * 提升技能等级
     */
    SKILL_UP(5),
    /**
     * 变更其他参数
     */
    OPTI(10),
    ;

    @Getter
    private final Integer code;

    /**
     * 指定的强化类型是否针对 Attribute
     */
    public static boolean isAttribute(PromotionTypeEnum type) {
        return type == ATTR || type == ATTR_UP;
    }

    public static boolean isEnchant(PromotionTypeEnum type) {
        return type == ENCH || type == ENCH_UP;
    }

    public static boolean isRewrite(PromotionTypeEnum promotionType) {
        return promotionType == ATTR || promotionType == ENCH;
    }
}
