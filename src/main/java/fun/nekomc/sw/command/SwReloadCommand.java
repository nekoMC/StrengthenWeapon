package fun.nekomc.sw.command;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.CommandSender;

/**
 * created: 2022/3/4 03:07
 *
 * @author Chiru
 */
class SwReloadCommand extends SwCommand {

    public SwReloadCommand(SwCommand... subCmd) {
        super("reload", false, "admin", subCmd);
    }

    @Override
    protected void rua(CommandSender sender, String[] args) {
        StrengthenWeapon.getInstance().reloadConfig();
        MsgUtils.consoleMsg("&c控制台仅允许使用reload指令");
    }
}
