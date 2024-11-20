package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.api.enums.ExpSource;

/**
 * Event called when a player gains experience
 * @deprecated use {@link studio.magemonkey.fabled.api.event.PlayerExperienceGainEvent} instead
 */
@Setter
@Getter
@Deprecated
@AllArgsConstructor
public class PlayerExperienceGainEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private              PlayerClass playerClass;
    private              double      amount;
    private              ExpSource   source;

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
