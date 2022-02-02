package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.ItemStackReader;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Spawns a dropped item at the specified location.
 */
public class ItemDropMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String FORWARD = "forward";
    private static final String UPWARD = "upward";
    private static final String RIGHT = "right";

    private static final String PICKUP_DELAY = "pickup_delay";
    private static final String DURATION = "duration";

    @Override
    public String getKey() {
        return "item drop";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        ItemStack item = ItemStackReader.read(settings);

        double forward = parseValues(caster, FORWARD, level, 0);
        double upward = parseValues(caster, UPWARD, level, 0);
        double right = parseValues(caster, RIGHT, level, 0);
        int delay = (int) parseValues(caster, PICKUP_DELAY, level, 0);
        int duration = (int) parseValues(caster, DURATION, level, 0);

        for (LivingEntity target : targets) {
            Location loc = target.getLocation();
            Vector dir = loc.getDirection().setY(0).normalize();
            Vector side = dir.clone().crossProduct(UP);
            loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));
            target.getWorld().dropItem(loc, item, drop -> {
                drop.setPickupDelay(delay);
                drop.setTicksLived(Math.max(6000-duration, 1));
            });
        }
        return targets.size() > 0;
    }
}