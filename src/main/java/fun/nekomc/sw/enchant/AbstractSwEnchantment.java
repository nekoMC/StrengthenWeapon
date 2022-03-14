package fun.nekomc.sw.enchant;

import cn.hutool.core.collection.CollUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.EnchantmentConfigDto;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.utils.ConfigManager;
import lombok.*;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 自定义附魔，参考：https://github.com/Auxilor/EcoEnchants
 * created: 2022/3/13 20:32
 *
 * @author Chiru
 */
@Builder
public abstract class AbstractSwEnchantment extends Enchantment implements Listener {

    @Getter
    private final String configKey;

    protected AbstractSwEnchantment(@NotNull String key) {
        super(new NamespacedKey(StrengthenWeapon.getInstance(), key));
        this.configKey = key;
    }

    @Override
    public int getMaxLevel() {
        return getConfig().getMaxLevel();
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
        List<String> targetMaterials = getConfig().getTargetMaterials();
        if (CollUtil.isEmpty(targetMaterials)) {
            return false;
        }
        return targetMaterials.contains(other.getKey().getKey());
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return getConfig().isTreasure();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && ((AbstractSwEnchantment) obj).configKey.equals(configKey);
    }

    @Override
    public int hashCode() {
        return configKey.hashCode();
    }

    /**
     * 获取 key 指定的附魔配置数据
     * 不在本类中对 ConfigDto 内容进行保存，配置统一由管理器管理，以方便 reload 的实现
     *
     * @return EnchantmentConfigDto
     */
    @NotNull
    public EnchantmentConfigDto getConfig() {
        EnchantmentConfigDto enchantmentConfigDto = ConfigManager.getConfigYml().getEnchantments().get(configKey);
        if (null == enchantmentConfigDto) {
            throw new ConfigurationException(ConfigManager.getConfiguredMsg("config_error"));
        }
        return enchantmentConfigDto;
    }
}
