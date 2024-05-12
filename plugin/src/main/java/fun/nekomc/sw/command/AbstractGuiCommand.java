package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * created: 2022/3/12 17:28
 *
 * @author Chiru
 */
public abstract class AbstractGuiCommand extends SwCommand {

    private static final int REQUIRE_ARG_MAX_SIZE = 1;

    protected AbstractGuiCommand(String command) {
        super(command, false, StrUtil.EMPTY);
    }

    /**
     * 为指定的玩家打开容器 GUI
     *
     * @param player 目标玩家，已通过权限校验
     * @return 指令执行状态
     */
    protected abstract boolean rua(Player player);

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // 获取指令参数
        String[] actualArgs = ignoreDontCareArgs(args);
        if (actualArgs.length > REQUIRE_ARG_MAX_SIZE) {
            throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        Player targetPlayer;
        // 指定了玩家时，需要管理员权限
        if (ArrayUtil.isNotEmpty(actualArgs)) {
            if (!sender.hasPermission(Constants.PERMISSION_NAMESPACE + Constants.ADMIN_PERMISSION_POINT)) {
                throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.NO_AUTH));
            }
            targetPlayer = StrengthenWeapon.server().getPlayer(actualArgs[0]);
        } else {
            // 未指定玩家时，为当前玩家打开 GUI
            if (!(sender instanceof Player)) {
                throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.NOT_PLAYER));
            }
            targetPlayer = (Player) sender;
        }
        return rua(targetPlayer);
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        String[] actualArgs = ignoreDontCareArgs(args);
        return actualArgs.length == 0 ? null : ListUtil.empty();
    }
}
