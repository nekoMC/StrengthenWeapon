package fun.nekomc.sw.skill;

import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.utils.EntityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 吸血实现
 * created: 2022/4/4 13:23
 *
 * @author Chiru
 */
public class SuckBloodSkill extends AbstractSwSkill {

    public SuckBloodSkill() {
        super("SUCK_BLOOD");
    }

    @Override
    public void onMeleeAttack(@NotNull LivingEntity attacker, @NotNull LivingEntity victim, int level, @NotNull EntityDamageByEntityEvent event) {
        // 计算需要加的血量
        int bloodToAdd = getSkillLvlAttribute(level) / 100;
        EntityUtils.healTargetBy(attacker, bloodToAdd);
    }
}
