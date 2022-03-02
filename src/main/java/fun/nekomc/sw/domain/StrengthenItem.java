package fun.nekomc.sw.domain;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 被 sw 识别的道具
 *
 * @author ourange
 */
@Data
public abstract class StrengthenItem {
    private String displayName;
    private String name;

    /**
     * 道具的解释文本
     */
    private List<String> lore;

    /**
     * 道具材质
     */
    private String material;

    /**
     * 等级名称（强化等级前缀）
     */
    private String levelName;

    /**
     * 强化等级
     */
    private int level;
    private String configName;

    public void setLore(List<String> lore) {
        this.lore = lore;
        lore.add(0, levelName + level);
        lore.add(lore.size(), name);
    }

    public void setLevel(int level) {
        this.level = level;
        updateLore();
    }

    public void updateLore() {
        lore.set(0, levelName + level);
    }
}
