package fun.nekomc.sw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 强化石
 *
 * @author ourange
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StrengthenStone extends StrengthenItem {
    public static final String STONE_NAME = "sw_stone";

    private int chance;

    @Override
    public void setLore(List<String> lore) {
        super.setLore(lore);
        lore.add(1, "§c强化成功率:" + chance + "%");
    }

    public StrengthenStone() {
        this.setConfigName(STONE_NAME);
    }
}
