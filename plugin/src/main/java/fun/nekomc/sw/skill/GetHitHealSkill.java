package fun.nekomc.sw.skill;

import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.utils.EntityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 受击恢复实现
 * created: 2022/4/21 23:51
 *
 * @author Chiru
 */
public class GetHitHealSkill extends AbstractSwSkill {

    public GetHitHealSkill() {
        super("GET_HIT_HEAL");
    }

    @Override
    public void onDamageWearingArmor(@NotNull LivingEntity victim, int level, @NotNull EntityDamageEvent event) {
        // 计算需要加的血量
        int bloodToAdd = getSkillLvlAttribute(level) / 100;
        EntityUtils.healTargetBy(victim, bloodToAdd);
    }
}
