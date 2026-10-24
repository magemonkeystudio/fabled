package studio.magemonkey.fabled.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.api.event.PlayerSkillCooldownEvent
 * <p>
 * Fired whenever a skill's cooldown value changes (started, refreshed, or
 * shortened/lengthened by an effect). Fires after the change has already
 * been applied, so {@link PlayerSkill#isOnCooldown()} and
 * {@link PlayerSkill#getCooldownLeft()} reflect the new state by the time
 * listeners run. Not cancellable.
 */
public class PlayerSkillCooldownEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final        PlayerSkill skill;

    private PlayerSkillCooldownEvent(final PlayerSkill skill) {
        this.skill = skill;
    }

    public static void invoke(final PlayerSkill skill) {
        Bukkit.getPluginManager().callEvent(new PlayerSkillCooldownEvent(skill));
    }

    /**
     * @return player owning the skill whose cooldown changed
     */
    public PlayerData getPlayerData() {
        return skill.getPlayerData();
    }

    /**
     * @return skill whose cooldown changed
     */
    public PlayerSkill getSkill() {
        return skill;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
