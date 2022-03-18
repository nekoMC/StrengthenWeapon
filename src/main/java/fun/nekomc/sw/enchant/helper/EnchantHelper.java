package fun.nekomc.sw.enchant.helper;

import cn.hutool.core.map.MapUtil;
import fun.nekomc.sw.enchant.AbstractSwEnchantment;
import fun.nekomc.sw.utils.DurabilityUtils;
import fun.nekomc.sw.utils.ItemUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 附魔相关校验工具类
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/EcoEnchants">参考 EcoEnchants</a>
 */
@UtilityClass
@Slf4j
public class EnchantHelper {
    /**
     * Does the specified ItemStack have a certain Enchantment present?
     *
     * @param item        The {@link ItemStack} to check
     * @param enchantment The enchantment to query
     * @return If the item has the queried enchantment
     */
    public static boolean item(@Nullable final ItemStack item,
                               @NotNull final Enchantment enchantment) {
        return getEnchantLevelOnItem(item, enchantment) != 0;
    }

    /**
     * 获取道具指定附魔的等级
     *
     * @param enchantment 附魔
     * @param itemStack   道具
     * @return 附魔等级，不存在时返回 0
     */
    public static int getEnchantLevelOnItem(ItemStack itemStack, Enchantment enchantment) {
        if (null == enchantment || null == itemStack) {
            return 0;
        }
        return itemStack.getEnchantments().get(enchantment);
    }


