package fun.nekomc.sw.command;

import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.CommandSender;

/**
 * sw help 指令实现
 * created: 2022/3/6 01:16
 *
 * @author Chiru
 */
public class SwHelpCommand extends SwCommand {

    protected SwHelpCommand() {
        super("help", false, StrUtil.EMPTY);
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // Help 指令不会校验参数数量
        String helpMsg = ConfigManager.getConfiguredMsg(Constants.Msg.HELP_MSG);
        if (sender.hasPermission(Constants.PERMISSION_NAMESPACE + Constants.ADMIN_PERMISSION_POINT)) {
            helpMsg += ConfigManager.getConfiguredMsg(Constants.Msg.ADMIN_HELP_MSG);
        }
        MsgUtils.returnMsgToSender(sender, helpMsg);
        return true;
    }
}
