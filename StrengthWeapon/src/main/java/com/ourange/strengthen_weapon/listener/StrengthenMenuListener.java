package com.ourange.strengthen_weapon.listener;

import com.ourange.strengthen_weapon.StrengthenWeapon;
import com.ourange.strengthen_weapon.domain.StrengthenItem;
import com.ourange.strengthen_weapon.domain.StrengthenStone;
import com.ourange.strengthen_weapon.service.StrengthenService;
import com.ourange.strengthen_weapon.utils.ItemLoreUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class StrengthenMenuListener implements Listener {
    private StrengthenWeapon plugin;
    private List<StrengthenItem> strengthenWeapons;
    private List<StrengthenStone> strengthenStones;
    private StrengthenService service;

    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
            HumanEntity player = event.getPlayer();
            if (event.getView().getType() == InventoryType.ANVIL && event.getView().getTitle().equalsIgnoreCase("Strengthen")) {
                Inventory anvilInv = event.getInventory();
                ItemStack stack1 = anvilInv.getItem(0);
                ItemStack stack2 = anvilInv.getItem(1);
                PlayerInventory inventory = player.getInventory();

                if (stack1 != null) {
                    int firstEmpty = inventory.firstEmpty();
                    inventory.setItem(firstEmpty, stack1);
                    anvilInv.setItem(0, null);
                }

                if (stack2 != null) {
                    int firstEmpty = inventory.firstEmpty();
                    inventory.setItem(firstEmpty, stack2);
                    anvilInv.setItem(1, null);
                }

            }

    }

    @EventHandler
    public void onInventoryClicked(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getView().getType() == InventoryType.ANVIL && event.getView().getTitle().equalsIgnoreCase("Strengthen")) {
                Inventory anvilInv = event.getInventory();

                int slot = event.getRawSlot();
                if (slot == 0) {
                    ItemStack stack1 = event.getCurrentItem();
                    ItemStack stack2 = event.getCursor();

                    if ((stack1 != null && checkSWItem(stack1, strengthenWeapons) == null)
                            || (stack2 != null && stack2.getType() != Material.AIR && checkSWItem(stack2, strengthenWeapons) == null)) {
                        event.setCancelled(true);
                    }
                }
                else if (slot == 1) {
                    ItemStack stack1 = event.getCurrentItem();
                    ItemStack stack2 = event.getCursor();

                    if ((stack1 != null && checkSWItem(stack1, strengthenStones) == null)
                            || (stack2 != null && stack2.getType() != Material.AIR && checkSWItem(stack2, strengthenStones) == null)) {
                        event.setCancelled(true);
                    }
                }
                else if (slot == 2) {
                    ItemStack stack = event.getCurrentItem();
                    if (stack != null && checkSWItem(stack, strengthenWeapons) != null) {
                        StrengthenItem strengthenItem = checkSWItem(anvilInv.getItem(0), strengthenWeapons);
                        StrengthenStone strengthenStone = (StrengthenStone) checkSWItem(anvilInv.getItem(1), strengthenStones);
                        ItemStack stack1 = anvilInv.getItem(0);
                        ItemStack stack3 = service.strengthen(player, stack1, strengthenItem,strengthenStone, false);

                        anvilInv.setItem(0, null);
                        anvilInv.setItem(1, null);
                        if (stack3 == null)
                            anvilInv.setItem(2, null);
                        else
                            anvilInv.setItem(2, stack3);
                    }
                }
                if (slot < 2) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            checkItems(anvilInv);
                        }
                    }, 2L);
                }
            }
        }
    }

    private void checkItems(Inventory anvilInv) {
        ItemStack stack1 = anvilInv.getItem(0);
        ItemStack stack2 = anvilInv.getItem(1);
        StrengthenItem strengthenItem = checkSWItem(stack1, strengthenWeapons);
        StrengthenStone strengthenStone = (StrengthenStone) checkSWItem(stack2, strengthenStones);

        if (strengthenItem != null && strengthenStone != null) {
                    ItemStack stack3 = service.strengthenSuccessResult(stack1, strengthenItem);
                    anvilInv.setItem(2, stack3);
        }
        else {
            anvilInv.setItem(2,null);
        }
    }

    private StrengthenItem checkSWItem(ItemStack stack, List<? extends StrengthenItem> items) {
        if (stack != null && stack.getItemMeta() != null) {
            List<String> lore = stack.getItemMeta().getLore();
            if (lore != null) {
                for (StrengthenItem item: items) {
                    if (ItemLoreUtils.getItemName(lore).equalsIgnoreCase(item.getName())
                    && stack.getType().toString().equalsIgnoreCase(item.getMaterial())) {
                        if(item instanceof StrengthenStone) {
                            if(ItemLoreUtils.getItemLevel(lore, item) != item.getLevel())
                                continue;
                        }
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public StrengthenWeapon getPlugin() {
        return plugin;
    }

    public void setPlugin(StrengthenWeapon plugin) {
        this.plugin = plugin;
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

    public StrengthenService getService() {
        return service;
    }

    public void setService(StrengthenService service) {
        this.service = service;
    }
}
