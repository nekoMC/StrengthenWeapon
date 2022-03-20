package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.enchant.helper.EnchantHelper;
import fun.nekomc.sw.exception.SwCommandException;
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
        Enchantment targetEnchant = EnchantHelper.getRegisteredEnchants().stream()
                .filter(enchant -> enchant.getKey().getKey().equals(enchantName))
                .findFirst()
                .map(swEnchant -> (Enchantment) swEnchant)
                .orElseGet(() -> Enchantment.getByKey(NamespacedKey.minecraft(enchantName)));
        String level = actualArgs[1];
        // 校验附魔、等级有效
        if (!StrUtil.isNumeric(level) || null == targetEnchant) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg("grammar_error"));
        }
        int targetLevel = Integer.parseInt(level);
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
}
