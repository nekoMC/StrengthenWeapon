package fun.nekomc.sw.domain;

import java.util.List;

/**
 * 被 sw 识别的道具
 * TODO: Lombok 简化
 * TODO: 配置化改造
 *
 * @author ourange
 */
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
        lore.add(0, levelName + level);
        lore.add(lore.size(), name);
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        updateLore();
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void updateLore() {
        lore.set(0, levelName + level);
    }

    @Override
    public String toString() {
        return "StrengthItem{" +
                "displayName='" + displayName + '\'' +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", material='" + material + '\'' +
                ", levelName='" + levelName + '\'' +
                ", level=" + level +
                '}';
    }
}
