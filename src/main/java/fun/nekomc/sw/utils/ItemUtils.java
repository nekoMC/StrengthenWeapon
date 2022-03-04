package fun.nekomc.sw.utils;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.dto.SwItemConfigDto;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 操作道具 Lore 的工具类
 *
 * @author ourange
 */
@UtilityClass
public class ItemUtils {

    /**
     * 获取 SW 道具的强化等级（解析 lore 内容）
     *
     * @param lore         解释文案
     * @param strengthItem 要操作的 SW 道具对象
     * @return 该道具的强化等级
     */
    public static int getItemLevel(List<String> lore, StrengthenItem strengthItem) {
        return Integer.parseInt(lore.get(0).split(strengthItem.getLevelName())[1]);
    }

    /**
     * 重设 SW 道具的强化等级（修改 lore 第一行文案）
     *
     * @param lore         原 lore
     * @param strengthItem 要操作的 SW 道具对象
     * @param level        新的强化等级
     */
    public static void setItemLevel(List<String> lore, StrengthenItem strengthItem, int level) {
        lore.set(0, strengthItem.getLevelName() + level);
    }

    /**
     * 获取 SW 道具名（即 lore 最后一项）
     *
     * @param lore 解释文案列表
     * @return SW 道具名
     */
    public static String getItemName(List<String> lore) {
        return lore.get(lore.size() - 1);
    }

    /**
     * 通过 ItemConfigDto 构建道具
     *
     * @param itemConfig SwItemConfigDto 对象，通常通过配置文件中读取获得
     * @return 道具，配置存在问题时返回 Optional.empty
     */
    public Optional<ItemStack> buildItemByConfig(SwItemConfigDto itemConfig) {
        Assert.notNull(itemConfig, "itemConfig cannot be null");
        // Material
        Material material = Material.getMaterial(itemConfig.getMaterial());
        if (null == material) {
            return Optional.empty();
        }
        ItemStack itemStack = new ItemStack(material);
        // Meta
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        meta.setDisplayName(itemConfig.getDisplayName());
        meta.setLore(itemConfig.getLore());
        meta.set

        itemStack.setItemMeta(meta);
        return Optional.of(itemStack);
    }
}
