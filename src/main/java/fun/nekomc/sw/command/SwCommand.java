package fun.nekomc.sw.command;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import fun.nekomc.sw.utils.ServiceUtils;
import lombok.Getter;
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
@Getter
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
    private List<SwCommand> subCmd;

    /**
     * 父级指令
     */
    private SwCommand parentCmd;

    /**
     * 当前指令在指令树中的深度
     */
    private int depth;

    /**
     * 构建指令
     *
     * @param cmd             当前指令名
     * @param isPlayerCmd     执行者是否必须为玩家
     * @param permissionPoint 执行该指令需要的权限点（控制台不受限）
     */
    protected SwCommand(String cmd, boolean isPlayerCmd, String permissionPoint) {
        this.nowCmd = cmd;
        this.playerCmd = isPlayerCmd;
        this.permissionPoint = permissionPoint;
        this.parentCmd = null;
        this.depth = 0;
    }

    /**
     * 为当前指令设置子指令，如果不涉及指令树动态变化，本方法只应该在建树时调一次
     *
     * @param subCmdArray 子指令
     */
    public void linkSubCmd(SwCommand... subCmdArray) {
        for (SwCommand swCommand : subCmdArray) {
            swCommand.parentCmd = this;
            swCommand.depth = this.depth + 1;
        }
        this.subCmd = Arrays.asList(subCmdArray);
    }

    /**
     * 获取能处理当前指令的指令节点
     *
     * @param sender 指令执行方
     * @param args   指令参数，不含指令本身
     * @return 能处理当前指令的指令节点，比如入参 args 为 sw reload，则返回 reload 指令节点
     */
    public Optional<SwCommand> getCmdNode(CommandSender sender, String[] args) {
        if (!preCheck(sender, args)) {
            return Optional.empty();
        }

        // 看看子指令能不能处理。能就撇给子指令，否则自己上
        Optional<SwCommand> runnableSubCommand = getRunnableSubCommand(args);
        if (runnableSubCommand.isPresent()) {
            return runnableSubCommand.get().getCmdNode(sender, args);
        }
        return Optional.of(this);
    }

    /**
     * 执行指令的实际业务动作，需要在子类进行重写
     *
     * @param sender 指令执行者，如果 playerCmd 为 true，则本参数一定为玩家
     * @param args   指令参数
     * @return 指令执行是否成功
     * @throws SwCommandException 当指令执行异常时抛出，回执消息统一由外部处理
     */
    public boolean rua(CommandSender sender, final String[] args) {
        // 没有重写本方法，摆烂给执行者
        MsgUtils.returnMsgToSender(sender, "Unsupported command");
        return false;
    }

    /**
     * 获取指令 Tab 提示信息列表，通常需要重写
     * 默认实现返回子指令列表，如果没有子指令返回 null
     *
     * @param sender 指令发送方
     * @param args   参数
     * @return 提示信息列表
     */
    public List<String> hint(CommandSender sender, final String[] args) {
        if (CollectionUtil.isEmpty(subCmd)) {
            return null;
        }
        return ServiceUtils.convertList(subCmd, SwCommand::getNowCmd);
    }

    /**
     * 将参数列表根据当前指令深度进行裁剪，如 sw give ChiruMori dog 指令
     * 当子指令 give 节点调用本方法时，会得到 [ChiruMori, dog]
     *
     * @param args 原始参数列表，如在上例中应为 [give, ChiruMori, dog]
     * @return 裁剪后的子参数
     */
    protected String[] ignoreDontCareArgs(final String[] args) {
        if (ArrayUtil.isEmpty(args)) {
            return null == args ? new String[0] : args;
        }
        return ArrayUtil.sub(args, depth, args.length);
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
        if (CollUtil.isEmpty(subCmd) || ArrayUtil.isEmpty(args)) {
            return Optional.empty();
        }
        String subCmdToMatch = args[depth];
        for (SwCommand subCmdCandidate : subCmd) {
            if (Objects.equals(subCmdCandidate.nowCmd, subCmdToMatch)) {
                return Optional.of(subCmdCandidate);
            }
        }
        return Optional.empty();
    }

}
