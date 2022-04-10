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

import java.util.ArrayList;
import java.util.List;

/**
 * sw lore set 行号 内容 指令实现
 * created: 2022/3/19 17:56
 *
 * @author Chiru
 */
class SwLoreSetCommand extends AbstractMainHandItemCommand {

    public SwLoreSetCommand() {
        super("set", Constants.ADMIN_PERMISSION_POINT, 2);
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 校验行号、内容
        String line = actualArgs[0];
        String newLore = actualArgs[1];
        if (!CharSequenceUtil.isNumeric(line) || CharSequenceUtil.isBlank(newLore)) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        int lineNumber = Integer.parseInt(line);
        if (lineNumber <= 0) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        newLore = newLore.replace("[]", " ");
        return setItemLoreAt(targetItem, lineNumber, newLore);
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        switch (actualArgLength) {
            case 0:
                return ListUtil.of(ConfigManager.getConfiguredMsg(Constants.Msg.LINE_NUMBER));
            case 1:
                return ListUtil.of("lore");
            default:
                return ListUtil.empty();
        }
    }

    /**
     * 设置道具指定行的 Lore 显示内容
     *
     * @param targetItem 目标道具
     * @param lineNumber 行号
     * @param content    显示内容
     * @return 操作状态
     */
    public static boolean setItemLoreAt(ItemStack targetItem, int lineNumber, String content) {
        // 进行 Lore 操作
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        List<String> targetLore = itemMeta.getLore();
        if (null == targetLore) {
            targetLore = new ArrayList<>(lineNumber);
        }
        // 空行占位
        while (targetLore.size() < lineNumber) {
            targetLore.add(CharSequenceUtil.EMPTY);
        }
        // & 替换为 §
        content = content.replace('&', '§');
        targetLore.set(lineNumber - 1, content);

        itemMeta.setLore(targetLore);
        targetItem.setItemMeta(itemMeta);
        return true;
    }
}
