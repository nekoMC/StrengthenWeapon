package fun.nekomc.sw.utils;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.exception.SwException;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * created: 2022/4/2 02:04
 *
 * @author Chiru
 */
@UtilityClass
public class PlayerHolder {

    private static final ThreadLocal<CommandSender> SENDER_HOLDER = new ThreadLocal<>();

    /**
     * 将 Player 设置到 Holder
     */
    public static void setPlayer(Player player) {
        SENDER_HOLDER.set(player);
    }

    /**
     * 从 Holder 中取 Player，取不到则返回 ConsoleSender
     */
    public static CommandSender getSender() {
        CommandSender commandSender = SENDER_HOLDER.get();
        if (null == commandSender) {
            return StrengthenWeapon.server().getConsoleSender();
        }
        return commandSender;
    }

    /**
     * 从 Holder 中取 Player，取不到就报错
     */
    public static Player ensurePlayer() {
        CommandSender commandSender = SENDER_HOLDER.get();
        if (!(commandSender instanceof Player)) {
            throw new SwException("no player was set");
        }
        return (Player) commandSender;
    }

    /**
     * 释放 Holder
     */
    public static void release() {
        SENDER_HOLDER.remove();
    }
}
