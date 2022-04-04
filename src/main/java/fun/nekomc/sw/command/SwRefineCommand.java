package fun.nekomc.sw.command;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.common.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * sw refine player 指令实现
 * 为玩家打开洗炼 GUI
 * created: 2022/3/12 16:50
 *
 * @author Chiru
 */
class SwRefineCommand extends AbstractGuiCommand {

    public SwRefineCommand() {
        super("refine");
    }

    @Override
    protected boolean rua(Player player) {
        Inventory inv = StrengthenWeapon.server()
                .createInventory(player, InventoryType.FURNACE, ConfigManager.getConfigYml().getRefineTitle());
        player.openInventory(inv);
        return true;
    }
}
