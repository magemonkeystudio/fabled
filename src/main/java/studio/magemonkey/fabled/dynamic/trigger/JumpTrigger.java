package studio.magemonkey.fabled.dynamic.trigger;

import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;

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
        Player player = event.getPlayer();
        if (player.isClimbing()) {
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
