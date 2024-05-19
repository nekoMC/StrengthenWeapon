package fun.nekomc.sw.skill;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * @author Mori
 * @since 2024/4/22 19:52
 */
@Getter
@Slf4j
public abstract class AbstractCoolDownSwSkill extends AbstractSwSkill {

    private final Map<UUID, Long> timer = new WeakHashMap<>(16);

    protected AbstractCoolDownSwSkill(String configKey) {
        super(configKey);
    }

    protected boolean passCoolDown(LivingEntity player, final int level) {
        UUID uuid = player.getUniqueId();
        Long lastSplash = timer.get(uuid);
        int coolDown = getSkillLvlAttribute(level);
        long now = System.currentTimeMillis();
        // 仍需 CD 时
        if (null != lastSplash && now - lastSplash < coolDown) {
            return false;
        }
        timer.put(uuid, now);
        return true;
    }
}
