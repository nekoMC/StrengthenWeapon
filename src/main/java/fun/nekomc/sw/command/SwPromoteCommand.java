package fun.nekomc.sw.command;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.dto.SwRawConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.exception.SwCommandException;
import fun.nekomc.sw.promote.PromotionOperation;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * sw promote 道具名 次数 指令实现
 * created: 2022/4/3 0:03
 *
 * @author Chiru
 */
class SwPromoteCommand extends AbstractMainHandItemCommand {

    public SwPromoteCommand() {
        super("promote", Constants.ADMIN_PERMISSION_POINT, 2);
    }

    @Override
    protected boolean handleMainHandItem(@NotNull Player player, @NotNull ItemStack targetItem, String[] actualArgs) {
        // 要基于什么道具强化
        String byItemName = actualArgs[0];
        // 强化次数
        if (!CharSequenceUtil.isNumeric(actualArgs[1])) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg(Constants.Msg.COMMAND_ERROR));
        }
        int time = Integer.parseInt(actualArgs[1]);
        // 强化配置
        Optional<SwItemConfigDto> itemConfig = ConfigManager.getItemConfig(byItemName);
        if (!itemConfig.isPresent() || !(itemConfig.get() instanceof SwRawConfigDto)) {
            throw new SwCommandException(player, ConfigManager.getConfiguredMsg(Constants.Msg.COMMAND_ERROR));
        }
        SwRawConfigDto rawConfig = (SwRawConfigDto) itemConfig.get();
        // 更新附加数据
        ItemsTypeEnum rawType = ItemsTypeEnum.valueOf(rawConfig.getType());
        Optional<SwItemAttachData> attachDataOpt = ItemUtils.getAttachData(targetItem);
        SwItemAttachData attachData = attachDataOpt.orElseGet(() -> new SwItemAttachData(0, 0));
        if (rawType == ItemsTypeEnum.REFINE_STONE) {
            attachData.setRefLvl(attachData.getRefLvl() + time);
        } else if (rawType == ItemsTypeEnum.STRENGTHEN_STONE) {
            attachData.setStrLvl(attachData.getStrLvl() + time);
        }
        ItemMeta itemMeta = targetItem.getItemMeta();
        if (null == itemMeta) {
            return false;
        }
        ItemUtils.updateAttachData(itemMeta, attachData);
        targetItem.setItemMeta(itemMeta);
        // 执行强化
        PromotionOperation.doPromoteByCandidatesRandomly(targetItem, rawConfig.getCandidates(), time, false);
        return true;
    }

    @Override
    public List<String> hint(CommandSender sender, String[] args) {
        // 获取 give 指令的参数
        String[] actualArgs = ignoreDontCareArgs(args);
        int actualArgLength = getArgsActualLength(actualArgs, args);
        switch (actualArgLength) {
            case 0:
                return ConfigManager.getItemNameList();
            case 1:
                return ListUtil.of(Constants.STR_ZERO, "1", "2");
            default:
                return ListUtil.empty();
        }
    }
}
