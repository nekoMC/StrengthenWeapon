package fun.nekomc.sw;

import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.command.CommandHandler;
import fun.nekomc.sw.listener.StrengthenMenuListener;
import fun.nekomc.sw.listener.SwBowListener;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.utils.ConfigManager;

import fun.nekomc.sw.utils.Constants;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author ourange
 */
public class StrengthenWeapon extends JavaPlugin {

    private SwBowListener swBowListener;
    private StrengthenMenuListener strengthenMenuListener;

    private static StrengthenWeapon instance = null;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        // Sonar 不推荐在成员方法中修改静态变量
        setInstance(this);
    }

    private static void setInstance(StrengthenWeapon instance) {
        StrengthenWeapon.instance = instance;
    }

    /**
     * 获取 plugin 实例
     *
     * @return StrengthenWeapon 对象
     * @throws SwException 如果插件尚未加载完全时抛出
     */
    public static StrengthenWeapon getInstance() {
        if (null == instance) {
            throw new SwException("插件正在加载中");
        }
        return instance;
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
        ConfigManager.loadConfig(this.getDataFolder().getPath());
        // 绑定指令解析器、设置指令 tab 联想
        CommandHandler handler = CommandHandler.getInstance();
        PluginCommand checkedPluginCommand = Objects.requireNonNull(Bukkit.getPluginCommand(Constants.BASE_COMMAND));
        checkedPluginCommand.setExecutor(handler);
        checkedPluginCommand.setTabCompleter(handler);
        //初始化并绑定监听器
        swBowListener = new SwBowListener();
//        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
        getServer().getPluginManager().registerEvents(swBowListener, this);
        strengthenMenuListener = new StrengthenMenuListener();
        strengthenMenuListener.setPlugin(this);
        strengthenMenuListener.setService(new StrengthenServiceImpl());
        getServer().getPluginManager().registerEvents(strengthenMenuListener, this);
        /*damageListener = new OnDamageListener();
        damageListener.setPlugin(this);
        damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());
        getServer().getPluginManager().registerEvents(damageListener,this);*/
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigManager.loadConfig(this.getDataFolder().getPath());
//        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
        /*damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());*/
    }
}
