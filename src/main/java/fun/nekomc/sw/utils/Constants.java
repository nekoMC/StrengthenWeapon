package fun.nekomc.sw.utils;

import lombok.experimental.UtilityClass;

/**
 * 全局静态常量
 * created: 2022/3/1 02:17
 *
 * @author Chiru
 */
@UtilityClass
public class Constants {

    public static final String CONFIG_FILE_NAME = "config.yml";

    public static final String ITEMS_CONFIG_FILE_NAME = "items.yml";

    /**
     * 权限节点命名空间
     */
    public static final String PERMISSION_NAMESPACE = "sw.";

    /**
     * 根指令
     */
    public static final String BASE_COMMAND = "sw";

    /**
     * 指令实际参数与预期参数数量不匹配时的报错信息，报这个说明有 BUG
     */
    public static final String COMMAND_PARAMETER_SIZE_ERROR_MSG = "COMMAND ERROR!";

    /**
     * 管理权限节点
     */
    public static final String ADMIN_PERMISSION_POINT = "Admin";

    /**
     * 提示信息的前缀
     */
    public static final String MSG_PREFIX = "§a[strengthWeapon] ";

    /**
     * 附魔在 Lore 显示的默认前缀
     */
    public static final String ENCHANT_DEFAULT_PREFIX = "§7";
}
