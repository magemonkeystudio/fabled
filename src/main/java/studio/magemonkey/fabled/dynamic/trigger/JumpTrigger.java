package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class JumpTrigger implements Trigger<PlayerMoveEvent> {

    @Override
    public String getKey() {
        return "JUMP";
    }

    @Override
    public Class<PlayerMoveEvent> getEvent() {
        return PlayerMoveEvent.class;
    }

    @Override
    public boolean shouldTrigger(PlayerMoveEvent event, int level, Settings settings) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();
        if (from.getBlockY() < to.getBlockY() && !player.isSwimming() && !player.isFlying()){
            return true;
        }
        return false;
    }

    @Override
    public void setValues(PlayerMoveEvent event, final CastData data) {

    }

    @Override
    public LivingEntity getCaster(PlayerMoveEvent event) {
        return event.getPlayer();
    }

    @Override
    public LivingEntity getTarget(PlayerMoveEvent event, Settings settings) {
        return event.getPlayer();
    }
}
