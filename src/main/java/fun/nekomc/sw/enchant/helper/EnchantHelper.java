package fun.nekomc.sw.enchant.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.enchant.AbstractSwEnchantment;
import fun.nekomc.sw.utils.DurabilityUtils;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.RomanNumberUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;

/**
 * 附魔相关校验工具类
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/EcoEnchants">参考 EcoEnchants</a>
 */
@UtilityClass
@Slf4j
public class EnchantHelper {

    @Getter
    private static final List<AbstractSwEnchantment> REGISTERED_ENCHANTS = new LinkedList<>();

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
        Integer lvl = itemStack.getEnchantments().get(enchantment);
        return null == lvl ? 0 : lvl;
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

        Map<Enchantment, Integer> enchantments;
        if (itemStack.getType() == Material.ENCHANTED_BOOK && null != itemStack.getItemMeta()) {
            enchantments = ((EnchantmentStorageMeta) itemStack.getItemMeta()).getStoredEnchants();
        } else {
            enchantments = itemStack.getEnchantments();
        }

        Map<AbstractSwEnchantment, Integer> swEnchantMap = new HashMap<>(4);
        enchantments.keySet().stream()
                .filter(AbstractSwEnchantment.class::isInstance)
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

        Map<AbstractSwEnchantment, Integer> enchantsMap = new HashMap<>();

        for (ItemStack itemStack : entity.getEquipment().getArmorContents()) {
            enchantsMap.putAll(EnchantHelper.getEnchantsOnItem(itemStack));
        }

        return enchantsMap;
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
                REGISTERED_ENCHANTS.add(swEnchantment);
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
            // 强制设置 Enchantment 可修改
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);

            if (null != handler) {
                handler.accept(byName);
            }

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

    /**
     * 更新物品的附魔显示（通过 Lore 显示，只针对 SwEnchantment 附魔有效）
     * 道具属性的修复监听器，参考：EcoEnchants - com.willfp.ecoenchants.enchantments.util.ItemConversions
     *
     * @param itemStack 指定物品
     */
    public void updateLore(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (null == meta) {
            return;
        }
        // 原 Lore
        List<String> itemLore = meta.getLore();
        if (null == itemLore) {
            itemLore = ListUtil.empty();
        }
        // 结果 Lore
        List<String> resultLore = new ArrayList<>();
        // 需要移除的附魔名称集合
        Set<String> enchantNameToDelete = REGISTERED_ENCHANTS.stream()
                .map(swEnchant -> ChatColor.stripColor(swEnchant.getConfig().getDisplayName()))
                .collect(Collectors.toSet());
        // 获取物品上的 SwEnchantment
        LinkedHashMap<AbstractSwEnchantment, Integer> enchantments = new LinkedHashMap<>(getEnchantsOnItem(itemStack));
        enchantments.forEach((enchantment, level) -> {
            // 需要隐藏的附魔
            if (enchantment.getConfig().isHideLore()) {
                return;
            }
            // 生成附魔要显示的 Lore
            String enchantName = enchantment.getConfig().getDisplayName();
            String enchantLvl = RomanNumberUtils.toNumeral(level);
            Map<String, String> valueMap = new HashMap<>(2);
            valueMap.put("name", enchantName);
            valueMap.put("lvl", enchantLvl);
            resultLore.add(StrUtil.format("{name} {lvl}", valueMap, true));
        });
        // 过滤掉原附魔上重复的附魔
        itemLore = itemLore.stream().filter(row -> {
            int lastBlank = row.lastIndexOf(" ");
            if (-1 == lastBlank) {
                return true;
            }
            String enchantName = ChatColor.stripColor(row.substring(0, lastBlank));
            // 过滤掉附魔名与当前附魔重复的
            return !enchantNameToDelete.contains(enchantName);
        }).collect(Collectors.toList());

        resultLore.addAll(itemLore);
        meta.setLore(resultLore);
        itemStack.setItemMeta(meta);
    }

    /**
     * 通过 Lore 恢复自定义附魔数据，参考 EcoEnchant
     *
     * @param itemStack 要操作的物品
     */
    public void fixByLore(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        Map<Enchantment, Integer> toAdd = new HashMap<>(4);

        List<String> lore = meta.getLore();

        if (lore == null) {
            return;
        }

        for (String line : new ArrayList<>(lore)) {
            restoreEnchantmentFromLoreLine(line, (enchant, lvl) -> {
                if (enchant.isPresent()) {
                    lore.remove(line);
                    toAdd.put(enchant.get(), lvl);
                }
            });
        }

        if (meta instanceof EnchantmentStorageMeta) {
            lore.clear();
            toAdd.forEach((enchantment, integer) -> ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, integer, true));
        } else {
            toAdd.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
        }
        itemStack.setItemMeta(meta);
        EnchantHelper.updateLore(itemStack);
    }

    /**
     * 从一行 Lore 信息中恢复附魔，并调用 handler
     */
    private static void restoreEnchantmentFromLoreLine(String line, ObjIntConsumer<Optional<AbstractSwEnchantment>> handler) {
        line = ChatColor.stripColor(line);

        Optional<AbstractSwEnchantment> enchant;
        int level;
        List<String> lineSplit = new ArrayList<>(Arrays.asList(line.split(" ")));
        if (CollUtil.isEmpty(lineSplit)) {
            handler.accept(Optional.empty(), 0);
            return;
        }
        if (lineSplit.size() == 1) {
            enchant = EnchantHelper.getByLoreName(lineSplit.get(0));
            level = 1;
        } else {
            // 兼容附魔名中含空格的情况
            Optional<AbstractSwEnchantment> attemptFullLine = EnchantHelper.getByLoreName(line);

            if (attemptFullLine.isPresent()) {
                enchant = attemptFullLine;
                level = 1;
            } else {
                String levelString = lineSplit.get(lineSplit.size() - 1);
                lineSplit.remove(levelString);
                levelString = levelString.trim();

                try {
                    level = RomanNumberUtils.fromNumeral(levelString);
                } catch (IllegalArgumentException e) {
                    handler.accept(Optional.empty(), 0);
                    return;
                }

                String enchantName = String.join(" ", lineSplit);
                enchant = EnchantHelper.getByLoreName(enchantName);
            }
        }
        handler.accept(enchant, level);
    }

    /**
     * 通过附魔名获取附魔对象，支持原生附魔和自定义附魔
     *
     * @param enchantName 附魔名
     * @return 指定的附魔对象
     */
    public static Optional<Enchantment> getByName(String enchantName) {
        Optional<Enchantment> target = REGISTERED_ENCHANTS.stream()
                .filter(enchant -> enchant.getKey().getKey().equals(enchantName))
                .findFirst()
                .map(Enchantment.class::cast);
        return target.isPresent()
                ? target
                : Optional.ofNullable(Enchantment.getByKey(NamespacedKey.minecraft(enchantName)));
    }

    /**
     * 通过附魔的显示名称获得指定的附魔对象，只针对自定义附魔有效
     *
     * @param loreName 附魔的显示名称
     * @return Optional 包装的 AbstractSwEnchantment 对象
     */
    public static Optional<AbstractSwEnchantment> getByLoreName(String loreName) {
        for (AbstractSwEnchantment registeredEnchant : REGISTERED_ENCHANTS) {
            String stripedDisplay = ChatColor.stripColor(registeredEnchant.getConfig().getDisplayName());
            if (Objects.equals(stripedDisplay, loreName)) {
                return Optional.of(registeredEnchant);
            }
        }
        return Optional.empty();
    }

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
