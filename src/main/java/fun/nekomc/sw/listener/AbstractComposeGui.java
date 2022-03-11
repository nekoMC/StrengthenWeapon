package fun.nekomc.sw.listener;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
     * 每个 slot 对物品类型的要求
     */
    private final Map<Integer, ItemsTypeEnum> slotTypeRule;

    private final HashMap<Player, Integer> playerTasks;

    /**
     * 输出格子的编号，以铁砧为例，outputCellIndex 为 2
     */
    protected final int outputCellIndex;

    AbstractComposeGui(InventoryType invType, String invTitle, int outputCellIndex) {
        this.invTitle = invTitle;
        this.invType = invType;
        this.invSize = invType.getDefaultSize();
        this.outputCellIndex = outputCellIndex;
        slotTypeRule = new HashMap<>(outputCellIndex);
        playerTasks = new HashMap<>();
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
    public void onInventoryClicked(InventoryClickEvent event) {
        if (cannotHandleView(event)) {
            return;
        }
        // 点击界外，不进行处理
        int clickSlot = event.getRawSlot();
        if (clickSlot < 0) {
            return;
        }
        Inventory inventory = event.getInventory();
        // 点击了目标物品时，执行实际强化逻辑
        if (outputCellIndex == clickSlot) {
            ItemStack targetItem = generateStrengthItem(inventory);
            consume(inventory);
            inventory.setItem(outputCellIndex, targetItem);
            return;
        }
        // 让其他该死的事件见鬼去吧，编写和维护都太 TM 麻烦
        // 取消玩家旧的任务
        Player player = (Player) event.getWhoClicked();
        Integer playerOldTask = playerTasks.get(player);
        BukkitScheduler scheduler = StrengthenWeapon.server().getScheduler();
        if (null != playerOldTask) {
            scheduler.cancelTask(playerOldTask);
        }
        // 记录玩家将要执行的任务
        Integer playerNowTask = scheduler.scheduleSyncDelayedTask(StrengthenWeapon.getInstance(), () -> {
            // 通过校验时，生成预览物品供展示
            if (checkRecipe(inventory)) {
                inventory.setItem(outputCellIndex, generatePreviewItem(inventory));
            }
            playerTasks.remove(player);
        }, 2L);
        playerTasks.put(player, playerNowTask);
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
            if (!itemConfig.isPresent()) {
                return false;
            }
            // 为该物品配置的 Type 与注册的规则匹配？
            String actualItemType = itemConfig.get().getType();
            if (!expectedType.name().equals(actualItemType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 遍历注册的合成规则 Map，从每个输入格窗中减掉一个物品（不校验物品内容）
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
     * 根据当前容器中物品生成预览用物品
     * 注意，该物品不应该被玩家通过任何方法获得，否则有被玩家利用漏洞进行刷物品的风险
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
