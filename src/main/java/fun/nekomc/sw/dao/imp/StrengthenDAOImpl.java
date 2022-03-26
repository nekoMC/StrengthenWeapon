package fun.nekomc.sw.dao.imp;

import fun.nekomc.sw.dao.StrengthenDAO;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.exception.ConfigurationException;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.MsgUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

/**
 * @author ourange
 */
public class StrengthenDAOImpl implements StrengthenDAO {

    public StrengthenDAOImpl() {
    }

    @Override
    public ItemStack strengthen(Player player, ItemStack itemStack, boolean isSuccess, boolean isSafe) {
        ItemStack resItemStack = itemStack.clone();
        ItemMeta itemMeta = resItemStack.getItemMeta();
        boolean breakdown = false;
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                // TODO: 临时处理，读取数据标签中的强化等级
                Optional<SwItemAttachData> attachDataOpt = ItemUtils.getAttachData(itemStack);
                if (!attachDataOpt.isPresent()) {
                    throw new ConfigurationException(ConfigManager.getConfiguredMsg(Constants.Msg.CONFIG_ERROR));
                }
                SwItemAttachData attachData = attachDataOpt.get();
                int level = attachData.getStrLvl();

                if (isSuccess) {//成功
                    // ItemUtils.setItemLevel(lore, strengthenItem, level + 1);
                    MsgUtils.sendMsg(player, "§6恭喜你强化成功，§b" + itemMeta.getDisplayName() + "§6已从强化等级:§b" + level + "§6提升至§b" + (level + 1) + "§6!");
                    level++;
                } else {//失败
                    if (isSafe) {//有保护
                        MsgUtils.sendMsg(player, "§6很可惜强化失败，由于保护券，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6并未发生改变！");
                    } else {//无保护
                        if (level > 10) {
                            breakdown = true;
                            MsgUtils.sendMsg(player, "§6很可惜强化失败，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6已损坏！");
                        } else if (level > 4) {
                            // ItemUtils.setItemLevel(lore, strengthenItem, level - 1);
                            MsgUtils.sendMsg(player, "§6很可惜强化失败，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6已降至§b" + (level - 1) + "§6!");
                            level--;
                        } else {
                            MsgUtils.sendMsg(player, "§6很可惜强化失败，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6并未发生改变！");
                        }
                    }
                }

                // TODO: 临时处理，更新强化等级
                attachData.setStrLvl(level);
                ItemUtils.updateAttachData(itemMeta, attachData);
                resItemStack.setItemMeta(itemMeta);
            }
        }
        if (breakdown) {
            return null;
        }
        return resItemStack;
    }

    @Override
    public ItemStack strengthenSuccessResult(ItemStack swWeapon, ItemStack swStone) {
        ItemStack resItemStack = swWeapon.clone();
        ItemMeta itemMeta = resItemStack.getItemMeta();
        if (itemMeta != null) {
            Optional<SwItemAttachData> weaponAttachedDataOpt = ItemUtils.getAttachData(swWeapon);
            if (!weaponAttachedDataOpt.isPresent()) {
                return null;
            }
            List<String> lore = itemMeta.getLore();
            SwItemAttachData weaponAttachData = weaponAttachedDataOpt.get();
            weaponAttachData.setStrLvl(weaponAttachData.getStrLvl() + 1);
            itemMeta.setLore(lore);
            resItemStack.setItemMeta(itemMeta);
        }
        return resItemStack;
    }
}
