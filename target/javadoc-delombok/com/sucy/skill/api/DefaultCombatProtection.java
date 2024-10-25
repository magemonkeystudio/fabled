package com.sucy.skill.api;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @deprecated use {@link studio.magemonkey.fabled.api.DefaultCombatProtection} instead
 */
@Deprecated(forRemoval = true)
public class DefaultCombatProtection {
    /**
     * Checks if the event is a fake damage event
     *
     * @param event event to check
     * @return true if fake, false otherwise
     * @deprecated use {@link studio.magemonkey.fabled.api.DefaultCombatProtection#isFakeDamageEvent(EntityDamageByEntityEvent)} instead
     */
    @Deprecated(forRemoval = true)
    public static boolean isFakeDamageEvent(EntityDamageByEntityEvent event) {
        return studio.magemonkey.fabled.api.DefaultCombatProtection.isFakeDamageEvent(event);
    }

    /**
     * Checks if the event was externally cancelled
     *
     * @param event event to check
     * @return true if externally cancelled, false otherwise
     * @deprecated use {@link studio.magemonkey.fabled.api.DefaultCombatProtection#isExternallyCancelled(EntityDamageByEntityEvent)} instead
     */
    @Deprecated(forRemoval = true)
    public static boolean isExternallyCancelled(EntityDamageByEntityEvent event) {
        return studio.magemonkey.fabled.api.DefaultCombatProtection.isExternallyCancelled(event);
    }
}
