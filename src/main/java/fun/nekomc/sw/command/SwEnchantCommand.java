package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.enchant.helper.EnchantHelper;
import fun.nekomc.sw.utils.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sw enchant id lvl 指令实现
 * created: 2022/3/19 17:11
 *
 * @author Chiru
 */
class SwEnchantCommand extends AbstractMainHandItemCommand {

    private final List<String> enchantNames;

    public SwEnchantCommand() {
        super("enchant", Constants.ADMIN_PERMISSION_POINT, 2);
        enchantNames = Arrays.stream(Enchantment.values())
                .map(enchant -> enchant.getKey().getKey())
                .collect(Collectors.toList());
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 查找要附魔的附魔对象
        String enchantName = actualArgs[0];
        String level = actualArgs[1];

        Enchantment targetEnchant = EnchantHelper.getRegisteredEnchants().stream()
                .filter(enchant -> enchant.getKey().getKey().equals(enchantName))
                .findFirst()
                .map(Enchantment.class::cast)
                .orElseGet(() -> Enchantment.getByKey(NamespacedKey.minecraft(enchantName)));
        // 校验附魔、等级有效
        if (!CharSequenceUtil.isNumeric(level) || null == targetEnchant) {
            return false;
        }
        int targetLevel = Integer.parseInt(level);

        return updateItemEnchant(targetItem, targetEnchant, targetLevel);
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        switch (actualArgLength) {
            case 0:
                return enchantNames;
            case 1:
                return ListUtil.of(Constants.STR_ZERO, "1", "2");
            default:
                return ListUtil.empty();
        }
    }

    /**
     * 更新道具的指定附魔及覆膜等级
     *
     * @param targetItem    目标道具
     * @param targetEnchant 要更新的附魔
     * @param targetLevel   更新后的附魔等级，如果为 0 则删除附魔
     * @return 操作是否成功
     */
    public boolean updateItemEnchant(ItemStack targetItem, Enchantment targetEnchant, int targetLevel) {
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        // 附魔书时，需要基于 EnchantmentStorageMeta 进行操作
        if (targetItem.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemMeta;
            if (0 == targetLevel) {
                bookMeta.removeStoredEnchant(targetEnchant);
            } else {
                bookMeta.addStoredEnchant(targetEnchant, targetLevel, true);
            }
        } else {
            if (0 == targetLevel) {
                itemMeta.removeEnchant(targetEnchant);
            } else {
                itemMeta.addEnchant(targetEnchant, targetLevel, true);
            }
        }
        targetItem.setItemMeta(itemMeta);
        // 刷新附魔 Lore
        EnchantHelper.updateLore(targetItem);
        return true;
    }
}
