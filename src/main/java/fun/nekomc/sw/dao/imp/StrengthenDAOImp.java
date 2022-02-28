package fun.nekomc.sw.dao.imp;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.dao.StrengthenDAO;
import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.domain.StrengthenStone;
import fun.nekomc.sw.domain.enumeration.WeaponsIndex;
import fun.nekomc.sw.utils.ItemLoreUtils;
import fun.nekomc.sw.utils.PlayerMsgUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StrengthenDAOImp implements StrengthenDAO {
    private StrengthenWeapon plugin;
    private List<StrengthenItem> strengthenWeapons;
    private List<StrengthenStone> strengthenStones;
    private StrengthenItem bow;

    public StrengthenDAOImp() { }

    @Override
    public ItemStack giveStrengthenBow(int count) {
        Material type = Material.valueOf(bow.getMaterial());
        ItemStack stack = new ItemStack(type);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(bow.getDisplayName());
            meta.setLore(bow.getLore());
            stack.setItemMeta(meta);
            stack.setAmount(count);
        }
        return stack;
    }

    @Override
    public ItemStack giveStrengthenStone(int count, int level) {
        int index = level - 1;
        StrengthenStone stone = strengthenStones.get(index);

        Material type = Material.valueOf(stone.getMaterial());
        ItemStack stack = new ItemStack(type);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(stone.getDisplayName());
            meta.setLore(stone.getLore());
            stack.setItemMeta(meta);
            stack.setAmount(count);
        }
        return stack;
    }

    @Override
    public ItemStack strengthen(Player player, ItemStack itemStack, StrengthenItem strengthenItem, boolean isSuccess, boolean isSafe) {
        ItemStack resItemStack = itemStack.clone();
        ItemMeta itemMeta = resItemStack.getItemMeta();
        boolean breakdown = false;
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                int level = ItemLoreUtils.getItemLevel(lore, strengthenItem);

                if(isSuccess) {//成功
                    ItemLoreUtils.setItemLevel(lore, strengthenItem, level + 1);
                    PlayerMsgUtils.sendMsg(player, "§6恭喜你强化成功，§b" + itemMeta.getDisplayName() + "§6已从强化等级:§b" + level + "§6提升至§b" + (level+1) + "§6!");
                }
                else {//失败
                    if (isSafe) {//有保护
                        PlayerMsgUtils.sendMsg(player, "§6很可惜强化失败，由于保护券，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6并未发生改变！");
                    }
                    else {//无保护
                        if (level > 10) {
                            breakdown = true;
                            PlayerMsgUtils.sendMsg(player, "§6很可惜强化失败，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6已损坏！");
                        }
                        else if (level > 4) {
                            ItemLoreUtils.setItemLevel(lore, strengthenItem, level - 1);
                            PlayerMsgUtils.sendMsg(player, "§6很可惜强化失败，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6已降至§b" + (level-1) + "§6!");
                        }
                        else {
                            PlayerMsgUtils.sendMsg(player, "§6很可惜强化失败，§b" + itemMeta.getDisplayName() + "§6强化等级:§b" + level + "§6并未发生改变！");
                        }
                    }
                }

                itemMeta.setLore(lore);
                resItemStack.setItemMeta(itemMeta);
            }
        }
        if (breakdown)
            return null;
        return resItemStack;
    }

    @Override
    public ItemStack strengthenSuccessResult(ItemStack itemStack, StrengthenItem strengthenItem) {
        ItemStack resItemStack = itemStack.clone();
        ItemMeta itemMeta = resItemStack.getItemMeta();
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                int level = ItemLoreUtils.getItemLevel(lore, strengthenItem);
                ItemLoreUtils.setItemLevel(lore, strengthenItem, level + 1);
                itemMeta.setLore(lore);
                resItemStack.setItemMeta(itemMeta);
            }
        }
        return resItemStack;
    }

    public List<StrengthenItem> getStrengthenWeapons() {
        return strengthenWeapons;
    }

    public void setStrengthenWeapons(List<StrengthenItem> strengthenWeapons) {
        this.strengthenWeapons = strengthenWeapons;
        this.bow = strengthenWeapons.get(WeaponsIndex.BOW.ordinal());
    }

    public List<StrengthenStone> getStrengthenStones() {
        return strengthenStones;
    }

    public void setStrengthenStones(List<StrengthenStone> strengthenStones) {
        this.strengthenStones = strengthenStones;
    }

    public void setPlugin(StrengthenWeapon plugin) {
        this.plugin = plugin;
    }

    public StrengthenWeapon getPlugin() {
        return plugin;
    }
}
