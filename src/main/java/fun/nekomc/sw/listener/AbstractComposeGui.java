package fun.nekomc.sw.listener;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 适用于合成场景的自定义容器实现
 * created: 2022/3/9 01:01
 *
 * @author Chiru
 */
public abstract class AbstractComposeGui implements Listener {

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
    protected final int outputCellIndex;

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
    protected boolean cannotHandleView(InventoryEvent inventoryEvent) {
        if (null == inventoryEvent) {
            return true;
        }
        InventoryView inventoryView = inventoryEvent.getView();
        return !(inventoryView.getPlayer() instanceof Player &&
                inventoryView.getType() == invType && invTitle.equalsIgnoreCase(inventoryView.getTitle()));
    }

    /**
     * 容器关闭时的处理
     * 默认实现：将容器内输入端的物品返回玩家背包，背包满则扔在地上
     *
     * @param event 容器关闭事件
     */
    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        if (cannotHandleView(event)) {
            return;
        }
        Player targetPlayer = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        for (int cellIndex = 0; cellIndex < invSize; cellIndex++) {
            // 关闭容器时，输出格子的道具不退回
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
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (cannotHandleView(event)) {
            return;
        }
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
     * TODO：瞎TM改
     *
     * @param event 点击事件
     */
    @EventHandler
    public void onInventoryClicked(InventoryClickEvent event) {
        if (cannotHandleView(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory targetInventory = event.getInventory();
        // 点击位置
        int slot = event.getRawSlot();
        // 事件是否取消
        boolean cancel = true;
        // Shift + 点击
        if (event.getClick().isShiftClick()) {
            //点击的是背包
            if (slot > outputCellIndex) {
                // 将点击的物品转移到容器的空格处
                int emptySlotIndex = targetInventory.firstEmpty();
                ItemStack itemClicked = event.getCurrentItem();
                targetInventory.setItem(emptySlotIndex, itemClicked);
                event.setCurrentItem(null);
                cancel = false;
                // 生成预览物品，TODO: 瞎TM写
                if (emptySlotIndex == outputCellIndex - 1) {
                    targetInventory.setItem(outputCellIndex, generatePreviewItem(targetInventory));
                }
            }
            // 点击的是anvil
            else {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null) {
                    // 玩家背包没有满
                    if (!PlayerBagUtils.isItemFull(itemStack, player)) {
                        if (slot == outputCellIndex) {
                            itemStack = generateStrengthItem(targetInventory);
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
            if (slot < outputCellIndex) {
                ItemStack cursorItem = event.getCursor();
                //检查鼠标中是否为规定物品
                boolean isItem = ItemUtils.isSwItem(cursorItem);
                //检查鼠标中是否为空气
                boolean isAir = (cursorItem != null && cursorItem.getType() == Material.AIR);
                if (isItem || isAir) {
                    cancel = false;
                }
            } else if (slot == outputCellIndex) {
                targetInventory.setItem(outputCellIndex, generateStrengthItem(targetInventory));
            } else {
                cancel = false;
            }
        }
        // 设置事件取消
        event.setCancelled(cancel);
        // 事件没有取消，检查是否能够合成
        if (!cancel) {
            StrengthenWeapon.server().getScheduler().scheduleSyncDelayedTask(StrengthenWeapon.getInstance(),
                    () -> targetInventory.setItem(outputCellIndex, generateStrengthItem(targetInventory)), 2L);
        }

    }

    /**
     * 校验容器的输入是否填满，如果传入 null 则返回 true
     */
    protected boolean inventoryInputNotFull(Inventory inventory) {
        if (null == inventory) {
            return true;
        }
        for (int invIndex = 0; invIndex < outputCellIndex; invIndex++) {
            if (null == inventory.getItem(invIndex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据当前容器中物品生成预览用物品
     *
     * @param inventory 容器
     * @return 预览用物品，仅用于展示
     */
    protected abstract ItemStack generatePreviewItem(@NotNull Inventory inventory);

    /**
     * 根据当前容器中物品生成实际强化后的物品
     *
     * @param inventory 容器
     * @return 实际给到玩家的物品
     */
    protected abstract ItemStack generateStrengthItem(@NotNull Inventory inventory);
}
