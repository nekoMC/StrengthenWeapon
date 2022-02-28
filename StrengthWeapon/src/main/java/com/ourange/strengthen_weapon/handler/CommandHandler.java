package com.ourange.strengthen_weapon.handler;

import com.ourange.strengthen_weapon.StrengthenWeapon;
import com.ourange.strengthen_weapon.domain.StrengthenItem;
import com.ourange.strengthen_weapon.domain.StrengthenStone;
import com.ourange.strengthen_weapon.service.imp.StrengthenServiceImp;
import com.ourange.strengthen_weapon.utils.ConfigFactory;
import com.ourange.strengthen_weapon.utils.PlayerMsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor {
    private StrengthenWeapon plugin;
    private ConfigFactory factory;
    private StrengthenServiceImp strengthService;


/*private static StrengthServiceImpl strengthService;
    private static final int DEFAULT_STACK = 64;
    public static final String ADMIN_PERMISSION = "strength.admin";*/

    public CommandHandler(StrengthenWeapon plugin, ConfigFactory factory){
        this.plugin = plugin;
        this.factory = factory;
        strengthService = new StrengthenServiceImp(plugin, factory.getStrengthenWeapons(), factory.getStrengthenStones());
        strengthService.setPlugin(plugin);
        /*strengthService.setPlugin(plugin);*/
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String mainCommand, String[] commandArray) {
        if (!"CONSOLE".equals(commandSender.getName())) {
            Player player = ((Player) commandSender).getPlayer();
            assert player != null;
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
                            PlayerMsgUtils.sendMsg(player, "§b请输入正确的指令:/sw sw_stone [1~" + strengthService.getStrengthenStones().size() + "]");
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
            plugin.consoleMsg("&c控制台仅允许使用reload指令");
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

    public StrengthenServiceImp getStrengthService() {
        return strengthService;
    }

    public void setStrengthService(StrengthenServiceImp strengthService) {
        this.strengthService = strengthService;
    }
}
