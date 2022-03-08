package fun.nekomc.sw.exception;

import org.bukkit.command.CommandSender;

/**
 * created: 2022/3/5 01:09
 *
 * @author Chiru
 */
public class SwCommandException extends SwException {

    /**
     * 指令执行者，Throwable 接口要求成员必须为 Serializable 或 transient（不参与序列化）
     */
    private final transient CommandSender sender;

    public SwCommandException(CommandSender sender, String msg) {
        super(msg);
        this.sender = sender;
    }

    public void feedback() {
        sender.sendMessage(getMessage());
    }
}
