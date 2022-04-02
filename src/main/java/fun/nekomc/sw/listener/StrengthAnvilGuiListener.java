package fun.nekomc.sw.listener;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SwBlankConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.common.ConfigManager;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

/**
 * 强化 GUI 的监听处理器
 *
 * @author ourange
 */
public class StrengthAnvilGuiListener extends TwoInputOneOutputGuiListener implements Listener {

    public StrengthAnvilGuiListener() {
        super(InventoryType.ANVIL, ConfigManager.getConfigYml().getStrengthTitle(), ItemsTypeEnum.BLANK, ItemsTypeEnum.STRENGTHEN_STONE, true);
    }

    @Override
    protected SwBlankConfigDto.StrengthRule getStrengthRuleFromBlankConfig(SwBlankConfigDto blankConfigDto) {
        Assert.notNull(blankConfigDto, "blankConfigDto cannot be null");
        return blankConfigDto.getStrength();
    }

    @Override
    protected int getLvlFromAttach(SwItemAttachData attachData) {
        if (null == attachData || null == attachData.getStrLvl()) {
            return 0;
        }
        return attachData.getStrLvl();
    }

    @Override
    protected SwItemAttachData newAttachDataAfterLvlUp(SwItemAttachData oldAttach) {
        int oldStr = (null == oldAttach || null == oldAttach.getStrLvl()) ? 0 : oldAttach.getStrLvl();
        int oldRef = (null == oldAttach || null == oldAttach.getRefLvl()) ? 0 : oldAttach.getRefLvl();
        return new SwItemAttachData(oldRef, oldStr + 1);
    }
}
