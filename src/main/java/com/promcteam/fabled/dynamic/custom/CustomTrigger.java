package com.promcteam.fabled.dynamic.custom;

import com.promcteam.fabled.dynamic.trigger.Trigger;
import org.bukkit.event.Event;

/**
 * Fabled © 2024
 * com.promcteam.fabled.dynamic.custom.CustomTrigger
 */
public interface CustomTrigger<E extends Event> extends Trigger<E>, CustomComponent {
}
