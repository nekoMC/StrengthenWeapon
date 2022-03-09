package fun.nekomc.sw.gui;

import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * 适用于合成场景的自定义容器实现
 * TODO: 参考 StrengthMenuListener 实现这条神奇的链子
 * created: 2022/3/9 01:01
 *
 * @author Chiru
 */
public abstract class AbstractComposeGui {

    /**
     * 容器类型
     */
    private final InventoryType invType;

    /**
     * 容器格子总数，如铁砧的总数为 3
     */
    private final Integer invSize;

    /**
     * 容器标题
     */
    private final String invTitle;

    /**
     * 输出格子的编号，以铁砧为例，outputCellIndex 为 2
     */
    private final int outputCellIndex;

    AbstractComposeGui(InventoryType invType, String invTitle, int outputCellIndex) {
        this.invTitle = invTitle;
        this.invType = invType;
        this.invSize = invType.getDefaultSize();
        this.outputCellIndex = outputCellIndex;
    }

    /**
     * 检验当前容器是否能处理指定的容器事件
     *
     * @param inventoryEvent 指定的容器事件
     * @return 是否能处理，即与注册的容器类型匹配且标题匹配
     */
    public boolean canHandleView(InventoryEvent inventoryEvent) {
        if (null == inventoryEvent) {
            return false;
        }
        InventoryView inventoryView = inventoryEvent.getView();
        return inventoryView.getPlayer() instanceof Player &&
                inventoryView.getType() == invType && invTitle.equalsIgnoreCase(inventoryView.getTitle());
    }

    /**
     * 容器关闭时的处理
     * 默认实现：将容器内输入端的物品返回玩家背包，背包满则扔在地上
     *
     * @param event 容器关闭事件
     */
    public void handleCloseEvent(InventoryCloseEvent event) {
        Player targetPlayer = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        for (int cellIndex = 0; cellIndex < invSize; cellIndex++) {
            // 输出格子的道具不退回
            if (cellIndex == outputCellIndex) {
                inventory.setItem(cellIndex, null);
                continue;
            }
            // 输入格子中的道具全部退回
            ItemStack nowItem = inventory.getItem(cellIndex);
            if (null != nowItem) {
                PlayerBagUtils.givePlayerItem(targetPlayer, nowItem);
                inventory.setItem(cellIndex, null);
            }
        }
    }

    /**
     * 容器拖拽事件处理
     * 默认实现：在容器内禁用点击拖拽（拆分）
     *
     * @param event 点击拖拽事件
     */
    public void handleDragEvent(InventoryDragEvent event) {
        Set<Integer> slots = event.getRawSlots();
        for (Integer slot : slots) {
            if (slot < invSize) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * 容器点击事件处理，不推荐子类直接重写本方法（特殊处理除外）
     * 默认实现：如果为合法的点击事件，则交给抽象方法处理，否则取消事件
     * TODO: 确认容器的默认实现是否支持物品的自动填充
     *
     * @param event 点击事件
     */
    protected void handleClickEvent(InventoryClickEvent event) {
//        // 点击位置
//        int slot = event.getRawSlot();
//        // 事件是否取消
//        boolean cancel = true;
//        // Shift + 点击
//        if (event.getClick().isShiftClick()) {
//            //点击的是背包
//            if (slot > SW_RESULT_INDEX) {
//                // 第一个格子中没有物品
//                if (anvilInv.getItem(SW_WEAPON_INDEX) == null) {
//                    ItemStack stack2 = event.getCurrentItem();
//                    if (ItemUtils.isSwItem(stack2)) {
//                        anvilInv.setItem(SW_WEAPON_INDEX, stack2);
//                        event.setCurrentItem(null);
//                        cancel = false;
//                    }
//                }
//                // 第一个格子中有物品，第二个格子中没有
//                else if (anvilInv.getItem(SW_STONE_INDEX) == null) {
//                    ItemStack stack2 = event.getCurrentItem();
//                    if (ItemUtils.isSwItem(stack2)) {
//                        anvilInv.setItem(SW_STONE_INDEX, stack2);
//                        event.setCurrentItem(null);
//                        cancel = false;
//                    }
//                }
//            }
//            // 点击的是anvil
//            else {
//                ItemStack itemStack = event.getCurrentItem();
//                if (itemStack != null) {
//                    // 玩家背包没有满
//                    if (!PlayerBagUtils.isItemFull(itemStack, player)) {
//                        if (slot == SW_RESULT_INDEX) {
//                            // TODO:
//                            itemStack = strengthen(anvilInv, player);
//                        }
//                        boolean allPutIn = PlayerBagUtils.itemToBag(itemStack, player, false);
//                        // 没有全部放入背包，在当前格子留下剩余
//                        if (!allPutIn) {
//                            event.setCurrentItem(itemStack);
//                        } else {
//                            event.setCurrentItem(null);
//                        }
//                        cancel = false;
//                    }
//                }
//            }
//        }
//        // 左键或右键
//        else if (event.isLeftClick() || event.isRightClick()) {
//            switch (slot) {
//                case SW_WEAPON_INDEX:
//                case SW_STONE_INDEX:
//                    ItemStack cursorItem = event.getCursor();
//                    //检查鼠标中是否为规定物品
//                    boolean isItem = ItemUtils.isSwItem(cursorItem);
//                    //检查鼠标中是否为空气
//                    boolean isAir = (cursorItem != null && cursorItem.getType() == Material.AIR);
//                    if (isItem || isAir) {
//                        cancel = false;
//                    }
//                    break;
//                case SW_RESULT_INDEX:
//                    ItemStack stack = event.getCurrentItem();
//                    if (stack != null && ItemUtils.isSwItem(stack)) {
//                        ItemStack swResult = strengthen(anvilInv, player);
//                        anvilInv.setItem(SW_RESULT_INDEX, swResult);
//                        cancel = false;
//                    }
//                    break;
//                default:
//                    cancel = false;
//            }
//        }
//        // 设置事件取消
//        event.setCancelled(cancel);
//        // 事件没有取消，检查是否能够合成
//        if (!cancel) {
//            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> checkRecipe(anvilInv), 2L);
//        }

    }
}
