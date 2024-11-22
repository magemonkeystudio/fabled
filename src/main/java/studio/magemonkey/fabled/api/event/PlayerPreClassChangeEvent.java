package studio.magemonkey.fabled.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.api.event.PlayerPreClassChangeEvent
 */
public class PlayerPreClassChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final        PlayerData  playerData;
    private final        PlayerClass playerClass;
    private final        FabledClass previousClass;
    private final        FabledClass newClass;
    private              boolean     cancelled;

    /**
     * Constructor
     *
     * @param playerData    player information
     * @param playerClass   data of the player changing classes
     * @param previousClass previous class of the player (null if wasn't a profession)
     * @param newClass      new class of the player (null if using the reset command)
     */
    public PlayerPreClassChangeEvent(PlayerData playerData,
                                     PlayerClass playerClass,
                                     FabledClass previousClass,
                                     FabledClass newClass) {
        this.playerData = playerData;
        this.playerClass = playerClass;
        this.previousClass = previousClass;
        this.newClass = newClass;
        this.cancelled = false;
    }

    /**
     * @return modified player class (null if not professed before)
     */
    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    /**
     * @return Data of the player changing classes
     */
    public PlayerData getPlayerData() {
        return playerData;
    }

    /**
     * @return previous class of the player (null if not professed before)
     */
    public FabledClass getPreviousClass() {
        return previousClass;
    }

    /**
     * @return new class of the player
     */
    public FabledClass getNewClass() {
        return newClass;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        cancelled = b;
    }
}
