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
    private RefineRule refine;

    @Data
    public static class RefineRule {
        /**
         * 最多可以洗炼的次数
         */
        private int limit;
        /**
         * 0 洗时的成功率
         */
        private int beginRate;
        /**
         * 每洗炼一次，成功率下降多少
         */
        private int rateLvlDown;
        /**
         * 可以使用的洗练材料（名称）
         */
        private List<String> compatible;
        /**
         * 洗练时的预览信息
         */
        private SwItemConfigDto preview;
        /**
         * 洗练失败后返还的物品名
         */
        private String broke;
    }
}
