package fun.nekomc.sw.skill;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * 概率性秒杀实现
 * created: 2022/4/21 23:58
 *
 * @author Chiru
 */
public class SecKillSkill extends AbstractSwSkill {

    public SecKillSkill() {
        super("SECKILL");
    }

    @Override
    public void onPotionSplash(@NotNull LivingEntity shooter, @NotNull ItemStack triggerItem, @NotNull ThrownPotion potion, int level, @NotNull PotionSplashEvent event) {
        Collection<LivingEntity> affectedEntities = event.getAffectedEntities();
        if (CollUtil.isEmpty(affectedEntities)) {
            return;
        }
        // 遍历药水范围内实体，执行秒杀
        affectedEntities.forEach(victim -> {
            // 是否可以激活秒杀
            if (passChance(level)) {
                doSecKill(shooter, victim);
            }
        });
    }

    @Override
    public void onArrowDamage(@NotNull LivingEntity attacker, @NotNull LivingEntity victim, @NotNull Arrow arrow, int level, @NotNull EntityDamageByEntityEvent event) {
        // 是否可以激活秒杀
        if (!passChance(level)) {
            return;
        }
        doSecKill(attacker, victim);
    }

    @Override
    public void onTridentDamage(@NotNull LivingEntity attacker, @NotNull LivingEntity victim, @NotNull Trident trident, int level, @NotNull EntityDamageByEntityEvent event) {
        // 是否可以激活秒杀
        if (!passChance(level)) {
            return;
        }
        doSecKill(attacker, victim);
    }

    @Override
    public void onMeleeAttack(@NotNull LivingEntity attacker, @NotNull LivingEntity victim, int level, @NotNull EntityDamageByEntityEvent event) {
        // 是否可以激活秒杀
        if (!passChance(level)) {
            return;
        }
        doSecKill(attacker, victim);
    }

    /**
     * 执行秒杀
     */
    private void doSecKill(LivingEntity attacker, LivingEntity victim) {
        // 得到 safe 配置的值，超出则无效
        int maxHpLimit = 0;
        Map<String, String> ext = getConfig().getExt();
        String safe = ext.get("safe");
        if (CharSequenceUtil.isNumeric(safe)) {
            maxHpLimit = Integer.parseInt(safe);
        }
        // 不能秒杀自己
        if (victim.equals(attacker)) {
            return;
        }
        // 保护血量超出 safe 配置的实体
        AttributeInstance maxHpAttr = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (null == maxHpAttr) {
            return;
        }
        double maxHp = maxHpAttr.getValue();
        if (maxHp >= maxHpLimit) {
            return;
        }
        // 执行秒杀
        victim.setHealth(0);
    }
}
