package fun.nekomc.sw.utils;

import org.bukkit.entity.Player;

/**
 * @author ourange
 */
public class PlayerMsgUtils {
    public static void sendMsg(Player player, String msg) {
        player.sendMessage(msg);
    }
}
