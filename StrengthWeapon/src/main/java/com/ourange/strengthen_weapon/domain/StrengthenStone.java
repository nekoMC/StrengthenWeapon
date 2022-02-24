package com.ourange.strengthen_weapon.domain;

import java.util.List;

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
