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
            throw new SwCommandException(sender, ConfigManager.getConfiguredMsg("grammar_error"));
        }
        String playerName = actualArgs[0];
        String itemName = actualArgs[1];
        int amount = actualArgs.length == REQUIRE_ARG_MIN_SIZE ? 1 : Integer.parseInt(actualArgs[2]);
        // 获取指定名称道具的配置
        Optional<SwItemConfigDto> itemConfigOptional = ConfigManager.getItemConfig(itemName);
        if (itemConfigOptional.isEmpty()) {
            throw new SwCommandException(sender, ConfigManager.getConfiguredMsg("unknown_item"));
        }
        // 给玩家指定的道具
        ItemStack itemStack;
        SwItemConfigDto itemConfig = itemConfigOptional.get();
        try {
            // 根据配置文件构建物品
            Optional<ItemStack> itemStackOpt = ItemUtils.buildItemByConfig(itemConfig);
            if (itemStackOpt.isEmpty()) {
                throw new SwCommandException(sender, "config_error");
            }
            itemStack = itemStackOpt.get();
        } catch (IllegalArgumentException | ConfigurationException e) {
            throw new SwCommandException(sender, e.getMessage());
        }
        itemStack.setAmount(amount);
        Player targetPlayer = StrengthenWeapon.server().getPlayer(playerName);
        if (null == targetPlayer) {
            throw new SwCommandException(sender, "unknown_player");
        }
        PlayerBagUtils.givePlayerItem(targetPlayer, itemStack);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        // 输入为 `sw give` 的情况，返回 null 以使用默认补全（用户名）
        return switch (actualArgLength) {
            case 0 -> null;
            case 1 -> ConfigManager.getItemNameList();
            case 2 -> ListUtil.of("1", "64");
            default -> ListUtil.empty();
        };
    }
}
