package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.PlayerJoinEvent;
import studio.magemonkey.fabled.api.player.FabledPlayer;
import org.bukkit.entity.LivingEntity;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.OnFirstJoinTrigger
 * 
 * This trigger is activated when a player joins the server for the first time.
 */
public class OnFirstJoinTrigger implements Trigger<PlayerJoinEvent> {

    @Override
    public String getKey() {
        return "ON_FIRST_JOIN";
    }

    @Override
    public Class<PlayerJoinEvent> getEvent() {
        return PlayerJoinEvent.class;
    }

    @Override
    public boolean shouldTrigger(final PlayerJoinEvent event, final int level, final Settings settings) {
        // Check if this is the first time the player has joined the server
        if (event.getPlayer().hasPlayedBefore()) {
            return false; // Not the first join
        }

        // At this point, the player is in their first join event
        FabledPlayer fabledPlayer = FabledPlayer.getPlayer(event.getPlayer());

        // Mana and cooldown checks can now be applied
        boolean hasEnoughMana = hasRequiredMana(fabledPlayer, settings);
        boolean notOnCooldown = !isOnCooldown(fabledPlayer, settings);

        // Only trigger if the player meets all conditions
        return hasEnoughMana && notOnCooldown;
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
            long lastJoin = player.getLastJoinTime(); // Assumindo que esse método existe
            long cooldownTime = settings.getLong("cooldown-time", 60000); // Tempo em milissegundos
            return (System.currentTimeMillis() - lastJoin) < cooldownTime;
        }
        return false; // Não está em cooldown se a verificação estiver desativada
    }

    @Override
    public void setValues(final PlayerJoinEvent event, final CastData data) {
        // Set event-related data, such as the player's name
        data.put("player-name", event.getPlayer().getName());
    }

    @Override
    public LivingEntity getCaster(final PlayerJoinEvent event) {
        return event.getPlayer();
    }

    @Override
    public LivingEntity getTarget(final PlayerJoinEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
