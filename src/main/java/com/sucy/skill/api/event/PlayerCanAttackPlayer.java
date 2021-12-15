/**
 * SkillAPI
 * com.sucy.skill.api.event.PhysicalDamageEvent
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

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Judge this event when attacking players
 * You can cancel this event to customize the judgment rules
 */
public class PlayerCanAttackPlayer extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity damager;
    private LivingEntity target;
    private boolean canAttack;
    private boolean cancelled;

    /**
     * Initializes a new event
     *
     * @param damager entity dealing the damage
     * @param target  entity receiving the damage
     */
    public PlayerCanAttackPlayer(LivingEntity damager, LivingEntity target, boolean canAttack) {
        this.damager = damager;
        this.target = target;
        this.canAttack = canAttack;
        this.cancelled = false;
    }

    /**
     * Retrieves the entity that dealt the damage
     *
     * @return entity that dealt the damage
     */
    public LivingEntity getDamager() {
        return damager;
    }

    /**
     * Retrieves the entity that received the damage
     *
     * @return entity that received the damage
     */
    public LivingEntity getTarget() {
        return target;
    }

    public boolean isCanAttack() {
        return isCanAttack();
    }

    /**
     * Checks whether or not the event is cancelled
     *
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state of the event
     *
     * @param cancelled the cancelled state of the event
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
