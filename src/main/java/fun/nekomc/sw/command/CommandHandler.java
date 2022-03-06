package fun.nekomc.sw.command;

import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
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
    private StrengthenServiceImpl strengthService;
    private final SwCommand commandTree;

    private static final CommandHandler INSTANCE = new CommandHandler();

    private CommandHandler() {
        // 构建指令树。根节点：sw
        commandTree = new RootSwCommand().linkSubCmd(
                // 一级节点：sw xx
                new SwReloadCommand(),
                new SwGiveCommand(),
                new SwHelpCommand()
        );
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
            if (!swCommand.preCheck(commandSender, argsRemovedBlank)) {
                return false;
            }
            try {
                return swCommand.rua(commandSender, argsRemovedBlank);
            } catch (SwCommandException e) {
                e.feedback();
                return false;
            }
        }).orElse(false);
    }

    /**
     * 重载handler本地数据
     *
     * @param strengthItems    List<StrengthenItem> 对象
     * @param strengthenStones List<StrengthenStones> 对象
     */
    public void reloadHandlerMethod(List<StrengthenItem> strengthItems, List<StrengthenStone> strengthenStones) {
        strengthService.setStrengthenWeapons(strengthItems);
        strengthService.setStrengthenStones(strengthenStones);
        //strengthService.reloadServiceConfig(strengthExtra);
    }

    public StrengthenServiceImpl getStrengthService() {
        return strengthService;
    }

    public void setStrengthService(StrengthenServiceImpl strengthService) {
        this.strengthService = strengthService;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Optional<SwCommand> cmdNodeDispatchTo = commandTree.getCmdNode(ArrayUtil.removeBlank(args));
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