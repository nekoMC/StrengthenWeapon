package fun.nekomc.sw.enchant;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * 药水效果类附魔
 * created: 2022/4/19 23:09
 *
 * @author Chiru
 */
public abstract class AbstractPotionEnchantment extends AbstractSwEnchantment {

    private final PotionEffectType type;

    public AbstractPotionEnchantment(String enchantKey, PotionEffectType type) {
        super(enchantKey);
        this.type = type;
    }

    /**
     * 为指定的药水添加效果，可以在子类重写以拓展药水的相关规则
     *
     * @param player         扔出药水的玩家
     * @param potionMeta     投掷出药水的 PotionMeta 对象
     * @param triggerEnchant 通过什么附魔触发的药水投掷
     * @param triggerLevel   triggerEnchant 的附魔等级
     * @param level          level 当前附魔的等级
     */
    @SuppressWarnings("unused")
    void decoratePotionMeta(@NotNull Player player,
                                     @NotNull PotionMeta potionMeta,
                                     @NotNull AbstractSwEnchantment triggerEnchant,
                                     final int triggerLevel,
                                     final int level) {
        int duration = getEnchantLvlAttribute(level);
        potionMeta.addCustomEffect(new PotionEffect(type, duration, level), true);
    }
}
