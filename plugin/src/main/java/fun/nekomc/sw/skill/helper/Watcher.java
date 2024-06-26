package fun.nekomc.sw.skill.helper;

import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * created: 2022/3/18 21:48
 *
 * @author Chiru
 * @see <a href="https://github.com/Auxilor/EcoEnchants">参考 EcoEnchants</a>
 */
@SuppressWarnings("unused")
public interface Watcher {

    /**
     * Called when an entity shoots another entity with an arrow.
     *
     * @param attacker The shooter.
     * @param victim   The victim.
     * @param arrow    The arrow entity.
     * @param level    The level of the enchantment on the arrow.
     * @param event    The event that called this watcher.
     */
    default void onArrowDamage(@NotNull final LivingEntity attacker,
                               @NotNull final LivingEntity victim,
                               @NotNull final Arrow arrow,
                               final int level,
                               @NotNull final EntityDamageByEntityEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity damages another entity with a trident throw.
     *
     * @param attacker The shooter.
     * @param victim   The victim.
     * @param trident  The trident entity.
     * @param level    The level of the enchantment on the trident.
     * @param event    The event that called this watcher.
     */
    default void onTridentDamage(@NotNull final LivingEntity attacker,
                                 @NotNull final LivingEntity victim,
                                 @NotNull final Trident trident,
                                 final int level,
                                 @NotNull final EntityDamageByEntityEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when a player jumps.
     *
     * @param player The player.
     * @param level  The level of the enchantment found on the player's armor.
     * @param event  The event that called this watcher.
     */
    default void onJump(@NotNull final Player player,
                        final int level,
                        @NotNull final PlayerMoveEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity attacks another entity with a melee attack.
     *
     * @param attacker The attacker.
     * @param victim   The victim.
     * @param level    The level of the enchantment found on the attacker's weapon.
     * @param event    The event that called this watcher.
     */
    default void onMeleeAttack(@NotNull final LivingEntity attacker,
                               @NotNull final LivingEntity victim,
                               final int level,
                               @NotNull final EntityDamageByEntityEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity shoots a bow.
     *
     * @param shooter The entity that shot the bow.
     * @param arrow   The arrow that was shot.
     * @param level   The level of the enchantment found on the bow.
     * @param event   The event that called this watcher.
     */
    default void onBowShoot(@NotNull final LivingEntity shooter,
                            @NotNull final Arrow arrow,
                            final int level,
                            @NotNull final EntityShootBowEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity shoots a projectile.
     *
     * @param shooter    The entity that shot the bow.
     * @param projectile The projectile that was shot.
     * @param level      The level of the enchantment found on the projectile.
     * @param event      The event that called this watcher.
     */
    default void onProjectileLaunch(@NotNull final LivingEntity shooter,
                                    @NotNull final Projectile projectile,
                                    final int level,
                                    @NotNull final ProjectileLaunchEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity takes fall damage.
     *
     * @param faller The entity that took the fall damage.
     * @param level  The level of the enchantment found on the entity's armor.
     * @param event  The event that called this watcher.
     */
    default void onFallDamage(@NotNull final LivingEntity faller,
                              final int level,
                              @NotNull final EntityDamageEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an arrow hits a block or entity.
     *
     * @param shooter The entity that shot the arrow.
     * @param level   The level of the enchantment found on the arrow.
     * @param event   The event that called this watcher.
     */
    default void onArrowHit(@NotNull final LivingEntity shooter,
                            final int level,
                            @NotNull final ProjectileHitEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when a trident hits a block or entity.
     *
     * @param shooter The entity that threw the trident.
     * @param level   The level of the enchantment found on the trident.
     * @param event   The event that called this watcher.
     */
    default void onTridentHit(@NotNull final LivingEntity shooter,
                              final int level,
                              @NotNull final ProjectileHitEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when a player breaks a block.
     *
     * @param player The player.
     * @param block  The block that was broken.
     * @param level  The level of the enchantment found on the player's main hand item.
     * @param event  The event that called this watcher.
     */
    default void onBlockBreak(@NotNull final Player player,
                              @NotNull final Block block,
                              final int level,
                              @NotNull final BlockBreakEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity takes damage wearing armor.
     *
     * @param victim The entity that took damage.
     * @param level  The level of the enchantment found on the entity's armor.
     * @param event  The event that called this watcher.
     */
    default void onDamageWearingArmor(@NotNull final LivingEntity victim,
                                      final int level,
                                      @NotNull final EntityDamageEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when a player damages a block.
     *
     * @param player The player that damaged the block.
     * @param block  The damaged block.
     * @param level  The level of the enchantment found on the player's main hand.
     * @param event  The event that called this watcher.
     */
    default void onDamageBlock(@NotNull final Player player,
                               @NotNull final Block block,
                               final int level,
                               @NotNull final BlockDamageEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when an entity throws a trident.
     *
     * @param shooter The entity that threw the trident.
     * @param trident The trident that was thrown.
     * @param level   The level of the enchantment found on the trident.
     * @param event   The event that called this watcher.
     */
    default void onTridentLaunch(@NotNull final LivingEntity shooter,
                                 @NotNull final Trident trident,
                                 final int level,
                                 @NotNull final ProjectileLaunchEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * Called when a player blocks an attack with a shield.
     *
     * @param blocker  The player that blocked the attack.
     * @param attacker The attacker.
     * @param level    The level of the enchantment found on the shield.
     * @param event    The event that called this watcher.
     */
    default void onDeflect(@NotNull final Player blocker,
                           @NotNull final LivingEntity attacker,
                           final int level,
                           @NotNull final EntityDamageByEntityEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * 当玩家成功钓上鱼时触发（鱼上钩后，成功收钩）
     *
     * @param player     钓鱼的玩家
     * @param fishingRod 使用的鱼竿
     * @param caught     钓上来的东西
     * @param godHasGift 神是否已有所赠与
     * @param level      鱼竿上指定附魔的等级
     * @param event      The event that called this watcher.
     */
    default void onFishing(@NotNull final Player player,
                           @NotNull final ItemStack fishingRod,
                           @NotNull final Item caught,
                           final boolean godHasGift,
                           final int level,
                           @NotNull final PlayerFishEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * 当玩家拿着含有指定附魔的物品右键时，此类附魔不会在非玩家实体上生效
     *
     * @param player     指定玩家
     * @param holdInHand 使用的物品
     * @param level      指定附魔的等级
     * @param event      The event that called this watcher.
     */
    default void onMainHandRightClick(@NotNull Player player,
                                      @NotNull ItemStack holdInHand,
                                      final int level,
                                      @NotNull PlayerInteractEvent event) {
        // Empty default as enchantments only override required watchers.
    }

    /**
     * 当玩家手持指定附魔的道具，触发药水投掷时
     *
     * @param shooter     触发投掷的实体（玩家）
     * @param triggerItem 触发药水投掷的物品
     * @param potion      投掷出的药水
     * @param level       指定附魔的等级
     * @param event       The event that called this watcher.
     */
    default void onPotionSplash(@NotNull LivingEntity shooter,
                                @NotNull ItemStack triggerItem,
                                @NotNull ThrownPotion potion,
                                final int level,
                                @NotNull PotionSplashEvent event) {
        // Empty default as enchantments only override required watchers.
    }

}
