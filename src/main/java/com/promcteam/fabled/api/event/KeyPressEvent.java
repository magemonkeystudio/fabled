package com.promcteam.fabled.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fabled © 2024
 * com.promcteam.fabled.api.event.KeyPressEvent
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KeyPressEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Key    key;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Key {
        LEFT, RIGHT, Q
    }
}
