/**
 * SkillAPI
 * com.sucy.skill.api.event.SkillPushEvent
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2021 Travja
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

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

/**
 * An event for when an entity is healed by
 * another entity with the use of a skill.
 */
public class SkillPushEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Retrieves the entity initiating the push event.
     *
     * @return entity that is doing the pushing.
     */
    @Getter
    private final LivingEntity caster;
    /**
     * Retrieves the entity getting pushed.
     *
     * @return entity that is getting pushing.
     */
    @Getter
    private final LivingEntity target;
    /**
     * Gets the {@link Vector} of the push.
     *
     * @return the velocity at which the push is happening.
     */
    @Getter
    private       Vector       velocity;
    private       boolean      cancelled;

    /**
     * Initializes a new event
     *
     * @param caster   entity doing the pushing
     * @param target   entity receiving the push
     * @param velocity the amount of damage dealt
     */
    public SkillPushEvent(LivingEntity caster, LivingEntity target, Vector velocity) {
        this.caster = caster;
        this.target = target;
        this.velocity = velocity;
        this.cancelled = false;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Sets the {@link Vector} the push will use.
     *
     * @param velocity
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    /**
     * Checks whether the event is cancelled
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
}
