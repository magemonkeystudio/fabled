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
    /** Horizontal facing direction captured at cast time (used for formula-based offsets). */
    private final Vector                castDir;
    /** Horizontal right direction captured at cast time (used for formula-based offsets). */
    private final Vector                castSide;
    private final DisplayEntityTransform transform;
    private final int                   level;
    private final int                   interpolationDuration;
    private final int                   teleportDuration;
    private final boolean               inheritRotation;
    private       int                   tickCount;

    /**
     * Creates a static (non-animated, non-follow) instance.
     */
    public DisplayEntityInstance(Entity display, LivingEntity target) {
        this(display, target, false, 0, 0, 0, null, null, null, 0, 0, 0, true);
    }

    /**
     * Creates an instance with optional follow but no time-based transform.
     */
    public DisplayEntityInstance(Entity display, LivingEntity target, boolean follow) {
        this(display, target, follow, 0, 0, 0, null, null, null, 0, 0, 0, true);
    }

    /**
     * Creates an instance with follow offsets but no time-based transform.
     */
    public DisplayEntityInstance(Entity display, LivingEntity target, boolean follow,
                                 double forward, double upward, double right) {
        this(display, target, follow, forward, upward, right, null, null, null, 0, 0, 0, true);
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
     * @param castDir               the horizontal facing direction at cast time, used for formula-based
     *                              world-space offsets; may be {@code null} to fall back to current facing
     * @param castSide              the horizontal right direction at cast time; may be {@code null}
     * @param level                 skill level passed to transform formulas as {@code l}
     * @param interpolationDuration Bukkit interpolation ticks for transform changes; 0 = instant snap
     * @param teleportDuration      Bukkit interpolation ticks for follow position updates; 0 = instant teleport
     * @param inheritRotation       when {@code true} the entity keeps the target's yaw/pitch on
     *                              follow teleports; when {@code false} yaw and pitch are zeroed
     */
    public DisplayEntityInstance(Entity display, LivingEntity target, boolean follow,
                                 double forward, double upward, double right,
                                 DisplayEntityTransform transform,
                                 Vector castDir, Vector castSide,
                                 int level,
                                 int interpolationDuration, int teleportDuration, boolean inheritRotation) {
        this.display = display;
        this.target = target;
        this.follow = follow;
        this.forward = forward;
        this.upward = upward;
        this.right = right;
        this.castDir = castDir != null ? castDir.clone() : null;
        this.castSide = castSide != null ? castSide.clone() : null;
        this.transform = transform;
        this.level = level;
        this.interpolationDuration = interpolationDuration;
        this.teleportDuration = teleportDuration;
        this.inheritRotation = inheritRotation;
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
                // Compute current-facing offset direction BEFORE zeroing yaw/pitch.
                Vector   dir  = loc.getDirection().setY(0).normalize();
                Vector   side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

                // Apply formula-based world-space offsets relative to the cast-time
                // facing direction (so slashing/orbiting effects hold their orientation).
                if (transform != null) {
                    double[] offset = transform.computeWorldOffset(currentTick, level);
                    double   fwd    = offset[0];
                    double   upw    = offset[1];
                    double   rgt    = offset[2];
                    if (fwd != 0 || upw != 0 || rgt != 0) {
                        Vector oDir  = castDir  != null ? castDir  : loc.getDirection().setY(0).normalize();
                        Vector oSide = castSide != null ? castSide : oDir.clone().crossProduct(UP);
                        loc.add(oDir.clone().multiply(fwd))
                           .add(0, upw, 0)
                           .add(oSide.clone().multiply(rgt));
                    }
                }

                if (!inheritRotation) {
                    loc.setYaw(0);
                    loc.setPitch(0);
                }

                if (!sameWorld) {
                    Chunk chunk = display.getLocation().getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
                if (teleportDuration > 0) {
                    ((Display) display).setTeleportDuration(teleportDuration);
                }
                display.teleport(loc);
            }
        });
    }
}
