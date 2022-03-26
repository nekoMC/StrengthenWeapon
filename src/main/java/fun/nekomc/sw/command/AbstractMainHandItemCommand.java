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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 需要对主手上物品进行操作的抽象指令
 * created: 2022/3/19 18:01
 *
 * @author Chiru
 */
abstract class AbstractMainHandItemCommand extends SwCommand {

    final int fixQueryArgCount;

    protected AbstractMainHandItemCommand(String cmd, String permissionNode, int fixQueryArgCount) {
        super(cmd, true, permissionNode);
        this.fixQueryArgCount = fixQueryArgCount;
    }

    @Override
    public boolean rua(CommandSender sender, String[] args) {
        // 获取参数
        String[] actualArgs = ignoreDontCareArgs(args);
        if (actualArgs.length != fixQueryArgCount) {
            throw new SwCommandException(sender, ConfigManager.getConfiguredMsg(Constants.Msg.GRAMMAR_ERROR));
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        // 主手物品为空，跳过
        if (null == player.getEquipment() || Material.AIR == player.getEquipment().getItemInMainHand().getType()) {
            return false;
        }
        ItemStack targetItem = player.getEquipment().getItemInMainHand();
        return handleMainHandItem(player, targetItem, actualArgs);
    }

    /**
     * 对主手上道具进行操作的具体实现
     *
     * @param player     手持道具的玩家
     * @param targetItem 目标道具
     * @param actualArgs 实际参数
     * @return 操作状态
     */
    protected abstract boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs);
}
