package fun.nekomc.sw.handler;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.utils.ConfigFactory;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.List;

/**
 * 命令行指令解释器
 *
 * @author ourange
 */
public class CommandHandler implements CommandExecutor {
    private StrengthenWeapon plugin;
    private ConfigFactory factory;
    private StrengthenServiceImpl strengthService;


/*private static StrengthServiceImpl strengthService;
    private static final int DEFAULT_STACK = 64;
    public static final String ADMIN_PERMISSION = "strength.admin";*/

    public CommandHandler(StrengthenWeapon plugin, ConfigFactory factory){
//        this.plugin = plugin;
//        this.factory = factory;
//        strengthService = new StrengthenServiceImpl(plugin, factory.getStrengthenWeapons(), factory.getStrengthenStones());
//        strengthService.setPlugin(plugin);
        /*strengthService.setPlugin(plugin);*/
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String mainCommand, String[] commandArray) {
        if (!"CONSOLE".equals(commandSender.getName())) {
            Player player = ((Player) commandSender).getPlayer();
            assert player != null;
            Assert.notNull(player, "player cannot be null!");
            // 未传递参数时，为玩家打开菜单
            if (commandArray.length < 1) {
                //strengthService.infoMenu(player);

                /*Merchant merchant = Bukkit.createMerchant("test merchant");
                List<MerchantRecipe> merchantRecipes = new ArrayList<>();
                ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
                MerchantRecipe recipe = new MerchantRecipe(itemStack, 100000);
                recipe.setExperienceReward(false);
                recipe.addIngredient(itemStack);
                recipe.addIngredient(new ItemStack(Material.DIAMOND));
                recipe.addIngredient(new ItemStack(Material.PAPER));

                merchantRecipes.add(recipe);

                merchant.setRecipes(merchantRecipes);

                player.sendMessage(merchant.getClass().toString());
                player.openMerchant(merchant, true);*/

                Inventory inv = plugin.getServer().createInventory(player, InventoryType.ANVIL, "Strengthen");
                player.openInventory(inv);
            } else {
                // 传递子指令时，给玩家指定的道具
                String sonCommand = commandArray[0];
                ItemStack stack = null;
                int amount = 0;
                switch (sonCommand) {
                    case "sw_bow":
                        stack = strengthService.giveStrengthBow(1);
                        giveSwItem(player, stack);
                        break;
                    case "sw_stone":
                        try {
                            int level = Integer.parseInt(commandArray[1]);
                            if (level > 0 && level <= strengthService.getStrengthenStones().size()) {
                                stack = strengthService.giveStrengthenStone(1, level);
                                giveSwItem(player, stack);
                            }
                        }
                        catch (Exception e) {
                            MsgUtils.sendMsg(player, "§b请输入正确的指令:/sw sw_stone [1~" + strengthService.getStrengthenStones().size() + "]");
                        }
                        break;
                    default:
                        break;
                }
            }
            return true;
        } else {
            if (commandArray.length > 0) {
                if ("reload".equals(commandArray[0])) {
                    plugin.reloadConfig();
                    return true;
                }
            }
            MsgUtils.consoleMsg("&c控制台仅允许使用reload指令");
        }
        return true;
    }

    /**
     * 重载handler本地数据
     * @param strengthItems List<StrengthenItem> 对象
     * @param strengthenStones List<StrengthenStones> 对象
     */
    public void reloadHandlerMethod(List<StrengthenItem> strengthItems, List<StrengthenStone> strengthenStones){
        strengthService.setStrengthenWeapons(strengthItems);
        strengthService.setStrengthenStones(strengthenStones);
        //strengthService.reloadServiceConfig(strengthExtra);
    }

    private void giveSwItem(Player player, ItemStack itemStack) {
        // TODO: 确认背包满时是否存在问题
        PlayerInventory inventory = player.getInventory();
        //int amount = itemStack.getAmount();

        int firstEmpty = inventory.firstEmpty();
        inventory.setItem(firstEmpty, itemStack);
    }

    public ConfigFactory getFactory() {
        return factory;
    }

    public void setFactory(ConfigFactory factory) {
        this.factory = factory;
    }

    public StrengthenServiceImpl getStrengthService() {
        return strengthService;
    }

    public void setStrengthService(StrengthenServiceImpl strengthService) {
        this.strengthService = strengthService;
    }
}
