/**
 * SkillAPI
 * com.sucy.skill.api.event.PlayerUpAttributeEvent
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.api.event;

import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * PlayerReadItemAttributeEvent
 * If you want to customize your own rules, you can cancel this event and make your own judgment!
 */
public class PlayerReadItemAttributeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private PlayerData player;
    private ItemStack itemStack;
    private String pattern;
    private boolean cancelled = false;

    /**
     * Constructor
     *
     * @param playerData data of the player raising the attribute
     */
    public PlayerReadItemAttributeEvent(PlayerData playerData, ItemStack itemStack, String pattern) {
        this.player = playerData;
        this.itemStack = itemStack;
        this.pattern = pattern;
    }

    /**
     * @return data of the player raising the attribute
     */
    public PlayerData getPlayerData() {
        return player;
    }

    /**
     * @return Get equipment reading rules
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @return Get the equipment that is acquiring attributes
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether or not the event is cancelled
     *
     * @param value true if cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean value) {
        cancelled = value;
    }

    /**
     * @return gets the handlers for the event
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
