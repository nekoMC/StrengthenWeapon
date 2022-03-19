package fun.nekomc.sw.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 强化石配置 Dto
 *
 * @author ourange
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SwStrengthenStoneConfigDto extends SwItemConfigDto {

    private int chance;

    /**
     * TODO: 拓展强化石可以强化的属性及概率等配置
     */
}
