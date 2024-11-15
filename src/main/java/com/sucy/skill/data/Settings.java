package com.sucy.skill.data;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.Fabled;

/**
 * Settings for the plugin
 * @deprecated in favor of {@link Fabled#getSettings()}
 */
@Deprecated(forRemoval = true)
public class Settings {

    /**
     * Checks if two entities are allies
     * @param attacker the attacker
     * @param target    the target
     * @return true if they are allies, false otherwise
     */
    public boolean isAlly(LivingEntity attacker, LivingEntity target) {
        return Fabled.getSettings().isAlly(attacker, target);
    }
}
