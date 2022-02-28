package com.ourange.strengthen_weapon.service.imp;

import com.ourange.strengthen_weapon.StrengthenWeapon;
import com.ourange.strengthen_weapon.dao.imp.StrengthenDAOImp;
import com.ourange.strengthen_weapon.domain.StrengthenItem;
import com.ourange.strengthen_weapon.domain.StrengthenStone;
import com.ourange.strengthen_weapon.domain.enumeration.WeaponsIndex;
import com.ourange.strengthen_weapon.service.StrengthenService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StrengthenServiceImp implements StrengthenService {
    private final StrengthenDAOImp dao;
    private StrengthenWeapon plugin;
    private List<StrengthenItem> strengthenWeapons;
    private List<StrengthenStone> strengthenStones;
    private final Random random = new Random();

    public StrengthenServiceImp(StrengthenWeapon plugin, List<StrengthenItem> strengthenWeapons, List<StrengthenStone> strengthenStones) {
        this.plugin = plugin;
        this.strengthenWeapons = strengthenWeapons;
        this.strengthenStones = strengthenStones;
        dao = new StrengthenDAOImp();
        dao.setPlugin(plugin);
        dao.setStrengthenWeapons(strengthenWeapons);
        dao.setStrengthenStones(strengthenStones);
    }
    @Override
    public ItemStack giveStrengthBow(int amount) {

        return dao.giveStrengthenBow(1);
    }

    @Override
    public ItemStack giveStrengthenStone(int amount, int level) {
        return dao.giveStrengthenStone(amount, level);
    }


    @Override
    public ItemStack strengthen(Player player, ItemStack itemStack, StrengthenItem strengthenItem, StrengthenStone strengthenStone, boolean isAdmin) {
        boolean isSafe = false;
        boolean isSuccess = strengthenSuccess(strengthenStone.getChance());
        return  dao.strengthen(player, itemStack, strengthenItem, isSuccess||isAdmin, isSafe);
    }

    public ItemStack strengthenSuccessResult(ItemStack itemStack, StrengthenItem strengthenItem) {
        return  dao.strengthenSuccessResult(itemStack, strengthenItem);
    }

    private boolean strengthenSuccess(int chance) {
        return chance > random.nextInt(101);
    }

    public List<StrengthenItem> getStrengthenWeapons() {
        return strengthenWeapons;
    }

    public void setStrengthenWeapons(List<StrengthenItem> strengthenWeapons) {
        this.strengthenWeapons = strengthenWeapons;
    }

    public List<StrengthenStone> getStrengthenStones() {
        return strengthenStones;
    }

    public void setStrengthenStones(List<StrengthenStone> strengthenStones) {
        this.strengthenStones = strengthenStones;
    }

    public StrengthenWeapon getPlugin() {
        return plugin;
    }

    public void setPlugin(StrengthenWeapon plugin) {
        this.plugin = plugin;
    }
}
