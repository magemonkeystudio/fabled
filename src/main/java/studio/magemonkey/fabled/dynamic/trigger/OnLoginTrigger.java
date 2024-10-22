package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.PlayerJoinEvent;
import studio.magemonkey.fabled.api.player.FabledPlayer;
import org.bukkit.entity.LivingEntity;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.OnLoginTrigger
 * 
 * This trigger is activated when a player joins the server. It can check for
 * mana and cooldown requirements before allowing further actions.
 */
public class OnLoginTrigger implements Trigger<PlayerJoinEvent> {

    @Override
    public String getKey() {
        return "ON_LOGIN";
    }

    @Override
    public Class<PlayerJoinEvent> getEvent() {
        return PlayerJoinEvent.class;
    }

    @Override
    public boolean shouldTrigger(final PlayerJoinEvent event, final int level, final Settings settings) {
        FabledPlayer fabledPlayer = FabledPlayer.getPlayer(event.getPlayer());

        // Check for mana and cooldown using helper methods
        return hasRequiredMana(fabledPlayer, settings) && !isOnCooldown(fabledPlayer, settings);
    }

    /**
     * Checks if the player has the required mana to trigger the event.
     * 
     * @param player   the FabledPlayer object representing the player
     * @param settings the Settings object containing configuration values
     * @return true if the player has enough mana or if mana check is disabled
     */
    private boolean hasRequiredMana(FabledPlayer player, Settings settings) {
        boolean checkMana = settings.getBool("Mana", false);
        double requiredMana = settings.getDouble("mana-requirement", 0);
        return !checkMana || player.getMana() >= requiredMana;
    }

    /**
     * Checks if the player is currently on cooldown and prevents the event from 
     * being triggered if true.
     * 
     * @param player   the FabledPlayer object representing the player
     * @param settings the Settings object containing configuration values
     * @return true if the player is on cooldown
     */
    private boolean isOnCooldown(FabledPlayer player, Settings settings) {
        boolean checkCooldown = settings.getBool("Cooldown", false);
        if (checkCooldown) {
            // Implement your cooldown logic here. For example:
            long lastLogin = player.getLastLoginTime(); // Assumindo que esse método existe
            long cooldownTime = settings.getLong("cooldown-time", 60000); // Tempo em milissegundos
            return (System.currentTimeMillis() - lastLogin) < cooldownTime;
        }
        return false; // Não está em cooldown se a verificação estiver desativada
    }

    @Override
    public void setValues(final PlayerJoinEvent event, final CastData data) {
        // Store player's name in the data object
        data.put("player-name", event.getPlayer().getName());
    }

    @Override
    public LivingEntity getCaster(final PlayerJoinEvent event) {
        // The player who triggered the event is considered the caster
        return event.getPlayer();
    }

    @Override
    public LivingEntity getTarget(final PlayerJoinEvent event, final Settings settings) {
        // The event target is the player themselves
        return event.getPlayer();
    }
}
