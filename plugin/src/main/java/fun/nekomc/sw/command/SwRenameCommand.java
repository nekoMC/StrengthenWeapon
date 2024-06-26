package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import fun.nekomc.sw.common.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * sw rename 新道具名 指令实现
 * created: 2022/4/3 00:16
 *
 * @author Chiru
 */
class SwRenameCommand extends AbstractMainHandItemCommand {

    public SwRenameCommand() {
        super("rename", Constants.ADMIN_PERMISSION_POINT, 1);
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        String newName = actualArgs[0];
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == newName || null == itemMeta) {
            return false;
        }
        // & 替换为 §
        newName = newName.replace('&', '§');
        itemMeta.setDisplayName(newName);
        targetItem.setItemMeta(itemMeta);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        if (actualArgLength == 0) {
            return ListUtil.of("<name>");
        }
        return ListUtil.empty();
    }
}