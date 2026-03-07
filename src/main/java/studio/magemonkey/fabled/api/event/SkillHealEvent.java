/**
 * Fabled
 * studio.magemonkey.fabled.api.event.SkillHealEvent
 * <p>
 * The MIT License (MIT)
 * <p>
 * © 2026 VoidEdge
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
import org.bukkit.event.entity.EntityRegainHealthEvent;

/**
 * An event for when an entity is healed by
 * another entity with the use of a skill.
 */
public class SkillHealEvent extends EntityRegainHealthEvent {
    /**
     * -- GETTER --
     * Retrieves the entity that dealt the heal
     *
     * @return entity that dealt the heal
     */
    @Getter
    private final LivingEntity healer;
    /**
     * -- GETTER --
     * Retrieves the entity that received the heal
     *
     * @return entity that received the heal
     */
    @Getter
    private final LivingEntity target;
    /**
     * -- GETTER --
     * Retrieves the amount of health healed
     *
     * @return amount of health healed
     * -- SETTER --
     * Sets the amount of health healed
     * @param amount amount of health healed
     */
    @Getter
    @Setter
    private double amount;
    private       boolean      cancelled;

    /**
     * Initializes a new event
     *
     * @param healer entity dealing the heal
     * @param target entity receiving the heal
     * @param amount the amount of health healed
     */
    public SkillHealEvent(LivingEntity healer, LivingEntity target, double amount) {
        super(target, amount, RegainReason.CUSTOM);
        this.healer = healer;
        this.target = target;
        this.amount = amount;
        this.cancelled = false;
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
}
