package fun.nekomc.sw.service;

import fun.nekomc.sw.domain.dto.SwStrengthenStoneConfigDto;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface StrengthenService {
    ItemStack giveStrengthBow(int amount);

    ItemStack giveStrengthenStone(int amount, int level);

    ItemStack strengthen(Player player, ItemStack itemStack, SwStrengthenStoneConfigDto swStrengthenStoneConfigDto, boolean isAdmin);

    ItemStack strengthenSuccessResult(ItemStack swWeapon, ItemStack swStone);
}
