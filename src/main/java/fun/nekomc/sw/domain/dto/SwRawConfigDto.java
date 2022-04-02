package fun.nekomc.sw.domain.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
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

    /**
     * 根据当前强化等级和配置的 times 参数计算需要强化多少次
     *
     * @param lvl 强化等级
     * @return 需要强化的次数
     */
    public int timesToStrength(int lvl) {
        // 未指定规则时，返回一次
        if (CollUtil.isEmpty(times)) {
            return 1;
        }
        // 计算总权重
        int totalWeight = 0;
        for (RateConfig time : times) {
            totalWeight += (time.chance + lvl * time.getRate());
        }
        // 计算随机数，并寻找满足该随机数范围的配置项
        int randomInt = RandomUtil.randomInt(0, totalWeight);
        int nowWeight = 0;
        for (RateConfig time : times) {
            int newToAdd = time.chance + lvl * time.getRate();
            if (nowWeight <= randomInt && randomInt < randomInt + nowWeight) {
                return time.time;
            }
            nowWeight += newToAdd;
        }
        return 1;
    }
}
