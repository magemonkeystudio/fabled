package com.sucy.skill.api.event;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * An event for every tick projectile
 * did not hit neither entity nor block
 */
public class ProjectileTickEvent extends EntityEvent {
    private static final HandlerList  handlers  = new HandlerList();
    /**
     * Retrieves the projectile entity
     */
    @Getter
    private final Projectile projectile;

    /**
     * Initializes a new event
     *
     * @param shooter        entity dealing the damage
     * @param projectile     projectile entity
     */
    public ProjectileTickEvent (LivingEntity shooter, Projectile projectile) {
        super(shooter);
        this.projectile = projectile;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
