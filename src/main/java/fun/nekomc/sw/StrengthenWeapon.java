package fun.nekomc.sw;

import cn.hutool.core.collection.CollUtil;
import fun.nekomc.sw.domain.dto.EnchantmentConfigDto;
import fun.nekomc.sw.enchant.*;
import fun.nekomc.sw.enchant.helper.EnchantHelper;
import fun.nekomc.sw.enchant.helper.WatcherTriggers;
import fun.nekomc.sw.enchant.magia.PotionEnchantment;
import fun.nekomc.sw.enchant.magia.SplashEnchantment;
import fun.nekomc.sw.exception.LifeCycleException;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.command.CommandHandler;
import fun.nekomc.sw.listener.ItemSecurityListener;
import fun.nekomc.sw.listener.RefineGuiListener;
import fun.nekomc.sw.listener.StrengthGuiListener;
import fun.nekomc.sw.common.ConfigManager;

import fun.nekomc.sw.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author ourange
 */
@Slf4j
public class StrengthenWeapon extends JavaPlugin {

    private static StrengthenWeapon instance = null;

    @Override
    public void onLoad() {
        initConfigFile();
        setInstance(this);
    }

    private void initConfigFile() {
        File configYmlFile = new File(this.getDataFolder(), ConfigManager.CONFIG_FILE_NAME);
        if (!configYmlFile.exists()) {
            saveResource(ConfigManager.CONFIG_FILE_NAME, false);
            saveResource(ConfigManager.DEFAULT_ITEM_FILE_NAME, false);
        }
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
            throw new LifeCycleException("插件正在加载中");
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
        ConfigManager.loadConfig(this.getDataFolder().getPath());

        loadCustomEnchantments();
        loadCommandHandler();
        loadListeners();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigManager.loadConfig(this.getDataFolder().getPath());
    }

    // ========== private ==========

    @SuppressWarnings("unchecked")
    private void loadCustomEnchantments() {
        Class<? extends AbstractSwEnchantment>[] enchantClasses = new Class[]{
                ArrowRainEnchantment.class,
                GiftOfTheSeaEnchantment.class,
                SuckBloodEnchantment.class,
                GetHitHealEnchantment.class,
                SecKillEnchantment.class,
                // Magia 支持
                SplashEnchantment.class,
                PotionEnchantment.Slow.class,
                PotionEnchantment.Blind.class,
                PotionEnchantment.Harm.class,
                PotionEnchantment.Absorption.class,
                PotionEnchantment.ConduitPower.class,
                PotionEnchantment.Confusion.class,
                PotionEnchantment.Resistance.class,
                PotionEnchantment.FireResistance.class,
                PotionEnchantment.Glowing.class,
                PotionEnchantment.Heal.class,
                PotionEnchantment.Hunger.class,
                PotionEnchantment.IncreaseDamage.class,
                PotionEnchantment.Invisibility.class,
                PotionEnchantment.Jump.class,
                PotionEnchantment.Float.class,
                PotionEnchantment.NightVision.class,
                PotionEnchantment.Poison.class,
                PotionEnchantment.Regeneration.class,
                PotionEnchantment.Saturation.class,
                PotionEnchantment.Speed.class,
                PotionEnchantment.WaterBreathing.class,
                PotionEnchantment.WeaknessBreathing.class,
                PotionEnchantment.Wither.class,
        };

        Set<String> customEnchantKeySet = getCustomEnchantKeySet();
        registerConfigCustomEnchantments(customEnchantKeySet, enchantClasses);
        Enchantment.stopAcceptingRegistrations();
        // 注册自定义附魔触发器
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(WatcherTriggers.getInstance(), this);
        log.info("Enchantments registered.");
    }

    private Set<String> getCustomEnchantKeySet() {
        Map<String, EnchantmentConfigDto> customEnchants = ConfigManager.getConfigYml().getEnchants();
        if (CollUtil.isEmpty(customEnchants)) {
            return CollUtil.empty(Set.class);
        }
        return customEnchants.keySet();
    }

    private void registerConfigCustomEnchantments(Set<String> customEnchantKeySet, Class<? extends AbstractSwEnchantment>[] enchantClasses) {
        // 在这里声明全部自定义附魔的键
        for (Class<? extends AbstractSwEnchantment> enchantClass : enchantClasses) {
            try {
                String enchantKey = parseEnchantKeyFromEnchantClass(enchantClass);
                if (customEnchantKeySet.contains(enchantKey)) {
                    AbstractSwEnchantment enchantment = enchantClass.getConstructor().newInstance();
                    EnchantHelper.register(enchantment);
                    continue;
                }
                log.warn("Disabled enchant: {}", enchantKey);
            } catch (ReflectiveOperationException e) {
                log.error("Malformed Enchantment Class: {}, msg: {}", enchantClass, e.getMessage());
            }
        }
    }

    private String parseEnchantKeyFromEnchantClass(Class<? extends AbstractSwEnchantment> enchantClass) throws ReflectiveOperationException {
        Field enchantKeyField = enchantClass.getField(AbstractSwEnchantment.ENCHANT_KEY);
        return (String) enchantKeyField.get(enchantClass);
    }

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
}
