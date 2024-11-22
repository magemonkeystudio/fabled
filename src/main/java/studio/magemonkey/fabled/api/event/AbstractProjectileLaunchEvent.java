package studio.magemonkey.fabled.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.projectile.CustomProjectile;

public class AbstractProjectileLaunchEvent<T extends CustomProjectile> extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final T projectile;

    /**
     * <p>Initializes a new event.</p>
     *
     * @param projectile the projectile that hit something
     */
    public AbstractProjectileLaunchEvent(T projectile) {
        this.projectile = projectile;
    }

    /**
     * <p>Retrieves the projectile</p>
     *
     * @return the projectile that hit something
     */
    public T getProjectile() {
        return projectile;
    }

    /**
     * <p>Bukkit method for taking care of the event handlers.</p>
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * <p>Bukkit method for taking care of the event handlers.</p>
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
