package com.promcteam.fabled.api.event;

import com.promcteam.fabled.api.player.PlayerData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
@Setter
public class PlayerAttributeChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    /**
     * Gets the PlayerData associated with the event
     *
     * @return PlayerData receiving the refund
     */
    private final        PlayerData  playerData;
    /**
     * Gets the name of the attribute that was refunded
     *
     * @return name of the refunded attribute
     */
    private final        String      attribute;
    /**
     * Gets the amount of change in the attribute. This will be negative
     * if the attribute was refunded and positive if it was upgraded.
     *
     * @return amount of change
     */
    private              int         change;
    /**
     * {@inheritDoc}
     */
    private              boolean     cancelled;

    public PlayerAttributeChangeEvent(PlayerData playerData, String attribute, int change) {
        super(playerData.getPlayer());
        this.playerData = playerData;
        this.attribute = attribute;
        this.change = change;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
