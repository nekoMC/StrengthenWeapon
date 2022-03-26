package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
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
        ItemMeta newMeta = ItemUtils.updateAttributeModifierInMeta(itemMeta, slot, attribute, modifyValue);
        targetItem.setItemMeta(newMeta);
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
