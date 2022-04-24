package fun.nekomc.sw.enchant;

import fun.nekomc.sw.enchant.helper.EnchantHelper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 受击恢复附魔实现
 * created: 2022/4/21 23:51
 *
 * @author Chiru
 */
public class GetHitHealEnchantment extends AbstractSwEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "GET_HIT_HEAL";

    public GetHitHealEnchantment() {
        super(ENCHANT_KEY);
    }

    @Override
    public void onDamageWearingArmor(@NotNull LivingEntity victim, int level, @NotNull EntityDamageEvent event) {
        // 计算需要加的血量
        int bloodToAdd = getEnchantLvlAttribute(level) / 100;
        EnchantHelper.healTargetBy(victim, bloodToAdd);
    }
}
