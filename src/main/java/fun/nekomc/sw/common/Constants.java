package fun.nekomc.sw.common;

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
    public static final String ADMIN_PERMISSION_POINT = "admin";

    /**
     * 提示信息的前缀
     */
    public static final String MSG_PREFIX = "§a[strengthWeapon] ";

    /***/
    public static final String STR_ZERO = "0";

    @UtilityClass
    public static class Msg {
        public static final String CONFIG_ERROR = "config_error";
        public static final String GRAMMAR_ERROR = "grammar_error";
        public static final String NO_AUTH = "no_auth";
        public static final String NOT_PLAYER = "not_player";
        public static final String HELP_MSG = "help_msg";
        public static final String ADMIN_HELP_MSG = "admin_help_msg";
        public static final String LINE_NUMBER = "line_number";
        public static final String UNKNOWN_ITEM = "unknown_item";
        public static final String COMMAND_ERROR = "command_error";
        public static final String RELOADED = "reloaded";
        public static final String PROMOTE_CHANGE = "promote_change";
        public static final String PROMOTE_RESET = "promote_reset";
        public static final String PROMOTE_FAIL = "promote_fail";
        public static final String CHECK_NOT_PASS = "check_not_pass";
        public static final String GOT_SEA_GIFT = "got_sea_gift";
    }
}
