package fun.nekomc.sw.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.ConfigYmlDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.SwException;
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
        Map<String, SwItemConfigDto> newConfigMap = ServiceUtils.convertMapValue(configYmlRawMap, rawMap -> {
            try {
                // 根据配置项的 type 决定将该项解析成哪个实例
                String itemType = Objects.requireNonNull(rawMap.get("type")).toString();
                ItemsTypeEnum itemsTypeEnum = Objects.requireNonNull(ItemsTypeEnum.valueOf(itemType), "无法识别的 type");
                Constructor<? extends SwItemConfigDto> configDtoConstructor =
                        (Constructor<? extends SwItemConfigDto>) itemsTypeEnum.getTypeConfigClass().getConstructor();
                SwItemConfigDto configDtoToFill = configDtoConstructor.newInstance();
                return BeanUtil.fillBeanWithMap(rawMap, configDtoToFill, true, true);
            } catch (Exception e) {
                MsgUtils.consoleMsg("配置文件解析粗错：%s", e.getMessage());
                return null;
            }
        });
        // 如果解析结束后，存在为 null 的配置，说明配置文件有问题，不进行更新
        if (!newConfigMap.containsValue(null)) {
            swItemConfigMap = newConfigMap;
        }
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

    /**
     * 获取 config.yml 加载后对应的 DTO
     *
     * @return ConfigYmlDto 实例
     */
    public static ConfigYmlDto getConfigYml() {
        if (null == configYmlDto) {
            throw new SwException("插件正在加载中");
        }
        return configYmlDto;
    }

}
