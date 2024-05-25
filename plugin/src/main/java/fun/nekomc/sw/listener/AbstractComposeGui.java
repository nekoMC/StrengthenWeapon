package fun.nekomc.sw.listener;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import fun.nekomc.sw.utils.PlayerHolder;
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

import java.util.*;
import java.util.function.Supplier;

/**
 * 适用于合成场景的自定义容器实现
 * 适用于存在输入输出窗格的容器，不适用于附魔台等容器
 * created: 2022/3/9 01:01
 *
 * @author Chiru
 */
public abstract class AbstractComposeGui implements Listener {

    /**
     * 如何读取配置文件中描述的容器类型
     */
    private final Supplier<InventoryType> invTypeSupplier;

    /**
     * 容器格子总数，如铁砧的总数为 3
     */
    private final Integer invSize;

    /**
     * 容器标题
     */
    private final String invTitle;

    /**
     * 每个 slot 对物品类型的要求
     */
    private final Map<Integer, ItemsTypeEnum> slotTypeRule;

    /**
     * 输出格子的编号，以铁砧为例，outputCellIndex 为 2
     */
    protected final int outputCellIndex;

    AbstractComposeGui(Supplier<InventoryType> invTypeSupplier, String invTitle, int outputCellIndex) {
        this.invTitle = invTitle;
        this.invTypeSupplier = invTypeSupplier;
        this.invSize = invTypeSupplier.get().getDefaultSize();
        this.outputCellIndex = outputCellIndex;
        slotTypeRule = new HashMap<>(outputCellIndex);
    }

