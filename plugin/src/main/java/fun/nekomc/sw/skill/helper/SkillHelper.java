package fun.nekomc.sw.skill.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SkillConfigDto;
import fun.nekomc.sw.skill.AbstractSwSkill;
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
 * 技能相关工具类
 *
 * @author Chiru
 */
@UtilityClass
@Slf4j
public class SkillHelper {

    @Getter
    private static final LinkedHashMap<String, AbstractSwSkill> SKILL_MAP = new LinkedHashMap<>();

    /**
     * 获取道具指定技能的等级
     *
     * @param skill     技能
     * @param itemStack 道具
     * @return 附魔等级，不存在时返回 0
     */
    public static int getSkillLevelOnItem(ItemStack itemStack, AbstractSwSkill skill) {
        if (null == skill || null == itemStack) {
            return 0;
        }
        Optional<SwItemAttachData> attachDataOpt = ItemUtils.getAttachData(itemStack);
        if (attachDataOpt.isEmpty()) {
            return 0;
        }
        Map<String, Integer> skillLvMap = attachDataOpt.get().getSkills();
        Integer lvl = skillLvMap.get(skill.getKey());
        return null == lvl ? 0 : lvl;
    }

    /**
     * 从物品上获取全部 {@link AbstractSwSkill}
     *
     * @param itemStack 要操作的物品
     * @return 包含全部 AbstractSwSkill 的 {@link HashMap} 值为 level
     */
    public static Map<AbstractSwSkill, Integer> getSkillsOnItem(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return MapUtil.empty();
        }
        if (itemStack.getType().equals(Material.AIR)) {
            return MapUtil.empty();
        }
        Optional<SwItemAttachData> attachDataOpt = ItemUtils.getAttachData(itemStack);
        if (attachDataOpt.isEmpty()) {
            return MapUtil.empty();
        }
        Map<String, Integer> skillLvMap = attachDataOpt.get().getSkills();
        // TODO: key 反解析为技能
    }

    /**
     * 获取箭关联的技能等级
     *
     * @param arrow 要查询的 {@link Arrow}
     * @param skill 关注的技能
     * @return 不存在时返回 0
     */
    public static int getArrowLevel(@NotNull final Arrow arrow,
                                    @NotNull final AbstractSwSkill skill) {
        ItemStack bow = ItemUtils.getArrowsBow(arrow);

        if (bow == null) {
            return 0;
        }

        return getSkillLevelOnItem(bow, skill);
    }

    /**
     * 获取箭上的全部 {@link AbstractSwSkill}
     *
     * @param arrow 要查询的 {@link Arrow}
     * @return 获取包含箭上全部的技能及其等级的 {@link HashMap}
     */
    public static Map<AbstractSwSkill, Integer> getEnchantsOnArrow(@NotNull final Arrow arrow) {
        ItemStack bow = ItemUtils.getArrowsBow(arrow);

        if (bow == null) {
            return new HashMap<>();
        }

        return getSkillsOnItem(bow);
    }

    /**
     * 获取 {@link LivingEntity} 主手上物品的指定技能等级
     *
     * @param entity 关注的实体
     * @param skill  要查询的技能
     * @return 不存在时返回 0
     */
    public static int getMainhandLevel(@NotNull final LivingEntity entity,
                                       @NotNull final AbstractSwSkill skill) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getItemInMainHand();

        return getSkillLevelOnItem(item, skill);
    }

    /**
     * 获取 {@link LivingEntity} 主手的全部 {@link AbstractSwSkill}
     *
     * @param entity 实体
     * @return 包含全部技能及其等级的 {@link HashMap}
     */
    public static Map<AbstractSwSkill, Integer> getSkillsOnMainhand(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        ItemStack item = entity.getEquipment().getItemInMainHand();

        return getSkillsOnItem(item);
    }

    /**
     * 获取 {@link LivingEntity} 副手物品的技能等级
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 不存在时返回 0
     */
    public static int getOffhandLevel(@NotNull final LivingEntity entity,
                                      @NotNull final AbstractSwSkill skill) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getItemInOffHand();

        return getSkillLevelOnItem(item, skill);
    }

    /**
     * 获取 {@link LivingEntity} 副手物品的全部 {@link AbstractSwSkill}
     *
     * @param entity 要查询的实体
     * @return 包含全部技能及其等级的 {@link HashMap}
     */
    public static Map<AbstractSwSkill, Integer> getEnchantsOnOffhand(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        ItemStack item = entity.getEquipment().getItemInOffHand();

        return getSkillsOnItem(item);
    }

    /**
     * 获取 {@link LivingEntity} 已装备的全部盔甲的技能等级和
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 技能等级和
     */
    public static int getArmorPoints(@NotNull final LivingEntity entity,
                                     @NotNull final AbstractSwSkill skill) {
        return getArmorPoints(entity, skill, 0);
    }

    /**
     * 获取 {@link LivingEntity} 已装备的全部盔甲的技能等级和
     * <p>
     * 并给予拥有指定技能的装备指定的耐久消耗
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @param damage 要给予装备的耐久消耗
     * @return 技能等级和
     */
    public static int getArmorPoints(@NotNull final LivingEntity entity,
                                     @NotNull final AbstractSwSkill skill,
                                     final int damage) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        boolean isPlayer = entity instanceof Player;

        AtomicInteger armorPoints = new AtomicInteger(0);
        List<ItemStack> armor = Arrays.asList(entity.getEquipment().getArmorContents());
        armor.forEach((itemStack -> {
            int level = getSkillLevelOnItem(itemStack, skill);
            if (level != 0) {
                armorPoints.addAndGet(getSkillLevelOnItem(itemStack, skill));
                if (damage > 0 && isPlayer) {
                    Player player = (Player) entity;
                    DurabilityUtils.damageItem(player, itemStack, level);
                }
            }
        }));

        return armorPoints.get();
    }

    /**
     * 获取 {@link LivingEntity} 装备上的全部 {@link AbstractSwSkill}
     *
     * @param entity 要查询的实体
     * @return 包含全部技能及其等级的 {@link HashMap}
     */
    public static Map<AbstractSwSkill, Integer> getEnchantsOnArmor(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        Map<AbstractSwSkill, Integer> enchantsMap = new HashMap<>();

        for (ItemStack itemStack : entity.getEquipment().getArmorContents()) {
            enchantsMap.putAll(SkillHelper.getSkillsOnItem(itemStack));
        }

        return enchantsMap;
    }

    /**
     * 获取 {@link LivingEntity} 装备的头盔的指定技能等级
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 不存在时返回 0
     */
    public static int getHelmetLevel(@NotNull final LivingEntity entity,
                                     @NotNull final AbstractSwSkill skill) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getHelmet();

        return getSkillLevelOnItem(item, skill);
    }

    /**
     * 获取 {@link LivingEntity} 装备胸甲的技能等级
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 不存在时返回 0
     */
    public static int getChestplateLevel(@NotNull final LivingEntity entity,
                                         @NotNull final AbstractSwSkill skill) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getChestplate();

        return getSkillLevelOnItem(item, skill);
    }

    /**
     * 获取 {@link LivingEntity} 所装备护腿的技能等级
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 不存在时返回 0
     */
    public static int getLeggingsLevel(@NotNull final LivingEntity entity,
                                       @NotNull final AbstractSwSkill skill) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getLeggings();

        return getSkillLevelOnItem(item, skill);
    }

    /**
     * 获取 {@link LivingEntity} 所装备鞋子的技能等级
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 不存在时返回 0
     */
    public static int getBootsLevel(@NotNull final LivingEntity entity,
                                    @NotNull final AbstractSwSkill skill) {
        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack item = entity.getEquipment().getBoots();

        return getSkillLevelOnItem(item, skill);
    }

    /**
     * 注册一个技能
     *
     * @param skill 技能
     */
    public static void register(@NotNull final AbstractSwSkill skill) {
        String skillKey = skill.getKey();
        if (SKILL_MAP.containsKey(skillKey)) {
            log.warn("Duplicate key: {}, 更新 [{}] 为 [{}]",
                    skillKey, SKILL_MAP.get(skillKey).getName(), skill.getName());
        }
        SKILL_MAP.put(skillKey, skill);
    }

    /**
     * 注销技能
     *
     * @param skill 要注销的技能
     */
    public static void unregister(@NotNull final AbstractSwSkill skill) {
        SKILL_MAP.remove(skill.getKey());
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
        Set<String> skillNames = SKILL_MAP.values().stream()
                .map(skill -> ChatColor.stripColor(skill.getConfig().getDisplayName()))
                .collect(Collectors.toSet());
        // 获取物品上的技能
        HashMap<AbstractSwSkill, Integer> skills = new HashMap<>(getSkillsOnItem(itemStack));
        skills.forEach((skill, level) -> {
            // 需要隐藏的
            if (skill.getConfig().isHideLore()) {
                return;
            }
            // 生成要显示的 Lore
            String lore = skill.lore(level);
            resultLore.add(lore);
        });
        // 过滤掉重复的
        itemLore = itemLore.stream().filter(row -> {
            int firstBlank = row.indexOf(" ");
            if (-1 == firstBlank) {
                return true;
            }
            String skillName = ChatColor.stripColor(row.substring(0, firstBlank));
            // 过滤掉显示名与当前显示名重复的
            return !skillNames.contains(skillName);
        }).collect(Collectors.toList());

        resultLore.addAll(itemLore);
        meta.setLore(resultLore);
        itemStack.setItemMeta(meta);
    }

    public Optional<AbstractSwSkill> skillFromLore(String lore) {
        for (AbstractSwSkill skill : SKILL_MAP.values()) {
            String displayName = skill.getConfig().getDisplayName();
            String configName = ChatColor.stripColor(displayName);
            int firstBlank = lore.indexOf(" ");
            if (-1 == firstBlank) {
                continue;
            }
            String skillNameFromLore = ChatColor.stripColor(lore.substring(0, firstBlank));
            if (Objects.equals(skillNameFromLore, configName)) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    // TODO:

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

        Map<AbstractSwSkill, Integer> toAdd = new HashMap<>(4);

        List<String> lore = meta.getLore();

        if (lore == null) {
            return;
        }

        for (String line : new ArrayList<>(lore)) {
            restoreFromLoreLine(line, (skill, lvl) -> {
                if (skill.isPresent()) {
                    lore.remove(line);
                    toAdd.put(skill.get(), lvl);
                }
            });
        }

        toAdd.forEach((skill, lvl) -> {
            // TODO: HERE
        });
        itemStack.setItemMeta(meta);
        SkillHelper.updateLore(itemStack);
    }

    /**
     * 从一行 Lore 信息中恢复技能，并调用 handler
     */
    private static void restoreFromLoreLine(String line, ObjIntConsumer<Optional<AbstractSwSkill>> handler) {

        Optional<AbstractSwSkill> skillOpt = SkillHelper.skillFromLore(line);
        if (skillOpt.isEmpty()) {
            handler.accept(skillOpt, 0);
            return;
        }
        int level = skillOpt.get().levelFromLore(line);
        handler.accept(skillOpt, level);
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
