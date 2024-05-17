package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.skill.AbstractSwSkill;
import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * sw skill id lvl 指令实现
 * created: 2024/5/12 15:42
 *
 * @author Chiru
 */
class SwSkillCommand extends AbstractMainHandItemCommand {

    private final List<String> skillNames;

    public SwSkillCommand() {
        super("skill", Constants.ADMIN_PERMISSION_POINT, 2);
        skillNames = new ArrayList<>(SkillHelper.SKILL_MAP.keySet());
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 查找要附魔的附魔对象
        String skillKey = actualArgs[0];
        String level = actualArgs[1];
        Optional<AbstractSwSkill> targetSkill = SkillHelper.getByKey(skillKey);
        // 校验附魔、等级有效
        if (!CharSequenceUtil.isNumeric(level) || targetSkill.isEmpty()) {
            return false;
        }
        int targetLevel = Integer.parseInt(level);

        return SkillHelper.updateItemSkill(targetItem, targetSkill.get(), targetLevel);
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        return switch (actualArgLength) {
            case 0 -> skillNames;
            case 1 -> ListUtil.of(Constants.STR_ZERO, "1", "2");
            default -> ListUtil.empty();
        };
    }
}
