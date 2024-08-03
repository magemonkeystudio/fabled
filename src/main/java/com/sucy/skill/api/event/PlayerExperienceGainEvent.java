package com.sucy.skill.api.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a player gains experience
 * @deprecated use {@link studio.magemonkey.fabled.api.event.PlayerExperienceGainEvent} instead
 */
@Setter
@Getter
@Deprecated
public class PlayerExperienceGainEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private              boolean     cancelled;

    /**
     * @return gets the handlers for the event
     */
    @NotNull
    @Override
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
