package fun.nekomc.sw.skill.helper;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.skill.AbstractSwSkill;
import fun.nekomc.sw.utils.DurabilityUtils;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerHolder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    public final LinkedHashMap<String, AbstractSwSkill> SKILL_MAP = new LinkedHashMap<>();

    public Optional<AbstractSwSkill> getByKey(String key) {
        if (StrUtil.isBlank(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(SKILL_MAP.get(key));
    }

    /**
     * 获取道具指定技能的等级
     *
     * @param skill     技能
     * @param itemStack 道具
     * @return 附魔等级，不存在时返回 0
     */
    public int getSkillLevelOnItem(ItemStack itemStack, AbstractSwSkill skill) {
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
    public Map<AbstractSwSkill, Integer> getSkillsOnItem(@Nullable final ItemStack itemStack) {
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
        Map<AbstractSwSkill, Integer> result = new HashMap<>();
        skillLvMap.forEach((key, level) -> {
            AbstractSwSkill skill = SKILL_MAP.get(key);
            if (null != skill) {
                result.put(skill, level);
            }
        });
        return result;
    }

    /**
     * 获取箭关联的技能等级
     *
     * @param arrow 要查询的 {@link Arrow}
     * @param skill 关注的技能
     * @return 不存在时返回 0
     */
    public int getArrowLevel(@NotNull final Arrow arrow,
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
    public Map<AbstractSwSkill, Integer> getSkillsOnArrow(@NotNull final Arrow arrow) {
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
    public int getMainhandLevel(@NotNull final LivingEntity entity,
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
    public Map<AbstractSwSkill, Integer> getSkillsOnMainhand(@NotNull final LivingEntity entity) {
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
    public int getOffhandLevel(@NotNull final LivingEntity entity,
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
    public Map<AbstractSwSkill, Integer> getSkillsOnOffhand(@NotNull final LivingEntity entity) {
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
    public int getArmorPoints(@NotNull final LivingEntity entity,
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
    public int getArmorPoints(@NotNull final LivingEntity entity,
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
    public Map<AbstractSwSkill, Integer> getSkillsOnArmor(@NotNull final LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return new HashMap<>();
        }

        Map<AbstractSwSkill, Integer> skillsMap = new HashMap<>();

        for (ItemStack itemStack : entity.getEquipment().getArmorContents()) {
            skillsMap.putAll(SkillHelper.getSkillsOnItem(itemStack));
        }

        return skillsMap;
    }

    /**
     * 获取 {@link LivingEntity} 装备的头盔的指定技能等级
     *
     * @param entity 要查询的实体
     * @param skill  关注的技能
     * @return 不存在时返回 0
     */
    public int getHelmetLevel(@NotNull final LivingEntity entity,
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
    public int getChestplateLevel(@NotNull final LivingEntity entity,
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
    public int getLeggingsLevel(@NotNull final LivingEntity entity,
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
    public int getBootsLevel(@NotNull final LivingEntity entity,
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
    public void register(@NotNull final AbstractSwSkill skill) {
        String skillKey = skill.getConfigKey();
        if (SKILL_MAP.containsKey(skillKey)) {
            log.warn("Duplicate key: {}, 更新 [{}] 为 [{}]",
                    skillKey, SKILL_MAP.get(skillKey).getSkillName(), skill.getSkillName());
        }
        SKILL_MAP.put(skillKey, skill);
    }

    /**
     * 注销技能
     *
     * @param skill 要注销的技能
     */
    public void unregister(@NotNull final AbstractSwSkill skill) {
        SKILL_MAP.remove(skill.getKey());
    }

    /**
     * 更新物品的附魔显示（通过 Lore 显示，只针对 SwSkill 有效）
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
            String skillName = skill.getSkillName();
            int firstBlank = lore.indexOf(" ");
            if (-1 == firstBlank) {
                continue;
            }
            String skillNameFromLore = ChatColor.stripColor(lore.substring(0, firstBlank));
            if (Objects.equals(skillNameFromLore, skillName)) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
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

        toAdd.forEach((skill, lvl) -> applySkillToItem(itemStack, skill, lvl));
        // 清空原 Lore
        itemStack.setItemMeta(meta);
        SkillHelper.updateLore(itemStack);
    }

    /**
     * 从一行 Lore 信息中恢复技能，并调用 handler
     */
    private void restoreFromLoreLine(String line, ObjIntConsumer<Optional<AbstractSwSkill>> handler) {

        Optional<AbstractSwSkill> skillOpt = SkillHelper.skillFromLore(line);
        if (skillOpt.isEmpty()) {
            handler.accept(skillOpt, 0);
            return;
        }
        int level = skillOpt.get().levelFromLore(line);
        handler.accept(skillOpt, level);
    }

    /**
     * 通过 Lore 恢复技能数据
     *
     * @param itemStack 要操作的物品
     */
    public void applySkillToItem(@NotNull final ItemStack itemStack,
                                 @NotNull final AbstractSwSkill skill,
                                 final int level) {
        if (itemStack.getType().equals(Material.AIR) || level == 0) {
            return;
        }
        Optional<SwItemAttachData> attachDataOpt = ItemUtils.getAttachData(itemStack);
        SwItemAttachData attachData = attachDataOpt.orElseGet(SwItemAttachData::new);
        attachData.putSkill(skill, level);
    }

    /**
     * 通过技能名获取附魔对象，支持原生附魔和自定义附魔
     *
     * @param skillName 技能名（去除颜色格式）
     * @return 指定的技能对象
     */
    public Optional<AbstractSwSkill> getByName(String skillName) {
        return SKILL_MAP.values().stream()
                .filter(skill -> Objects.equals(skill.getSkillName(), skillName))
                .findFirst();
    }

    /**
     * 通过附魔的显示名称获得指定的技能对象
     *
     * @param loreName 附魔的显示名称
     * @return Optional 包装的 AbstractSwSkill 对象
     */
    public Optional<AbstractSwSkill> getByLoreName(String loreName) {
        for (AbstractSwSkill skill : SKILL_MAP.values()) {
            String stripedDisplay = ChatColor.stripColor(skill.getDisplayName());
            if (Objects.equals(stripedDisplay, loreName)) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    /**
     * 更新道具的指定技能及等级
     *
     * @param targetItem  目标道具
     * @param targetSkill 要更新的技能
     * @param targetLevel 更新后的等级，如果为 0 则删除
     * @return 操作是否成功
     */
    public static boolean updateItemSkill(ItemStack targetItem, AbstractSwSkill targetSkill, int targetLevel) {
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        SwItemAttachData attachData = ItemUtils.getAttachData(targetItem).orElse(new SwItemAttachData());
        attachData.putSkill(targetSkill, targetLevel);
        ItemUtils.updateAttachData(itemMeta, attachData);
        // 刷新附魔 Lore
        SkillHelper.updateLore(targetItem);

        String itemName = itemMeta.getDisplayName();
        if (CharSequenceUtil.isEmpty(itemName)) {
            itemName = itemMeta.getLocalizedName();
        }
        log.info("{} updated [{}]'s Enchantment: {}",
                PlayerHolder.getSender().getName(), itemName, targetSkill.getConfigKey());
        return true;
    }
}
