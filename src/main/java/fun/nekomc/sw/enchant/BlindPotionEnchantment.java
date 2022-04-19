package fun.nekomc.sw.enchant;

import org.bukkit.potion.PotionEffectType;

/**
 * 药水效果：失明
 * created: 2022/4/19 23:51
 *
 * @author Chiru
 */
public class BlindPotionEnchantment extends AbstractPotionEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "BLIND_POTION";

    public BlindPotionEnchantment() {
        super(ENCHANT_KEY, PotionEffectType.BLINDNESS);
    }
}
