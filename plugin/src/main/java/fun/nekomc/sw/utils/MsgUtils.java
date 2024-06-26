package fun.nekomc.sw.utils;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.common.Constants;
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

    private static ConsoleCommandSender consoleSender;

    /**
     * 给玩家发送消息
     *
     * @param player 玩家
     * @param msg    消息内容
     * @param args   附加参数（用于格式化消息内容）
     */
    public static void sendMsg(Player player, String msg, Object... args) {
        player.sendMessage(String.format(msg, args));
    }

    /**
     * 向控制台输出消息
     *
     * @param msg  消息内容
     * @param args 附加参数（用于格式化消息内容）
     */
    public static void consoleMsg(String msg, Object... args) {
        if (null == consoleSender) {
            consoleSender = StrengthenWeapon.server().getConsoleSender();
        }
        String templateToFormat = Constants.MSG_PREFIX + msg;
        String formattedMsg = String.format(templateToFormat, args);
        consoleSender.sendMessage(formattedMsg);
    }

    /**
     * 根据执行者决定如何发送消息
     *
     * @param sender 指令执行者
     * @param msg    消息内容
     * @param args   附加参数（用于格式化消息内容）
     */
    public static void returnMsgToSender(CommandSender sender, String msg, Object... args) {
        if (sender instanceof Player) {
            sendMsg((Player) sender, msg, args);
        } else {
            consoleMsg(msg, args);
        }
    }

    public static void sendToSenderInHolder(String configuredMsg, Object... args) {
        CommandSender sender = PlayerHolder.getSender();
        returnMsgToSender(sender, configuredMsg, args);
    }

    public static void broadcase(String msg, Object... args) {
        StrengthenWeapon.server().broadcastMessage(String.format(msg, args));
    }
}
