package fun.nekomc.sw.command;

import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 命令行指令解释器
 *
 * @author ourange
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    private final SwCommand commandTree;

    private static final CommandHandler INSTANCE = new CommandHandler();

    private CommandHandler() {
        // 构建指令树。根节点：sw
        commandTree = new RootSwCommand().linkSubCmd(
                // 一级节点：sw xx
                new SwStrengthenCommand(),
                new SwRefineCommand(),
                new SwReloadCommand(),
                new SwGiveCommand(),
                new SwEnchantCommand(),
                new SwAttributeCommand(),
                new SwLoreCommand().linkSubCmd(
                        // 二级节点：sw lore xx
                        new SwLoreSetCommand(),
                        new SwLoreDelCommand()),
                new SwHelpCommand());
        // 构建指令树结构
        commandTree.buildCmdTree();
    }

    public static CommandHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String mainCommand, String[] commandArray) {
        String[] argsRemovedBlank = ArrayUtil.removeBlank(commandArray);
        Optional<SwCommand> cmdNodeDispatchTo = commandTree.getCmdNode(argsRemovedBlank);
        return cmdNodeDispatchTo.map(swCommand -> {
            // 指令预校验，满足注册的条件时，才有可能被分配到相关节点执行
            if (!swCommand.preCheck(commandSender, argsRemovedBlank)) {
                return false;
            }
            try {
                return swCommand.rua(commandSender, argsRemovedBlank);
            } catch (SwCommandException e) {
                e.feedback();
            } catch (Exception e) {
                MsgUtils.returnMsgToSender(commandSender, ConfigManager.getConfiguredMsg(Constants.Msg.COMMAND_ERROR), e.getMessage());
            }
            return false;
        }).orElse(false);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String[] removedLast = ArrayUtil.remove(args, args.length - 1);
        Optional<SwCommand> cmdNodeDispatchTo = commandTree.getCmdNode(ArrayUtil.removeBlank(removedLast));
        return cmdNodeDispatchTo.map(swCommand -> {
            try {
                return swCommand.hint(sender, args);
            } catch (SwCommandException e) {
                e.feedback();
                return null;
            }
        }).orElse(null);
    }

}
