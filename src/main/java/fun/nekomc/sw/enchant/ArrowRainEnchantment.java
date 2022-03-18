package fun.nekomc.sw.enchant;

import fun.nekomc.sw.utils.NumberUtils;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    protected ArrowRainEnchantment() {
        super(ENCHANT_KEY);
    }

    /**
     * 实体（玩家）用含有本附魔的弓射出箭时
     */
    @Override
    public void onArrowDamage(@NotNull LivingEntity attacker, @NotNull LivingEntity victim, @NotNull Arrow arrow, int level, @NotNull EntityDamageByEntityEvent event) {
        double range = 2.0;
        // 计算
        int addArrowCount = level * getConfig().getAddition();
        for (int i = 0; i < addArrowCount; i++) {
            // 计算额外箭的生成位置
            Location location = attacker.getEyeLocation().clone();
            double ranX = (NumberUtils.randFloat(0, 1.0) - 0.5) * range;
            double ranY = (NumberUtils.randFloat(0, 1.0) - 0.5) * range;
            double ranZ = (NumberUtils.randFloat(0, 1.0) - 0.5) * range;
            location.add(ranX, ranY, ranZ);
            // 额外的箭，每高一级，多射一支
            Arrow extraArrow = attacker.getWorld().spawn(location, Arrow.class);
            // 速度
            extraArrow.setVelocity(arrow.getVelocity());
            extraArrow.setShooter(attacker);
            // 不可拾取
            extraArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }
}
