package fun.nekomc.sw.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.domain.dto.ConfigYmlDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.LifeCycleException;
import fun.nekomc.sw.utils.MsgUtils;
import fun.nekomc.sw.utils.ServiceUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

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
     * 物品配置文件路径，相对于 config.yml 的路径
     */
    public static final String ITEMS_CONFIG_FOLDER_NAME = "items";
    /**
     * 配置文件中，需要在物品中配置本属性，以指定配置物品的类别
     */
    private static final String ITEM_TYPE_CONFIG_KEY = "type";
    /**
     * 识别的配置文件后缀名
     */
    private static final String CONFIG_FILE_SUFFIX = ".yml";

    /**
     * Yaml 加载器，此处需要指定加载器，否则报错
     * <a href="https://stackoverflow.com/questions/26463078/snakeyaml-class-not-found-exception">参考</a>
     */
    private final Yaml yamlLoader = new Yaml(new CustomClassLoaderConstructor(ConfigManager.class.getClassLoader()));
    private Map<String, SwItemConfigDto> swItemConfigMap;
    private ConfigYmlDto configYmlDto;

    /**
     * 加载、重载配置文件。即读取配置文件内容到 loader 中
     */
    public static void loadConfig(String rootPath) {
        Assert.notBlank(rootPath, "配置文件根路径不能为空！");
        MsgUtils.consoleMsg("§c§l正在读取配置文件...");

        loadConfigYml(rootPath);
        loadConfigFiles(rootPath);
    }

    private void loadConfigYml(String rootPath) {
        configYmlDto = loadConfigFile(rootPath);
    }

    private static ConfigYmlDto loadConfigFile(String filePath) {
        File configYmlFile = new File(filePath, Constants.CONFIG_FILE_NAME);
        try (FileInputStream input = new FileInputStream(configYmlFile)) {
            return yamlLoader.loadAs(input, ConfigYmlDto.class);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    private void loadConfigFiles(String configFolderPath) {
        swItemConfigMap = new HashMap<>(16);
        List<File> configFileList = getConfigFileList(configFolderPath);
        for (File itemsConfigFile : configFileList) {
            // 将 Map 的值转 SwItemConfigDto 对象
            Map<String, Map<String, ?>> configMapLoadedByFile = loadConfigFileAsMap(itemsConfigFile);
            Map<String, SwItemConfigDto> loadedConfigMap = ServiceUtils.convertMapValue(configMapLoadedByFile, ConfigManager::convertRawMapToConfigDto);
            updateConfigMapIfNecessary(loadedConfigMap);
        }
    }

    private static List<File> getConfigFileList(String configFilesFolderPath) {
        File configFolder = new File(configFilesFolderPath, ITEMS_CONFIG_FOLDER_NAME);
        return FileUtil.loopFiles(configFolder, file -> file.getName().endsWith(CONFIG_FILE_SUFFIX));
    }

    /**
     * 读取 yml 配置文件，不校验文件是否存在，不生成文件
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, ?>> loadConfigFileAsMap(File configYmlFile) {
        try (FileInputStream input = new FileInputStream(configYmlFile)) {
            return yamlLoader.loadAs(input, Map.class);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    private static void updateConfigMapIfNecessary(Map<String, SwItemConfigDto> newConfigMap) {
        // 如果解析结束后，存在为 null 的配置，说明配置文件有问题，不进行更新
        if (!newConfigMap.containsValue(null)) {
            swItemConfigMap.putAll(newConfigMap);
        }
    }

    @SuppressWarnings("unchecked")
    private static SwItemConfigDto convertRawMapToConfigDto(Map<String, ?> rawMap) {
        try {
            ItemsTypeEnum itemType = getItemTypeFromConfigMap(rawMap);
            Constructor<? extends SwItemConfigDto> configDtoConstructor =
                    (Constructor<? extends SwItemConfigDto>) itemType.getTypeConfigClass().getConstructor();
            SwItemConfigDto configDtoToFill = configDtoConstructor.newInstance();
            return BeanUtil.fillBeanWithMap(rawMap, configDtoToFill, true, true);
        } catch (Exception e) {
            MsgUtils.consoleMsg("配置文件解析粗错：%s", e.getMessage());
            return null;
        }
    }

    private static ItemsTypeEnum getItemTypeFromConfigMap(Map<String, ?> configMap) {
        String itemType = Objects.requireNonNull(configMap.get(ITEM_TYPE_CONFIG_KEY)).toString();
        return Objects.requireNonNull(ItemsTypeEnum.valueOf(itemType), "无法识别的 type");
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
     * 获取当前可用的道具配置
     *
     * @return 配置文件中指定的道具列表
     */
    public static Collection<SwItemConfigDto> getItemConfigList() {
        if (CollUtil.isEmpty(swItemConfigMap)) {
            return ListUtil.empty();
        }
        return swItemConfigMap.values();
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

    /**
     * 获取 config.yml 加载后对应的 DTO
     *
     * @return ConfigYmlDto 实例
     */
    public static ConfigYmlDto getConfigYml() {
        if (null == configYmlDto) {
            throw new LifeCycleException("配置文件为空，或插件正在加载中");
        }
        return configYmlDto;
    }

}
