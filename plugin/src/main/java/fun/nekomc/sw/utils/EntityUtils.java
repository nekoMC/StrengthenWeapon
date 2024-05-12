package fun.nekomc.sw.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

/**
 * created: 2024/5/12 14:10
 *
 * @author Chiru
 */
@UtilityClass
public class EntityUtils {


    /**
     * 恢复指定实体的血量
     *
     * @param target 要恢复血量的实体
     * @param hp     要恢复的血量
     */
    public void healTargetBy(LivingEntity target, int hp) {
        AttributeInstance attribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        // 计算需要加的血量
        if (hp <= 0 || null == attribute) {
            return;
        }
        double maxHealth = attribute.getValue();
        double newHp = target.getHealth() + hp;
        // 确保血量在正确区间内
        newHp = Math.max(newHp, 0.0);
        newHp = Math.min(newHp, maxHealth);
        target.setHealth(newHp);
    }
}
