package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.PlayerJoinEvent;
import studio.magemonkey.fabled.api.player.FabledPlayer;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.OnFirstJoinTrigger
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
        FabledPlayer fabledPlayer = FabledPlayer.getPlayer(event.getPlayer());

        if (!event.getPlayer().hasPlayedBefore()) {
            // Verificação de mana
            boolean checkMana = settings.getBool("Mana", false);
            if (checkMana && fabledPlayer.getMana() < settings.getDouble("mana-requirement", 0)) {
                return false; // Cancela se o jogador não tiver "mana" suficiente
            }

            // Verificação de cooldown
            boolean checkCooldown = settings.getBool("Cooldown", false);
            if (checkCooldown) {
                // Lógica para verificar cooldown
                return false; // Cancela se estiver em cooldown
            }

            return true;
        }
        return false;
    }

    @Override
    public void setValues(final PlayerJoinEvent event, final CastData data) {
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
