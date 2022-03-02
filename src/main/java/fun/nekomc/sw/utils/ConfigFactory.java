package fun.nekomc.sw.utils;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.StrengthenBow;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局配置管理器
 *
 * @author ourange
 */
public class ConfigFactory {

    private StrengthenWeapon plugin;

    private final FileConfiguration configuration = new YamlConfiguration();
    private List<StrengthenItem> strengthenWeapons;
    private List<StrengthenStone> strengthenStones;

    public ConfigFactory() {
    }

    public ConfigFactory(StrengthenWeapon plugin) {
        this.plugin = plugin;
        this.strengthenWeapons = new ArrayList<>();
        initFile();
        initItems();
    }

    /**
     * 初始化配置文件
     */
    public void initFile() {
        try {
            configuration.load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            plugin.consoleMsg("§c§l配置文件不存在，正在生成配置文件....");
            plugin.saveDefaultConfig();
            initFile();
        }
        plugin.consoleMsg("§c§l本地配置文件初始化成功，正在读取配置文件...");
    }

    /**
     * 初始化所有强化物品以及强化石
     */
    public void initItems() {
        StrengthenBow strengthenBow = new StrengthenBow();
        initStrengthenItem(strengthenBow);
        strengthenWeapons.add(strengthenBow);

        strengthenStones = initStrengthenStone();
        plugin.consoleMsg("§6§l配置文件读取成功！");
    }

    /**
     * 初始化强化物品
     *
     * TODO: Bean 方式重构
     */
    private void initStrengthenItem(StrengthenItem item) {
        String configName = item.getConfigName();

        item.setDisplayName(configuration.getString(configName + ".displayName"));
        item.setName(configuration.getString(configName + ".name"));
        item.setLore(configuration.getStringList(configName + ".lore"));
        item.setLevelName(configuration.getString(configName + ".levelName"));
        item.setLevel(configuration.getInt(configName + ".level"));
        item.setMaterial(configuration.getString(configName + ".material"));

    }

    /**
     * 初始化强化石
     */
    private List<StrengthenStone> initStrengthenStone() {
        List<StrengthenStone> stones = new ArrayList<>();


        String configName = StrengthenStone.STONE_NAME;
        List<Integer> chances = configuration.getIntegerList(configName + ".chance");
        // 根据 chance 的配置生成不同强化石
        for (int i = 0; i < chances.size(); i++) {
            StrengthenStone stone = new StrengthenStone();

            stone.setChance(chances.get(i));
            initStrengthenItem(stone);
            stone.setLevel(i + 1);
            stones.add(stone);
        }

        return stones;
    }

    public List<StrengthenItem> getStrengthenWeapons() {
        return strengthenWeapons;
    }

    public void setStrengthenWeapons(List<StrengthenItem> strengthenWeapons) {
        this.strengthenWeapons = strengthenWeapons;
    }

    public List<StrengthenStone> getStrengthenStones() {
        return strengthenStones;
    }

    public void setStrengthenStones(List<StrengthenStone> strengthenStones) {
        this.strengthenStones = strengthenStones;
    }
}
