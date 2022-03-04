package fun.nekomc.sw.command;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 命令行指令解释器
 *
 * @author ourange
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    private StrengthenServiceImpl strengthService;
    private final SwCommand commandTree;

    private static final CommandHandler INSTANCE = new CommandHandler();

/*private static StrengthServiceImpl strengthService;
    private static final int DEFAULT_STACK = 64;
    public static final String ADMIN_PERMISSION = "strength.admin";*/

    private CommandHandler() {
        // 构建指令树

        // 根节点：sw
        commandTree = new BaseSwCommand();
        // 一级节点：sw xx
        SwCommand swReload = new SwReloadCommand();
        SwCommand swGivePlayerItem = new SwGiveCommand();
        commandTree.linkSubCmd(swReload, swGivePlayerItem);
        // TODO: 写几个配置，调试指令
    }

    public static CommandHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String mainCommand, String[] commandArray) {
        Optional<SwCommand> cmdNodeDispatchTo = commandTree.getCmdNode(commandSender, commandArray);
        return cmdNodeDispatchTo.map(swCommand -> {
            try {
                return swCommand.rua(commandSender, commandArray);
            } catch (SwCommandException e) {
                e.feedback();
            }
            return false;
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

    /**
     * 子命令联想
     */
    private final String[] subUserCommands = {"sw_bow", "sw_stone"};

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }
        if (args.length == 0) {
            return Arrays.asList(subUserCommands);
        }
        return Arrays.stream(subUserCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }

}
