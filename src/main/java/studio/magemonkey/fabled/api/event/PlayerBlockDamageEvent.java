package studio.magemonkey.fabled.api.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event call when player block damage with a shield
 */
@Getter
public class PlayerBlockDamageEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final        Entity      source;
    private final        double      damage;
    private final        String      type;


    public PlayerBlockDamageEvent(
            Player player,
            Entity damager,
            double damageBlocked,
            String type
    ) {
        super(player);
        this.source = damager;
        this.damage = damageBlocked;

        if (type == null || (!type.equals("melee") && !type.equals("projectile"))) type = "melee";
        this.type = type;
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
