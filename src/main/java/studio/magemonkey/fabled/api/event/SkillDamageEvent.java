/**
 * Fabled
 * studio.magemonkey.fabled.api.event.SkillDamageEvent
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
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.skills.Skill;

/**
 * An event for when an entity is damaged by
 * another entity with the use of a skill.
 */
public class SkillDamageEvent extends Event implements Cancellable {
    private static final HandlerList  handlers  = new HandlerList();
    /**
     * Retrieves the entity that dealt the damage
     *
     * @return entity that dealt the damage
     */
    @Getter
    private final        LivingEntity damager;
    /**
     * Retrieves the entity that received the damage
     *
     * @return entity that received the damage
     */
    @Getter
    private final        LivingEntity target;
    @Getter
    private final        String       classification;
    /**
     * @return skill used to deal the damage
     */
    @Getter
    private final        Skill        skill;
    /**
     * -- GETTER --
     *  Retrieves the amount of damage dealt
     *
     * @return amount of damage dealt
     * -- SETTER --
     *  Sets the amount of damage dealt
     *
     * @param damage amount of damage dealt
     */
    @Setter
    @Getter
    private              double       damage;
    @Getter
    @Setter
    private              boolean      knockback;
    @Getter
    private              boolean      ignoreDivinity;
    private              boolean      cancelled = false;

    /**
     * Initializes a new event
     *
     * @param skill          skill used to deal damage
     * @param damager        entity dealing the damage
     * @param target         entity receiving the damage
     * @param damage         the amount of damage dealt
     * @param classification the damage type to use
     * @param knockback      whether to apply knockback to the target
     * @param ignoreDivinity whether to ignore divinity
     */
    public SkillDamageEvent(Skill skill, LivingEntity damager, LivingEntity target, double damage,
                            String classification, boolean knockback, boolean ignoreDivinity) {
        this.skill = skill;
        this.damager = damager;
        this.target = target;
        this.damage = damage;
        this.classification = classification;
        this.knockback = knockback;
        this.ignoreDivinity = ignoreDivinity;
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

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
