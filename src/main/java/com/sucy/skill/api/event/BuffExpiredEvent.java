package com.sucy.skill.api.event;

import com.sucy.skill.api.util.Buff;
import com.sucy.skill.api.util.BuffType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BuffExpiredEvent extends Event {

    private static final HandlerList  handlers = new HandlerList();
    private final Buff buff;
    private final BuffType type;
    private final LivingEntity entity;

    public BuffExpiredEvent(LivingEntity entity, Buff buff, BuffType type) {
        this.entity = entity;
        this.buff = buff;
        this.type = type;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Buff getBuff() {
        return buff;
    }

    public BuffType getType() {
        return type;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
