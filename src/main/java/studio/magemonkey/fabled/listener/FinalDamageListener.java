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
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.PhysicalDamageEvent;
import studio.magemonkey.fabled.api.skills.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.DoubleUnaryOperator;

/**
 * Lets Fabled act on a damage plugin's final numbers.
 * <p>
 * When a damage plugin (Divinity) registered as a {@link FinalDamageEvent} provider will handle a hit, it
 * recalculates the damage and writes it to the Bukkit event itself, so anything Fabled wrote to the Bukkit
 * event earlier would be lost or counted into the plugin's input. For those hits Fabled leaves the Bukkit
 * event's damage alone and instead:
 * <ul>
 *     <li>queues its own scaling of the Bukkit event (immunities, cause defenses, environmental triggers)
 *     with {@link #scaleDamage} and applies it to the final damage, and</li>
 *     <li>fires {@link PhysicalDamageEvent} from the final damage, so physical triggers, attributes, buffs
 *     and statuses see and change the plugin's numbers.</li>
 * </ul>
 * Without such a provider, {@link #scaleDamage} writes to the Bukkit event straight away and
 * {@link MainListener#onPhysicalDamage} fires {@link PhysicalDamageEvent} as it always has.
 */
public class FinalDamageListener extends FabledListener {
    /**
     * Scaling queued for hits a provider will fire a {@link FinalDamageEvent} for. Weak, as a provider
     * fires none for a hit that is dodged or cancelled first.
     */
    private static final Map<EntityDamageEvent, List<DoubleUnaryOperator>> PENDING = new WeakHashMap<>();

    /**
     * Scales the damage of a Bukkit damage event. When a damage plugin will fire a {@link FinalDamageEvent}
     * for it, the scaling is applied to that event's final damage instead, as the plugin overwrites the
     * Bukkit event's damage.
     *
     * @param event   the Bukkit damage event
     * @param scaling the scaling to apply to the damage
     */
    public static void scaleDamage(@NotNull EntityDamageEvent event, @NotNull DoubleUnaryOperator scaling) {
        if (FinalDamageEvent.willFire(event)) {
            PENDING.computeIfAbsent(event, e -> new ArrayList<>()).add(scaling);
        } else {
            event.setDamage(scaling.applyAsDouble(event.getDamage()));
        }
    }

    /**
     * Applies queued scaling, then fires {@link PhysicalDamageEvent} with the same filters as
     * {@link MainListener#onPhysicalDamage}, and writes the result back to the final damage.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFinalDamage(FinalDamageEvent event) {
        applyPending(event);
        firePhysical(event);
    }

    private void applyPending(FinalDamageEvent event) {
        List<DoubleUnaryOperator> scaling = PENDING.remove(event.getDamageEvent());
        if (scaling == null) return;

        double before = event.getTotalDamage();
        double damage = before;
        for (DoubleUnaryOperator op : scaling) {
            damage = op.applyAsDouble(damage);
        }
        writeBack(event, before, damage);
    }

    private void firePhysical(FinalDamageEvent event) {
        // Same filters as MainListener#onPhysicalDamage. Skill damage is still flagged here, as the provider
        // fires this event from within the Bukkit damage call the skill makes
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
        writeBack(event, damage, physical.getDamage());
    }

    private static void writeBack(FinalDamageEvent event, double before, double after) {
        if (Double.compare(before, after) == 0) return;
        // The final damage can't hold a negative or NaN amount
        event.setTotalDamage(Double.isNaN(after) ? 0 : Math.max(0, after));
    }

    @Override
    public void cleanup() {
        PENDING.clear();
    }
}
