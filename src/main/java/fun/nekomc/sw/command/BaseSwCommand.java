package fun.nekomc.sw.command;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.utils.Constants;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * created: 2022/3/4 02:52
 *
 * @author Chiru
 */
class BaseSwCommand extends SwCommand {

    public BaseSwCommand(SwCommand... subCmd) {
        super(Constants.BASE_COMMAND, true, "", subCmd);
    }

    @Override
    protected void rua(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            Inventory inv = StrengthenWeapon.getInstance().getServer().createInventory(player, InventoryType.ANVIL, "Strengthen");
            player.openInventory(inv);
        } else {
            // 传递子指令时，给玩家指定的道具
            String sonCommand = args[0];
            ItemStack stack = null;
            int amount = 0;
            switch (sonCommand) {
                // TODO: 恢复此处代码功能
//                case "sw_bow":
//                    stack = strengthService.giveStrengthBow(1);
//                    giveSwItem(player, stack);
//                    break;
//                case "sw_stone":
//                    try {
//                        int level = Integer.parseInt(args[1]);
//                        if (level > 0 && level <= strengthService.getStrengthenStones().size()) {
//                            stack = strengthService.giveStrengthenStone(1, level);
//                            giveSwItem(player, stack);
//                        }
//                    } catch (Exception e) {
//                        MsgUtils.sendMsg(player, "§b请输入正确的指令:/sw sw_stone [1~" + strengthService.getStrengthenStones().size() + "]");
//                    }
//                    break;
                default:
                    break;
            }
        }
    }
}
