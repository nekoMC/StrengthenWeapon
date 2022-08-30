package fun.nekomc.sw.utils;

import cn.hutool.core.lang.Assert;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author ourange
 */
@UtilityClass
public class PlayerBagUtils {

    /**
     * <a href="https://github.com/MiniDay/HamsterAPI/blob/master/src/main/java/cn/hamster3/api/HamsterAPI.java">参考</a>
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
}

