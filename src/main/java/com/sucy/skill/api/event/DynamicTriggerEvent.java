package com.sucy.skill.api.event;

import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicTriggerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity caster;
    private final DynamicSkill skill;
    private final Event event;
    private final String triggerKey;

    public DynamicTriggerEvent(LivingEntity caster, DynamicSkill skill, @Nullable Event event, String triggerKey) {
        this.caster = caster;
        this.skill = skill;
        this.event = event;
        this.triggerKey = triggerKey.toLowerCase();
    }

    @NotNull
    public LivingEntity getCaster() {
        return caster;
    }

    @NotNull
    public DynamicSkill getSkill() {
        return skill;
    }

    @Nullable
    public Event getEvent() {
        return event;
    }

    @NotNull
    public String getTrigger() {
        return triggerKey;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
