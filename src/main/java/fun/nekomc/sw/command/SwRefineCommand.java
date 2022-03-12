package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.PlayerBagUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

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
                .createInventory(player, InventoryType.ENCHANTING, ConfigManager.getConfigYml().getRefineTitle());
        player.openInventory(inv);
        return true;
    }
}
