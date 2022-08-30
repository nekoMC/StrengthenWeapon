package fun.nekomc.sw.common;

import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.domain.dto.ConfigYmlDto;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.LifeCycleException;
import fun.nekomc.sw.utils.MsgUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * create at 2022/8/26 15:17
 *
 * @author Chiru
 */
public class ConfigManagerTest {

    private static MockedStatic<MsgUtils> mockedMsgUtils;

    @BeforeEach
    void commonMockBeforeAll() {
        mockedMsgUtils = mockStatic(MsgUtils.class);
        doNothing().when(MsgUtils.class);
    }

    @AfterEach
    void releaseMockAfterAll() {
        mockedMsgUtils.close();
    }

    @Test
    @SneakyThrows
    public void normalTest() {

        assertThrows(LifeCycleException.class, ConfigManager::getConfigYml, "加载前获取配置报错");

        ConfigManager.loadConfig("test_case/normal");
        // 验证 ConfigYmlDto 解析结构正确
        ConfigYmlDto loadedConfigYml = ConfigManager.getConfigYml();
        assertEquals("§c§lTITLE", loadedConfigYml.getStrengthTitle(), "配置项解析");
        assertNull(loadedConfigYml.getRefineTitle(), "缺省的配置项应该为 null");
        // 验证方法正确
        assertEquals("§b单行信息: %s！", ConfigManager.getConfiguredMsg("one_line"), "消息内容解析");
        // 验证 Item 配置解析正确
        assertEquals(3, ConfigManager.getItemConfigList().size());
        assertEquals(3, ConfigManager.getItemNameList().size());

        assertFalse(ConfigManager.getItemConfig("unknown_item").isPresent(), "不存在的配置");
        Optional<SwItemConfigDto> speedBowConfigOpt = ConfigManager.getItemConfig("speed_bow");
        assertTrue(speedBowConfigOpt.isPresent(), "物品配置解析");

        SwItemConfigDto speedBowConfig = speedBowConfigOpt.get();
        assertEquals("BLANK", speedBowConfig.getType(), "物品配置项解析");
    }

    @Test
    public void testEmptyConfig() {
        ConfigManager.loadConfig("test_case/empty");

        assertTrue(ConfigManager.getItemConfigList().isEmpty(), "不存在物品配置时，配置列表为空");
        assertTrue(ConfigManager.getItemNameList().isEmpty(), "不存在物品配置时，名称列表为空");
        assertThrows(LifeCycleException.class, ConfigManager::getConfigYml, "配置文件为空，报错");
    }

    @Test
    public void testConfigPartitionMissingWithError() {
        ConfigManager.loadConfig("test_case/part_missing");

        Optional<SwItemConfigDto> rodConfigOpt1 = ConfigManager.getItemConfig("rod1");
        assertFalse(rodConfigOpt1.isPresent(), "物品未配置 type 时，不解析");

        Optional<SwItemConfigDto> rodConfigOpt2 = ConfigManager.getItemConfig("rod2");
        assertFalse(rodConfigOpt2.isPresent(), "配置存在错误时，均不解析");

        assertTrue(StrUtil.isBlank(ConfigManager.getConfiguredMsg("k")), "残缺的 Map 可正常解析");
        assertFalse(ConfigManager.getConfigYml().isUsingLoreGetter(), "基本类型缺省时，使用默认值");
    }

    @Test
    public void testConfigPartitionMissingWithoutError() {
        ConfigManager.loadConfig("test_case/normal_missing");

        Optional<SwItemConfigDto> rodConfigOpt = ConfigManager.getItemConfig("rod3");
        assertTrue(rodConfigOpt.isPresent(), "物品非 type 缺失时，可正常解析出内容");

        assertNull(ConfigManager.getConfigYml().getMessage(), "config.yml 没有有效值时，值均解析为 null");
    }
}
