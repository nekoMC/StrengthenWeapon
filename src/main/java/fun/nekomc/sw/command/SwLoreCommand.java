package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import fun.nekomc.sw.enchant.helper.EnchantHelper;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.utils.ConfigManager;
import fun.nekomc.sw.utils.Constants;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * sw lore 指令实现
 * created: 2022/3/19 17:56
 *
 * @author Chiru
 */
class SwLoreCommand extends SwCommand {

    public SwLoreCommand() {
        super("lore", true, Constants.ADMIN_PERMISSION_POINT);
    }
}
