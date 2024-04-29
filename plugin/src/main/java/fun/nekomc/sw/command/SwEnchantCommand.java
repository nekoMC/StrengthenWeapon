package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.utils.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
        enchantNames = Arrays.stream(Enchantment.values())
                .map(enchant -> enchant.getKey().getKey())
                .collect(Collectors.toList());
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 查找要附魔的附魔对象
        String enchantName = actualArgs[0];
        String level = actualArgs[1];

        Optional<Enchantment> targetEnchantOpt = SkillHelper.getByName(enchantName);
        // 校验附魔、等级有效
        if (!CharSequenceUtil.isNumeric(level) || !targetEnchantOpt.isPresent()) {
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
        switch (actualArgLength) {
            case 0:
                return enchantNames;
            case 1:
                return ListUtil.of(Constants.STR_ZERO, "1", "2");
            default:
                return ListUtil.empty();
        }
    }
}
