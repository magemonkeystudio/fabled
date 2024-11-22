package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.Statistic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

public class JumpTrigger implements Trigger<PlayerStatisticIncrementEvent> {

    @Override
    public String getKey() {
        return "JUMP";
    }

    @Override
    public Class<PlayerStatisticIncrementEvent> getEvent() {
        return PlayerStatisticIncrementEvent.class;
    }

    @Override
    public boolean shouldTrigger(PlayerStatisticIncrementEvent event, int level, Settings settings) {
        LivingEntity player = (LivingEntity) event.getPlayer();
        if (player.isClimbing()) {
            return false;
        }
        if (!player.isOnGround()) {
            return false;
        }
        Statistic statistic = event.getStatistic();
        return statistic == Statistic.JUMP;
    }

    @Override
    public void setValues(PlayerStatisticIncrementEvent event, final CastData data) {

    }

    @Override
    public LivingEntity getCaster(PlayerStatisticIncrementEvent event) {
        return event.getPlayer();
    }

    @Override
    public LivingEntity getTarget(PlayerStatisticIncrementEvent event, Settings settings) {
        return event.getPlayer();
    }
}
