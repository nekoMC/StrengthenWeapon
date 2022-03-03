package fun.nekomc.sw.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.ConfigurationException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * 全局配置管理器
 * 因作用于全局，且不涉及面向对象拓展，采用静态类实现
 *
 * @author ourange
 */
@UtilityClass
@Slf4j
public class ConfigFactory {

    private final YamlConfiguration yamlConfigFileLoader = new YamlConfiguration();
    private Map<String, SwItemConfigDto> swItemConfigMap;

    /**
     * 加载、重载配置文件。即读取配置文件内容到 loader 中
     */
    public static void loadConfig(String rootPath) {
        Assert.notBlank(rootPath, "配置文件根路径不能为空！");

        // 读取 config.yml，如果不存在则自动生成
        Map<String, Object> configYmlRawMap = loadConfigYml(rootPath);
        // 将 Map 的值转 SwItemConfigDto 对象
        swItemConfigMap = ServiceUtils.convertMapValue(configYmlRawMap,
                raw -> BeanUtil.fillBeanWithMap(((MemorySection) raw).getValues(true), new SwItemConfigDto(), true, true));
        log.info("{}", swItemConfigMap);
        // 在这里拓展道具的解析和其他配置文件的读取
    }

    /**
     * 根据道具名获取指定道具的配置（配置文件中定义的配置）
     *
     * @param itemName 道具配置名，在配置文件中，一个配置项的名称
     * @return Optional 包装的 SwItemConfigDto 对象，可能为空
     */
    public static Optional<SwItemConfigDto> getItemConfig(String itemName) {
        if (CollectionUtil.isEmpty(swItemConfigMap)) {
            return Optional.empty();
        }
        return Optional.ofNullable(swItemConfigMap.get(itemName));
    }

    /**
     * 读取 config.yml 文件为 Map
     */
    private static Map<String, Object> loadConfigYml(String rootPath) {
        // 加载 config.yml 配置文件
        File configYmlFile = new File(rootPath, Constants.CONFIG_FILE_NAME);
        // 配置文件不存在时，保存默认配置文件，保存后，可以被 File 对象立即感知到
        if (!configYmlFile.exists()) {
            MsgUtils.consoleMsg("§c§l配置文件不存在，正在生成配置文件....");
            StrengthenWeapon.getInstance().saveDefaultConfig();
        }
        MsgUtils.consoleMsg("§c§l本地配置文件初始化成功，正在读取配置文件...");
        try {
            yamlConfigFileLoader.load(configYmlFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new ConfigurationException(e);
        }
        return yamlConfigFileLoader.getValues(false);
    }

// 备用，用作参考
//
//    /**
//     * 初始化所有强化物品以及强化石
//     */
//    public void initItems() {
//        StrengthenBow strengthenBow = new StrengthenBow();
//        initStrengthenItem(strengthenBow);
//        strengthenWeapons.add(strengthenBow);
//
//        strengthenStones = initStrengthenStone();
//        plugin.consoleMsg("§6§l配置文件读取成功！");
//    }
//
//    /**
//     * 初始化强化物品
//     * <p>
//     * TODO: Bean 方式重构
//     */
//    private void initStrengthenItem(StrengthenItem item) {
//        String configName = item.getConfigName();
//
//        item.setDisplayName(yamlConfigFileLoader.getString(configName + ".displayName"));
//        item.setName(yamlConfigFileLoader.getString(configName + ".name"));
//        item.setLore(yamlConfigFileLoader.getStringList(configName + ".lore"));
//        item.setLevelName(yamlConfigFileLoader.getString(configName + ".levelName"));
//        item.setLevel(yamlConfigFileLoader.getInt(configName + ".level"));
//        item.setMaterial(yamlConfigFileLoader.getString(configName + ".material"));
//
//    }
//
//    /**
//     * 初始化强化石
//     */
//    private List<StrengthenStone> initStrengthenStone() {
//        List<StrengthenStone> stones = new ArrayList<>();
//
//
//        String configName = StrengthenStone.STONE_NAME;
//        List<Integer> chances = yamlConfigFileLoader.getIntegerList(configName + ".chance");
//        // 根据 chance 的配置生成不同强化石
//        for (int i = 0; i < chances.size(); i++) {
//            StrengthenStone stone = new StrengthenStone();
//
//            stone.setChance(chances.get(i));
//            initStrengthenItem(stone);
//            stone.setLevel(i + 1);
//            stones.add(stone);
//        }
//
//        return stones;
//    }
//
//    public List<StrengthenItem> getStrengthenWeapons() {
//        return strengthenWeapons;
//    }
//
//    public void setStrengthenWeapons(List<StrengthenItem> strengthenWeapons) {
//        this.strengthenWeapons = strengthenWeapons;
//    }
//
//    public List<StrengthenStone> getStrengthenStones() {
//        return strengthenStones;
//    }
//
//    public void setStrengthenStones(List<StrengthenStone> strengthenStones) {
//        this.strengthenStones = strengthenStones;
//    }
}
