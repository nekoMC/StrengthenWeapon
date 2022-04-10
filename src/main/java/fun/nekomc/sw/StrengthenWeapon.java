package fun.nekomc.sw;

import cn.hutool.core.collection.CollUtil;
import fun.nekomc.sw.domain.dto.EnchantmentConfigDto;
import fun.nekomc.sw.enchant.AbstractSwEnchantment;
import fun.nekomc.sw.enchant.ArrowRainEnchantment;
import fun.nekomc.sw.enchant.GiftOfTheSeaEnchantment;
import fun.nekomc.sw.enchant.SuckBloodEnchantment;
import fun.nekomc.sw.enchant.helper.EnchantHelper;
import fun.nekomc.sw.enchant.helper.WatcherTriggers;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.command.CommandHandler;
import fun.nekomc.sw.listener.ItemSecurityListener;
import fun.nekomc.sw.listener.RefineGuiListener;
import fun.nekomc.sw.listener.StrengthGuiListener;
import fun.nekomc.sw.common.ConfigManager;

import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author ourange
 */
public class StrengthenWeapon extends JavaPlugin {

    private static StrengthenWeapon instance = null;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        // Sonar 不推荐在成员方法中直接修改静态变量
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
    @SuppressWarnings("unchecked")
    public void onEnable() {
        // 配置管理器
        ConfigManager.loadConfig(this.getDataFolder().getPath());
        // 自定义附魔
        Class<? extends AbstractSwEnchantment>[] classes = new Class[]{
                ArrowRainEnchantment.class,
                GiftOfTheSeaEnchantment.class,
                SuckBloodEnchantment.class,
        };
        loadCustomEnchantments(classes);
        // 指令解析器
        loadCommandHandler();
        // 其他事件监听器
        loadListeners();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigManager.loadConfig(this.getDataFolder().getPath());
    }

    // ========== private ==========

    /**
     * 绑定指令解析器、设置指令 tab 联想
     */
    private void loadCommandHandler() {
        CommandHandler handler = CommandHandler.getInstance();
        PluginCommand checkedPluginCommand = Objects.requireNonNull(Bukkit.getPluginCommand(Constants.BASE_COMMAND));
        checkedPluginCommand.setExecutor(handler);
        checkedPluginCommand.setTabCompleter(handler);
    }

    /**
     * 初始化并绑定事件监听器
     */
    private void loadListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        // 容器
        pluginManager.registerEvents(new StrengthGuiListener(), this);
        pluginManager.registerEvents(new RefineGuiListener(), this);
        // 防自定义附魔
        pluginManager.registerEvents(new ItemSecurityListener(), this);
    }

    /**
     * 初始化自定义附魔
     */
    private void loadCustomEnchantments(Class<? extends AbstractSwEnchantment>[] enchantClasses) {
        Map<String, EnchantmentConfigDto> customEnchants = ConfigManager.getConfigYml().getEnchants();
        if (CollUtil.isEmpty(customEnchants)) {
            return;
        }
        PluginManager pluginManager = getServer().getPluginManager();
        Set<String> configEnchants = customEnchants.keySet();
        // 在这里声明全部自定义附魔的键
        for (Class<? extends AbstractSwEnchantment> enchantClass : enchantClasses) {
            try {
                String enchantKey = (String) enchantClass.getField("ENCHANT_KEY").get(enchantClass);
                if (!configEnchants.contains(enchantKey)) {
                    MsgUtils.consoleMsg("Unsupported enchant key: {}", enchantKey);
                    return;
                }

                AbstractSwEnchantment enchantment = enchantClass.getConstructor().newInstance();
                EnchantHelper.register(enchantment);
            } catch (ReflectiveOperationException e) {
                MsgUtils.consoleMsg("Malformed Enchantment Class: {}", enchantClass);
            }
        }
        Enchantment.stopAcceptingRegistrations();
        // 注册自定义附魔触发器
        pluginManager.registerEvents(WatcherTriggers.getInstance(), this);
        MsgUtils.consoleMsg("Enchantments registered.");
    }
}
