package fun.nekomc.sw.exception;

import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.CommandSender;

/**
 * created: 2022/3/5 01:09
 *
 * @author Chiru
 */
public class SwCommandException extends SwException {

    /**
     * 指令执行者
     */
    private final CommandSender sender;

    public SwCommandException(CommandSender sender, String msg) {
        super(msg);
        this.sender = sender;
    }

    public void feedback() {
        sender.sendMessage(getMessage());
    }
}
