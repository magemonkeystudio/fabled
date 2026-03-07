package studio.magemonkey.fabled.dynamic.trigger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.trigger.BlockBreakTrigger
 */
public class LaunchTrigger implements Trigger<ProjectileLaunchEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "LAUNCH";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProjectileLaunchEvent> getEvent() {
        return ProjectileLaunchEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final ProjectileLaunchEvent event, final int level, final Settings settings) {
        List<String> types = settings.getStringList("types");
        if (types.isEmpty()) {
            types = new ArrayList<>(List.of(settings.getString("type", "Any")));
        } 
        return types.contains("Any") 
                || types.stream()
                .anyMatch(proj -> event.getEntity().getType().name().equalsIgnoreCase(proj.replace(' ', '_')));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final ProjectileLaunchEvent event, final CastData data) {
        data.put("api-velocity", event.getEntity().getVelocity().length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof LivingEntity) {
            return (LivingEntity) event.getEntity().getShooter();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final ProjectileLaunchEvent event, final Settings settings) {
        return getCaster(event);
    }
}
