package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.dynamic.ItemChecker;

public class ConsumeTrigger implements Trigger<PlayerItemConsumeEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "CONSUME";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerItemConsumeEvent> getEvent() {return PlayerItemConsumeEvent.class;}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(PlayerItemConsumeEvent event, int level, Settings settings) {
        return ItemChecker.check(event.getItem(), level, settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(PlayerItemConsumeEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(PlayerItemConsumeEvent event) {return event.getPlayer();}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(PlayerItemConsumeEvent event, Settings settings) {return event.getPlayer();}
}
