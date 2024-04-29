package fun.nekomc.sw.skill;

import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.utils.ItemUtils;
import fun.nekomc.sw.utils.MsgUtils;
import fun.nekomc.sw.utils.ServiceUtils;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * 海之馈赠附魔实现
 * created: 2022/3/15 23:27
 *
 * @author Chiru
 */
public class GiftOfTheSeaEnchantment extends AbstractSwEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "GIFT_OF_THE_SEA";

    public GiftOfTheSeaEnchantment() {
        super(ENCHANT_KEY);
    }

    @Override
    public void onFishing(@NotNull Player player, @NotNull ItemStack fishingRod, @NotNull Item caught,
                          int level, @NotNull PlayerFishEvent event) {
        // 计算概率
        int chanceToGetGift = getEnchantLvlAttribute(level);
        int actualRandom = RandomUtil.randomInt(0, 100);
        if (actualRandom >= chanceToGetGift) {
            return;
        }
        // 可以获得宝藏
        Collection<SwItemConfigDto> candidates = ConfigManager.getItemConfigList();
        SwItemConfigDto itemConfigToReturn = ServiceUtils.randomByWeight(candidates, SwItemConfigDto::getRarity);
        Optional<ItemStack> targetItem = ItemUtils.buildItemByConfig(itemConfigToReturn);
        if (!targetItem.isPresent()) {
            return;
        }
        // 获得的目标物品
        ItemStack target = targetItem.get();
        caught.setItemStack(target);
        if (null != target.getItemMeta()) {
            String displayName = target.getItemMeta().getDisplayName();
            MsgUtils.sendMsg(player, ConfigManager.getConfiguredMsg(Constants.Msg.GOT_SEA_GIFT), displayName);
        }
    }
}
