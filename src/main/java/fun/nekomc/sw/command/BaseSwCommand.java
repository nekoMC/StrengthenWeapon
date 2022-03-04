package fun.nekomc.sw.command;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.utils.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * sw 根指令
 * created: 2022/3/4 02:52
 *
 * @author Chiru
 */
class BaseSwCommand extends SwCommand {

    public BaseSwCommand() {
        super(Constants.BASE_COMMAND, true, "");
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // 预期指令为 `sw`
        Assert.isTrue(ArrayUtil.isEmpty(args), Constants.COMMAND_PARAMETER_SIZE_ERROR_MSG);
        Player player = (Player) sender;
        Inventory inv = StrengthenWeapon.server().createInventory(player, InventoryType.ANVIL, "Strengthen");
        player.openInventory(inv);
        return true;
    }
}
