package com.sucy.skill.api.event;

import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player changes classes
 * @deprecated use {@link studio.magemonkey.fabled.api.event.PlayerClassChangeEvent} instead
 */
@Getter
@Deprecated
@AllArgsConstructor
public class PlayerClassChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerClass playerClass;
    private final RPGClass    previousClass;
    private final RPGClass    newClass;

    public PlayerData getPlayerData() {
        return playerClass.getPlayerData();
    }

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
