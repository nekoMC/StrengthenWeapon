package fun.nekomc.sw.utils;

import fun.nekomc.sw.StrengthenWeapon;
import lombok.experimental.UtilityClass;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * 统一消息处理工具类
 *
 * @author ourange
 */
@UtilityClass
public class MsgUtils {

    private static final ConsoleCommandSender CONSOLE_SENDER = StrengthenWeapon.getInstance().getServer().getConsoleSender();

    public static void sendMsg(Player player, String msg) {
        player.sendMessage(msg);
    }

    public static void consoleMsg(String msg) {
        CONSOLE_SENDER.sendMessage("§a[strengthWeapon] " + msg);
    }
}
