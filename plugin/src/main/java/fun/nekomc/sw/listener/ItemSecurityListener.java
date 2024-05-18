package fun.nekomc.sw.listener;

import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义道具的附魔、祛魔事件保护（禁止玩家自定义进行附魔与祛魔）
 * created: 2022/3/12 22:15
 *
 * @author Chiru
 */
public class ItemSecurityListener implements Listener {

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        // 允许玩家自定义附魔，则忽略事件
        Boolean enablePlayerEnchant = ConfigManager.getConfigYml().getEnablePlayerEnchant();
        if (null != enablePlayerEnchant && enablePlayerEnchant) {
            return;
        }
        // 禁止玩家对插件物品进行自定义附魔时，取消事件（附魔）
        if (ItemUtils.isSwItem(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
        // 允许玩家自定义附魔，则忽略事件
        Boolean enablePlayerEnchant = ConfigManager.getConfigYml().getEnablePlayerEnchant();
        if (null != enablePlayerEnchant && enablePlayerEnchant) {
            return;
        }
        // 禁止玩家对插件物品进行自定义附魔时，取消事件（合金升级）
        if (ItemUtils.isSwItem(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelClickResultSlotEvent(InventoryClickEvent event) {
        // 允许玩家自定义附魔，则忽略事件
        Boolean enablePlayerEnchant = ConfigManager.getConfigYml().getEnablePlayerEnchant();
        if (null != enablePlayerEnchant && enablePlayerEnchant) {
            return;
        }
        // 禁止玩家对插件物品进行自定义附魔时，取消事件（祛魔、改名、铁砧附魔等）
        boolean clickResult = event.getSlotType() == InventoryType.SlotType.RESULT;
        ItemStack currentItem = event.getCurrentItem();
        if (clickResult && ItemUtils.isSwItem(currentItem)) {
            event.setCancelled(true);
        }
        // 修复附魔
        if (ConfigManager.getConfigYml().isUsingLoreGetter()) {
            SkillHelper.fixByLore(currentItem);
        }
    }

    /**
     * 道具属性的修复监听器
     * On player hold item.
     * <p>
     * Listener for lore conversion.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void loreConverter(@NotNull final PlayerItemHeldEvent event) {
        if (!ConfigManager.getConfigYml().isUsingLoreGetter()) {
            return;
        }

        ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());

        SkillHelper.fixByLore(itemStack);
    }
}
