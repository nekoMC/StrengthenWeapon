package fun.nekomc.sw.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.enchant.helper.EnchantHelper;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.SwException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作道具数据的工具类
 *
 * @author ourange
 */
@UtilityClass
@Slf4j
public class ItemUtils {

    /**
     * 通过 ItemConfigDto 构建道具
     *
     * @param itemConfig SwItemConfigDto 对象，通常通过配置文件中读取获得
     * @return 道具，配置存在问题时返回 Optional.empty
     */
    public static Optional<ItemStack> buildItemByConfig(SwItemConfigDto itemConfig) {
        Assert.notNull(itemConfig, "itemConfig cannot be null");
        // Material
        Material material = Material.getMaterial(itemConfig.getMaterial());
        if (null == material) {
            return Optional.empty();
        }
        ItemStack itemStack = new ItemStack(material);
        // Meta
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        meta.setDisplayName(itemConfig.getDisplayName());
        meta.setUnbreakable(itemConfig.isUnbreakable());
        ((Damageable) meta).setDamage(itemConfig.getDamage());
        // Meta - 附魔、属性修改
        meta.setAttributeModifiers(itemConfig.getAttributeModifiers());
        itemConfig.getEnchantMap().forEach((enchant, lvl) -> meta.addEnchant(enchant, lvl, true));
        // Meta - 附加信息，为白板写入带有初始强化等级的附加信息
        ItemsTypeEnum itemType = ItemsTypeEnum.valueOf(itemConfig.getType());
        SwItemAttachData itemDefaultAttachData = itemType == ItemsTypeEnum.BLANK
                ? SwItemAttachData.LVL0_ATTACH_DATA
                : SwItemAttachData.EMPTY_ATTACH_DATA;
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        persistentDataContainer.set(getWarpedKey(itemConfig.getName()),
                SwItemAttachData.EMPTY_ATTACH_DATA, itemDefaultAttachData);
        // 包装 lore 信息
        List<String> replacedLore = replaceLore(itemConfig.getLore(), itemDefaultAttachData);
        meta.setLore(replacedLore);
        itemStack.setItemMeta(meta);
        // 刷新附魔 Lore
        EnchantHelper.updateLore(itemStack);
        return Optional.of(itemStack);
    }

