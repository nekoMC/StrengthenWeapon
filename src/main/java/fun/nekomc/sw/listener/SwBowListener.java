package fun.nekomc.sw.listener;

import fun.nekomc.sw.domain.StrengthenItem;
import fun.nekomc.sw.utils.ItemLoreUtils;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import java.util.Random;

public class SwBowListener implements Listener {
    private StrengthenItem strengthenBow;

    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event) {

        if (event.getProjectile() instanceof Arrow) {
            if (event.getEntity() instanceof Player) {
                if (event.getBow() != null && event.getBow().getItemMeta() != null
                        && event.getBow().getItemMeta().getLore() != null) {

                    String bowName = ItemLoreUtils.getItemName(event.getBow().getItemMeta().getLore());
                    String swBowName = ItemLoreUtils.getItemName(strengthenBow.getLore());
                    if (bowName.equalsIgnoreCase(swBowName)) {
                        Arrow arrow = (Arrow) event.getProjectile();
                        Player player = (Player) event.getEntity();

                        int level = ItemLoreUtils.getItemLevel(event.getBow().getItemMeta().getLore(), strengthenBow);
                        //额外的箭
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
