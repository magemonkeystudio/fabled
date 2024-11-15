package studio.magemonkey.fabled.api.event;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.dynamic.TempEntity;

/**
 * Event call when player block damage with a shield
 */
public class PlayerBlockDamageEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private              Entity      source;
    @Getter
    private final        double      damage;
    @Getter
    private              String      type     = "melee";


    public PlayerBlockDamageEvent(
            Entity damager,
            PlayerStatisticIncrementEvent statEvent) {
        super(statEvent.getPlayer());
        source = damager;
        if (source instanceof Projectile) {
            type = "projectile";
            ProjectileSource ps = ((Projectile) source).getShooter();
            if (ps instanceof BlockProjectileSource) {
                Location locTarget = ((BlockProjectileSource) ps).getBlock().getLocation();
                source = new TempEntity(locTarget);
            } else source = (Entity) ps;
        }
        damage = (statEvent.getNewValue() - statEvent.getPreviousValue()) / 10D;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
