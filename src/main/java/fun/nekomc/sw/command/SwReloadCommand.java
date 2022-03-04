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

    public SwReloadCommand() {
        super("reload", false, "admin");
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        StrengthenWeapon.getInstance().reloadConfig();
        // TODO: 这里怎么回事，瞎TM改
        MsgUtils.consoleMsg("&c控制台仅允许使用reload指令");
        return false;
    }
}
