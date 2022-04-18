package fun.nekomc.sw.enchant;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 魔杖附魔实现
 * created: 2022/4/17 22:59
 *
 * @author Chiru
 */
public class SplashEnchantment extends AbstractSwEnchantment {

    /**
     * AbstractSwEnchantment 子类必须声明本属性，以完成自动注册匹配
     */
    public static final String ENCHANT_KEY = "SPLASH";

    protected SplashEnchantment() {
        super(ENCHANT_KEY);
    }

    @Override
    public void onMainHandRightClick(@NotNull Player player, @NotNull ItemStack holdInHand, int level, @NotNull PlayerInteractEvent event) {
        super.onMainHandRightClick(player, holdInHand, level, event);
    }
}
