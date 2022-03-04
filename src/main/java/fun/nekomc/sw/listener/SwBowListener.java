package fun.nekomc.sw.listener;

import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.Random;

/**
 * 连发弓监听器、功能实现
 *
 * @author ourange
 */
public class SwBowListener implements Listener {
    private StrengthenItem strengthenBow;

    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event) {
        // 玩家用奇怪的（包含 lore）弓射出箭时
        if (event.getProjectile() instanceof Arrow) {
            if (event.getEntity() instanceof Player) {
                if (event.getBow() != null && event.getBow().getItemMeta() != null
                        && event.getBow().getItemMeta().getLore() != null) {
                    // 这把弓如果是认证的连发弓
                    String bowName = ItemUtils.getItemName(event.getBow().getItemMeta().getLore());
                    String swBowName = ItemUtils.getItemName(strengthenBow.getLore());
                    if (bowName.equalsIgnoreCase(swBowName)) {
                        Arrow arrow = (Arrow) event.getProjectile();
                        Player player = (Player) event.getEntity();

                        int level = ItemUtils.getItemLevel(event.getBow().getItemMeta().getLore(), strengthenBow);
                        // 额外的箭，每高一级，多射一支
                        Random random = new Random();
                        double range = 2.0;//范围
                        for (int i = 1; i <= level; i++) {
                            Location location = player.getEyeLocation().clone();
                            double ranX = (random.nextDouble() - 0.5) * range;
                            double ranY = (random.nextDouble() - 0.5) * range;
                            double ranZ = (random.nextDouble() - 0.5) * range;
                            location.add(ranX, ranY, ranZ);
                            Arrow extraArrow = player.getWorld().spawn(location, Arrow.class);
                            extraArrow.setVelocity(arrow.getVelocity());//速度
                            extraArrow.setShooter(player);
                            //extraArrow.setDamage();
                            extraArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);//不可拾取
                        }
                        //arrow.setDamage();
                        // 原始的箭也不允许拾取
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    }
                }
            }
        }
    }

    public StrengthenItem getStrengthenBow() {
        return strengthenBow;
    }

    public void setStrengthenBow(StrengthenItem strengthItem) {
        this.strengthenBow = strengthItem;
    }
}
