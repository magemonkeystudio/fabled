package com.sucy.skill.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player changes classes
 * @deprecated use {@link studio.magemonkey.fabled.api.event.PlayerClassChangeEvent} instead
 */
@Deprecated
public class PlayerClassChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
