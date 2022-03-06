package fun.nekomc.sw.listener;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import fun.nekomc.sw.service.StrengthenService;
import fun.nekomc.sw.utils.ItemLoreUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import com.sun.istack.internal.NotNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Set;

/**
 * @author ourange
 */
public class StrengthenMenuListener implements Listener {
    private static final int SW_WEAPON_INDEX = 0;
    private static final int SW_STONE_INDEX = 1;
    private static final int SW_RESULT_INDEX = 2;
    private StrengthenWeapon plugin;
    private List<StrengthenItem> strengthenWeapons;
    private List<StrengthenStone> strengthenStones;
    private StrengthenService service;

    /**
     * 容器关闭时，将容器内的物品返回玩家背包，背包满则扔在地上
     * @param event 容器关闭事件
     */
    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (checkInventory(event.getView())) {
            Inventory anvilInv = event.getInventory();
            ItemStack swWeapon = anvilInv.getItem(SW_WEAPON_INDEX);
            ItemStack swStone = anvilInv.getItem(SW_STONE_INDEX);

            if (swWeapon != null) {
                PlayerBagUtils.itemToBag(swWeapon, player, true);
                anvilInv.setItem(SW_WEAPON_INDEX, null);
            }

            if (swStone != null) {
                PlayerBagUtils.itemToBag(swStone, player, true);
                anvilInv.setItem(SW_STONE_INDEX, null);
            }
        }
    }

    /**
     * 强化容器拖拽事件取消
     * @param event 拖拽事件
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (checkInventory(event.getView())) {
                Set<Integer> slots = event.getRawSlots();

                if (slots.contains(SW_WEAPON_INDEX)
                        || slots.contains(SW_STONE_INDEX)
                        || slots.contains(SW_RESULT_INDEX)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * 不同的点击类型和位置进行判断及强化
     * @param event 点击事件
     */
    @EventHandler
    public void onInventoryClicked(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (checkInventory(event.getView())) {
                Inventory anvilInv = event.getInventory();
                // 点击位置
                int slot = event.getRawSlot();
                // 事件是否取消
                boolean cancel = true;
                // Shift + 点击
                if (event.getClick().isShiftClick()) {
                    //点击的是背包
                    if (slot > SW_RESULT_INDEX) {
                        // 第一个格子中没有物品
                        if (anvilInv.getItem(SW_WEAPON_INDEX) == null) {
                            ItemStack stack2 = event.getCurrentItem();
                            if (checkSwItem(stack2, strengthenWeapons) != null) {
                                anvilInv.setItem(SW_WEAPON_INDEX, stack2);
                                event.setCurrentItem(null);
                                cancel = false;
                            }
                        }
                        // 第一个格子中有物品，第二个格子中没有
                        else if (anvilInv.getItem(SW_STONE_INDEX) == null) {
                            ItemStack stack2 = event.getCurrentItem();
                            if (checkSwItem(stack2, strengthenStones) != null) {
                                anvilInv.setItem(SW_STONE_INDEX, stack2);
                                event.setCurrentItem(null);
                                cancel = false;
                            }
                        }
                    }
                    // 点击的是anvil
                    else {
                        ItemStack itemStack = event.getCurrentItem();
                        if (itemStack != null) {
                            // 玩家背包没有满
                            if (!PlayerBagUtils.isItemFull(itemStack, player)) {
                                if (slot == SW_RESULT_INDEX) {
                                    if (checkSwItem(itemStack, strengthenWeapons) != null) {
                                        itemStack = strengthen(anvilInv, player);
                                    }
                                }
                                boolean allPutIn = PlayerBagUtils.itemToBag(itemStack, player, false);
                                // 没有全部放入背包，在当前格子留下剩余
                                if (!allPutIn) {
                                    event.setCurrentItem(itemStack);
                                } else {
                                    event.setCurrentItem(null);
                                }
                                cancel = false;
                            }
                        }
                    }
                }
                // 左键或右键
                else if (event.isLeftClick() || event.isRightClick()) {
                    switch (slot) {
                        case SW_WEAPON_INDEX:
                        case SW_STONE_INDEX:
                            ItemStack cursorItem = event.getCursor();
                            //检查鼠标中是否为规定物品
                            boolean isItem = (checkSwItem(cursorItem, swItemByIndex(slot)) != null);
                            //检查鼠标中是否为空气
                            boolean isAir = (cursorItem != null && cursorItem.getType() == Material.AIR);
                            if (isItem || isAir) {
                                cancel = false;
                            }
                            break;
                        case SW_RESULT_INDEX:
                            ItemStack stack = event.getCurrentItem();
                            if (stack != null && checkSwItem(stack, strengthenWeapons) != null) {
                                ItemStack swResult = strengthen(anvilInv, player);
                                anvilInv.setItem(SW_RESULT_INDEX, swResult);
                                cancel = false;
                            }
                            break;
                        default:
                            cancel = false;
                    }
                }
                // 设置事件取消
                event.setCancelled(cancel);
                // 事件没有取消，检查是否能够合成
                if (!cancel) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> checkRecipe(anvilInv), 2L);
                }

            }
        }
    }

    private List<? extends StrengthenItem> swItemByIndex(int index) {
        if (index == SW_WEAPON_INDEX) {
            return strengthenWeapons;
        }
        if ( index == SW_STONE_INDEX) {
            return strengthenStones;
        }
        return null;
    }

    private boolean checkInventory(InventoryView inventoryView) {
        return inventoryView.getType() == InventoryType.ANVIL && "Strengthen".equalsIgnoreCase(inventoryView.getTitle());
    }

    private void checkRecipe(Inventory anvilInv) {
        ItemStack swWeapon = anvilInv.getItem(SW_WEAPON_INDEX);
        ItemStack swStone = anvilInv.getItem(SW_STONE_INDEX);
        ItemStack swResult = null;
        StrengthenItem strengthenItem = checkSwItem(swWeapon, strengthenWeapons);
        StrengthenStone strengthenStone = (StrengthenStone) checkSwItem(swStone, strengthenStones);

        if (strengthenItem != null && strengthenStone != null) {
            swResult = service.strengthenSuccessResult(swWeapon, strengthenItem);
        }
        anvilInv.setItem(SW_RESULT_INDEX, swResult);
    }

    private StrengthenItem checkSwItem(ItemStack stack, List<? extends StrengthenItem> items) {
        if (stack != null && stack.getItemMeta() != null) {
            List<String> lore = stack.getItemMeta().getLore();
            if (lore != null) {
                for (StrengthenItem item: items) {
                    if (ItemLoreUtils.getItemName(lore).equalsIgnoreCase(item.getName())
                            && stack.getType().toString().equalsIgnoreCase(item.getMaterial())) {
                        if(item instanceof StrengthenStone) {
                            if(ItemLoreUtils.getItemLevel(lore, item) != item.getLevel()) {
                                continue;
                            }
                        }
                        return item;
                    }
                }
            }
        }
        return null;
    }

    private ItemStack strengthen(Inventory anvilInv, Player player) {
        ItemStack swWeapon = anvilInv.getItem(SW_WEAPON_INDEX);
        ItemStack swStone = anvilInv.getItem(SW_STONE_INDEX);
        ItemStack swResult = null;

        if (swWeapon != null && swStone != null) {
            StrengthenItem strengthenItem = checkSwItem(swWeapon, strengthenWeapons);
            StrengthenStone strengthenStone = (StrengthenStone) checkSwItem(swStone, strengthenStones);

            swResult = service.strengthen(player, swWeapon, strengthenItem, strengthenStone, false);

            anvilInv.setItem(SW_WEAPON_INDEX, consumeItem(swWeapon));
            anvilInv.setItem(SW_STONE_INDEX, consumeItem(swStone));
        }
        return swResult;
    }

    private ItemStack consumeItem(@NotNull ItemStack itemStack) {
        ItemStack resStack = itemStack.clone();
        int amount = itemStack.getAmount();
        if (amount > 1) {
            resStack.setAmount(amount - 1);
        } else {
            resStack = null;
        }
        return  resStack;
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
