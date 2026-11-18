package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import studio.magemonkey.codex.api.damage.FinalDamageEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.listener.FinalDamageListener;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.EnvironmentalTrigger
 * <p>
 * Fires on the final damage of a hit (Codex's {@link FinalDamageEvent}), so its immediate buff applies to the
 * damage the hit actually deals, whether a damage plugin calculated it or not.
 */
public class EnvironmentalTrigger implements Trigger<FinalDamageEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "ENVIRONMENT_DAMAGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<FinalDamageEvent> getEvent() {
        return FinalDamageEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final FinalDamageEvent event, final int level, final Settings settings) {
        if (event.getDamageEvent() instanceof EntityDamageByEntityEvent
                && DefaultCombatProtection.isFakeDamageEvent((EntityDamageByEntityEvent) event.getDamageEvent())) {
            return false;
        }

        List<String> types = settings.getStringList("type").stream()
                .map(str -> str.replace(' ', '_').toUpperCase(Locale.US))
                .collect(Collectors.toList());

        boolean isAny = types.contains("ANY");

        return isAny || types.contains(event.getCause().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final FinalDamageEvent event, final CastData data) {
        data.put("api-taken", event.getTotalDamage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final FinalDamageEvent event) {
        return event.getVictim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final FinalDamageEvent event, final Settings settings) {
        return getCaster(event);
    }

    /**
     * Handles applying other effects after the skill resolves
     *
     * @param event event details
     * @param skill skill to resolve
     */
    @Override
    public void postProcess(final FinalDamageEvent event, final DynamicSkill skill) {
        FinalDamageListener.scale(event, skill::applyImmediateBuff);
    }
}
