package com.promcteam.fabled.api.event;

import com.promcteam.fabled.api.player.PlayerCombos;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerComboStepEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCombos playerCombos;

    public PlayerComboStepEvent(PlayerCombos playerCombos) {
        this.playerCombos = playerCombos;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerCombos getPlayerCombos() {return playerCombos;}
}
