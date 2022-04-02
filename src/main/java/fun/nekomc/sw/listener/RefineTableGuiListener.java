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
public class RefineTableGuiListener extends TwoInputOneOutputGuiListener implements Listener {

    public RefineTableGuiListener() {
        super(InventoryType.FURNACE, ConfigManager.getConfigYml().getRefineTitle(), ItemsTypeEnum.BLANK, ItemsTypeEnum.REFINE_STONE);
    }

    @Override
    protected SwBlankConfigDto.StrengthRule getStrengthRuleFromBlankConfig(SwBlankConfigDto blankConfigDto) {
        Assert.notNull(blankConfigDto, "blankConfigDto cannot be null");
        return blankConfigDto.getRefine();
    }

    @Override
    protected int getLvlFromAttach(SwItemAttachData attachData) {
        if (null == attachData || null == attachData.getRefLvl()) {
            return 0;
        }
        return attachData.getRefLvl();
    }

    @Override
    protected SwItemAttachData newAttachDataAfterLvlUp(SwItemAttachData oldAttach) {
        int oldRef = (null == oldAttach || null == oldAttach.getRefLvl()) ? 0 : oldAttach.getRefLvl();
        return new SwItemAttachData(oldRef + 1, 0);
    }
}
