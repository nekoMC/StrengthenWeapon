package fun.nekomc.sw.enchant.magia;

import fun.nekomc.sw.enchant.AbstractSwEnchantment;
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
public abstract class PotionEnchantment extends AbstractSwEnchantment {

    private final PotionEffectType type;

    PotionEnchantment(String enchantKey, PotionEffectType type) {
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

    // ========== 药水效果：减益 ========== //

    /**
     * 药水效果：缓慢
     */
    public static class Slow extends PotionEnchantment {
        public static final String ENCHANT_KEY = "SLOW_POTION";
        public Slow() {
            super(ENCHANT_KEY, PotionEffectType.SLOW);
        }
    }

    /**
     * 药水效果：瞬间伤害
     */
    public static class Harm extends PotionEnchantment {
        public static final String ENCHANT_KEY = "HARM_POTION";
        public Harm() {
            super(ENCHANT_KEY, PotionEffectType.HARM);
        }
    }

    /**
     * 药水效果：失明
     */
    public static class Blind extends PotionEnchantment {
        public static final String ENCHANT_KEY = "BLIND_POTION";
        public Blind() {
            super(ENCHANT_KEY, PotionEffectType.BLINDNESS);
        }
    }

    /**
     * 药水效果：恶心
     */
    public static class Confusion extends PotionEnchantment {
        public static final String ENCHANT_KEY = "CONFUSION_POTION";
        public Confusion() {
            super(ENCHANT_KEY, PotionEffectType.CONFUSION);
        }
    }

    /**
     * 药水效果：发光
     */
    public static class Glowing extends PotionEnchantment {
        public static final String ENCHANT_KEY = "GLOWING_POTION";
        public Glowing() {
            super(ENCHANT_KEY, PotionEffectType.GLOWING);
        }
    }

    /**
     * 药水效果：饥饿
     */
    public static class Hunger extends PotionEnchantment {
        public static final String ENCHANT_KEY = "HUNGER_POTION";
        public Hunger() {
            super(ENCHANT_KEY, PotionEffectType.HUNGER);
        }
    }

    /**
     * 药水效果：漂浮
     */
    public static class Float extends PotionEnchantment {
        public static final String ENCHANT_KEY = "FLOAT_POTION";
        public Float() {
            super(ENCHANT_KEY, PotionEffectType.LEVITATION);
        }
    }

    /**
     * 药水效果：夜视
     */
    public static class Poison extends PotionEnchantment {
        public static final String ENCHANT_KEY = "POISON_POTION";
        public Poison() {
            super(ENCHANT_KEY, PotionEffectType.POISON);
        }
    }

    /**
     * 药水效果：虚弱
     */
    public static class WeaknessBreathing extends PotionEnchantment {
        public static final String ENCHANT_KEY = "WEAKNESS_POTION";
        public WeaknessBreathing() {
            super(ENCHANT_KEY, PotionEffectType.WEAKNESS);
        }
    }

    /**
     * 药水效果：凋零
     */
    public static class Wither extends PotionEnchantment {
        public static final String ENCHANT_KEY = "WITHER_POTION";
        public Wither() {
            super(ENCHANT_KEY, PotionEffectType.WITHER);
        }
    }

    // ========== 药水效果：增益 ========== //

    /**
     * 药水效果：伤害吸收
     */
    public static class Absorption extends PotionEnchantment {
        public static final String ENCHANT_KEY = "ABSORPTION_POTION";
        public Absorption() {
            super(ENCHANT_KEY, PotionEffectType.ABSORPTION);
        }
    }

    /**
     * 药水效果：涌潮能量
     */
    public static class ConduitPower extends PotionEnchantment {
        public static final String ENCHANT_KEY = "CONDUIT_POTION";
        public ConduitPower() {
            super(ENCHANT_KEY, PotionEffectType.CONDUIT_POWER);
        }
    }

    /**
     * 药水效果：抗性
     */
    public static class Resistance extends PotionEnchantment {
        public static final String ENCHANT_KEY = "RESISTANCE_POTION";
        public Resistance() {
            super(ENCHANT_KEY, PotionEffectType.DAMAGE_RESISTANCE);
        }
    }

    /**
     * 药水效果：火焰抗性
     */
    public static class FireResistance extends PotionEnchantment {
        public static final String ENCHANT_KEY = "FIRE_RESISTANCE_POTION";
        public FireResistance() {
            super(ENCHANT_KEY, PotionEffectType.FIRE_RESISTANCE);
        }
    }

    /**
     * 药水效果：生命恢复
     */
    public static class Heal extends PotionEnchantment {
        public static final String ENCHANT_KEY = "HEAL_POTION";
        public Heal() {
            super(ENCHANT_KEY, PotionEffectType.HEAL);
        }
    }

    /**
     * 药水效果：伤害增加
     */
    public static class IncreaseDamage extends PotionEnchantment {
        public static final String ENCHANT_KEY = "INCREASE_DMG_POTION";
        public IncreaseDamage() {
            super(ENCHANT_KEY, PotionEffectType.INCREASE_DAMAGE);
        }
    }

    /**
     * 药水效果：隐身
     */
    public static class Invisibility extends PotionEnchantment {
        public static final String ENCHANT_KEY = "INVISIBILITY_POTION";
        public Invisibility() {
            super(ENCHANT_KEY, PotionEffectType.INVISIBILITY);
        }
    }

    /**
     * 药水效果：跳跃提升
     */
    public static class Jump extends PotionEnchantment {
        public static final String ENCHANT_KEY = "JUMP_POTION";
        public Jump() {
            super(ENCHANT_KEY, PotionEffectType.JUMP);
        }
    }

    /**
     * 药水效果：夜视
     */
    public static class NightVision extends PotionEnchantment {
        public static final String ENCHANT_KEY = "NIGHT_VISION_POTION";
        public NightVision() {
            super(ENCHANT_KEY, PotionEffectType.NIGHT_VISION);
        }
    }

    /**
     * 药水效果：生命恢复
     */
    public static class Regeneration extends PotionEnchantment {
        public static final String ENCHANT_KEY = "REGENERATION_POTION";
        public Regeneration() {
            super(ENCHANT_KEY, PotionEffectType.REGENERATION);
        }
    }

    /**
     * 药水效果：饱和
     */
    public static class Saturation extends PotionEnchantment {
        public static final String ENCHANT_KEY = "SATURATION_POTION";
        public Saturation() {
            super(ENCHANT_KEY, PotionEffectType.SATURATION);
        }
    }

    /**
     * 药水效果：速度提升
     */
    public static class Speed extends PotionEnchantment {
        public static final String ENCHANT_KEY = "SPEED_POTION";
        public Speed() {
            super(ENCHANT_KEY, PotionEffectType.SPEED);
        }
    }

    /**
     * 药水效果：水下呼吸
     */
    public static class WaterBreathing extends PotionEnchantment {
        public static final String ENCHANT_KEY = "WATER_BREATHING_POTION";
        public WaterBreathing() {
            super(ENCHANT_KEY, PotionEffectType.WATER_BREATHING);
        }
    }

}
