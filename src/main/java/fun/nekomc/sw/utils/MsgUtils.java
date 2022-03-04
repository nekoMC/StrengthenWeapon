package fun.nekomc.sw.utils;

import fun.nekomc.sw.StrengthenWeapon;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * 统一消息处理工具类
 *
 * @author ourange
 */
@UtilityClass
public class MsgUtils {

    private static final ConsoleCommandSender CONSOLE_SENDER = StrengthenWeapon.server().getConsoleSender();

    /**
     * 给玩家发送消息
     *
     * @param player 玩家
     * @param msg    消息内容
     */
    public static void sendMsg(Player player, String msg) {
        player.sendMessage(msg);
    }

    /**
     * 向控制台输出消息
     *
     * @param msg 消息内容
     */
    public static void consoleMsg(String msg) {
        CONSOLE_SENDER.sendMessage("§a[strengthWeapon] " + msg);
    }

    /**
     * 根据执行者决定如何发送消息
     *
     * @param sender 指令执行者
     * @param msg    消息内容
     */
    public static void returnMsgToSender(CommandSender sender, String msg) {
        if (sender instanceof Player) {
            sendMsg((Player) sender, msg);
        } else {
            consoleMsg(msg);
        }
    }
}
