package fun.nekomc.sw.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 洗炼石
 * created: 2022/3/26 19:47
 *
 * @author Chiru
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SwRefineStoneConfigDto extends SwItemConfigDto {

    /**
     * 强化候选
     *
     * @see fun.nekomc.sw.promote.PromotionOperation
     */
    List<String> candidates;
}