    /**
     * 容器关闭时的处理
     * 默认实现：将容器内输入端的物品返回玩家背包，背包满则扔在地上
     *
     * @param event 容器关闭事件
     */
    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        if (unCareInventory(event)) {
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
        if (unCareInventory(event)) {
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
        if (unCareInventory(event)) {
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

        // 事件重分发：吃掉 shift + 点击事件
        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            return;
        }
        // 不是点击事件的情况
        if (!event.isRightClick() && !event.isLeftClick()) {
            return;
        }
        PlayerHolder.setPlayer(player);
        // 事件重分发：点击背包、容器
        if (slot > outputCellIndex) {
            onClickBag(wrappedEvent);
        } else {
            // 事件重分发：点击容器
            onClickInventory(wrappedEvent);
        }
        PlayerHolder.release();
    }

    /**
     * 点击背包事件
     */
    @SuppressWarnings("unused")
    protected void onClickBag(WrappedInventoryClickEvent wrapped) {
    }

    /**
     * 检验当前容器是否能处理指定的容器事件
     *
     * @param inventoryEvent 指定的容器事件
     * @return 是否能处理，即与注册的容器类型匹配且标题匹配
     */
    protected boolean unCareInventory(InventoryEvent inventoryEvent) {
        if (null == inventoryEvent) {
            return true;
        }
        InventoryView inventoryView = inventoryEvent.getView();
        boolean playerClickedTargetInventory = inventoryView.getPlayer() instanceof Player &&
                inventoryView.getType() == invTypeSupplier.get() && invTitle.equalsIgnoreCase(inventoryView.getTitle());
        return !playerClickedTargetInventory;
    }

    /**
     * 点击容器事件
     * 事件整理思路（无关紧要的事件全取消）参考：<a href="https://github.com/WesJD/AnvilGUI">AnvilGUI</a>
     */
    protected void onClickInventory(WrappedInventoryClickEvent wrapped) {
        // 消除默认处理干扰
        wrapped.cancelEvent();
        // 点击容器后，清空输出格窗
        wrapped.inventory.setItem(outputCellIndex, null);
        // 点击输出格窗，进行自定义处理，防止在输出格窗生成道具后取走原料
        if (wrapped.slot == outputCellIndex) {
            // 不满足配方要求，不处理
            if (!checkRecipe(wrapped.inventory)) {
                return;
            }
            // 执行强化，如果得到 null 则不处理
            ItemStack strengthened = generateStrengthItem(wrapped);
            if (null == strengthened) {
                return;
            }
            consume(wrapped.inventory);
            wrapped.event.getView().setCursor(strengthened);
            return;
        }
        ItemStack cursorItem = wrapped.event.getCursor();
        //检查鼠标中是否为空气
        boolean cursorIsAir = (cursorItem == null || cursorItem.getType() == Material.AIR);
        // 空鼠标点击输入格窗
        if (cursorIsAir) {
            cursorItem = null;
        }
        // 交换鼠标和目标窗格中的物品
        ItemStack itemInSlot = wrapped.inventory.getItem(wrapped.slot);
        wrapped.event.getView().setCursor(itemInSlot);
        wrapped.inventory.setItem(wrapped.slot, cursorItem);
        // 生成预览物品
        if (checkRecipe(wrapped.inventory)) {
            wrapped.inventory.setItem(outputCellIndex, generatePreviewItem(wrapped));
        }
    }

    /**
     * 检验当前容器输入端的物品是否符合注册的规则
     */
    private boolean checkRecipe(Inventory targetInv) {
        if (null == targetInv) {
            return false;
        }
        for (Map.Entry<Integer, ItemsTypeEnum> slotAndTypeEntry : slotTypeRule.entrySet()) {
            Integer slot = slotAndTypeEntry.getKey();
            ItemsTypeEnum expectedType = slotAndTypeEntry.getValue();
            // 物品的元数据中存在数据，且存在相关配置？
            ItemStack item = targetInv.getItem(slot);
            String nameFromMeta = ItemUtils.getNameFromMeta(item);
            Optional<SwItemConfigDto> itemConfig = ConfigManager.getItemConfig(nameFromMeta);
            if (itemConfig.isEmpty()) {
                return false;
            }
            // 为该物品配置的 Type 与注册的规则匹配？
            String actualItemType = itemConfig.get().getType();
            if (!expectedType.name().equals(actualItemType)) {
                return false;
            }
        }
        return recipeMatch(targetInv);
    }

    /**
     * 精确校验合成规则，如果需要，则在子类进行重写
     *
     * @param targetInv 目标容器，内中元素已经过 checkRecipe 校验
     * @return 是否能通过精确校验
     */
    protected abstract boolean recipeMatch(Inventory targetInv);

    /**
     * 遍历注册的合成规则 Map，从每个输入窗格中减掉一个物品（不校验物品内容）
     *
     * @param targetInv 要操作的容器
     * @throws SwException 操作无法完成时抛出，比如容器为空，输入格窗为空等
     */
    protected void consume(Inventory targetInv) {
        if (null == targetInv) {
            throw new SwException("targetInv cannot be null");
        }
        for (Integer slot : slotTypeRule.keySet()) {
            ItemStack item = targetInv.getItem(slot);
            if (null == item) {
                throw new SwException("cannot consume input slots");
            }
            int itemAmount = item.getAmount();
            if (itemAmount == 1) {
                targetInv.setItem(slot, null);
            } else {
                item.setAmount(itemAmount - 1);
                targetInv.setItem(slot, item);
            }
        }
    }

    /**
     * 声明指定的窗格要限制的物品类型
     *
     * @param slot     输入端窗格编号
     * @param typeEnum 物品类型
     */
    protected void registerCheckRule(int slot, ItemsTypeEnum typeEnum) {
        Assert.isTrue(slot < outputCellIndex, "slot cannot greater than outputCellIndex");
        slotTypeRule.put(slot, typeEnum);
    }

    /**
     * 根据当前容器中物品生成预览用物品，只要输入窗格的物品满足注册条件就会触发
     * 预览用的物品用于描述本次强化的提示，比如成功率，无法强化原因等
     *
     * @param wrapped 包装后的事件
     * @return 预览用物品，仅用于展示
     */
    protected abstract ItemStack generatePreviewItem(@NotNull WrappedInventoryClickEvent wrapped);

    /**
     * 根据当前容器中物品生成实际强化后的物品，返回 null 时取消本次强化
     * 返回其他物品时，会覆盖掉预览物品
     *
     * @param wrapped 包装后的事件
     * @return 实际给到玩家的物品
     */
    protected abstract ItemStack generateStrengthItem(@NotNull WrappedInventoryClickEvent wrapped);

    /**
     * 对原始容器点击事件的包装
     * 当前点击事件的划分粒度太粗，进行重新划分以细化
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
