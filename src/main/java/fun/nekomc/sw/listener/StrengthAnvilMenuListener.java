package fun.nekomc.sw.listener;

import fun.nekomc.sw.domain.enumeration.ItemsTypeEnum;
import fun.nekomc.sw.service.imp.StrengthenServiceImpl;
import fun.nekomc.sw.utils.ConfigManager;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;


/**
 * 全局事件监听分发器
 *
 * @author ourange
 */
public class StrengthAnvilMenuListener extends AbstractComposeGui implements Listener {

    private StrengthenServiceImpl service;

    public StrengthAnvilMenuListener() {
        super(InventoryType.ANVIL, ConfigManager.getConfigYml().getStrengthTitle(), 2);
        // 注册校验规则
        registerCheckRule(0, ItemsTypeEnum.BLANK);
        registerCheckRule(1, ItemsTypeEnum.STRENGTHEN_STONE);
    }

    @Override
    protected ItemStack generatePreviewItem(@NotNull Inventory inventory) {
        ItemStack item = inventory.getItem(0);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Demo For Display");
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    protected ItemStack generateStrengthItem(@NotNull Inventory inventory) {
        ItemStack item = inventory.getItem(0);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Demo For Result");
        item.setItemMeta(itemMeta);
        return item;
    }

    public void setService(StrengthenServiceImpl service){
        this.service = service;
    }
}
