package fun.nekomc.sw.command;

import fun.nekomc.sw.utils.ConfigFactory;
import fun.nekomc.sw.utils.MsgUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

/**
 * sw help 指令实现
 * created: 2022/3/6 01:16
 *
 * @author Chiru
 */
public class SwHelpCommand extends SwCommand {

    protected SwHelpCommand() {
        super("help", false, StringUtils.EMPTY);
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // Help 指令不会校验参数数量
        String helpMsg = ConfigFactory.getConfiguredMsg("help_msg");
        MsgUtils.returnMsgToSender(sender, helpMsg);
        return true;
    }
}