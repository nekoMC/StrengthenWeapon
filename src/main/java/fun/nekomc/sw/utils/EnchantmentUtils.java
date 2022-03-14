package fun.nekomc.sw.utils;

import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.enchant.AbstractSwEnchantment;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 附魔操作工具类，参考：https://github.com/Auxilor/EcoEnchants
 * created: 2022/3/13 12:19
 *
 * @author Chiru
 */
@UtilityClass
@Slf4j
public class EnchantmentUtils {
    /**
     * If the enchantment has successfully passed its specified chance.
     *
     * @param enchantment The enchantment to query.
     * @param level       The level to base the chance off of.
     * @return If the enchantment should then be executed.
     */
    public static boolean passedChance(@NotNull final AbstractSwEnchantment enchantment,
                                       final int level) {
        return RandomUtil.randomDouble() < (double) (enchantment.getConfig().getAddition() * level) / 100;
    }

    /**
     * Register enchantment with the server.
     *
     * @param enchantment The enchantment.
     */
    public static void register(@NotNull final Enchantment enchantment) {
        removeAndHandle(enchantment, byNameMap -> {
            if (enchantment instanceof AbstractSwEnchantment) {
                AbstractSwEnchantment swEnchantment = (AbstractSwEnchantment) enchantment;
                if (swEnchantment.getConfig().isEnabled()) {
                    byNameMap.put(swEnchantment.getConfig().getDisplayName(), enchantment);
                }
            }
        });
        Enchantment.registerEnchantment(enchantment);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private static void removeAndHandle(@NotNull Enchantment enchantment, Consumer<Map<String, Enchantment>> handler) {
        try {
            Field byIdField = Enchantment.class.getDeclaredField("byKey");
            Field byNameField = Enchantment.class.getDeclaredField("byName");
            byIdField.setAccessible(true);
            byNameField.setAccessible(true);
            Map<NamespacedKey, Enchantment> byKey = (Map<NamespacedKey, Enchantment>) byIdField.get(null);
            Map<String, Enchantment> byName = (Map<String, Enchantment>) byNameField.get(null);
            byKey.remove(enchantment.getKey());
            byName.remove(enchantment.getName());

            if (enchantment instanceof AbstractSwEnchantment) {
                byName.remove(((AbstractSwEnchantment) enchantment).getConfig().getDisplayName());
            }

            Map<String, Enchantment> byNameClone = new HashMap<>(byName);
            for (Map.Entry<String, Enchantment> entry : byNameClone.entrySet()) {
                if (entry.getValue().getKey().equals(enchantment.getKey())) {
                    byName.remove(entry.getKey());
                }
            }

            if (null != handler) {
                handler.accept(byName);
            }

            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            f.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("error while reflecting", e);
        }
    }

    /**
     * Unregister enchantment with the server.
     *
     * @param enchantment The enchantment.
     */
    public static void unregister(@NotNull final Enchantment enchantment) {
        removeAndHandle(enchantment, null);
    }
}
