package fun.nekomc.sw.command;

import fun.nekomc.sw.utils.Constants;
import org.apache.commons.lang.StringUtils;

/**
 * sw 根指令
 * created: 2022/3/4 02:52
 *
 * @author Chiru
 */
class RootSwCommand extends SwCommand {

    public RootSwCommand() {
        super(Constants.BASE_COMMAND, true, StringUtils.EMPTY);
    }
}
