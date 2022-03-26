package fun.nekomc.sw.listener;

import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * created: 2022/3/27 01:35
 *
 * @author Chiru
 */
public class RefineTableGuiListener extends AbstractComposeGui implements Listener {

    private static final int SW_WEAPON_INDEX = 0;
    private static final int SW_STONE_INDEX = 1;

    public RefineTableGuiListener() {
        super(InventoryType.SMITHING, ConfigManager.getConfigYml().getRefineTitle(), 2);
        // 注册校验规则
        registerCheckRule(SW_WEAPON_INDEX, ItemsTypeEnum.BLANK);
        registerCheckRule(SW_STONE_INDEX, ItemsTypeEnum.REFINE_STONE);
    }

    @Override
    protected ItemStack generatePreviewItem(@NotNull WrappedInventoryClickEvent wrapped) {
        return null;
    }

    @Override
    protected ItemStack generateStrengthItem(@NotNull WrappedInventoryClickEvent wrapped) {
        return null;
    }
}
