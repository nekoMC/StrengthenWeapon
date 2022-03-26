package fun.nekomc.sw.domain.enumeration;

import lombok.Getter;

/**
 * 对道具进行提升的操作类型
 * create at 2022/3/23 12:17
 *
 * @author Chiru
 */
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
     * 变更其他参数
     */
    OPTI(4),
    ;

    @Getter
    private final Integer code;

    PromotionTypeEnum(Integer code) {
        this.code = code;
    }

    /**
     * 指定的强化类型是否针对 Attribute
     */
    public static boolean isAttribute(PromotionTypeEnum type) {
        return type == ATTR || type == ATTR_UP;
    }
}
