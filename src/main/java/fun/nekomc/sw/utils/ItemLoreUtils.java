package fun.nekomc.sw.utils;

import fun.nekomc.sw.domain.StrengthenItem;

import java.util.List;

public class ItemLoreUtils {

    public static int getItemLevel(List<String> lore, StrengthenItem strengthItem) {
        return Integer.parseInt(lore.get(0).split(strengthItem.getLevelName())[1]);
    }

    public static void setItemLevel(List<String> lore, StrengthenItem strengthItem, int level) {
        lore.set(0, strengthItem.getLevelName() + level);
    }
    public static String getItemName(List<String> lore) {
        return lore.get(lore.size() - 1);
    }
}
