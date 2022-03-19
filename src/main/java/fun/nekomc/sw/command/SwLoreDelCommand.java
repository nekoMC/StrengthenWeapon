package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * sw lore del 行号 指令实现
 * created: 2022/3/19 17:56
 *
 * @author Chiru
 */
class SwLoreDelCommand extends AbstractMainHandItemCommand {

    public SwLoreDelCommand() {
        super("del", Constants.ADMIN_PERMISSION_POINT, 1);
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 校验行号、内容
        String line = actualArgs[0];
        if (!StrUtil.isNumeric(line)) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg("grammar_error"));
        }
        int lineNumber = Integer.parseInt(line);
        // 进行 Lore 操作
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        List<String> targetLore = itemMeta.getLore();
        if (null == targetLore) {
            return false;
        }
        // 空行占位
        if (targetLore.size() < lineNumber) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg("cannot_operate"));
        }
        targetLore.remove(lineNumber - 1);

        itemMeta.setLore(targetLore);
        targetItem.setItemMeta(itemMeta);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        if (actualArgLength == 0) {
            return ListUtil.of(ConfigManager.getConfiguredMsg("line_number"));
        }
        return ListUtil.empty();
    }
}
