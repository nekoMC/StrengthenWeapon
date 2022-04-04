package fun.nekomc.sw.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 强化石、洗练石等材料
 * created: 2022/3/26 19:47
 *
 * @author Chiru
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SwRawConfigDto extends SwItemConfigDto {

    /**
     * 一次强化可获得强化次数及概率配置
     */
    private List<RateConfig> times;

    /**
     * 强化候选
     *
     * @see fun.nekomc.sw.promote.PromotionOperation
     */
    private List<String> candidates;

    /**
     * 成功率加成
     */
    private int addition;

    /**
     * 强化次数及概率配置
     */
    @Data
    public static class RateConfig implements Serializable {
        /**
         * 可获得强化的次数
         */
        private int time;
        /**
         * 起始成功率
         */
        private int chance;
        /**
         * 每提升一级对概率的影响
         */
        private int rate;
    }
}
