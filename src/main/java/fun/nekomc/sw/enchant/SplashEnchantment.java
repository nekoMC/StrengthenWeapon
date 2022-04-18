package fun.nekomc.sw.enchant;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 药剂师附魔实现
 * created: 2022/4/17 22:59
 *
 * @author Chiru
 */
@Slf4j
public class SplashEnchantment extends AbstractSwEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "SPLASH";

    private final Map<UUID, Long> timer = new WeakHashMap<>(16);

    /**
     * Items that don't cause spells to activate when right clicked.
     */
    private static final List<Material> BLACKLIST_CLICKED_BLOCKS = new ArrayList<>(Arrays.asList(
            Material.CRAFTING_TABLE,
            Material.GRINDSTONE,
            Material.ENCHANTING_TABLE,
            Material.FURNACE,
            Material.SMITHING_TABLE,
            Material.LEVER,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.RESPAWN_ANCHOR,
            Material.NOTE_BLOCK,
            Material.ITEM_FRAME,
            Material.CHEST,
            Material.BARREL,
            Material.BEACON,
            Material.LECTERN,
            Material.FLETCHING_TABLE,
            Material.SMITHING_TABLE,
            Material.STONECUTTER,
            Material.SMOKER,
            Material.BLAST_FURNACE,
            Material.BREWING_STAND,
            Material.DISPENSER,
            Material.DROPPER,
            Material.FIRE
    ));

    static {
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.BUTTONS.getValues());
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.BEDS.getValues());
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.DOORS.getValues());
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.FENCE_GATES.getValues());
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.TRAPDOORS.getValues());
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.ANVIL.getValues());
        BLACKLIST_CLICKED_BLOCKS.addAll(Tag.SHULKER_BOXES.getValues());
    }

    public SplashEnchantment() {
        super(ENCHANT_KEY);
    }

    @Override
    public void onMainHandRightClick(@NotNull Player player, @NotNull ItemStack holdInHand, int level, @NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getState() instanceof Container
                    || event.getClickedBlock().getState() instanceof BlockInventoryHolder
                    || BLACKLIST_CLICKED_BLOCKS.contains(event.getClickedBlock().getType())) {
                return;
            }
        }
        if (!passCoolDown(player, level)) {
            return;
        }
        log.warn("过了 CD");
    }

    private boolean passCoolDown(Player player, final int level) {
        UUID uuid = player.getUniqueId();
        Long lastSplash = timer.get(uuid);
        int coolDown = getEnchantLvlAttribute(level);
        long now = System.currentTimeMillis();
        // 仍需 CD 时
        if (null != lastSplash && now - lastSplash < coolDown) {
            return false;
        }
        timer.put(uuid, now);
        return true;
    }
}
