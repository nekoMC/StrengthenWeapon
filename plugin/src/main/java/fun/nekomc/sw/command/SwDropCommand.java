package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * sw drop player x y z item 指令实现
 * created: 2022/4/20 00:33
 *
 * @author Chiru
 */
class SwDropCommand extends SwCommand {

    private static final int REQUIRE_ARG_MIN_SIZE = 5;
    private static final int REQUIRE_ARG_MAX_SIZE = 6;

    public SwDropCommand() {
        super("drop", false, Constants.ADMIN_PERMISSION_POINT);
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        if (actualArgs.length < REQUIRE_ARG_MIN_SIZE || actualArgs.length > REQUIRE_ARG_MAX_SIZE) {
            throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        try {
            String triggerName = actualArgs[0];
            Entity targetEntity;
            if (triggerName.split("-").length != 5) {
                targetEntity = StrengthenWeapon.server().getPlayer(triggerName);
            } else {
                UUID entityUuid = UUID.fromString(triggerName);
                targetEntity = StrengthenWeapon.server().getEntity(entityUuid);
            }
            if (null == targetEntity) {
                throw new SwCommandException(sender, Constants.Msg.UNKNOWN_ENTITY);
            }
            World targetWorld = targetEntity.getWorld();
            double x = Double.parseDouble(actualArgs[1]);
            double y = Double.parseDouble(actualArgs[2]);
            double z = Double.parseDouble(actualArgs[3]);
            String itemName = actualArgs[4];
            Optional<SwItemConfigDto> itemConfig = ConfigManager.getItemConfig(itemName);
            if (itemConfig.isEmpty()) {
                throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.UNKNOWN_ITEM));
            }
            Optional<ItemStack> itemStackOpt = ItemUtils.buildItemByConfig(itemConfig.get());
            if (itemStackOpt.isEmpty()) {
                throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
            }
            int amount = actualArgs.length == REQUIRE_ARG_MIN_SIZE ? 1 : Integer.parseInt(actualArgs[5]);
            ItemStack itemStack = itemStackOpt.get();
            itemStack.setAmount(amount);
            targetWorld.dropItem(new Location(targetWorld, x, y, z), itemStack);
        } catch (NumberFormatException e) {
            throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        // 输入为 `sw drop` 的情况，返回 null 以使用默认补全（用户名）
        return switch (actualArgLength) {
            case 0 -> null;
            case 1 -> ListUtil.of("x");
            case 2 -> ListUtil.of("y");
            case 3 -> ListUtil.of("z");
            case 4 -> ConfigManager.getItemNameList();
            case 5 -> ListUtil.of("1", "64");
            default -> ListUtil.empty();
        };
    }
}
