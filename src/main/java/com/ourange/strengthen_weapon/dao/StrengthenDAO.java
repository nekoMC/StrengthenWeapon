package com.ourange.strengthen_weapon.dao;

import com.ourange.strengthen_weapon.domain.StrengthenItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface StrengthenDAO {
    ItemStack giveStrengthenBow(int count);

    ItemStack giveStrengthenStone(int count, int level);

    ItemStack strengthen(Player player, ItemStack itemStack, StrengthenItem strengthenItem, boolean isSuccess, boolean isSafe);

    ItemStack strengthenSuccessResult(ItemStack itemStack, StrengthenItem strengthenItem);
}
