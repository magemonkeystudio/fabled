package studio.magemonkey.fabled.api.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;

public class DisplayEntityInstance {
    private static final Vector UP = new Vector(0, 1, 0);

    private final Entity                display;
    private final LivingEntity          target;
    private final boolean               follow;
    private final double                forward;
    private final double                upward;
    private final double                right;
    private final DisplayEntityTransform transform;
    private final int                   level;
    private final int                   interpolationDuration;
    private       int                   tickCount;

    /**
     * Creates a static (non-animated, non-follow) instance.
     */
    public DisplayEntityInstance(Entity display, LivingEntity target) {
        this(display, target, false, 0, 0, 0, null, 0, 0);
    }

    /**
     * Creates an instance with optional follow but no time-based transform.
     */
    public DisplayEntityInstance(Entity display, LivingEntity target, boolean follow) {
        this(display, target, follow, 0, 0, 0, null, 0, 0);
    }

    /**
     * Creates an instance with follow offsets but no time-based transform.
     */
    public DisplayEntityInstance(Entity display, LivingEntity target, boolean follow,
                                 double forward, double upward, double right) {
        this(display, target, follow, forward, upward, right, null, 0, 0);
    }

    /**
     * Full constructor.
     *
     * @param display               the spawned Display entity
     * @param target                the entity the display is attached to
     * @param follow                whether to teleport the display to follow the target each tick
     * @param forward               follow forward offset (world-space, ignored when follow=false)
     * @param upward                follow upward offset
     * @param right                 follow right offset
     * @param transform             time-based transform formulas, or {@code null} for a static transform
     * @param level                 skill level passed to transform formulas as {@code l}
     * @param interpolationDuration Bukkit interpolation ticks; 0 = instant snap
     */
    public DisplayEntityInstance(Entity display, LivingEntity target, boolean follow,
                                 double forward, double upward, double right,
                                 DisplayEntityTransform transform, int level,
                                 int interpolationDuration) {
        this.display = display;
        this.target = target;
        this.follow = follow;
        this.forward = forward;
        this.upward = upward;
        this.right = right;
        this.transform = transform;
        this.level = level;
        this.interpolationDuration = interpolationDuration;
        this.tickCount = 0;
    }

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
     * Ticks the display entity: applies a time-based transform update and/or
     * teleports the entity to follow its target.
     */
    public void tick() {
        if (!follow && transform == null) return;

        final int currentTick = tickCount++;

        Bukkit.getScheduler().runTask(Fabled.inst(), () -> {
            if (!display.isValid()) return;

            if (transform != null) {
                Display d = (Display) display;
                if (interpolationDuration > 0) {
                    d.setInterpolationDelay(0);
                    d.setInterpolationDuration(interpolationDuration);
                }
                d.setTransformation(transform.compute(currentTick, level));
            }

            if (follow) {
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
            }
        });
    }
}
