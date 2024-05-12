package fun.nekomc.sw;

import cn.hutool.core.collection.CollUtil;
import fun.nekomc.sw.domain.dto.SkillConfigDto;
import fun.nekomc.sw.skill.*;
import fun.nekomc.sw.skill.helper.SkillHelper;
import fun.nekomc.sw.skill.helper.WatcherTriggers;
import fun.nekomc.sw.skill.magia.PotionSkill;
import fun.nekomc.sw.skill.magia.WitcherSkill;
import fun.nekomc.sw.exception.LifeCycleException;
import fun.nekomc.sw.exception.SwException;
import fun.nekomc.sw.command.CommandHandler;
import fun.nekomc.sw.listener.ItemSecurityListener;
import fun.nekomc.sw.listener.RefineGuiListener;
import fun.nekomc.sw.listener.StrengthGuiListener;
import fun.nekomc.sw.common.ConfigManager;

import fun.nekomc.sw.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

        loadSkills();
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
    private void loadSkills() {
        Class<? extends AbstractSwSkill>[] skills = new Class[]{
                ArrowRainSkill.class,
                GiftOfTheSeaSkill.class,
                SuckBloodSkill.class,
                GetHitHealSkill.class,
                SecKillSkill.class,
                // Magia 支持
                WitcherSkill.class,
                PotionSkill.Slow.class,
                PotionSkill.Blind.class,
                PotionSkill.Harm.class,
                PotionSkill.Absorption.class,
                PotionSkill.ConduitPower.class,
                PotionSkill.Confusion.class,
                PotionSkill.Resistance.class,
                PotionSkill.FireResistance.class,
                PotionSkill.Glowing.class,
                PotionSkill.Heal.class,
                PotionSkill.Hunger.class,
                PotionSkill.IncreaseDamage.class,
                PotionSkill.Invisibility.class,
                PotionSkill.Jump.class,
                PotionSkill.Float.class,
                PotionSkill.NightVision.class,
                PotionSkill.Poison.class,
                PotionSkill.Regeneration.class,
                PotionSkill.Saturation.class,
                PotionSkill.Speed.class,
                PotionSkill.WaterBreathing.class,
                PotionSkill.WeaknessBreathing.class,
                PotionSkill.Wither.class,
        };

        Set<String> customSkillKeySet = getCustomSkillKeySet();
        registerSkills(customSkillKeySet, skills);
        // 注册自定义附魔触发器
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(WatcherTriggers.getInstance(), this);
        log.info("Enchantments registered.");
    }

    private Set<String> getCustomSkillKeySet() {
        Map<String, SkillConfigDto> skillConfigs = ConfigManager.getConfigYml().getSkills();
        if (CollUtil.isEmpty(skillConfigs)) {
            return CollUtil.empty(Set.class);
        }
        return skillConfigs.keySet();
    }

    private void registerSkills(Set<String> customSkillKeySet, Class<? extends AbstractSwSkill>[] skillClasses) {
        // 在这里声明全部自定义附魔的键
        for (Class<? extends AbstractSwSkill> skillClass : skillClasses) {
            try {
                AbstractSwSkill skill = skillClass.getConstructor().newInstance();
                String skillKey = skill.getConfigKey();
                if (customSkillKeySet.contains(skillKey)) {
                    SkillHelper.register(skill);
                    continue;
                }
                log.warn("Disabled skill: {}", skillKey);
            } catch (ReflectiveOperationException e) {
                log.error("Malformed Skill Class: {}, msg: {}", skillClass, e.getMessage());
            }
        }
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

    /**
     * 将指定的名称包装为 container 识别的命名空间 key
     */
    public static NamespacedKey getWarpedKey(String key) {
        return new NamespacedKey(StrengthenWeapon.getInstance(), key);
    }
}
