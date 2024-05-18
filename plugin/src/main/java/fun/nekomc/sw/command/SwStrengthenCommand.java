package fun.nekomc.sw.command;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.common.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * sw strengthen player 指令实现
 * 为玩家打开强化 GUI
 * created: 2022/3/12 16:49
 *
 * @author Chiru
 */
class SwStrengthenCommand extends AbstractGuiCommand {

    public SwStrengthenCommand() {
        super("strengthen");
    }

    @Override
    protected boolean rua(Player player) {
        Inventory inv = StrengthenWeapon.server()
                .createInventory(player, InventoryType.valueOf(ConfigManager.getConfigYml().getStrengthType()),
                        ConfigManager.getConfigYml().getStrengthTitle());
        player.openInventory(inv);
        return true;
    }
}
