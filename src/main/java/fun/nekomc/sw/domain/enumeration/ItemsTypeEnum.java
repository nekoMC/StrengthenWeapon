package fun.nekomc.sw.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ourange
 */
@Getter
@AllArgsConstructor
public enum ItemsTypeEnum implements BaseEnum<Integer> {
    /**
     * TODO: 确认改造如何处理
     * 连发弓
     */
    BOW(0, String.class),

    ;

    /**
     * 该类型的编码
     */
    private final Integer code;

    /**
     * 该类型使用的配置 DTO（将配置文件内容解析到哪个类）
     */
    private final Class<?> typeConfigClass;
}
