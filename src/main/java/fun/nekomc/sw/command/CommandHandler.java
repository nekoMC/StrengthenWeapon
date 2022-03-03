package fun.nekomc.sw.command;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.utils.ConfigFactory;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 命令行指令解释器
 *
 * @author ourange
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    private StrengthenWeapon plugin;
    private ConfigFactory factory;
    private StrengthenServiceImpl strengthService;
    private final SwCommand commandTree;

    private static final CommandHandler INSTANCE = new CommandHandler();

/*private static StrengthServiceImpl strengthService;
    private static final int DEFAULT_STACK = 64;
    public static final String ADMIN_PERMISSION = "strength.admin";*/

    private CommandHandler() {
        // 初始化指令树
        commandTree = new BaseSwCommand(
                // TODO: 拓展更多指令
                new SwReloadCommand()
        );
    }

    public static CommandHandler getInstance() {
        return INSTANCE;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String mainCommand, String[] commandArray) {
        commandTree.execute(commandSender, commandArray);
        return true;
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

    private void giveSwItem(Player player, ItemStack itemStack) {
        // TODO: 确认背包满时是否存在问题
        PlayerInventory inventory = player.getInventory();
        //int amount = itemStack.getAmount();

        int firstEmpty = inventory.firstEmpty();
        inventory.setItem(firstEmpty, itemStack);
    }

    public ConfigFactory getFactory() {
        return factory;
    }

    public void setFactory(ConfigFactory factory) {
        this.factory = factory;
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
