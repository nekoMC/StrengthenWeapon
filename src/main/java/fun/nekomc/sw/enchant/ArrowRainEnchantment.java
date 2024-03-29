package fun.nekomc.sw.enchant;

import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.domain.dto.EnchantmentConfigDto;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 箭雨附魔实现
 * created: 2022/3/15 23:27
 *
 * @author Chiru
 */
public class ArrowRainEnchantment extends AbstractSwEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "ARROW_RAIN";

    public ArrowRainEnchantment() {
        super(ENCHANT_KEY);
    }

    /**
     * 实体（玩家）用含有本附魔的弓射出箭时
     */
    @Override
    public void onProjectileLaunch(@NotNull LivingEntity shooter, @NotNull Projectile projectile, int level, @NotNull ProjectileLaunchEvent event) {
        if (!(projectile instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) projectile;
        double range = 2.0;
        // 计算
        int addArrowCount = getEnchantLvlAttribute(level);
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
