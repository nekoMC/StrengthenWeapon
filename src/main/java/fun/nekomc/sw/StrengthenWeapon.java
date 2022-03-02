package fun.nekomc.sw;

import fun.nekomc.sw.domain.enumeration.WeaponsIndex;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.handler.CommandHandler;
import fun.nekomc.sw.listener.StrengthenMenuListener;
import fun.nekomc.sw.listener.SwBowListener;
import fun.nekomc.sw.utils.ConfigFactory;

import fun.nekomc.sw.utils.MsgUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ourange
 */
public class StrengthenWeapon extends JavaPlugin {
    private static final String DEFAULT_COMMAND = "sw";

    private ConfigFactory factory;
    private CommandHandler handler;
    private SwBowListener swBowListener;
    private StrengthenMenuListener strengthenMenuListener;

    private static StrengthenWeapon INSTANCE = null;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        INSTANCE = this;
    }

    /**
     * 获取 plugin 实例
     *
     * @return StrengthenWeapon 对象
     * @throws SwException 如果插件尚未加载完全时抛出
     */
    public static StrengthenWeapon getInstance() {
        if (null == INSTANCE) {
            throw new SwException("插件正在加载中");
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ConfigFactory.loadConfig(this.getDataFolder().getPath());
        //初始化并绑定handler
        handler = new CommandHandler(this, factory);
        Objects.requireNonNull(Bukkit.getPluginCommand(DEFAULT_COMMAND)).setExecutor(handler);
        handler.setFactory(factory);
        //设置tab联想
        Objects.requireNonNull(Bukkit.getPluginCommand(DEFAULT_COMMAND)).setTabCompleter(this);
        //初始化并绑定监听器
        // TODO: 分类整理各 Listener 以拓展更多内容
//        swBowListener = new SwBowListener();
//        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
//        getServer().getPluginManager().registerEvents(swBowListener, this);
//        strengthenMenuListener = new StrengthenMenuListener();
//        strengthenMenuListener.setPlugin(this);
//        strengthenMenuListener.setStrengthenWeapons(factory.getStrengthenWeapons());
//        strengthenMenuListener.setStrengthenStones(factory.getStrengthenStones());
//        strengthenMenuListener.setService(handler.getStrengthService());
//        getServer().getPluginManager().registerEvents(strengthenMenuListener, this);
        /*damageListener = new OnDamageListener();
        damageListener.setPlugin(this);
        damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());
        getServer().getPluginManager().registerEvents(damageListener,this);*/
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        MsgUtils.consoleMsg("§c§l正在重新读取§a[§bconfig.yml§a]§c§l文件...");
//        factory.initFile();
//        factory.initItems();
//        handler.reloadHandlerMethod(factory.getStrengthenWeapons(), factory.getStrengthenStones());
//        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
//        strengthenMenuListener.setStrengthenWeapons(factory.getStrengthenWeapons());
        /*damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());*/
    }

    /**
     * 子命令联想
     */
    private final String[] subUserCommands = {"sw_bow", "sw_stone"};

    @Override
    public @Nullable
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }
        if (args.length == 0) {
            return Arrays.asList(subUserCommands);
        }
        return Arrays.stream(subUserCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
