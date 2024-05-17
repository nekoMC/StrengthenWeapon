package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.utils.*;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * sw enchant id lvl 指令实现
 * created: 2022/3/19 17:11
 *
 * @author Chiru
 */
class SwEnchantCommand extends AbstractMainHandItemCommand {

    private final List<String> enchantNames;

    public SwEnchantCommand() {
        super("enchant", Constants.ADMIN_PERMISSION_POINT, 2);
        enchantNames = Registry.ENCHANTMENT.stream()
                .map(enchant -> enchant.getKey().getKey())
                .collect(Collectors.toList());
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 查找要附魔的附魔对象
        String enchantName = actualArgs[0];
        String level = actualArgs[1];
        //Optional.ofNullable(Enchantment.getByKey(NamespacedKey.minecraft(enchantName))
        Optional<Enchantment> targetEnchantOpt = ItemUtils.getEnchantByName(enchantName);
        // 校验附魔、等级有效
        if (!CharSequenceUtil.isNumeric(level) || targetEnchantOpt.isEmpty()) {
            return false;
        }
        int targetLevel = Integer.parseInt(level);

        return ItemUtils.updateItemEnchant(targetItem, targetEnchantOpt.get(), targetLevel);
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        return switch (actualArgLength) {
            case 0 -> enchantNames;
            case 1 -> ListUtil.of(Constants.STR_ZERO, "1", "2");
            default -> ListUtil.empty();
        };
    }
}