    /**
     * Get all {@link AbstractSwEnchantment}s on a specified ItemStack.
     *
     * @param itemStack The ItemStack to query.
     * @return A {@link HashMap} of all AbstractSwEnchantment, where the key represents the level.
     */
    public static Map<AbstractSwEnchantment, Integer> getEnchantsOnItem(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return MapUtil.empty();
        }
        if (itemStack.getType().equals(Material.AIR)) {
            return MapUtil.empty();
        }

        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        Map<AbstractSwEnchantment, Integer> swEnchantMap = new HashMap<>(4);
        enchantments.keySet().stream()
                .filter(enchant -> enchant instanceof AbstractSwEnchantment)
                .forEach(enchant -> swEnchantMap.put((AbstractSwEnchantment) enchant, enchantments.get(enchant)));
        return swEnchantMap;
    }

    /**
     * Does the specified Arrow have a certain Enchantment present?
     * <p>
     * AbstractSwEnchantment automatically gives an arrow NBT data consisting of the enchantments present to avoid switching errors.
     *
     * @param arrow       The {@link Arrow} to check.
     * @param enchantment The enchantment to query.
     * @return If the arrow has the queried enchantment.
     */
    public static boolean arrow(@NotNull final Arrow arrow,
                                @NotNull final Enchantment enchantment) {
        return getArrowLevel(arrow, enchantment) != 0;
    }

    /**
     * What level specified Arrow has of a certain Enchantment present?
     * <p>
     * AbstractSwEnchantment automatically gives an arrow NBT data consisting of the enchantments present to avoid switching errors.
     *
     * @param arrow       The {@link Arrow} to check.
     * @param enchantment The enchantment to query.
     * @return The level found on the arrow, or 0 if not found.
     */
    public static int getArrowLevel(@NotNull final Arrow arrow,
                                    @NotNull final Enchantment enchantment) {
        ItemStack bow = ItemUtils.getArrowsBow(arrow);

        if (bow == null) {
            return 0;
        }

        return getEnchantLevelOnItem(bow, enchantment);
    }

    /**
     * Get all {@link AbstractSwEnchantment}s on a specified Arrow.
     *
     * @param arrow The Arrow to query.
     * @return A {@link HashMap} of all AbstractSwEnchantment, where the key represents the level.
     */
    public static Map<AbstractSwEnchantment, Integer> getEnchantsOnArrow(@NotNull final Arrow arrow) {
        ItemStack bow = ItemUtils.getArrowsBow(arrow);

        if (bow == null) {
            return new HashMap<>();
        }

        return getEnchantsOnItem(bow);
    }

    /**
     * Does the specified {@link LivingEntity} have a certain Enchantment present on the item in their main hand?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return If the LivingEntity has the enchantment.
     */
    public static boolean mainhand(@NotNull final LivingEntity entity,
                                   @NotNull final Enchantment enchantment) {
        return getMainhandLevel(entity, enchantment) != 0;
    }

    /**
     * What level of the specified enchantment does the queried {@link LivingEntity} have on their main hand item?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The level found on the mainhand item, or 0 if not found.
     */
    public static int getMainhandLevel(@NotNull final LivingEntity entity,
                                       @NotNull final Enchantment enchantment) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getItemInMainHand();

        return getEnchantLevelOnItem(item, enchantment);
    }

    /**
     * Get all {@link AbstractSwEnchantment}s on a queried {@link LivingEntity}s main hand item.
     *
     * @param entity The entity to query.
     * @return A {@link HashMap} of all AbstractSwEnchantment, where the key represents the level.
     */
    public static Map<AbstractSwEnchantment, Integer> getEnchantsOnMainhand(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        ItemStack item = entity.getEquipment().getItemInMainHand();

        return getEnchantsOnItem(item);
    }

    /**
     * Does the specified {@link LivingEntity} have a certain Enchantment present on the item in their offhand?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return If the LivingEntity has the enchantment.
     */
    public static boolean offhand(@NotNull final LivingEntity entity,
                                  @NotNull final Enchantment enchantment) {
        return getOffhandLevel(entity, enchantment) != 0;
    }

    /**
     * What level of the specified enchantment does the queried {@link LivingEntity} have on their offhand item?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The level found on the offhand item, or 0 if not found.
     */
    public static int getOffhandLevel(@NotNull final LivingEntity entity,
                                      @NotNull final Enchantment enchantment) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getItemInOffHand();

        return getEnchantLevelOnItem(item, enchantment);
    }

    /**
     * Get all {@link AbstractSwEnchantment}s on a queried {@link LivingEntity}s offhand item.
     *
     * @param entity The entity to query.
     * @return A {@link HashMap} of all AbstractSwEnchantment, where the key represents the level.
     */
    public static Map<AbstractSwEnchantment, Integer> getEnchantsOnOffhand(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        ItemStack item = entity.getEquipment().getItemInOffHand();

        return getEnchantsOnItem(item);
    }

    /**
     * Get a cumulative total of all levels on a {@link LivingEntity}s armor of a certain enchantment.
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The cumulative total of all levels, ie 4 pieces all with level 3 returns 12
     */
    public static int getArmorPoints(@NotNull final LivingEntity entity,
                                     @NotNull final Enchantment enchantment) {
        return getArmorPoints(entity, enchantment, 0);
    }

    /**
     * Get a cumulative total of all levels on a {@link LivingEntity}s armor of a certain enchantment.
     * <p>
     * Then, apply a specified amount of damage to all items with said enchantment.
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @param damage      The amount of damage to deal to all armor pieces.
     * @return The cumulative total of all levels, ie 4 pieces all with level 3 returns 12.
     */
    public static int getArmorPoints(@NotNull final LivingEntity entity,
                                     @NotNull final Enchantment enchantment,
                                     final int damage) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        boolean isPlayer = entity instanceof Player;

        AtomicInteger armorPoints = new AtomicInteger(0);
        List<ItemStack> armor = Arrays.asList(entity.getEquipment().getArmorContents());
        armor.forEach((itemStack -> {
            int level = getEnchantLevelOnItem(itemStack, enchantment);
            if (level != 0) {
                armorPoints.addAndGet(getEnchantLevelOnItem(itemStack, enchantment));
                if (damage > 0 && isPlayer) {
                    Player player = (Player) entity;
                    DurabilityUtils.damageItem(player, itemStack, level);
                }
            }
        }));

        return armorPoints.get();
    }

    /**
     * Get all {@link AbstractSwEnchantment}s on a queried {@link LivingEntity}s armor.
     *
     * @param entity The entity to query.
     * @return A {@link HashMap} of all AbstractSwEnchantment, where the key represents the cumulative total levels.
     */
    public static Map<AbstractSwEnchantment, Integer> getEnchantsOnArmor(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        Map<AbstractSwEnchantment, Integer> AbstractSwEnchantment = new HashMap<>();

        for (ItemStack itemStack : entity.getEquipment().getArmorContents()) {
            AbstractSwEnchantment.putAll(EnchantHelper.getEnchantsOnItem(itemStack));
        }

        return AbstractSwEnchantment;
    }

    /**
     * Does the specified {@link LivingEntity} have a certain Enchantment present on their helmet?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return If the LivingEntity has the enchantment.
     */
    public static boolean helmet(@NotNull final LivingEntity entity,
                                 @NotNull final Enchantment enchantment) {
        return getHelmetLevel(entity, enchantment) != 0;
    }

    /**
     * What level of the specified enchantment does the queried {@link LivingEntity} have on their helmet?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The level found, or 0 if not found.
     */
    public static int getHelmetLevel(@NotNull final LivingEntity entity,
                                     @NotNull final Enchantment enchantment) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getHelmet();

        return getEnchantLevelOnItem(item, enchantment);
    }

    /**
     * Does the specified {@link LivingEntity} have a certain Enchantment present on their chestplate?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return If the LivingEntity has the enchantment.
     */
    public static boolean chestplate(@NotNull final LivingEntity entity,
                                     @NotNull final Enchantment enchantment) {
        return getChestplateLevel(entity, enchantment) != 0;
    }

    /**
     * What level of the specified enchantment does the queried {@link LivingEntity} have on their chestplate?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The level found, or 0 if not found.
     */
    public static int getChestplateLevel(@NotNull final LivingEntity entity,
                                         @NotNull final Enchantment enchantment) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getChestplate();

        return getEnchantLevelOnItem(item, enchantment);
    }

    /**
     * Does the specified {@link LivingEntity} have a certain Enchantment present on their leggings?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return If the LivingEntity has the enchantment.
     */
    public static boolean leggings(@NotNull final LivingEntity entity,
                                   @NotNull final Enchantment enchantment) {
        return getLeggingsLevel(entity, enchantment) != 0;
    }

    /**
     * What level of the specified enchantment does the queried {@link LivingEntity} have on their leggings?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The level found, or 0 if not found.
     */
    public static int getLeggingsLevel(@NotNull final LivingEntity entity,
                                       @NotNull final Enchantment enchantment) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getLeggings();

        return getEnchantLevelOnItem(item, enchantment);
    }

    /**
     * Does the specified {@link LivingEntity} have a certain Enchantment present on their boots?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return If the LivingEntity has the enchantment.
     */
    public static boolean boots(@NotNull final LivingEntity entity,
                                @NotNull final Enchantment enchantment) {
        return getBootsLevel(entity, enchantment) != 0;
    }

    /**
     * What level of the specified enchantment does the queried {@link LivingEntity} have on their boots?
     *
     * @param entity      The entity to query.
     * @param enchantment The enchantment to check.
     * @return The level found, or 0 if not found.
     */
    public static int getBootsLevel(@NotNull final LivingEntity entity,
                                    @NotNull final Enchantment enchantment) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getBoots();

        return getEnchantLevelOnItem(item, enchantment);
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
                byNameMap.put(swEnchantment.getConfig().getDisplayName(), enchantment);
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
