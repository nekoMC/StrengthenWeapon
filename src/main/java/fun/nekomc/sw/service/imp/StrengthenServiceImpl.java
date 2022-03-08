package fun.nekomc.sw.service.imp;

import fun.nekomc.sw.dao.imp.StrengthenDAOImpl;
import fun.nekomc.sw.domain.StrengthenStone;
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

    public StrengthenServiceImpl() {
        dao = new StrengthenDAOImpl();
    }

    @Override
    public ItemStack giveStrengthBow(int amount) {

        return dao.giveStrengthenBow(1);
    }

    @Override
    public ItemStack giveStrengthenStone(int amount, int level) {
        return dao.giveStrengthenStone(amount, level);
    }


    @Override
    public ItemStack strengthen(Player player, ItemStack itemStack, StrengthenStone strengthenStone, boolean isAdmin) {
        boolean isSafe = false;
        boolean isSuccess = strengthenSuccess(strengthenStone.getChance());
        return dao.strengthen(player, itemStack, isSuccess || isAdmin, isSafe);
    }

    @Override
    public ItemStack strengthenSuccessResult(ItemStack swWeapon, ItemStack swStone) {
        return dao.strengthenSuccessResult(swWeapon, swStone);
    }

    private boolean strengthenSuccess(int chance) {
        return chance > random.nextInt(101);
    }
}
