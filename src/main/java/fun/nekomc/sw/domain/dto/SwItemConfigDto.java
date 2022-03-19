package fun.nekomc.sw.domain.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.exception.ConfigurationException;
import lombok.Data;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SW 道具配置
 * created: 2022/3/3 02:44
 *
 * @author Chiru
 */
@Data
public abstract class SwItemConfigDto implements Serializable {

    /**
     * 道具类型
     *
     * @see fun.nekomc.sw.domain.enumeration.ItemsTypeEnum
     */
    private String type;

    /**
     * 程序内部使用的道具名
     */
    private String name;

    /**
     * 道具名
     */
    private String displayName;

    /**
     * 道具的解释文本
     */
    private List<String> lore;

    /**
     * 道具材质
     */
    private String material;

    /**
     * 道具是否无法破坏
     */
    private boolean unbreakable;

    /**
     * 附加属性配置
     */
    private Map<String, Map<String, Double>> attributes;

    /**
     * 附魔列表
     */
    private List<String> enchantments;

    /**
     * 将 slotAndAttributeMap 属性解析为 Bukkit 能识别的 Map
     *
     * @return Multimap<Attribute, AttributeModifier> 对象
     */
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        Multimap<Attribute, AttributeModifier> multimapRes = ArrayListMultimap.create();
        if (CollUtil.isEmpty(attributes)) {
            return multimapRes;
        }
        // 构建附加属性
        attributes.forEach((slot, attributeMap) -> {
            EquipmentSlot slotType = EquipmentSlot.valueOf(slot);
            attributeMap.forEach((attr, val) -> {
                // 解析形如 Movement_Speed: 0.3、ARMOR: 3 的属性配置
                String attrName = attr.toUpperCase();
                Attribute attribute = Attribute.valueOf(attrName);
                String modifierName = String.format("%s.%s.%s", attrName, slot, val);
                AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
                // 数值位于 (-1.0, 1.0) 范围内，乘系数
                if (Math.abs(val) < 1) {
                    operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                }
                AttributeModifier nowModifier = new AttributeModifier(UUID.randomUUID(),
                        modifierName, val, operation, slotType);
                multimapRes.put(attribute, nowModifier);
            });
        });
        return multimapRes;
    }

    /**
     * 将 enchantments 属性解析为 Bukkit 能识别的附魔 Map
     *
     * @return Map<Enchantment, Integer> 对象
     */
    public Map<Enchantment, Integer> getEnchantMap() {
        if (CollUtil.isEmpty(enchantments)) {
            return MapUtil.empty();
        }
        HashMap<Enchantment, Integer> enchantMap = new HashMap<>(enchantments.size());
        for (String enchantment : enchantments) {
            // 解析附魔配置
            String[] enchantNameAndLevel = enchantment.split(":");
            String enchantmentName = enchantNameAndLevel[0].toLowerCase();
            Enchantment targetEnchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName));
            if (null == targetEnchant) {
                // 无法解析时，尝试作为自定义附魔进行解析
                targetEnchant = Enchantment.getByKey(new NamespacedKey(StrengthenWeapon.getInstance(), enchantmentName));
            }
            if (null == targetEnchant) {
                throw new ConfigurationException("无法识别的附魔：" + enchantment);
            }
            int lvl = 0;
            if (enchantNameAndLevel.length == 2) {
                lvl = Integer.parseInt(enchantNameAndLevel[1]);
            }
            enchantMap.put(targetEnchant, lvl);
        }
        return enchantMap;
    }
}
