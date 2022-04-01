package fun.nekomc.sw.service;

import fun.nekomc.sw.domain.dto.SwStrengthenStoneConfigDto;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 强化、洗炼，业务逻辑层
 *
 * @author ourange
 */
public interface StrengthenService {

    ItemStack strengthen(Player player, ItemStack itemStack, SwStrengthenStoneConfigDto swStrengthenStoneConfigDto, boolean isAdmin);

}
