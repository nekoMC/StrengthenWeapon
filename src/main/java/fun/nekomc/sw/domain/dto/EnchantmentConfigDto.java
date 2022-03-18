package fun.nekomc.sw.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * created: 2022/3/14 00:50
 *
 * @author Chiru
 */
@Data
public class EnchantmentConfigDto implements Serializable {

    /**
     * 当前附魔的 key
     */
    private String key;

    /**
     * The materials of the targets.
     */
    private List<String> targetMaterials;

    /**
     * The display name of the enchantment.
     */
    private String displayName;

    /**
     * The maximum level for the enchantment to be obtained naturally.
     */
    private int maxLevel;

    /**
     * The enchantments that conflict with this enchantment.
     */
    private List<String> conflicts;

    /**
     * The description of the enchantment.
     */
    private String description;

    /**
     * 宝藏附魔？
     */
    private boolean treasure;

    /**
     * 每级附魔增加的属性值
     */
    private int addition;
}
