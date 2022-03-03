package fun.nekomc.sw.command;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 抽象指令类
 * created: 2022/3/4 00:52
 *
 * @author Chiru
 */
@Slf4j
public abstract class SwCommand {

    /**
     * 当前指令
     */
    private final String nowCmd;

    /**
     * 执行本指令需要的权限点
     */
    private final String permissionPoint;

    /**
     * 是否必须由玩家执行，即不允许控制台、其他实体执行
     */
    private final boolean playerCmd;

    /**
     * 当前指令的子指令
     */
    private final List<SwCommand> subCmd;

    /**
     * 构建指令，以指令 <code> sw give ChiruMori lingo_ame </code> 为例
     *
     * @param cmd             当前指令名，即例子中 sw
     * @param isPlayerCmd     执行者是否必须为玩家
     * @param permissionPoint 执行该指令需要的权限点（控制台不受限）
     * @param subCmd          子指令列表，如例子中的 give 指令
     */
    protected SwCommand(String cmd, boolean isPlayerCmd, String permissionPoint, SwCommand... subCmd) {
        this.nowCmd = cmd;
        this.playerCmd = isPlayerCmd;
        this.permissionPoint = permissionPoint;
        this.subCmd = Arrays.asList(subCmd);
    }

    /**
     * 执行指令
     *
     * @param sender 指令执行方
     * @param args   指令参数，不含指令本身
     */
    public void execute(CommandSender sender, String[] args) {
        if (!preCheck(sender, args)) {
            return;
        }

        // 看看子指令能不能处理。能就撇给子指令，否则自己上
        Optional<SwCommand> runnableSubCommand = getRunnableSubCommand(args);
        if (runnableSubCommand.isPresent()) {
            runnableSubCommand.get().execute(sender, ArrayUtil.sub(args, 1, args.length));
            return;
        }
        rua(sender, args);
    }

    /**
     * 执行指令的实际业务动作，需要在子类进行重写
     *
     * @param sender 指令执行者，如果 playerCmd 为 true，则本参数一定为玩家
     * @param args   指令参数
     */
    protected void rua(CommandSender sender, String[] args) {
        // 没有重写本方法，摆烂
        if (sender instanceof Player) {
            MsgUtils.sendMsg((Player) sender, "Unsupported command");
            return;
        }
        MsgUtils.consoleMsg("Unsupported command");
    }

    // ========== private ========== //

    /**
     * 执行指令前的预校验
     */
    private boolean preCheck(CommandSender sender, String[] args) {
        Assert.notNull(sender, "sender cannot be null");
        Assert.notNull(args, "args cannot be null");
        if (playerCmd && !(sender instanceof Player)) {
            log.warn("该指令只能被玩家执行！");
            return false;
        }
        Player player = (Player) sender;
        // 存在权限要求，且不满足这个要求，返回 false
        if (!StringUtils.isBlank(permissionPoint) &&
                !player.hasPermission(Constants.PERMISSION_NAMESPACE + permissionPoint)) {
            MsgUtils.sendMsg(player, "您没有权限执行该指令！");
            return false;
        }
        return true;
    }

    /**
     * 获取能执行当前指令的子命令
     *
     * @param args 参数列表
     * @return Optional 包装的命令对象，不存在时返回 Optional.empty()
     */
    private Optional<SwCommand> getRunnableSubCommand(String[] args) {
        if (CollectionUtil.isEmpty(subCmd) || ArrayUtil.isEmpty(args)) {
            return Optional.empty();
        }
        String subCmdToMatch = args[0];
        for (SwCommand subCmd : subCmd) {
            if (Objects.equals(subCmd.nowCmd, subCmdToMatch)) {
                return Optional.of(subCmd);
            }
        }
        return Optional.empty();
    }

}
