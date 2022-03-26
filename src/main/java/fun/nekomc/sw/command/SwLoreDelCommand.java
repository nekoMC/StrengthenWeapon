package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
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
        if (!CharSequenceUtil.isNumeric(line)) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        int lineNumber = Integer.parseInt(line);

        return delItemLoreAt(targetItem, lineNumber);
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        if (actualArgLength == 0) {
            return ListUtil.of(ConfigManager.getConfiguredMsg(Constants.Msg.LINE_NUMBER));
        }
        return ListUtil.empty();
    }

    /**
     * 删除道具指定行的 Lore
     *
     * @param targetItem 要操作的道具
     * @param lineNumber 行号
     * @return 操作状态
     */
    public static boolean delItemLoreAt(ItemStack targetItem, int lineNumber) {
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
            return false;
        }
        targetLore.remove(lineNumber - 1);

        itemMeta.setLore(targetLore);
        targetItem.setItemMeta(itemMeta);
        return true;
    }
}
