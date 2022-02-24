package com.ourange.strengthen_weapon.service;

import com.ourange.strengthen_weapon.domain.StrengthenItem;
import com.ourange.strengthen_weapon.domain.StrengthenStone;
import com.ourange.strengthen_weapon.domain.enumeration.WeaponsIndex;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface StrengthenService {
    ItemStack giveStrengthBow(int amount);

    ItemStack giveStrengthenStone(int amount, int level);

    ItemStack strengthen(Player player, ItemStack itemStack, StrengthenItem strengthenItem, StrengthenStone strengthenStone, boolean isAdmin);

    ItemStack strengthenSuccessResult(ItemStack itemStack, StrengthenItem strengthenItem);
}
