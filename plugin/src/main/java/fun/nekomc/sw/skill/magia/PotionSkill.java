package fun.nekomc.sw.skill.magia;

import fun.nekomc.sw.skill.AbstractSwSkill;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * 药水效果类
 * created: 2022/4/19 23:09
 *
 * @author Chiru
 */
public abstract class PotionSkill extends AbstractSwSkill {

    private final PotionEffectType type;

    protected PotionSkill(@NotNull String key, @NotNull PotionEffectType type) {
        super(key);
        this.type = type;
    }

    /**
     * 为指定的药水添加效果，可以在子类重写以拓展药水的相关规则
     *
     * @param player         扔出药水的玩家
     * @param potionMeta     投掷出药水的 PotionMeta 对象
     * @param triggerSkill   通过什么技能触发的药水投掷
     * @param triggerLevel   triggerSkill 的附魔等级
     * @param level          level 当前附魔的等级
     */
    @SuppressWarnings("unused")
    void decoratePotionMeta(@NotNull Player player,
                                     @NotNull PotionMeta potionMeta,
                                     @NotNull AbstractSwSkill triggerSkill,
                                     final int triggerLevel,
                                     final int level) {
        int duration = getSkillLvlAttribute(level);
        potionMeta.addCustomEffect(new PotionEffect(type, duration, level), true);
    }

    // ========== 药水效果：减益 ========== //

    /**
     * 药水效果：缓慢
     */
    public static class Slow extends PotionSkill {
        public Slow() {
            super("SLOW_POTION", PotionEffectType.SLOW);
        }
    }

    /**
     * 药水效果：瞬间伤害
     */
    public static class Harm extends PotionSkill {
        public Harm() {
            super("HARM_POTION", PotionEffectType.HARM);
        }
    }

    /**
     * 药水效果：失明
     */
    public static class Blind extends PotionSkill {
        public Blind() {
            super("BLIND_POTION", PotionEffectType.BLINDNESS);
        }
    }

    /**
     * 药水效果：恶心
     */
    public static class Confusion extends PotionSkill {
        public Confusion() {
            super("CONFUSION_POTION", PotionEffectType.CONFUSION);
        }
    }

    /**
     * 药水效果：发光
     */
    public static class Glowing extends PotionSkill {
        public Glowing() {
            super("GLOWING_POTION", PotionEffectType.GLOWING);
        }
    }

    /**
     * 药水效果：饥饿
     */
    public static class Hunger extends PotionSkill {
        public Hunger() {
            super("HUNGER_POTION", PotionEffectType.HUNGER);
        }
    }

    /**
     * 药水效果：漂浮
     */
    public static class Float extends PotionSkill {
        public Float() {
            super("FLOAT_POTION", PotionEffectType.LEVITATION);
        }
    }

    /**
     * 药水效果：中毒
     */
    public static class Poison extends PotionSkill {
        public Poison() {
            super("POISON_POTION", PotionEffectType.POISON);
        }
    }

    /**
     * 药水效果：虚弱
     */
    public static class WeaknessBreathing extends PotionSkill {
        public WeaknessBreathing() {
            super("WEAKNESS_POTION", PotionEffectType.WEAKNESS);
        }
    }

    /**
     * 药水效果：凋零
     */
    public static class Wither extends PotionSkill {
        public Wither() {
            super("WITHER_POTION", PotionEffectType.WITHER);
        }
    }

    // ========== 药水效果：增益 ========== //

    /**
     * 药水效果：伤害吸收
     */
    public static class Absorption extends PotionSkill {
        public Absorption() {
            super("ABSORPTION_POTION", PotionEffectType.ABSORPTION);
        }
    }

    /**
     * 药水效果：涌潮能量
     */
    public static class ConduitPower extends PotionSkill {
        public ConduitPower() {
            super("CONDUIT_POTION", PotionEffectType.CONDUIT_POWER);
        }
    }

    /**
     * 药水效果：抗性
     */
    public static class Resistance extends PotionSkill {
        public Resistance() {
            super("RESISTANCE_POTION", PotionEffectType.DAMAGE_RESISTANCE);
        }
    }

    /**
     * 药水效果：火焰抗性
     */
    public static class FireResistance extends PotionSkill {
        public FireResistance() {
            super("FIRE_RESISTANCE_POTION", PotionEffectType.FIRE_RESISTANCE);
        }
    }

    /**
     * 药水效果：生命恢复
     */
    public static class Heal extends PotionSkill {
        public Heal() {
            super("HEAL_POTION", PotionEffectType.HEAL);
        }
    }

    /**
     * 药水效果：伤害增加
     */
    public static class IncreaseDamage extends PotionSkill {
        public IncreaseDamage() {
            super("INCREASE_DMG_POTION", PotionEffectType.INCREASE_DAMAGE);
        }
    }

    /**
     * 药水效果：隐身
     */
    public static class Invisibility extends PotionSkill {
        public Invisibility() {
            super("INVISIBILITY_POTION", PotionEffectType.INVISIBILITY);
        }
    }

    /**
     * 药水效果：跳跃提升
     */
    public static class Jump extends PotionSkill {
        public Jump() {
            super("JUMP_POTION", PotionEffectType.JUMP);
        }
    }

    /**
     * 药水效果：夜视
     */
    public static class NightVision extends PotionSkill {
        public NightVision() {
            super("NIGHT_VISION_POTION", PotionEffectType.NIGHT_VISION);
        }
    }

    /**
     * 药水效果：生命恢复
     */
    public static class Regeneration extends PotionSkill {
        public Regeneration() {
            super("REGENERATION_POTION", PotionEffectType.REGENERATION);
        }
    }

    /**
     * 药水效果：饱和
     */
    public static class Saturation extends PotionSkill {
        public Saturation() {
            super("SATURATION_POTION", PotionEffectType.SATURATION);
        }
    }

    /**
     * 药水效果：速度提升
     */
    public static class Speed extends PotionSkill {
        public Speed() {
            super("SPEED_POTION", PotionEffectType.SPEED);
        }
    }

    /**
     * 药水效果：水下呼吸
     */
    public static class WaterBreathing extends PotionSkill {
        public WaterBreathing() {
            super("WATER_BREATHING_POTION", PotionEffectType.WATER_BREATHING);
        }
    }

}
