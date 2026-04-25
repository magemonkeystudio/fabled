package studio.magemonkey.fabled.api.displayentity;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;

@AllArgsConstructor
@RequiredArgsConstructor
public class DisplayEntityInstance {
    private static final Vector       UP = new Vector(0, 1, 0);
    private final        Entity        display;
    private final        LivingEntity  target;
    private final        boolean       follow;
    private              double        forward;
    private              double        upward;
    private              double        right;

    /**
     * @return true if the instance is still valid
     */
    public boolean isValid() {
        return target.isValid() && display.isValid();
    }

    /**
     * Removes the display entity
     */
    public void remove() {
        Bukkit.getScheduler().runTask(Fabled.inst(), display::remove);
    }

    /**
     * Ticks the display entity (for follow behavior)
     */
    public void tick() {
        if (follow) {
            Bukkit.getScheduler().runTask(Fabled.inst(), () -> {
                boolean sameWorld = display.getWorld().equals(target.getWorld());

                Location loc  = target.getLocation().clone();
                Vector   dir  = loc.getDirection().setY(0).normalize();
                Vector   side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

                if (!sameWorld) {
                    Chunk chunk = display.getLocation().getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
                display.teleport(loc);
            });
        }
    }
}
