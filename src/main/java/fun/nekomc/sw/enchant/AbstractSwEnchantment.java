package fun.nekomc.sw.enchant;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义附魔，参考：https://github.com/Auxilor/EcoEnchants
 * created: 2022/3/13 20:32
 *
 * @author Chiru
 */
public abstract class AbstractSwEnchantment extends Enchantment implements Listener {

    /**
     * The materials of the targets.
     */
    @Getter
    private final Set<Material> targetMaterials = new HashSet<>();

    /**
     * The display name of the enchantment.
     */
    @Getter
    private String displayName;

    /**
     * The maximum level for the enchantment to be obtained naturally.
     */
    private int maxLevel;

    /**
     * The enchantments that conflict with this enchantment.
     */
    @Getter
    private Set<Enchantment> conflicts;

    /**
     * The description of the enchantment.
     */
    @Getter
    private String description;

    /**
     * If the enchantment is enabled.
     */
    private boolean enabled;

    public AbstractSwEnchantment(@NotNull NamespacedKey key) {
        super(key);
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEARABLE;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return false;
    }
}
