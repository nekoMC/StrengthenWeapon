package fun.nekomc.sw.utils;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.dto.SwItemAttachData;
import fun.nekomc.sw.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.ConfigurationException;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

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
    public static Optional<ItemStack> buildItemByConfig(SwItemConfigDto itemConfig) {
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
        meta.setUnbreakable(itemConfig.isUnbreakable());
        // Meta - 附魔、属性修改
        meta.setAttributeModifiers(itemConfig.getAttributeModifiers());
        itemConfig.getEnchantMap().forEach((enchant, lvl) -> meta.addEnchant(enchant, lvl, true));
        // Meta - 附加信息
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        persistentDataContainer.set(getWarpedKey(itemConfig.getName()),
                SwItemAttachData.DEFAULT_ATTACH_DATA, SwItemAttachData.DEFAULT_ATTACH_DATA);

        itemStack.setItemMeta(meta);
        return Optional.of(itemStack);
    }

    /**
     * 将 SwItemAttachData 更新到 ItemStack 的 PersistentDataContainer 中
     *
     * @param itemMeta   道具 ItemMeta，必须包含合法的 SwItemAttachData 数据
     * @param attachData 更新后的 SwItemAttachData
     */
    public static void updateAttachData(ItemMeta itemMeta, SwItemAttachData attachData) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(getWarpedKey(getNameFromDataContainer(persistentDataContainer)),
                SwItemAttachData.DEFAULT_ATTACH_DATA, attachData);
    }

    /**
     * 获取道具的附加信息
     *
     * @param itemStack 道具 ItemStack
     * @return SwItemAttachData，不存在时返回 Optional.empty()
     */
    public static Optional<SwItemAttachData> getAttachData(ItemStack itemStack) {
        if (null == itemStack) {
            return Optional.empty();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (null == itemMeta) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey warpedItemName = getWarpedKey(getNameFromDataContainer(persistentDataContainer));
        SwItemAttachData attachData = persistentDataContainer.get(warpedItemName, SwItemAttachData.DEFAULT_ATTACH_DATA);
        return Optional.ofNullable(attachData);
    }

    /**
     * 从 ItemStack 中获取道具名称，预期名称只有一个，存在多个时抛异常
     *
     * @param itemStack 道具
     * @return 道具名称，如 sw_bow
     * @throws ConfigurationException 当存在多个道具名时抛出
     */
    public static String getNameFromMeta(ItemStack itemStack) {
        Optional<PersistentDataContainer> dataContainerFromItem = getDataContainerFromItem(itemStack);
        return dataContainerFromItem.map(ItemUtils::getNameFromDataContainer).orElse(null);
    }

    // ========== private ========== //

    /**
     * 从 Container 中获取道具名称，预期名称只有一个，存在多个时抛异常
     */
    private static String getNameFromDataContainer(PersistentDataContainer container) {
        Set<NamespacedKey> keys = container.getKeys();
        String res = null;
        // 遍历 container 中的全部 key，预期只有一个 key
        for (NamespacedKey key : keys) {
            if (null == res) {
                res = key.getKey();
            } else {
                throw new ConfigurationException(ConfigFactory.getConfiguredMsg("config_error"));
            }
        }
        return res;
    }

    /**
     * 从 ItemStack 中获取 Container
     */
    private static Optional<PersistentDataContainer> getDataContainerFromItem(ItemStack itemStack) {
        if (null == itemStack) {
            return Optional.empty();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (null == itemMeta) {
            return Optional.empty();
        }
        return Optional.of(itemMeta.getPersistentDataContainer());
    }

    /**
     * 将指定的名称包装为 container 识别的命名空间 key
     */
    private static NamespacedKey getWarpedKey(String key) {
        return new NamespacedKey(StrengthenWeapon.getInstance(), key);
    }
}
