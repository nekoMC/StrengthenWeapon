package fun.nekomc.sw.listener;

import cn.hutool.core.lang.Assert;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.domain.SwItemAttachData;
import fun.nekomc.sw.domain.dto.SwBlankConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

/**
 * 洗炼 GUI 的监听处理器
 *
 * @author Chiru
 */
public class RefineGuiListener extends TwoInputOneOutputGuiListener implements Listener {

    public RefineGuiListener() {
        super(() -> InventoryType.valueOf(ConfigManager.getConfigYml().getRefineType()), ConfigManager.getConfigYml().getRefineTitle(),
                ItemsTypeEnum.BLANK, ItemsTypeEnum.REFINE_STONE, false);
    }

    @Override
    protected SwBlankConfigDto.StrengthRule getStrengthRuleFromBlankConfig(SwBlankConfigDto blankConfigDto) {
        Assert.notNull(blankConfigDto, "blankConfigDto cannot be null");
        return blankConfigDto.getRefine();
    }

    @Override
    protected int getLvlFromAttach(SwItemAttachData attachData) {
        if (null == attachData || null == attachData.getRefineLevel()) {
            return 0;
        }
        return attachData.getRefineLevel();
    }

    @Override
    protected SwItemAttachData newAttachDataAfterLvlUp(SwItemAttachData oldAttach) {
        if (oldAttach == null) {
            return new SwItemAttachData(1, 0, null);
        }
        int oldRef = null == oldAttach.getRefineLevel() ? 0 : oldAttach.getRefineLevel();
        return new SwItemAttachData(oldRef + 1, 0, oldAttach.getSkills());
    }
}
