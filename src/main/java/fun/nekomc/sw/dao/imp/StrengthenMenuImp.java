package fun.nekomc.sw.dao.imp;

import fun.nekomc.sw.dao.StrengthenMenuDAO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class StrengthenMenuImp implements StrengthenMenuDAO {
    @Override
    public void strengthenMenu(Player player) {
        Bukkit.createInventory(player, InventoryType.ANVIL, "Strengthen");

    }
}
