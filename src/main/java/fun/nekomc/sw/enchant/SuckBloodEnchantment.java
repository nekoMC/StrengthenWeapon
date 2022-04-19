package fun.nekomc.sw.enchant;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 吸血附魔实现
 * created: 2022/4/4 13:23
 *
 * @author Chiru
 */
public class SuckBloodEnchantment extends AbstractSwEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "SUCK_BLOOD";

    public SuckBloodEnchantment() {
        super(ENCHANT_KEY);
    }

    @Override
    public void onMeleeAttack(@NotNull LivingEntity attacker, @NotNull LivingEntity victim, int level, @NotNull EntityDamageByEntityEvent event) {
        AttributeInstance attribute = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        // 计算需要加的血量
        int bloodToAdd = getEnchantLvlAttribute(level) / 100;
        if (bloodToAdd <= 0 || null == attribute) {
            return;
        }
        double maxHealth = attribute.getValue();
        double newHp = attacker.getHealth() + bloodToAdd;
        // 确保血量在正确区间内
        newHp = Math.max(newHp, 0.0);
        newHp = Math.min(newHp, maxHealth);
        attacker.setHealth(newHp);
    }
}
