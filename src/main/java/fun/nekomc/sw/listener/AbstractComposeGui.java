package fun.nekomc.sw.listener;

import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
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
        boolean playerClickedTargetInventory = inventoryView.getPlayer() instanceof Player &&
                inventoryView.getType() == invType && invTitle.equalsIgnoreCase(inventoryView.getTitle());
        return !playerClickedTargetInventory;
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
     *
     * @param event 点击事件
     */
    @EventHandler
    public void dispatchClickEvent(InventoryClickEvent event) {
        if (cannotHandleView(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory targetInventory = event.getInventory();
        // 点击位置
        int slot = event.getRawSlot();
        // 点击界外，不进行处理
        if (slot < 0) {
            return;
        }
        boolean leftClick = event.isLeftClick();
        // 包装事件（如果存在更多处理分支则考虑将事件拆分后扔到全局，拆出监听器单独处理）
        WrappedInventoryClickEvent wrappedEvent = WrappedInventoryClickEvent.builder()
                .clickPlayer(player)
                .slot(slot)
                .offset(slot - outputCellIndex)
                .leftClick(leftClick)
                .inventory(targetInventory)
                .event(event).build();

        if (event.getClick().isShiftClick()) {
            if (slot > outputCellIndex) {
                // 事件重分发：Shift + 点击背包
                onShiftClickBag(wrappedEvent);
                return;
            }
            // 事件重分发：Shift + 点击容器
            onShiftClickInventory(wrappedEvent);
            return;
        }
        // 不是点击事件的情况
        if (!event.isRightClick() && !event.isLeftClick()) {
            return;
        }
        // 事件重分发：点击背包
        if (slot > outputCellIndex) {
            onClickBag(wrappedEvent);
            return;
        }
        // 事件重分发：点击容器
        onClickInventory(wrappedEvent);
    }

    /**
     * Shift + 点击背包事件，含滚轮事件（不关注左右键）
     * TODO: 合成时使用堆叠的多个材料时，是否导致多消耗
     */
    public void onShiftClickBag(WrappedInventoryClickEvent wrapped) {
        // 容器满，取消事件
        int emptySlotIndex = wrapped.inventory.firstEmpty();
        if (-1 == emptySlotIndex) {
            wrapped.cancelEvent();
            return;
        }
        // 将点击的物品转移到容器的空格处
        ItemStack itemClicked = wrapped.event.getCurrentItem();
        wrapped.inventory.setItem(emptySlotIndex, itemClicked);
        wrapped.event.setCurrentItem(null);
        // 生成预览物品
        if (inventoryFullAfterWrappedEvent(wrapped)) {
            wrapped.inventory.setItem(outputCellIndex, generatePreviewItem(wrapped.inventory));
        }
    }

    /**
     * Shift + 点击容器事件
     */
    public void onShiftClickInventory(WrappedInventoryClickEvent wrapped) {
        // 点击容器后，清空输出格窗
        wrapped.inventory.setItem(outputCellIndex, null);

        ItemStack itemStack = wrapped.event.getCurrentItem();
        if (itemStack == null) {
            return;
        }
        // 玩家背包没有满
        if (PlayerBagUtils.isItemFull(itemStack, wrapped.clickPlayer)) {
            return;
        }
        // 点击输出端，执行实际强化逻辑
        if (wrapped.slot == outputCellIndex) {
            itemStack = generateStrengthItem(wrapped.inventory);
        }
        boolean allPutIn = PlayerBagUtils.itemToBag(itemStack, wrapped.clickPlayer, false);
        // 没有全部放入背包，在当前格子留下剩余
        if (!allPutIn) {
            wrapped.event.setCurrentItem(itemStack);
        } else {
            wrapped.event.setCurrentItem(null);
        }
    }

    /**
     * 点击背包事件
     */
    public void onClickBag(WrappedInventoryClickEvent wrapped) {
    }

    /**
     * 点击容器事件
     */
    public void onClickInventory(WrappedInventoryClickEvent wrapped) {
        // 点击容器后，清空输出格窗
        wrapped.inventory.setItem(outputCellIndex, null);
        // 点击输出格窗，按照强化规则生成强化后的物品
        if (wrapped.slot == outputCellIndex) {
            wrapped.inventory.setItem(outputCellIndex, generateStrengthItem(wrapped.inventory));
            return;
        }
        // 点击输入格窗，处理？
        ItemStack cursorItem = wrapped.event.getCursor();
        //检查鼠标中是否为规定物品
        boolean isSwItem = ItemUtils.isSwItem(cursorItem);
        //检查鼠标中是否为空气
        boolean isAir = (cursorItem != null && cursorItem.getType() == Material.AIR);
        if (!isSwItem && !isAir) {
            wrapped.cancelEvent();
        }
        // 生成预览物品
        if (inventoryFullAfterWrappedEvent(wrapped)) {
            wrapped.inventory.setItem(outputCellIndex, generatePreviewItem(wrapped.inventory));
        }
    }

    /**
     * 校验当容器输入端指定的索引置入物品后，是否可以填满该容器的输入端
     * 通常不能直接判断当前容器是否填满，因为物品置入、触发事件时，指定的物品并未放到容器内
     */
    private boolean inventoryFullAfterWrappedEvent(WrappedInventoryClickEvent event) {
        // 合法的容器、点击事件，且操作的目标物品不是 null
        if (null == event.inventory || event.slot < 0 || event.slot > outputCellIndex ||
                null == event.inventory.getItem(event.slot)) {
            return false;
        }
        for (int invIndex = 0; invIndex < outputCellIndex; invIndex++) {
            boolean nowSlotIsNull = null == event.inventory.getItem(invIndex);
            boolean isTargetSlot = event.slot == invIndex;
            // (a && !b) || (!a && b) 条件可以简化为 a ^ b
            if (nowSlotIsNull ^ isTargetSlot) {
                return false;
            }
        }
        return true;
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

    /**
     * 对原始容器点击事件的包装
     * 当前点击事件的划分粒度太粗，进行重新划分以细化
     * TODO: 确认 CTRL 点击、滚轮的场景是否能覆盖到
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    protected static class WrappedInventoryClickEvent {
        /**
         * 被包装的原始事件
         */
        InventoryClickEvent event;
        /**
         * 点击的窗格索引
         */
        int slot;
        /**
         * 点击的位置与输出位置的偏移，以铁砧为例：
         * offset=0 表示点击的为输出格窗
         * offset<0 表示点击的为输入端，slot 可能为 0 或 1
         * offset>0 则表示点击的为玩家背包
         */
        int offset;
        /**
         * 触发点击的玩家
         */
        Player clickPlayer;
        /**
         * 操作的目标容器对象
         */
        Inventory inventory;
        /**
         * 左键点击
         */
        boolean leftClick;

        /**
         * 取消该事件
         */
        public void cancelEvent() {
            event.setCancelled(true);
        }
    }
}
