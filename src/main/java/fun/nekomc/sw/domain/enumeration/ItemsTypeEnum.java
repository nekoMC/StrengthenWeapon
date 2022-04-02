package fun.nekomc.sw.domain.enumeration;

import fun.nekomc.sw.domain.dto.SwBlankConfigDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.dto.SwRawConfigDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ourange
 */
@Getter
@AllArgsConstructor
public enum ItemsTypeEnum implements BaseEnum<Integer> {
    /**
     * 白板，可用于强化洗练的白板道具或装备等
     * 强化洗炼过数次，仍能继续强化的道具也属于白板
     */
    BLANK(0, SwBlankConfigDto.class),

    /**
     * 白板，可用于强化洗练的白板道具或装备等
     * 强化洗炼过数次，仍能继续强化的道具也属于白板
     */
    REFINE_STONE(1, SwRawConfigDto.class),

    /**
     * 强化石，可以强化配置属性的消耗品
     */
    STRENGTHEN_STONE(2, SwRawConfigDto.class),

    /**
     * 默认物品类别，如普通的方块
     */
    DEFAULT(10, SwItemConfigDto.class),
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
