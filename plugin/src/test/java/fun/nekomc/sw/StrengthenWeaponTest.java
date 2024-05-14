package fun.nekomc.sw;

import cn.hutool.core.io.FileUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.exception.LifeCycleException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.io.File;
import java.lang.reflect.Field;

import static fun.nekomc.sw.common.ConfigManager.ITEMS_CONFIG_FOLDER_NAME;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * create at 2022/8/25 17:31
 *
 * @author Chiru
 */
public class StrengthenWeaponTest {

    @Test
    void testOnLoad() {
        clearWorkspace();

        assertThrows(LifeCycleException.class, StrengthenWeapon::getInstance, "onLoad 前，单例不加载");

        shouldAutoGenerateConfigYmlOnLoad();
        shouldNotChangeConfigYmlIfExistsOnLoad();

        assertNotNull(StrengthenWeapon.getInstance(), "onLoad 后，单例加载");

        clearWorkspace();
    }

    @SneakyThrows
    private void clearWorkspace() {
        String workPath = new File("./").getAbsolutePath() + "/";
        FileUtil.del(workPath + ConfigManager.CONFIG_FILE_NAME);
        FileUtil.del(workPath + ConfigManager.DEFAULT_ITEM_FILE_NAME);
        FileUtil.del(workPath + ITEMS_CONFIG_FOLDER_NAME);
        // 通过反射清理 StrengthenWeapon.instance
        Field instance = StrengthenWeapon.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    private void shouldAutoGenerateConfigYmlOnLoad() {
        StrengthenWeapon plugin = mockPluginCallOnLoadMethod();
        plugin.onLoad();
        verify(plugin, times(1)).saveResource(ConfigManager.CONFIG_FILE_NAME, false);
        verify(plugin, times(1)).saveResource(ConfigManager.DEFAULT_ITEM_FILE_NAME, false);
    }

    private void shouldNotChangeConfigYmlIfExistsOnLoad() {
        StrengthenWeapon plugin = mockPluginCallOnLoadMethod();
        plugin.onLoad();
        // 配置文件存在时，不需要修改。
        verify(plugin, times(0)).saveResource(ConfigManager.CONFIG_FILE_NAME, false);
        verify(plugin, times(0)).saveResource(ConfigManager.DEFAULT_ITEM_FILE_NAME, false);
    }

    private StrengthenWeapon mockPluginCallOnLoadMethod() {
        StrengthenWeapon plugin = mock(StrengthenWeapon.class);

        doAnswer(this::mockSaveResourceMethod).when(plugin).saveResource(ConfigManager.CONFIG_FILE_NAME, false);
        doCallRealMethod().when(plugin).onLoad();
        return plugin;
    }

    private Object mockSaveResourceMethod(InvocationOnMock invocationOnMock) {
        Object pathArg = invocationOnMock.getArgument(0);
        Object replaceFlagArg = invocationOnMock.getArgument(1);

        String filepath = pathArg.toString();
        boolean replaceFlag = (boolean) replaceFlagArg;

        File targetFile = new File(filepath);
        boolean shouldCreate = replaceFlag || !targetFile.exists();
        if (shouldCreate) {
            FileUtil.touch(targetFile);
        }
        return null;
    }
}
