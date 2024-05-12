package fun.nekomc.sw.skill.helper;

import fun.nekomc.sw.StrengthenWeapon;
import fun.nekomc.sw.skill.AbstractSwSkill;
import fun.nekomc.sw.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * 拷贝自：https://github.com/Auxilor/EcoEnchants
 * created: 2022/3/16 00:39
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/EcoEnchants">参考 EcoEnchants</a>
 */
public class WatcherTriggers implements Listener {

    private static final WatcherTriggers INSTANCE = new WatcherTriggers();

    public static WatcherTriggers getInstance() {
        return INSTANCE;
    }

    /**
     * Called when an entity shoots another entity with an arrow.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArrowDamage(@NotNull final EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getDamager();

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity victim = (LivingEntity) event.getEntity();

        if (arrow.getShooter() == null) {
            return;
        }

        if (!(arrow.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity) arrow.getShooter();

        SkillHelper.getSkillsOnArrow(arrow)
                .forEach(((skill, level) -> skill.onArrowDamage(attacker, victim, arrow, level, event)));
    }

    /**
     * Called when an entity damages another entity with a trident throw.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentDamage(@NotNull final EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Trident)) {
            return;
        }
        Trident trident = (Trident) event.getDamager();

        if (!(trident.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity) trident.getShooter();

        if (trident.getShooter() == null) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity victim = (LivingEntity) event.getEntity();

        ItemStack item = trident.getItem();

        SkillHelper.getSkillsOnItem(item)
                .forEach(((skill, level) -> skill.onTridentDamage(attacker, victim, trident, level, event)));
    }

    /**
     * Called when an entity attacks another entity with a melee attack.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onMeleeAttack(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity) event.getDamager();

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity victim = (LivingEntity) event.getEntity();

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
            return;
        }

        SkillHelper.getSkillsOnMainhand(attacker)
                .forEach((skill, level) -> skill.onMeleeAttack(attacker, victim, level, event));
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

        SkillHelper.getSkillsOnMainhand(shooter)
                .forEach((skill, level) -> skill.onBowShoot(shooter, arrow, level, event));
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

        // 主手不是弓和弩，检查副手
        if (item.getType() != Material.BOW && item.getType() != Material.CROSSBOW) {
            item = shooter.getEquipment().getItemInOffHand();
        }

        if (projectile instanceof Trident) {
            item = ((Trident) projectile).getItem();
        }

        SkillHelper.getSkillsOnItem(item)
                .forEach((skill, level) -> skill.onProjectileLaunch(shooter, projectile, level, event));
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

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity victim = (LivingEntity) event.getEntity();

        SkillHelper.getSkillsOnArmor(victim)
                .forEach((skill, level) -> skill.onFallDamage(victim, level, event));
    }

    /**
     * Called when an arrow hits a block or entity.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(@NotNull final ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getEntity();

        if (!(arrow.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        if (event.getEntity().getShooter() == null) {
            return;
        }

        SkillHelper.getSkillsOnArrow(arrow)
                .forEach(((skill, level) -> skill.onArrowHit(shooter, level, event)));
    }

    /**
     * Called when a trident hits a block or entity.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentHit(@NotNull final ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident)) {
            return;
        }
        Trident trident = (Trident) event.getEntity();

        if (!(trident.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity shooter = (LivingEntity) trident.getShooter();

        if (shooter == null) {
            return;
        }

        ItemStack item = trident.getItem();

        SkillHelper.getSkillsOnItem(item)
                .forEach((skill, level) -> skill.onTridentHit(shooter, level, event));
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

        SkillHelper.getSkillsOnMainhand(player)
                .forEach((skill, level) -> skill.onBlockBreak(player, block, level, event));
    }

    /**
     * Called when an entity takes damage wearing armor.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamageWearingArmor(@NotNull final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity victim = (LivingEntity) event.getEntity();

        SkillHelper.getSkillsOnArmor(victim)
                .forEach((skill, level) -> skill.onDamageWearingArmor(victim, level, event));
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

        SkillHelper.getSkillsOnMainhand(player)
                .forEach((skill, level) -> skill.onDamageBlock(player, block, level, event));
    }

    /**
     * Called when an entity throws a trident.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentLaunch(@NotNull final ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident)) {
            return;
        }
        Trident trident = (Trident) event.getEntity();

        if (!(trident.getShooter() instanceof LivingEntity)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) trident.getShooter();
        ItemStack item = trident.getItem();
        assert shooter != null;

        SkillHelper.getSkillsOnItem(item)
                .forEach((skill, level) -> skill.onTridentLaunch(shooter, trident, level, event));
    }

    /**
     * Called when a player blocks an attack with a shield.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDeflect(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player blocker = (Player) event.getEntity();

        LivingEntity attacker = ItemUtils.tryAsPlayer(event.getDamager());

        if (attacker == null) {
            return;
        }

        if (!blocker.isBlocking()) {
            return;
        }

        Map<AbstractSwSkill, Integer> skills = blocker.getInventory().getItemInMainHand().getType() == Material.SHIELD
                ? SkillHelper.getSkillsOnMainhand(blocker)
                : SkillHelper.getSkillsOnOffhand(blocker);

        skills.forEach((skill, level) -> skill.onDeflect(blocker, attacker, level, event));
    }

    /**
     * 将弓绑定到箭的元数据上
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLaunchArrow(ProjectileLaunchEvent event) {
        Projectile arrow = event.getEntity();

        if (!(arrow instanceof Arrow)) {
            return;
        }

        if (!(arrow.getShooter() instanceof LivingEntity)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        if (shooter.getEquipment() == null) {
            return;
        }

        ItemStack item = shooter.getEquipment().getItemInMainHand();

        // 主手不是弓和弩，检查副手
        if (item.getType() != Material.BOW && item.getType() != Material.CROSSBOW) {
            item = shooter.getEquipment().getItemInOffHand();
        }

        if (item.getType() == Material.AIR || !item.hasItemMeta() || item.getItemMeta() == null) {
            return;
        }

        arrow.setMetadata("shot-from", new FixedMetadataValue(StrengthenWeapon.getInstance(), item));
    }

    /**
     * 钓上鱼时
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFish(PlayerFishEvent event) {
        if (PlayerFishEvent.State.CAUGHT_FISH != event.getState()) {
            return;
        }
        // 触发事件的玩家
        Player player = event.getPlayer();
        // 触发事件的钓鱼竿
        EntityEquipment playerEquipment = player.getEquipment();
        if (null == playerEquipment) {
            return;
        }
        // 主手不是钓鱼竿，检查副手
        ItemStack fishingRod = playerEquipment.getItemInMainHand().getType() == Material.FISHING_ROD
                ? playerEquipment.getItemInMainHand()
                : playerEquipment.getItemInOffHand();
        if (fishingRod.getType() == Material.AIR || !fishingRod.hasItemMeta() || fishingRod.getItemMeta() == null) {
            return;
        }
        // 钓上来了啥
        Item caughtItem = (Item) event.getCaught();
        if (null == caughtItem) {
            return;
        }
        // 鱼竿都有啥附魔
        Map<AbstractSwSkill, Integer> skills = SkillHelper.getSkillsOnItem(fishingRod);
        skills.forEach((skill, level) -> skill.onFishing(player, fishingRod, caughtItem, level, event));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        // 握着啥
        ItemStack holdInHand = event.getItem();
        // 触发事件的玩家
        Player player = event.getPlayer();

        if (null == holdInHand || holdInHand.getType() == Material.AIR) {
            return;
        }

        Map<AbstractSwSkill, Integer> skills = SkillHelper.getSkillsOnItem(holdInHand);

        skills.forEach((skill, level) -> skill.onMainHandRightClick(player, holdInHand, level, event));
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent potionSplashEvent) {
        ThrownPotion potion = potionSplashEvent.getEntity();

        Optional<ItemStack> triggerOptional = ItemUtils.getPotionTriggerItem(potion);
        if (!triggerOptional.isPresent()) {
            return;
        }

        ProjectileSource shooter = potion.getShooter();
        if (!(shooter instanceof LivingEntity)) {
            return;
        }

        ItemStack triggerItem = triggerOptional.get();

        Map<AbstractSwSkill, Integer> skills = SkillHelper.getSkillsOnItem(triggerItem);
        skills.forEach((skill, level) -> skill.onPotionSplash((LivingEntity) shooter, triggerItem, potion, level, potionSplashEvent));
    }
}
