package fun.nekomc.sw.enchant;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.EnchantmentConfigDto;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义附魔，参考：https://github.com/Auxilor/EcoEnchants
 * created: 2022/3/13 20:32
 *
 * @author Chiru
 */
@Builder
public abstract class AbstractSwEnchantment extends Enchantment implements Listener {

    /**
     * The materials of the targets.
     */
    @Getter
    private final Set<Material> targetMaterials;

    /**
     * The display name of the enchantment.
     */
    @Getter
    private final String displayName;

    /**
     * The maximum level for the enchantment to be obtained naturally.
     */
    private final int maxLevel;

    /**
     * The enchantments key that conflict with this enchantment.
     */
    @Getter
    private final List<String> conflicts;

    /**
     * The description of the enchantment.
     */
    @Getter
    private final String description;

    /**
     * If the enchantment is enabled.
     */
    @Getter
    private final boolean enabled;

    protected AbstractSwEnchantment(@NotNull EnchantmentConfigDto config) {
        super(new NamespacedKey(StrengthenWeapon.getInstance(), config.getKey()));
        displayName = config.getDisplayName();
        targetMaterials = config.getTargetMaterials().stream().map(Material::getMaterial).collect(Collectors.toSet());
        maxLevel = config.getMaxLevel();
        conflicts = config.getConflicts();
        description = config.getDescription();
        enabled = config.isEnabled();
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
        return conflicts.contains(other.getKey().getKey());
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && ((AbstractSwEnchantment) obj).displayName.equals(displayName);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
