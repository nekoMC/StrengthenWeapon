package fun.nekomc.sw.dao;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 强化操作
 *
 * @author ourange
 */
public interface StrengthenDAO {

    /**
     * 整一把弓
     *
     * @param count 堆叠数量
     * @return 弓的 ItemStack
     */
    ItemStack giveStrengthenBow(int count);

    /**
     * 整个强化石
     *
     * @param count 堆叠数量
     * @param level 强化等级，N级强化石
     * @return 强化石的 ItemStack
     */
    ItemStack giveStrengthenStone(int count, int level);

    /**
     * 强化操作
     *
     * @param player         玩家（用于给他通知）
     * @param itemStack      道具信息
     * @param isSuccess      是否强化成功
     * @param isSafe         是否有强化保护
     * @return 强化后（可能失败）的道具，损坏时变成 null
     */
    ItemStack strengthen(Player player, ItemStack itemStack, boolean isSuccess, boolean isSafe);

    /**
     * 铁定成功的强化操作
     *
     * @param swWeapon 待强化道具
     * @param swStone  强化石等消耗品
     * @return 强化后的道具
     */
    ItemStack strengthenSuccessResult(ItemStack swWeapon, ItemStack swStone);
}
