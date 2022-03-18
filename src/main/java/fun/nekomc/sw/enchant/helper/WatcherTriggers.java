package fun.nekomc.sw.enchant.helper;

import fun.nekomc.sw.enchant.AbstractSwEnchantment;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 拷贝自：https://github.com/Auxilor/EcoEnchants
 * created: 2022/3/16 00:39
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/EcoEnchants">参考 EcoEnchants</a>
 */
public class WatcherTriggers implements Listener {

    /**
     * Called when an entity shoots another entity with an arrow.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArrowDamage(@NotNull final EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        if (((Arrow) event.getDamager()).getShooter() == null) {
            return;
        }

        if (!(((Arrow) event.getDamager()).getShooter() instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker instanceof Player) {
            return;
        }

        EnchantHelper.getEnchantsOnArrow(arrow)
                .forEach(((enchant, level) -> enchant.onArrowDamage(attacker, victim, arrow, level, event)));
    }

    /**
     * Called when an entity damages another entity with a trident throw.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentDamage(@NotNull final EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Trident trident)) {
            return;
        }

        if (!(((Trident) event.getDamager()).getShooter() instanceof LivingEntity attacker)) {
            return;
        }

        if (((Trident) event.getDamager()).getShooter() == null) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        ItemStack item = trident.getItem();

        EnchantHelper.getEnchantsOnItem(item)
                .forEach(((enchant, level) -> enchant.onTridentDamage(attacker, victim, trident, level, event)));
    }

    /**
     * Called when an entity attacks another entity with a melee attack.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onMeleeAttack(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity attacker)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
            return;
        }

        EnchantHelper.getEnchantsOnMainhand(attacker)
                .forEach((enchant, level) -> enchant.onMeleeAttack(attacker, victim, level, event));
    }

    /**
     * Called when an entity shoots a bow.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(@NotNull final EntityShootBowEvent event) {
        if (event.getProjectile().getType() != EntityType.ARROW) {
            return;
        }

        LivingEntity shooter = event.getEntity();
        Arrow arrow = (Arrow) event.getProjectile();

        EnchantHelper.getEnchantsOnMainhand(shooter)
                .forEach((enchant, level) -> enchant.onBowShoot(shooter, arrow, level, event));
    }

    /**
     * Called when an entity launches a projectile.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(@NotNull final ProjectileLaunchEvent event) {

        if (!(event.getEntity() instanceof AbstractArrow)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();

        Projectile projectile = event.getEntity();

        if (shooter.getEquipment() == null) {
            return;
        }

        ItemStack item = shooter.getEquipment().getItemInMainHand();

        if (projectile instanceof Trident trident) {
            item = trident.getItem();
        }

        EnchantHelper.getEnchantsOnItem(item)
                .forEach((enchant, level) -> enchant.onProjectileLaunch(shooter, projectile, level, event));
    }

    /**
     * Called when an entity takes fall damage.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onFallDamage(@NotNull final EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        EnchantHelper.getEnchantsOnArmor(victim)
                .forEach((enchant, level) -> enchant.onFallDamage(victim, level, event));
    }

    /**
     * Called when an arrow hits a block or entity.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(@NotNull final ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) {
            return;
        }

        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (event.getEntity().getShooter() == null) {
            return;
        }

        EnchantHelper.getEnchantsOnArrow(arrow)
                .forEach(((enchant, level) -> enchant.onArrowHit(shooter, level, event)));
    }

    /**
     * Called when a trident hits a block or entity.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentHit(@NotNull final ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) {
            return;
        }

        if (event.getEntity().getShooter() == null) {
            return;
        }

        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }

        ItemStack item = trident.getItem();

        EnchantHelper.getEnchantsOnItem(item)
                .forEach((enchant, level) -> enchant.onTridentHit(shooter, level, event));
    }

    /**
     * Called when a player breaks a block.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull final BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        EnchantHelper.getEnchantsOnMainhand(player)
                .forEach((enchant, level) -> enchant.onBlockBreak(player, block, level, event));
    }

    /**
     * Called when an entity takes damage wearing armor.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamageWearingArmor(@NotNull final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        EnchantHelper.getEnchantsOnArmor(victim)
                .forEach((enchant, level) -> enchant.onDamageWearingArmor(victim, level, event));
    }

    /**
     * Called when a player damages a block.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamageBlock(@NotNull final BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (event.getBlock().getDrops(player.getInventory().getItemInMainHand()).isEmpty()) {
            return;
        }

        EnchantHelper.getEnchantsOnMainhand(player)
                .forEach((enchant, level) -> enchant.onDamageBlock(player, block, level, event));
    }

    /**
     * Called when an entity throws a trident.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentLaunch(@NotNull final ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof LivingEntity)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) trident.getShooter();
        ItemStack item = trident.getItem();
        assert shooter != null;

        EnchantHelper.getEnchantsOnItem(item)
                .forEach((enchant, level) -> enchant.onTridentLaunch(shooter, trident, level, event));
    }

    /**
     * Called when a player blocks an attack with a shield.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDeflect(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player blocker)) {
            return;
        }

        LivingEntity attacker = ItemUtils.tryAsPlayer(event.getDamager());

        if (attacker == null) {
            return;
        }

        if (!blocker.isBlocking()) {
            return;
        }

        Map<AbstractSwEnchantment, Integer> enchants = blocker.getInventory().getItemInMainHand().getType() == Material.SHIELD
                ? EnchantHelper.getEnchantsOnMainhand(blocker)
                : EnchantHelper.getEnchantsOnOffhand(blocker);

        enchants.forEach((enchant, level) -> enchant.onDeflect(blocker, attacker, level, event));
    }
}
