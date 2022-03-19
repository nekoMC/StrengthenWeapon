package fun.nekomc.sw.utils;

import cn.hutool.core.lang.Assert;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author ourange
 */
public class PlayerBagUtils {

    /**
     * 参考：https://github.com/MiniDay/HamsterAPI/blob/master/src/main/java/cn/hamster3/api/HamsterAPI.java
     * 将物品放入玩家背包，如果玩家背包满，则在玩家位置生成掉落物
     */
    public static void givePlayerItem(Player player, ItemStack itemStack) {
        Assert.notNull(player, "player cannot be null");
        Assert.notNull(itemStack, "itemStack cannot be null");

        World world = player.getWorld();
        // addItem 返回放不下的物品 Map，遍历以生成掉落物
        for (ItemStack dropItem : player.getInventory().addItem(itemStack).values()) {
            world.dropItem(player.getLocation(), dropItem);
        }
    }

    /**
     * 物品放入玩家背包
     *
     * @param itemStack 物品
     * @param player    玩家
     * @param drop      背包满，剩余物品是否丢到地面
     * @return 物品是否成功全部放入玩家背包
     */
    public static boolean itemToBag(ItemStack itemStack, Player player, boolean drop) {
        Inventory inventory = player.getInventory();
        int amount = itemStack.getAmount();
        int maxSize = itemStack.getMaxStackSize();
        while (amount > 0) {
            int firstIndex = getIndexItem(itemStack, inventory.getContents());
            if (firstIndex != -1) {
                ItemStack stack = inventory.getItem(firstIndex);
                if (stack != null) {
                    int stackAmount = stack.getAmount();
                    stack.setAmount(Math.min(amount + stackAmount, maxSize));
                    amount -= maxSize - stackAmount;
                    inventory.setItem(firstIndex, stack);
                }
            } else {
                break;
            }
        }
        if (amount > 0) {
            itemStack.setAmount(amount);
            int firstEmpty = inventory.firstEmpty();
            if (firstEmpty == -1) {
                if (drop) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    return false;
                }
            } else {
                inventory.setItem(firstEmpty, itemStack);
            }
        }
        return true;
    }

    /**
     * 检查物品在玩家背包中是否还存在空位
     *
     * @param itemStack 物品
     * @param player    玩家
     * @return 该物品在玩家背包中是否还存在空位
     */
    public static boolean isItemFull(ItemStack itemStack, Player player) {
        Inventory inventory = player.getInventory();
        int firstIndex = getIndexItem(itemStack, inventory.getContents());
        if (firstIndex == -1) {
            int firstEmpty = inventory.firstEmpty();
            return firstEmpty == -1;
        }
        return false;
    }

    /**
     * 返回背包中该物品所在的第一个位置
     *
     * @param itemStack      物品
     * @param inventoryItems 所有物品
     * @return 第一个位置
     */
    private static int getIndexItem(ItemStack itemStack, ItemStack[] inventoryItems) {
        for (int i = 0; i < inventoryItems.length; i++) {
            if (itemStack.isSimilar(inventoryItems[i])) {
                if (inventoryItems[i].getAmount() < itemStack.getMaxStackSize()) {
                    return i;
                }
            }
        }
        return -1;
    }
}

