package fun.nekomc.sw.domain.enumeration;

import cn.hutool.core.lang.Assert;

import java.util.stream.Stream;

/**
 * 封装了部分枚举常用方法的接口
 * create at 2021/11/19 17:51
 *
 * @author Chiru
 */
public interface BaseEnum<C> {

    /**
     * 将编码转化为枚举类实例
     *
     * @param enumType 枚举类类对象
     * @param code     编码
     * @param <C>      code 的泛型
     * @param <E>      枚举类的泛型
     * @return 相应的枚举类实例
     */
    static <C, E extends BaseEnum<C>> E fromCode(Class<E> enumType, C code) {
        Assert.notNull(enumType, "EnumType can't be null");
        Assert.isTrue(enumType.isEnum(), "EnumType must be enum");
        Assert.notNull(code, "Code can't be null");

        return Stream.of(enumType.getEnumConstants())
                .filter(t -> t.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown code: " + code));
    }

    /**
     * 获取枚举类型 code 值，通常会被 Lombok 实现
     *
     * @return code
     */
    C getCode();
}
