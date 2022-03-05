package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ConfigFactory;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * sw give player item 指令实现
 * created: 2022/3/5 00:26
 *
 * @author Chiru
 */
class SwGiveCommand extends SwCommand {

    private static final int REQUIRE_ARG_MIN_SIZE = 2;
    private static final int REQUIRE_ARG_MAX_SIZE = 3;

    public SwGiveCommand() {
        super("give", false, Constants.ADMIN_PERMISSION_POINT);
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        if (actualArgs.length < REQUIRE_ARG_MIN_SIZE || actualArgs.length > REQUIRE_ARG_MAX_SIZE) {
            throw new SwCommandException(sender, "语法错误，格式：/sw give <玩家> <道具> <数量>");
        }
        String playerName = actualArgs[0];
        String itemName = actualArgs[1];
        int amount = actualArgs.length == REQUIRE_ARG_MIN_SIZE ? 1 : Integer.parseInt(args[2]);
        // 获取指定名称道具的配置
        Optional<SwItemConfigDto> itemConfigOptional = ConfigFactory.getItemConfig(itemName);
        if (!itemConfigOptional.isPresent()) {
            throw new SwCommandException(sender, "不存在的道具");
        }
        // 给玩家指定的道具
        SwItemConfigDto itemConfig = itemConfigOptional.get();
        Optional<ItemStack> itemStackOpt = ItemUtils.buildItemByConfig(itemConfig);
        if (!itemStackOpt.isPresent()) {
            throw new SwCommandException(sender, "无法解析物品，请检查配置文件");
        }
        ItemStack itemStack = itemStackOpt.get();
        itemStack.setAmount(amount);
        Player targetPlayer = StrengthenWeapon.server().getPlayer(playerName);
        if (null == targetPlayer) {
            throw new SwCommandException(sender, "玩家不存在");
        }
        givePlayerItem(targetPlayer, itemStack);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        // 输入为 `sw give` 的情况，返回 null 以使用默认补全（用户名）
        if (ArrayUtil.isEmpty(actualArgs)) {
            return null;
        }
        // 输入为 `sw give player` 的情况，返回全部可用的道具名称
        if (actualArgs.length == 1) {
            return ConfigFactory.getItemNameList();
        }
        // 输入为 `sw give player item` 的情况，返回数量的提示
        if (actualArgs.length == REQUIRE_ARG_MIN_SIZE) {
            return ListUtil.of("<数量>");
        }
        return ListUtil.empty();
    }

    // ========== private ========== //

    /**
     * 参考：https://github.com/MiniDay/HamsterAPI/blob/master/src/main/java/cn/hamster3/api/HamsterAPI.java
     * 如果玩家背包满，则在玩家位置生成掉落物
     */
    private void givePlayerItem(Player player, ItemStack itemStack) {
        World world = player.getWorld();
        for (ItemStack dropItem : player.getInventory().addItem(itemStack).values()) {
            world.dropItem(player.getLocation(), dropItem);
        }
    }
}