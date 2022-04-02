package fun.nekomc.sw.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 白板道具（装备）的配置 DTO
 *
 * @author ourange
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SwBlankConfigDto extends SwItemConfigDto implements Serializable {

    /**
     * 洗练规则
     */
    private StrengthRule refine;

    /**
     * 强化规则
     */
    private StrengthRule strength;

    @Data
    public static class StrengthRule implements Serializable {
        /**
         * 最多可以洗炼（强化）的次数
         */
        private int limit;
        /**
         * 0 洗（强化）时的成功率
         */
        private int beginRate;
        /**
         * 每洗炼（强化）一次，成功率下降多少
         */
        private int rateLvlDown;
        /**
         * 可以使用的洗练（强化）材料（名称）
         */
        private List<String> compatible;
        /**
         * 洗练（强化）时的预览信息
         */
        private SwItemConfigDto preview;
        /**
         * 洗练（强化）失败后返还的物品 key
         */
        private String broke;
    }
}
