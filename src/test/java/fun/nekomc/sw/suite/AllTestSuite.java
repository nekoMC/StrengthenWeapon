package fun.nekomc.sw.suite;

import fun.nekomc.sw.StrengthenWeaponTest;
import org.junit.platform.suite.api.*;

/**
 * create at 2022/8/29 20:39
 *
 * @author Chiru
 */
@Suite
@SuiteDisplayName("全部用例")
@SelectClasses({StrengthenWeaponTest.class})
@SelectPackages({"fun.nekomc.sw.common"})
public class AllTestSuite {
}
