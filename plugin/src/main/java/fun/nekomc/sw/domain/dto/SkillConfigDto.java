package fun.nekomc.sw.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * created: 2022/3/14 00:50
 *
 * @author Chiru
 */
@Data
public class SkillConfigDto implements Serializable {

    /**
     * 当前附魔的 key
     */
    private String key;

    /**
     * 显示名
     */
    private String displayName;

    /**
     * 每级增加的属性值
     */
    private int addition;

    /**
     * 是否在 Lore 中隐藏
     */
    private boolean hideLore;

    /**
     * 附魔等级为 1 时的属性值
     */
    private Integer start;

    /**
     * 等级展示规则，合法值：roman, repeat, format
     */
    private String showLevel;

    /**
     * 拓展配置属性
     */
    private Map<String, String> ext;
}
