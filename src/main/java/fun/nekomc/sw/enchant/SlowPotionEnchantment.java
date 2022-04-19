package fun.nekomc.sw.enchant;

import org.bukkit.potion.PotionEffectType;

/**
 * 药水效果：延缓
 * created: 2022/4/19 23:26
 *
 * @author Chiru
 */
public class SlowPotionEnchantment extends AbstractPotionEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "SLOW_POTION";

    public SlowPotionEnchantment() {
        super(ENCHANT_KEY, PotionEffectType.SLOW);
    }
}
