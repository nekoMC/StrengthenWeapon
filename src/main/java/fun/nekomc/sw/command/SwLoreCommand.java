package fun.nekomc.sw.command;

import fun.nekomc.sw.common.Constants;

/**
 * sw lore 指令实现
 * created: 2022/3/19 17:56
 *
 * @author Chiru
 */
class SwLoreCommand extends SwCommand {

    public SwLoreCommand() {
        super("lore", true, Constants.ADMIN_PERMISSION_POINT);
    }
}
