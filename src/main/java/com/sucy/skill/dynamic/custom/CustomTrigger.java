package com.sucy.skill.dynamic.custom;

import com.sucy.skill.dynamic.trigger.Trigger;
import org.bukkit.event.Event;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.custom.CustomTrigger
 */
public interface CustomTrigger<E extends Event> extends Trigger<E>, CustomComponent {
}
