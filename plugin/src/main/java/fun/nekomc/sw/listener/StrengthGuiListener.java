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
public class StrengthGuiListener extends TwoInputOneOutputGuiListener implements Listener {

    public StrengthGuiListener() {
        super(() -> InventoryType.valueOf(ConfigManager.getConfigYml().getStrengthType()),
                ConfigManager.getConfigYml().getStrengthTitle(), ItemsTypeEnum.BLANK, ItemsTypeEnum.STRENGTHEN_STONE, true);
    }

    @Override
    protected SwBlankConfigDto.StrengthRule getStrengthRuleFromBlankConfig(SwBlankConfigDto blankConfigDto) {
        Assert.notNull(blankConfigDto, "blankConfigDto cannot be null");
        return blankConfigDto.getStrength();
    }

    @Override
    protected int getLvlFromAttach(SwItemAttachData attachData) {
        if (null == attachData || null == attachData.getStrengthenLevel()) {
            return 0;
        }
        return attachData.getStrengthenLevel();
    }

    @Override
    protected SwItemAttachData newAttachDataAfterLvlUp(SwItemAttachData oldAttach) {
        if (oldAttach == null) {
            return new SwItemAttachData(0, 1, null);
        }
        int oldStr = null == oldAttach.getStrengthenLevel() ? 0 : oldAttach.getStrengthenLevel();
        int oldRef = null == oldAttach.getRefineLevel() ? 0 : oldAttach.getRefineLevel();
        return new SwItemAttachData(oldRef, oldStr + 1, oldAttach.getSkills());
    }
}
