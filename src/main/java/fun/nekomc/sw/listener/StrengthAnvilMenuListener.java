package fun.nekomc.sw.listener;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.SwStrengthenStoneConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.service.StrengthenService;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * 全局事件监听分发器
 *
 * @author ourange
 */
public class StrengthAnvilMenuListener extends AbstractComposeGui implements Listener {

    private static final int SW_WEAPON_INDEX = 0;
    private static final int SW_STONE_INDEX = 1;

    private StrengthenServiceImpl service;

    public StrengthAnvilMenuListener() {
        super(InventoryType.ANVIL, ConfigManager.getConfigYml().getStrengthTitle(), 2);
        // 注册校验规则
        registerCheckRule(0, ItemsTypeEnum.BLANK);
        registerCheckRule(1, ItemsTypeEnum.STRENGTHEN_STONE);
    }

//    /**
//     * 不同的点击类型和位置进行判断及强化
//     *
//     * @param event 点击事件
//     */
//    @EventHandler
//    public void onInventoryClicked(InventoryClickEvent event) {
//        if (event.getWhoClicked() instanceof Player) {
//            Player player = (Player) event.getWhoClicked();
//            if (checkInventory(event.getView())) {
//                Inventory anvilInv = event.getInventory();
//                // 点击位置
//                int slot = event.getRawSlot();
//                // 事件是否取消
//                boolean cancel = true;
//                // Shift + 点击
//                if (event.getClick().isShiftClick()) {
//                    //点击的是背包
//                    if (slot > outputCellIndex) {
//                        // 第一个格子中没有物品
//                        if (anvilInv.getItem(outputCellIndex) == null) {
//                            ItemStack stack2 = event.getCurrentItem();
//                            if (ItemUtils.isSwItem(stack2)) {
//                                anvilInv.setItem(SW_WEAPON_INDEX, stack2);
//                                event.setCurrentItem(null);
//                                cancel = false;
//                            }
//                        }
//                        // 第一个格子中有物品，第二个格子中没有
//                        else if (anvilInv.getItem(SW_STONE_INDEX) == null) {
//                            ItemStack stack2 = event.getCurrentItem();
//                            if (ItemUtils.isSwItem(stack2)) {
//                                anvilInv.setItem(SW_STONE_INDEX, stack2);
//                                event.setCurrentItem(null);
//                                cancel = false;
//                            }
//                        }
//                    }
//                    // 点击的是anvil
//                    else {
//                        ItemStack itemStack = event.getCurrentItem();
//                        if (itemStack != null) {
//                            // 玩家背包没有满
//                            if (!PlayerBagUtils.isItemFull(itemStack, player)) {
//                                if (slot == outputCellIndex) {
//                                    // TODO:
//                                    itemStack = strengthen(anvilInv, player);
//                                }
//                                boolean allPutIn = PlayerBagUtils.itemToBag(itemStack, player, false);
//                                // 没有全部放入背包，在当前格子留下剩余
//                                if (!allPutIn) {
//                                    event.setCurrentItem(itemStack);
//                                } else {
//                                    event.setCurrentItem(null);
//                                }
//                                cancel = false;
//                            }
//                        }
//                    }
//                }
//                // 左键或右键
//                else if (event.isLeftClick() || event.isRightClick()) {
//                    switch (slot) {
//                        case SW_WEAPON_INDEX:
//                        case SW_STONE_INDEX:
//                            ItemStack cursorItem = event.getCursor();
//                            //检查鼠标中是否为规定物品
//                            boolean isItem = ItemUtils.isSwItem(cursorItem);
//                            //检查鼠标中是否为空气
//                            boolean isAir = (cursorItem != null && cursorItem.getType() == Material.AIR);
//                            if (isItem || isAir) {
//                                cancel = false;
//                            }
//                            break;
//                        case 2:
//                            ItemStack stack = event.getCurrentItem();
//                            if (stack != null && ItemUtils.isSwItem(stack)) {
//                                ItemStack swResult = strengthen(anvilInv, player);
//                                anvilInv.setItem(outputCellIndex, swResult);
//                                cancel = false;
//                            }
//                            break;
//                        default:
//                            cancel = false;
//                    }
//                }
//                // 设置事件取消
//                event.setCancelled(cancel);
//                // 事件没有取消，检查是否能够合成
//                if (!cancel) {
//                    StrengthenWeapon.server().getScheduler().scheduleSyncDelayedTask(StrengthenWeapon.getInstance(), () -> checkRecipe(anvilInv), 2L);
//                }
//
//            }
//        }
//    }
//
//    private boolean checkInventory(InventoryView inventoryView) {
//        return inventoryView.getType() == InventoryType.ANVIL && "Strengthen".equalsIgnoreCase(inventoryView.getTitle());
//    }
//
//    private void checkRecipe(Inventory anvilInv) {
//        ItemStack swWeapon = anvilInv.getItem(SW_WEAPON_INDEX);
//        ItemStack swStone = anvilInv.getItem(SW_STONE_INDEX);
//        if (null == swWeapon || null == swStone) {
//            return;
//        }
//        ItemStack swResult = service.strengthenSuccessResult(swWeapon, swStone);
//        anvilInv.setItem(outputCellIndex, swResult);
//    }

//    private StrengthenItem checkSwItem(ItemStack stack, List<? extends SwItemConfigDto> items) {
//        if (stack != null && stack.getItemMeta() != null) {
//            List<String> lore = stack.getItemMeta().getLore();
//            if (lore != null) {
//                for (SwItemConfigDto item: items) {
//                    if (ItemUtils.getItemName(lore).equalsIgnoreCase(item.getName())
//                            && stack.getType().toString().equalsIgnoreCase(item.getMaterial())) {
//                        if(item instanceof SwStrengthenStoneConfigDto) {
//                            if(ItemUtils.getItemLevel(lore, item) != item.getLevel()) {
//                                continue;
//                            }
//                        }
//                        return item;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private ItemStack strengthen(Inventory anvilInv, Player player) {
        ItemStack swWeapon = anvilInv.getItem(SW_WEAPON_INDEX);
        ItemStack swStone = anvilInv.getItem(SW_STONE_INDEX);
        ItemStack swResult = null;

        if (swWeapon != null && swStone != null) {
            // StrengthenItem strengthenItem = checkSwItem(swWeapon, strengthenWeapons);
            // StrengthenStone strengthenStone = (StrengthenStone) checkSwItem(swStone, strengthenStones);
            // ===================================
            // TODO: 临时处理：直接将 swStone 传入，忽略校验结果以恢复强化石功能
            // 后续方案：拓展强化石配置（拓展为一个子级 DTO，通过新增的字段以区分如何解析 type）
            SwStrengthenStoneConfigDto swStrengthenStoneConfigDto = new SwStrengthenStoneConfigDto();
            swStrengthenStoneConfigDto.setChance(50);
            // ===================================
            swResult = service.strengthen(player, swWeapon, swStrengthenStoneConfigDto, false);
        }
        return swResult;
    }

    @Override
    protected ItemStack generatePreviewItem(@NotNull Inventory inventory) {
        ItemStack item = inventory.getItem(0);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Demo For Display");
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    protected ItemStack generateStrengthItem(@NotNull Inventory inventory) {
        ItemStack item = inventory.getItem(0);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Demo For Result");
        item.setItemMeta(itemMeta);
        return item;
    }

    public void setService(StrengthenServiceImpl service){
        this.service = service;
    }
}
