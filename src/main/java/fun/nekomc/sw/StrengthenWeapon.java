package fun.nekomc.sw;

import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.command.CommandHandler;
import fun.nekomc.sw.listener.StrengthenMenuListener;
import fun.nekomc.sw.listener.SwBowListener;
import fun.nekomc.sw.utils.ConfigFactory;

import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
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

    private ConfigFactory factory;
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

    /**
     * 获取 Server 实例
     */
    public static Server server() {
        return getInstance().getServer();
    }

    @Override
    public void onEnable() {
        // 初始化配置管理器
        ConfigFactory.loadConfig(this.getDataFolder().getPath());
        // 绑定指令解析器、设置指令 tab 联想
        CommandHandler handler = CommandHandler.getInstance();
        PluginCommand checkedPluginCommand = Objects.requireNonNull(Bukkit.getPluginCommand(Constants.BASE_COMMAND));
        checkedPluginCommand.setExecutor(handler);
        checkedPluginCommand.setTabCompleter(handler);
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
        ConfigFactory.loadConfig(this.getDataFolder().getPath());
//        handler.reloadHandlerMethod(factory.getStrengthenWeapons(), factory.getStrengthenStones());
//        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
//        strengthenMenuListener.setStrengthenWeapons(factory.getStrengthenWeapons());
        /*damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());*/
    }
}
