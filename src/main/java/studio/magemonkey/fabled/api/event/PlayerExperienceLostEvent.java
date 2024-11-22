/**
 * Fabled
 * studio.magemonkey.fabled.api.event.PlayerExperienceLostEvent
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

/**
 * Event called when a player loses class experience
 */
public class PlayerExperienceLostEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final        PlayerClass playerClass;
    private              boolean     cancelled;
    private              boolean     changeLevel;
    private              double      amount;

    /**
     * Constructor
     *
     * @param playerClass class of the player losing experience
     * @param amount      amount of experience being lost
     */
    public PlayerExperienceLostEvent(PlayerClass playerClass, double amount, boolean changeLevel) {
        this.playerClass = playerClass;
        this.amount = amount;
        this.changeLevel = changeLevel;
        cancelled = false;
    }

    /**
     * @return data of the player losing experience
     */
    public PlayerData getPlayerData() {
        return playerClass.getPlayerData();
    }

    /**
     * @return player's class that is receiving the experience
     */
    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    /**
     * @return amount of experience being lost
     */
    public double getExp() {
        return amount;
    }

    /**
     * @return whether to lower the level if the exp lost exceeds the current exp,
     * or to cap at 0 exp and keep the current level
     */
    public boolean isLevelChangeAllowed() {return changeLevel;}

    /**
     * Sets the amount of experience being gained
     *
     * @param amount new amount of experience
     * @throws IllegalArgumentException if experience is less than 0
     */
    public void setExp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }

        this.amount = amount;
    }

    /**
     * Sets whether to lower the level if the exp lost exceeds the current exp,
     * or to cap at 0 exp and keep the current level
     */
    public void setLevelChangeAllowed(boolean changeLevel) {this.changeLevel = changeLevel;}

    /**
     * @return whether the gain in experience is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether the gain in experience is cancelled
     *
     * @param cancelled true/false
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return gets the handlers for the event
     */
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
