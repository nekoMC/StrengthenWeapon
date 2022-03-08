package fun.nekomc.sw.service;

import fun.nekomc.sw.domain.StrengthenStone;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface StrengthenService {
    ItemStack giveStrengthBow(int amount);

    ItemStack giveStrengthenStone(int amount, int level);

    ItemStack strengthen(Player player, ItemStack itemStack, StrengthenStone strengthenStone, boolean isAdmin);

    ItemStack strengthenSuccessResult(ItemStack swWeapon, ItemStack swStone);
}
