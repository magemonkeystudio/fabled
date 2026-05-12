package studio.magemonkey.fabled.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.player.PlayerData;

/**
 * Fired after Fabled finishes calculating a player's max mana (including class bonuses and attribute scaling).
 * Listeners may call {@link #setMaxMana(double)} to apply additional flat bonuses.
 */
public class PlayerMaxManaChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final        PlayerData  playerData;
    private              double      maxMana;

    public PlayerMaxManaChangeEvent(PlayerData playerData, double maxMana) {
        this.playerData = playerData;
        this.maxMana = maxMana;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = Math.max(0, maxMana);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
