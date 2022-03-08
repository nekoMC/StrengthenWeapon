package fun.nekomc.sw.gui;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

/**
 * 适用于合成场景的自定义容器实现
 * TODO: 参考 StrengthMenuListener 实现这条神奇的链子
 * created: 2022/3/9 01:01
 *
 * @author Chiru
 */
public abstract class AbstractComposeGui {

    /**
     * 容器类型
     */
    private final InventoryType invType;

    /**
     * 容器标题
     */
    private final String invTitle;

    /**
     * 输出格子的编号，以铁砧为例，outputCellIndex 为 2
     */
    private final int outputCellIndex;

    AbstractComposeGui(InventoryType invType, String invTitle, int outputCellIndex) {
        this.invTitle = invTitle;
        this.invType = invType;
        this.outputCellIndex = outputCellIndex;
    }

    /**
     * 检验当前容器是否能处理指定 View 下的事件
     *
     * @param inventoryView 指定容器的 View
     * @return 是否能处理，即与注册的容器类型匹配且标题匹配
     */
    public boolean canHandleView(InventoryView inventoryView) {
        return inventoryView != null
                && inventoryView.getType() == invType && invTitle.equalsIgnoreCase(inventoryView.getTitle());
    }
}
