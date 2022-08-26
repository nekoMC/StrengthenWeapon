package fun.nekomc.sw;

import fun.nekomc.sw.common.Constants;
import jdk.jfr.internal.test.WhiteBox;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

/**
 * create at 2022/8/25 17:31
 *
 * @author Chiru
 */
public class StrengthenWeaponTest {

    public static final String TEST_TMP_FILE_FOLDER = "./test_tmp/";

    private final File testWorkspace = new File(TEST_TMP_FILE_FOLDER);
    private final File configYmlFile = new File(testWorkspace, Constants.CONFIG_FILE_NAME);

    @Test
    public void testGenerateConfigFileOnLoad() {
        shouldAutoGenerateConfigYmlOnLoad();
        shouldNotChangeConfigYmlIfExistsOnLoad();
    }

    private void shouldAutoGenerateConfigYmlOnLoad() {
        StrengthenWeapon plugin = new StrengthenWeapon();
        setPluginWorkspaceToTestTmpFolder(plugin);
        plugin.onLoad();
    }

    private void shouldNotChangeConfigYmlIfExistsOnLoad() {
        StrengthenWeapon plugin = new StrengthenWeapon();
        setPluginWorkspaceToTestTmpFolder(plugin);
    }

    private void clearWorkspace() {
        if (testWorkspace.exists()) {
            boolean deleteFail = !testWorkspace.delete();
            if (deleteFail) {
                // TODO FAIL
            }
        }
    }

    private void setPluginWorkspaceToTestTmpFolder(JavaPlugin plugin) {
        Whitebox.setInternalState(plugin, "dataFolder", testWorkspace);
    }
}
