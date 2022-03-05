package fun.nekomc.sw.command;

import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ConfigFactory;
import fun.nekomc.sw.utils.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

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

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // 预期指令为 `sw`
        if (ArrayUtil.isNotEmpty(args)) {
            throw new SwCommandException(sender, ConfigFactory.getConfiguredMsg("grammar_error"));
        }
        Player player = (Player) sender;
        Inventory inv = StrengthenWeapon.server().createInventory(player, InventoryType.ANVIL, "Strengthen");
        player.openInventory(inv);
        return true;
    }
}
