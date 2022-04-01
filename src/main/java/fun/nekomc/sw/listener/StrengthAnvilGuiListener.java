package fun.nekomc.sw.listener;

import fun.nekomc.sw.domain.dto.SwItemConfigDto;
import fun.nekomc.sw.domain.dto.SwStrengthenStoneConfigDto;
import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.service.StrengthenService;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.common.ConfigManager;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * 全局事件监听分发器
 *
 * @author ourange
 */
public class StrengthAnvilGuiListener extends AbstractComposeGui implements Listener {

    private static final int SW_WEAPON_INDEX = 0;
    private static final int SW_STONE_INDEX = 1;

    private final StrengthenService service;

    public StrengthAnvilGuiListener() {
        super(InventoryType.ANVIL, ConfigManager.getConfigYml().getStrengthTitle(), 2);
        // 注册校验规则
        registerCheckRule(SW_WEAPON_INDEX, ItemsTypeEnum.BLANK);
        registerCheckRule(SW_STONE_INDEX, ItemsTypeEnum.STRENGTHEN_STONE);
        service = StrengthenServiceImpl.getInstance();
    }

    @Override
    protected boolean recipeMatch(Inventory targetInv) {
        return true;
    }

    @Override
    protected ItemStack generatePreviewItem(@NotNull WrappedInventoryClickEvent wrapped) {
        // TODO 生成预览物品
        ItemStack item = wrapped.inventory.getItem(0).clone();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Demo For Display");
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    protected ItemStack generateStrengthItem(@NotNull WrappedInventoryClickEvent wrapped) {
        // TODO 实际强化逻辑
        ItemStack item = wrapped.inventory.getItem(SW_WEAPON_INDEX);
        ItemStack stone = wrapped.inventory.getItem(SW_STONE_INDEX);
        String stoneName = ItemUtils.getNameFromMeta(stone);
        Optional<SwItemConfigDto> stoneConfig = ConfigManager.getItemConfig(stoneName);
        return stoneConfig.map(swItemConfigDto ->
                service.strengthen(wrapped.clickPlayer, item, (SwStrengthenStoneConfigDto) swItemConfigDto, false)
        ).orElse(null);
    }
}
