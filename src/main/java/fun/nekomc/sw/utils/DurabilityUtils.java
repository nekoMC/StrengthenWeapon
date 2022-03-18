package fun.nekomc.sw.utils;

import fun.nekomc.sw.exception.SwException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for item durability.
 * created: 2022/3/18 22:29
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/eco">参考 Eco</a>
 */
public final class DurabilityUtils {

    /**
     * Damage an item in a player's inventory.
     *
     * @param player The player.
     * @param item   The item to damage.
     * @param damage The amount of damage to deal.
     */
    public static void damageItem(@NotNull final Player player,
                                  @NotNull final ItemStack item,
                                  final int damage) {
        if (unbreakable(item)) {
            return;
        }

        PlayerItemDamageEvent event3 = new PlayerItemDamageEvent(player, item, damage);
        Bukkit.getPluginManager().callEvent(event3);

        if (!event3.isCancelled()) {
            int damage2 = event3.getDamage();
            Damageable meta = (Damageable) item.getItemMeta();
            if (null == meta) {
                // un reach
                throw new SwException("meta data lost!");
            }
            meta.setDamage(meta.getDamage() + damage2);

            if (meta.getDamage() >= item.getType().getMaxDurability()) {
                meta.setDamage(item.getType().getMaxDurability());
                item.setItemMeta(meta);
                PlayerItemBreakEvent event = new PlayerItemBreakEvent(player, item);
                Bukkit.getPluginManager().callEvent(event);
                item.setType(Material.AIR);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1, 1);
            } else {
                item.setItemMeta(meta);
            }
        }
    }

    /**
     * Damage an item.
     *
     * @param item   The item to damage.
     * @param damage The amount of damage to deal.
     */
    public static void damageItem(@NotNull final ItemStack item,
                                  final int damage) {
        if (unbreakable(item)) {
            return;
        }

        Damageable meta = (Damageable) item.getItemMeta();
        if (null == meta) {
            // un reach
            throw new SwException("meta data lost!");
        }
        meta.setDamage(meta.getDamage() + damage);

        if (meta.getDamage() >= item.getType().getMaxDurability()) {
            meta.setDamage(item.getType().getMaxDurability());
            item.setItemMeta(meta);
            item.setType(Material.AIR);
        } else {
            item.setItemMeta(meta);
        }
    }

    /**
     * Damage an item in a player's inventory without breaking it.
     *
     * @param item   The item to damage.
     * @param damage The amount of damage to deal.
     * @param player The player.
     */
    public static void damageItemNoBreak(@NotNull final ItemStack item,
                                         final int damage,
                                         @NotNull final Player player) {
        if (item.getItemMeta() == null) {
            return;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return;
        }

        if (!(item.getItemMeta() instanceof Damageable)) {
            return;
        }

        PlayerItemDamageEvent event3 = new PlayerItemDamageEvent(player, item, damage);
        Bukkit.getPluginManager().callEvent(event3);

        if (!event3.isCancelled()) {
            int damage2 = event3.getDamage();
            Damageable meta = (Damageable) item.getItemMeta();
            meta.setDamage(meta.getDamage() + damage2);

            if (meta.getDamage() >= item.getType().getMaxDurability()) {
                meta.setDamage(item.getType().getMaxDurability() - 1);
            }
            item.setItemMeta(meta);
        }
    }

    /**
     * Repair an item in a player's inventory.
     *
     * @param item   The item to damage.
     * @param repair The amount of damage to heal.
     */
    public static void repairItem(@NotNull final ItemStack item,
                                  final int repair) {
        if (item.getItemMeta() == null) {
            return;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return;
        }

        if (item.getItemMeta() instanceof Damageable meta) {
            meta.setDamage(meta.getDamage() - repair);

            if (meta.getDamage() < 0) {
                meta.setDamage(0);
            }
            item.setItemMeta(meta);
        }
    }

    private DurabilityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static boolean unbreakable(ItemStack item) {
        if (item.getItemMeta() == null) {
            return true;
        }

        if (item.getItemMeta().isUnbreakable()) {
            return true;
        }

        if (!(item.getItemMeta() instanceof Damageable)) {
            return true;
        }

        // Special edge case
        return item.getType() == Material.CARVED_PUMPKIN || item.getType() == Material.PLAYER_HEAD;
    }
}
