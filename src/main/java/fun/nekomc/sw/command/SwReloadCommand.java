package fun.nekomc.sw.command;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.CommandSender;

/**
 * sw reload 指令实现
 * created: 2022/3/4 03:07
 *
 * @author Chiru
 */
class SwReloadCommand extends SwCommand {

    public SwReloadCommand() {
        super("reload", false, Constants.ADMIN_PERMISSION_POINT);
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        Assert.isTrue(args.length == 1, Constants.COMMAND_PARAMETER_SIZE_ERROR_MSG);
        StrengthenWeapon.getInstance().reloadConfig();
        MsgUtils.returnMsgToSender(sender, "§c§l§a[§bconfig.yml§a]§c§l文件已重新读取");
        return true;
    }
}
