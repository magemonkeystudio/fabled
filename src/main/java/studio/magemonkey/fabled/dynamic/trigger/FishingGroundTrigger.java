package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

public class FishingGroundTrigger implements Trigger<ProjectileHitEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FISHING_GROUND";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProjectileHitEvent> getEvent() {
        return ProjectileHitEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(ProjectileHitEvent event, int level, Settings settings) {
        // FISHING_BOBBER/FISHING_HOOK
        return event.getEntityType().getKey().getKey().equals("fishing_bobber") && event.getHitEntity() == null
                && event.getHitBlock() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(ProjectileHitEvent event, final CastData data) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(ProjectileHitEvent event) {
        return ((LivingEntity) event.getEntity().getShooter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(ProjectileHitEvent event, Settings settings) {
        return ((LivingEntity) event.getEntity().getShooter());
    }

}
