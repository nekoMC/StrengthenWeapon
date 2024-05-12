package fun.nekomc.sw.command;

import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.common.Constants;

/**
 * sw 根指令
 * created: 2022/3/4 02:52
 *
 * @author Chiru
 */
class RootSwCommand extends SwCommand {

    public RootSwCommand() {
        super(Constants.BASE_COMMAND, true, StrUtil.EMPTY);
    }
}
