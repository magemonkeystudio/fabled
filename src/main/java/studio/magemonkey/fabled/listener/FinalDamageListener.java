/**
 * Fabled
 * studio.magemonkey.fabled.listener.FinalDamageListener
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
package studio.magemonkey.fabled.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.codex.api.damage.FinalDamageEvent;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.PhysicalDamageEvent;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.mechanic.ImmunityMechanic;

import java.util.function.DoubleUnaryOperator;

/**
 * Applies Fabled's damage changes to the final damage of a hit.
 * <p>
 * Codex fires {@link FinalDamageEvent} for every hit: a damage plugin (Divinity) fires it with its calculated
 * damage, and otherwise Codex's fallback fires it with the Bukkit event's damage and writes the result back.
 * Fabled never writes the Bukkit event's damage itself, so its changes land on the damage the hit deals and
 * a damage plugin neither overwrites them nor counts them into its own input.
 * <p>
 * In order: immunity multipliers ({@link EventPriority#NORMAL}), the defense-&lt;cause&gt; attributes
 * ({@code AttributeListener}, {@link EventPriority#HIGH}), then {@link PhysicalDamageEvent} for physical hits
 * ({@link EventPriority#HIGHEST}), whose listeners (physical triggers, attributes, buffs, statuses) change the
 * final damage. Environmental triggers fire on this event at {@link EventPriority#HIGHEST} after it.
 */
public class FinalDamageListener extends FabledListener {

    /**
     * Scales the final damage of a hit. Negative and NaN results become 0, which the final damage can't hold.
     *
     * @param event   the final damage event
     * @param scaling the scaling to apply to the total damage
     */
    public static void scale(@NotNull FinalDamageEvent event, @NotNull DoubleUnaryOperator scaling) {
        double before = event.getTotalDamage();
        double after  = scaling.applyAsDouble(before);
        if (Double.compare(before, after) == 0) return;
        event.setTotalDamage(Double.isNaN(after) ? 0 : Math.max(0, after));
    }

    /**
     * Damage type immunities that reduce damage instead of blocking it. Full immunities cancel the hit
     * before it is calculated, see {@link MainListener#onDamage}.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onImmunity(FinalDamageEvent event) {
        LivingEntity victim = event.getVictim();
        if (victim == null || !FlagManager.hasFlag(victim, "immune:" + event.getCause().name())) return;

        double multiplier = Fabled.getMetaDouble(victim, ImmunityMechanic.META_KEY);
        if (multiplier <= 0) event.setCancelled(true);
        else scale(event, damage -> damage * multiplier);
    }

    /**
     * Launches physical damage events to differentiate skill damage from physical damage, on the final damage,
     * and writes their result back to it.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPhysicalDamage(FinalDamageEvent event) {
        // Skill damage is still flagged here, as the event is fired from within the Bukkit damage call the
        // skill makes
        if (!(event.getDamageEvent() instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getDamageEvent();
        LivingEntity              victim      = event.getVictim();
        double                    damage      = event.getTotalDamage();
        if (Skill.isSkillDamage()
                || DefaultCombatProtection.isFakeDamageEvent(damageEvent)
                || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM
                || victim == null
                || damage <= 0) {
            return;
        }

        LivingEntity damager = event.getAttacker();
        if (damager == null) damager = ListenerUtil.getDamager(damageEvent);
        boolean projectile = event.getProjectile() != null || damageEvent.getDamager() instanceof Projectile;

        PhysicalDamageEvent physical = new PhysicalDamageEvent(damager, victim, damage, projectile);
        Bukkit.getPluginManager().callEvent(physical);
        if (physical.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        scale(event, ignored -> physical.getDamage());
    }
}
