package fun.nekomc.sw.skill;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 霹雳实现
 * created: 2024/5/19 15:44
 *
 * @author Chiru
 */
public class ThunderSkill extends AbstractSwSkill {
    
    public ThunderSkill() {
        super("THUNDER");
    }

    /**
     * 箭射中生效
     */
    @Override
    public void onArrowHit(@NotNull final LivingEntity shooter,
                           final int level,
                           @NotNull final ProjectileHitEvent event) {
        // 是否可以激活雷电
        if (passChance(level)) {
            // 在箭射中的地方生成雷电
            event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
        }
    }

    /**
     * 三叉戟击中生效
     */
    @Override
    public void onTridentHit(@NotNull final LivingEntity shooter,
                             final int level,
                             @NotNull final ProjectileHitEvent event) {
        // 是否可以激活雷电
        if (passChance(level)) {
            // 在三叉戟击中的地方生成雷电
            event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
        }
    }

    /**
     * 攻击生效
     */
    @Override
    public void onMeleeAttack(@NotNull final LivingEntity attacker,
                              @NotNull final LivingEntity victim,
                              final int level,
                              @NotNull final EntityDamageByEntityEvent event) {
        // 是否可以激活雷电
        if (passChance(level)) {
            // 在受害者位置生成雷电
            victim.getWorld().strikeLightning(victim.getLocation());
        }
    }
}
