package fun.nekomc.sw.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 白板道具（装备）的配置 DTO
 *
 * @author ourange
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SwBlankConfigDto extends SwItemConfigDto implements Serializable {

    /**
     * TODO: 拓展为实际业务需要字段，需要在本类中描述该白板的具体强化规则等
     */
    private String sampleValue;
}
