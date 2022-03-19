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
        if (!StrUtil.isNumeric(line) || StrUtil.isBlank(newLore)) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg("grammar_error"));
        }
        int lineNumber = Integer.parseInt(line);
        if (lineNumber <= 0) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg("grammar_error"));
        }
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
        while(targetLore.size() < lineNumber) {
            targetLore.add(StrUtil.EMPTY);
        }
        // & 替换为 §
        newLore = newLore.replace('&', '§');
        targetLore.set(lineNumber - 1, newLore);

        itemMeta.setLore(targetLore);
        targetItem.setItemMeta(itemMeta);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        switch (actualArgLength) {
            case 0:
                return ListUtil.of(ConfigManager.getConfiguredMsg("line_number"));
            case 1:
                return ListUtil.of("lore");
            default:
                return ListUtil.empty();
        }
    }
}