    /**
     * 将 SwItemAttachData 更新到 ItemStack 的 PersistentDataContainer 中
     *
     * @param itemMeta   道具 ItemMeta，必须包含合法的 SwItemAttachData 数据
     * @param attachData 更新后的 SwItemAttachData
     */
    public static void updateAttachData(ItemMeta itemMeta, SwItemAttachData attachData) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        String nameFromDataContainer = getNameFromDataContainer(persistentDataContainer);
        if (StringUtils.isBlank(nameFromDataContainer)) {
            throw new SwException(ConfigManager.getConfiguredMsg(Constants.Msg.UNKNOWN_ITEM));
        }
        persistentDataContainer.set(getWarpedKey(nameFromDataContainer), SwItemAttachData.EMPTY_ATTACH_DATA, attachData);
        Optional<SwItemConfigDto> itemConfigOptional = ConfigManager.getItemConfig(nameFromDataContainer);
        // 配置文件内容缺失时，不进行后续处理
        if (!itemConfigOptional.isPresent()) {
            MsgUtils.consoleMsg("配置项缺失，请检查道具[%s]的配置", nameFromDataContainer);
            return;
        }
        // 更新 lore 信息
        List<String> replacedLore = replaceLore(itemConfigOptional.get().getLore(), attachData);
        itemMeta.setLore(replacedLore);
    }

    /**
     * 获取道具的附加信息
     *
     * @param itemStack 道具 ItemStack
     * @return SwItemAttachData，不存在时返回 Optional.empty()
     */
    public static Optional<SwItemAttachData> getAttachData(ItemStack itemStack) {
        if (null == itemStack) {
            return Optional.empty();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (null == itemMeta) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        String nameFromDataContainer = getNameFromDataContainer(persistentDataContainer);
        if (StringUtils.isBlank(nameFromDataContainer)) {
            return Optional.empty();
        }
        NamespacedKey warpedItemName = getWarpedKey(nameFromDataContainer);
        SwItemAttachData attachData = persistentDataContainer.get(warpedItemName, SwItemAttachData.EMPTY_ATTACH_DATA);
        return Optional.ofNullable(attachData);
    }

    /**
     * 从 ItemStack 中获取道具名称，预期名称只有一个，存在多个时抛异常
     *
     * @param itemStack 道具
     * @return 道具名称，如 sw_bow
     * @throws ConfigurationException 当存在多个道具名时抛出
     */
    @Nullable
    public static String getNameFromMeta(ItemStack itemStack) {
        Optional<PersistentDataContainer> dataContainerFromItem = getDataContainerFromItem(itemStack);
        return dataContainerFromItem.map(ItemUtils::getNameFromDataContainer).orElse(null);
    }

    /**
     * 检查指定的物品是否满足 SW 相关配置，仅校验自定义数据标签
     *
     * @param stack ItemStack 对象
     * @return 是否存在自定义数据标签
     */
    public boolean isSwItem(ItemStack stack) {
        return getAttachData(stack).isPresent();
    }

    /**
     * Get the bow from an arrow.
     *
     * @param arrow The arrow.
     * @return The bow, or null if no bow.
     */
    @Nullable
    public static ItemStack getArrowsBow(@NotNull final Arrow arrow) {
        List<MetadataValue> values = arrow.getMetadata("shot-from");

        if (values.isEmpty()) {
            return null;
        }

        if (!(values.get(0).value() instanceof ItemStack)) {
            return null;
        }

        return (ItemStack) values.get(0).value();
    }

    public Player tryAsPlayer(Entity entity) {
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            return (Player) tameable.getOwner();
        }
        return null;
    }

    /**
     * 更新元数据中描述的道具属性信息
     *
     * @param originMeta  需要修改的元数据，会在调用后发生变化
     * @param slot        生效槽位
     * @param attribute   目标属性
     * @param modifyValue 变更的属性值，如 0.2 2 2.2
     * @param check       是否只允许修改物品已有的属性（值）
     * @return 操作是否成功
     */
    public boolean updateAttributeModifierInMeta(ItemMeta originMeta, EquipmentSlot slot,
                                                 Attribute attribute, String modifyValue, boolean check) {
        if (null == originMeta) {
            return false;
        }
        // Meta 中的原属性
        Multimap<Attribute, AttributeModifier> attributeModifiers = originMeta.getAttributeModifiers();
        attributeModifiers = null == attributeModifiers
                ? LinkedListMultimap.create()
                : LinkedListMultimap.create(attributeModifiers);
        // 移除旧属性
        Collection<AttributeModifier> originAttributeModifiers = attributeModifiers.get(attribute);
        List<AttributeModifier> modifiers = originAttributeModifiers.stream()
                .filter(modifier -> modifier.getSlot() != slot)
                .collect(Collectors.toList());
        if (check && originAttributeModifiers.size() == modifiers.size()) {
            MsgUtils.sendToSenderInHolder(ConfigManager.getConfiguredMsg(Constants.Msg.CHECK_NOT_PASS));
            return false;
        }
        // 不为 0 时，解析并设置新属性值
        if (!Constants.STR_ZERO.equals(modifyValue)) {
            AttributeModifier.Operation operation = modifyValue.contains(".")
                    ? AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    : AttributeModifier.Operation.ADD_NUMBER;
            double modifierDoubleValue = Double.parseDouble(modifyValue);
            String modifierName = String.format("%s.%s.%s", attribute.name(), slot, modifyValue);
            AttributeModifier targetModifier = new AttributeModifier(UUID.randomUUID(),
                    modifierName, modifierDoubleValue, operation, slot);
            // 设置属性
            modifiers.add(targetModifier);

            String itemName = originMeta.getDisplayName();
            if (CharSequenceUtil.isEmpty(itemName)) {
                itemName = originMeta.getLocalizedName();
            }
            log.info("{} updated [{}]'s Attribute: {}", PlayerHolder.getSender().getName(), itemName, modifierName);
        }
        attributeModifiers.replaceValues(attribute, modifiers);
        originMeta.setAttributeModifiers(attributeModifiers);
        return true;
    }

    /**
     * 更新道具的指定附魔及附魔等级
     *
     * @param targetItem    目标道具
     * @param targetEnchant 要更新的附魔
     * @param targetLevel   更新后的附魔等级，如果为 0 则删除附魔
     * @return 操作是否成功
     */
    public static boolean updateItemEnchant(ItemStack targetItem, Enchantment targetEnchant, int targetLevel) {
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        // 附魔书时，需要基于 EnchantmentStorageMeta 进行操作
        if (targetItem.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemMeta;
            if (0 == targetLevel) {
                bookMeta.removeStoredEnchant(targetEnchant);
            } else {
                bookMeta.addStoredEnchant(targetEnchant, targetLevel, true);
            }
        } else {
            if (0 == targetLevel) {
                itemMeta.removeEnchant(targetEnchant);
            } else {
                itemMeta.addEnchant(targetEnchant, targetLevel, true);
            }
        }
        targetItem.setItemMeta(itemMeta);
        // 刷新附魔 Lore
        EnchantHelper.updateLore(targetItem);

        String itemName = itemMeta.getDisplayName();
        if (CharSequenceUtil.isEmpty(itemName)) {
            itemName = itemMeta.getLocalizedName();
        }
        log.info("{} updated [{}]'s Enchantment: {}",
                PlayerHolder.getSender().getName(), itemName, targetEnchant.getKey().getKey());
        return true;
    }

    /**
     * 获取某个物品的配置 DTO
     *
     * @param item    目标物品
     * @param dtoType DTO 的 Class 对象
     * @param <D>     DTO 类型
     * @return Optional 包装的该 DTO
     */
    @SuppressWarnings("unchecked")
    public <D> Optional<D> getConfigDtoFromItem(ItemStack item, Class<D> dtoType) {
        String itemName = ItemUtils.getNameFromMeta(item);
        Optional<SwItemConfigDto> itemConfig = ConfigManager.getItemConfig(itemName);
        // 没有配置或没有配置类型不匹配，返回空
        if (!itemConfig.isPresent() || !(dtoType.isInstance(itemConfig.get()))) {
            return Optional.empty();
        }
        return Optional.of((D) itemConfig.get());
    }

    /**
     * 获取触发药水的物品（一定为具有魔法相关附魔的物品，否则返回空）
     *
     * @param potion 被投掷出的药水
     * @return 触发药水投掷的物品
     */
    public static Optional<ItemStack> getPotionTriggerItem(ThrownPotion potion) {
        List<MetadataValue> values = potion.getMetadata("thrown-from");

        if (values.isEmpty()) {
            return Optional.empty();
        }

        Object gotFromMeta = values.get(0).value();
        if (!(gotFromMeta instanceof ItemStack)) {
            return Optional.empty();
        }

        return Optional.of((ItemStack) gotFromMeta);
    }

    // ========== private ========== //

    /**
     * 从 Container 中获取道具名称，预期名称只有一个，存在多个时抛异常
     */
    @Nullable
    private static String getNameFromDataContainer(PersistentDataContainer container) {
        Set<NamespacedKey> keys = container.getKeys();
        String res = null;
        // 遍历 container 中的全部 key，预期只有一个 key
        for (NamespacedKey key : keys) {
            if (null == res) {
                res = key.getKey();
            } else {
                throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
            }
        }
        return res;
    }

    /**
     * 使用物品内标签的内容替换 Lore
     */
    private static List<String> replaceLore(List<String> originLore, SwItemAttachData attachData) {
        Map<String, Object> attachDataMap = BeanUtil.beanToMap(attachData, true, true);
        List<String> toReplace = originLore;
        for (Map.Entry<String, Object> kvEntry : attachDataMap.entrySet()) {
            String key = kvEntry.getKey();
            String val = kvEntry.getValue().toString();
            toReplace = toReplace.stream()
                    .map(lore -> lore.replace("${" + key + "}", val))
                    .collect(Collectors.toList());
        }
        return toReplace;
    }

    /**
     * 从 ItemStack 中获取 Container
     */
    private static Optional<PersistentDataContainer> getDataContainerFromItem(ItemStack itemStack) {
        if (null == itemStack) {
            return Optional.empty();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (null == itemMeta) {
            return Optional.empty();
        }
        return Optional.of(itemMeta.getPersistentDataContainer());
    }

    /**
     * 将指定的名称包装为 container 识别的命名空间 key
     */
    private static NamespacedKey getWarpedKey(String key) {
        return new NamespacedKey(StrengthenWeapon.getInstance(), key);
    }
}
