package fun.nekomc.sw.domain;

import java.util.List;

/**
 * 强化石
 *
 * @author ourange
 */
public class StrengthenStone extends StrengthenItem{
    public static final String STONE_NAME = "sw_stone";

    private int chance;

    @Override
    public void setLore(List<String> lore) {
        super.setLore(lore);
        lore.add(1, "§c强化成功率:" + chance + "%");
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public StrengthenStone() {
        this.setConfigName(STONE_NAME);
    }
}