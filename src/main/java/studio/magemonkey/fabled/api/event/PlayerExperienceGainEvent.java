/**
 * Fabled
 * studio.magemonkey.fabled.api.event.PlayerExperienceGainEvent
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

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

/**
 * Event called when a player gains class experience
 */
public class PlayerExperienceGainEvent extends com.sucy.skill.api.event.PlayerExperienceGainEvent {
    /**
     * -- GETTER --
     *
     * @return player's class that is receiving the experience
     */
    @Getter
    private              PlayerClass playerClass;
    /**
     * -- GETTER --
     *
     * @return where the experience came from
     */
    @Getter
    private              ExpSource   source;
    private              double      amount;

    /**
     * Constructor
     *
     * @param playerClass class of the player gaining experience
     * @param amount      amount of experience being gained
     * @param source      source of the experience
     */
    public PlayerExperienceGainEvent(PlayerClass playerClass, double amount, ExpSource source) {
        this.playerClass = playerClass;
        this.amount = amount;
        this.source = source;
        setCancelled(false);
    }

    /**
     * @return data of the player gaining experience
     */
    public PlayerData getPlayerData() {
        return playerClass.getPlayerData();
    }

    /**
     * @return amount of experience being gained
     */
    public double getExp() {
        return amount;
    }

    /**
     * Sets the amount of experience being gained
     *
     * @param amount new amount of experience
     * @throws IllegalArgumentException if experience is less than 0
     */
    public void setExp(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }

        this.amount = amount;
    }

    /**
     * @return whether the gain in experience is cancelled
     */
    @Override
    public boolean isCancelled() {
        return super.isCancelled();
    }

    /**
     * Sets whether the gain in experience is cancelled
     *
     * @param cancelled true/false
     */
    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);
    }

    /**
     * @return gets the handlers for the event
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return super.getHandlers();
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return com.sucy.skill.api.event.PlayerExperienceGainEvent.getHandlerList();
    }
}
