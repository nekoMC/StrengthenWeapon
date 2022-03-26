package fun.nekomc.sw.service.imp;

import fun.nekomc.sw.dao.imp.StrengthenDAOImpl;
import fun.nekomc.sw.domain.dto.SwStrengthenStoneConfigDto;
import fun.nekomc.sw.service.StrengthenService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * @author ourange
 */
public class StrengthenServiceImpl implements StrengthenService {
    private final StrengthenDAOImpl dao;
    private final Random random = new Random();

    private StrengthenServiceImpl() {
        dao = new StrengthenDAOImpl();
    }

    public static StrengthenService getInstance() {
        return null;
    }

    @Override
    public ItemStack strengthen(Player player, ItemStack itemStack, SwStrengthenStoneConfigDto swStrengthenStoneConfigDto, boolean isAdmin) {
        boolean isSafe = false;
        boolean isSuccess = strengthenSuccess(swStrengthenStoneConfigDto.getChance());
        return dao.strengthen(player, itemStack, isSuccess || isAdmin, isSafe);
    }

    private boolean strengthenSuccess(int chance) {
        return chance > random.nextInt(101);
    }
}
