package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import fun.nekomc.sw.utils.Constants;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * sw attribute slot attribute 值 指令实现
 * created: 2022/3/19 17:11
 *
 * @author Chiru
 */
class SwAttributeCommand extends AbstractMainHandItemCommand {

    public SwAttributeCommand() {
        super("attribute", Constants.ADMIN_PERMISSION_POINT, 3);
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 生效的 Slot
        EquipmentSlot slot = EquipmentSlot.valueOf(actualArgs[0]);
        // 要操作的 Attribute
        Attribute attribute = Attribute.valueOf(actualArgs[1]);
        // 变化的属性值
        String modifyValue = actualArgs[2];

        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        Multimap<Attribute, AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers();
        attributeModifiers = null == attributeModifiers
                ? LinkedListMultimap.create()
                : LinkedListMultimap.create(attributeModifiers);
        // 移除旧属性
        List<AttributeModifier> modifiers = attributeModifiers.get(attribute).stream()
                .filter(modifier -> modifier.getSlot() != slot)
                .collect(Collectors.toList());
        // 不为 0 时，解析并设置新属性值
        if (!Constants.STR_ZERO.equals(modifyValue)) {
            AttributeModifier.Operation operation = modifyValue.contains(".")
                    ? AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    : AttributeModifier.Operation.ADD_NUMBER;
            double modifierDoubleValue = Double.parseDouble(modifyValue);
            String modifierName = String.format("%s.%s.%s", actualArgs[1], slot, modifyValue);
            AttributeModifier targetModifier = new AttributeModifier(UUID.randomUUID(),
                    modifierName, modifierDoubleValue, operation, slot);
            // 设置属性
            modifiers.add(targetModifier);
        }
        attributeModifiers.replaceValues(attribute, modifiers);
        itemMeta.setAttributeModifiers(attributeModifiers);
        targetItem.setItemMeta(itemMeta);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        switch (actualArgLength) {
            case 0:
                return Arrays.stream(EquipmentSlot.values())
                        .map(EquipmentSlot::name)
                        .collect(Collectors.toList());
            case 1:
                return Arrays.stream(Attribute.values())
                        .map(Attribute::name)
                        .collect(Collectors.toList());
            case 2:
                return ListUtil.of(Constants.STR_ZERO, "0.2", "2", "2.2");
            default:
                return ListUtil.empty();
        }
    }
}
