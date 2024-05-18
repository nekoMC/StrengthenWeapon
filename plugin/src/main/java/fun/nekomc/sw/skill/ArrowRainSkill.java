package fun.nekomc.sw.skill;

import cn.hutool.core.util.RandomUtil;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 箭雨实现
 * created: 2022/3/15 23:27
 *
 * @author Chiru
 */
public class ArrowRainSkill extends AbstractSwSkill {

    public ArrowRainSkill() {
        super("ARROW_RAIN");
    }

    /**
     * 实体（玩家）用含有本技能的弓射出箭时
     */
    @Override
    public void onProjectileLaunch(@NotNull LivingEntity shooter, @NotNull Projectile projectile, int level, @NotNull ProjectileLaunchEvent event) {
        if (!(projectile instanceof Arrow arrow)) {
            return;
        }
        double range = 2.0;
        // 计算
        int addArrowCount = getSkillLvlAttribute(level);
        for (int i = 0; i < addArrowCount; i++) {
            // 计算额外箭的生成位置
            Location location = shooter.getEyeLocation().clone();
            double ranX = (RandomUtil.randomDouble(0, 1.0) - 0.5) * range;
            double ranY = (RandomUtil.randomDouble(0, 1.0) - 0.5) * range;
            double ranZ = (RandomUtil.randomDouble(0, 1.0) - 0.5) * range;
            location.add(ranX, ranY, ranZ);
            // 额外的箭，每高一级，多射一支
            Arrow extraArrow = shooter.getWorld().spawn(location, Arrow.class);
            // 速度
            extraArrow.setVelocity(arrow.getVelocity());
            extraArrow.setShooter(shooter);
            // 不可拾取
            extraArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }
}
