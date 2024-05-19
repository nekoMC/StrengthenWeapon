package fun.nekomc.sw.skill;

import cn.hutool.core.util.RandomUtil;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.common.Constants;
import fun.nekomc.sw.domain.enumeration.RarityIndustryEnum;
import fun.nekomc.sw.utils.MsgUtils;
import fun.nekomc.sw.utils.ServiceUtils;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 海之馈赠实现
 * created: 2022/3/15 23:27
 *
 * @author Chiru
 */
public class GiftOfTheSeaSkill extends AbstractSwSkill {

    public GiftOfTheSeaSkill() {
        super("GIFT_OF_THE_SEA");
    }

    @Override
    public void onFishing(@NotNull Player player, @NotNull ItemStack fishingRod, @NotNull Item caught,
                          boolean godHasGift, int level, @NotNull PlayerFishEvent event) {
        // 神给过了，不再重复给
        if (godHasGift) {
            return;
        }
        // 计算概率
        int chanceToGetGift = getSkillLvlAttribute(level);
        int actualRandom = RandomUtil.randomInt(0, 100);
        if (actualRandom < chanceToGetGift) {
            Optional<ItemStack> reward = ServiceUtils.getReward(player, RarityIndustryEnum.FISHERY, false);
            if (reward.isPresent()) {
                ItemStack itemStack = reward.get();
                caught.setItemStack(itemStack);
                String displayName = itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
                MsgUtils.sendMsg(player, ConfigManager.getConfiguredMsg(Constants.Msg.GOT_SEA_GIFT), displayName);
            }
        }
    }
}
