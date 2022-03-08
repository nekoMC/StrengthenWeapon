package fun.nekomc.sw.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.ConfigYmlDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.ConfigurationException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class ConfigManager {

    /**
     * Yaml 加载器，此处需要指定加载器，否则报错
     * https://stackoverflow.com/questions/26463078/snakeyaml-class-not-found-exception
     */
    private final Yaml yamlLoader = new Yaml(new CustomClassLoaderConstructor(ConfigManager.class.getClassLoader()));
    private Map<String, SwItemConfigDto> swItemConfigMap;
    private ConfigYmlDto configYmlDto;

    /**
     * 加载、重载配置文件。即读取配置文件内容到 loader 中
     */
    @SuppressWarnings("unchecked")
    public static void loadConfig(String rootPath) {
        Assert.notBlank(rootPath, "配置文件根路径不能为空！");
        MsgUtils.consoleMsg("§c§l正在读取配置文件...");
        // 读取（生成） config.yml
        configYmlDto = loadConfigFile(rootPath, Constants.CONFIG_FILE_NAME, ConfigYmlDto.class);
        // 读取（生成） items.yml
        Map<String, Map<String, ?>> configYmlRawMap = loadConfigFile(rootPath, Constants.ITEMS_CONFIG_FILE_NAME, Map.class);
        // 将 Map 的值转 SwItemConfigDto 对象
        swItemConfigMap = ServiceUtils.convertMapValue(configYmlRawMap,
                raw -> BeanUtil.fillBeanWithMap(raw, new SwItemConfigDto(), true, true));
    }

    /**
     * 获取当前可用的道具列表
     *
     * @return 配置文件中指定的道具列表
     */
    public static List<String> getItemNameList() {
        if (CollUtil.isEmpty(swItemConfigMap)) {
            return ListUtil.empty();
        }
        return new ArrayList<>(swItemConfigMap.keySet());
    }

    /**
     * 根据道具名获取指定道具的配置（配置文件中定义的配置）
     *
     * @param itemName 道具配置名，在配置文件中，一个配置项的名称
     * @return Optional 包装的 SwItemConfigDto 对象，可能为空
     */
    public static Optional<SwItemConfigDto> getItemConfig(String itemName) {
        if (CollUtil.isEmpty(swItemConfigMap)) {
            return Optional.empty();
        }
        return Optional.ofNullable(swItemConfigMap.get(itemName));
    }

    /**
     * 获取 config.yml 中配置的消息
     *
     * @param msgKey 消息键
     * @return 不存在时返回空串
     */
    public static String getConfiguredMsg(String msgKey) {
        return configYmlDto.getMessageByKey(msgKey);
    }

    // ========== private ========== //

    /**
     * 读取 config.yml 文件为 Map
     */
    private static <T> T loadConfigFile(String rootPath, String targetFileName, Class<T> targetClass) {
        // 加载配置文件
        File configYmlFile = new File(rootPath, targetFileName);
        // 配置文件不存在时，保存默认配置文件，保存后，可以被 File 对象立即感知到
        if (!configYmlFile.exists()) {
            MsgUtils.consoleMsg("§c§l配置文件[%s]不存在，正在生成配置文件....", targetFileName);
            StrengthenWeapon.getInstance().saveResource(Constants.ITEMS_CONFIG_FILE_NAME, false);
        }
        try {
            return yamlLoader.loadAs(new FileInputStream(configYmlFile), targetClass);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
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
