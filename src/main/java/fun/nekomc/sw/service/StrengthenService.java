package fun.nekomc.sw.service;

import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface StrengthenService {
    ItemStack giveStrengthBow(int amount);

    ItemStack giveStrengthenStone(int amount, int level);

    ItemStack strengthen(Player player, ItemStack itemStack, StrengthenItem strengthenItem, StrengthenStone strengthenStone, boolean isAdmin);

    ItemStack strengthenSuccessResult(ItemStack itemStack, StrengthenItem strengthenItem);
}
