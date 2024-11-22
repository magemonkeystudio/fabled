/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.BlockMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.particle.ParticleHelper;

import java.util.*;
import java.util.function.Supplier;

/**
 * Mechanic that changes blocks for a duration before
 * returning them to what they were
 */
public class BlockMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String SHAPE     = "shape";
    private static final String TYPE      = "type";
    private static final String RADIUS    = "radius";
    private static final String WIDTH     = "width";
    private static final String HEIGHT    = "height";
    private static final String DEPTH     = "depth";
    private static final String BLOCK     = "block";
    private static final String DATA      = "data";
    private static final String SECONDS   = "seconds";
    private static final String FORWARD   = "forward";
    private static final String UPWARD    = "upward";
    private static final String RIGHT     = "right";
    private static final String RESET_YAW = "reset-yaw";
    private static final String PERMANENT = "permanent";

    private static final Map<Location, Integer>    pending  = new HashMap<>();
    private static final Map<Location, BlockState> original = new HashMap<>();

    private final Map<Integer, List<RevertTask>> tasks = new HashMap<>();

    /**
     * Checks whether the location is modified by a block mechanic
     *
     * @param loc location to check
     * @return true if modified, false otherwise
     */
    public static boolean isPending(Location loc) {
        return pending.containsKey(loc);
    }

    @Override
    public String getKey() {
        return "block";
    }

    @Override
    protected void doCleanUp(final LivingEntity caster) {
        final List<RevertTask> casterTasks = tasks.remove(caster.getEntityId());
        if (casterTasks != null) {
            casterTasks.forEach(task -> {
                task.revert();
                task.cancel();
            });
        }
    }

    private Location getLocation(LivingEntity caster, int level, LivingEntity target) {
        // Get the location with offsets included

        double  forward  = parseValues(caster, FORWARD, level, 0);
        double  upward   = parseValues(caster, UPWARD, level, 0);
        double  right    = parseValues(caster, RIGHT, level, 0);
        boolean resetYaw = settings.getBool(RESET_YAW, false);

        Location loc    = target.getLocation();
        Location dirLoc = target.getLocation().clone();
        if (resetYaw) dirLoc.setYaw(0);
        Vector dir = dirLoc.getDirection().setY(0).normalize();
        Vector nor = dir.clone().crossProduct(UP);
        loc.add(dir.multiply(forward).add(nor.multiply(right)));
        loc.add(0, upward, 0);
        return loc;
    }

    private List<Block> getAffectedBlocks(LivingEntity caster, int level, List<LivingEntity> targets) {
        boolean sphere = settings.getString(SHAPE, "sphere").equalsIgnoreCase("sphere");

        String  type  = settings.getString(TYPE, "solid").toLowerCase();
        boolean solid = type.equals("solid");
        boolean air   = type.equals("air");
        Material matType =
                !solid && !air && !type.equals("any") ? Material.valueOf(type.toUpperCase(Locale.US).replace(' ', '_'))
                        : null;
        boolean resetYaw = settings.getBool(RESET_YAW, false);

        List<Block> blocks = new ArrayList<>();
        World       w      = caster.getWorld();

        // Grab blocks in a sphere
        if (sphere) {
            double radius = parseValues(caster, RADIUS, level, 3);
            double x, y, z, dx, dy, dz;
            double rSq    = radius * radius;
            for (LivingEntity t : targets) {
                Location loc = getLocation(caster, level, t);
                x = loc.getBlockX();
                y = loc.getBlockY();
                z = loc.getBlockZ();

                // Get all blocks within the radius of the center
                for (int i = (int) (x - radius) + 1; i < (int) (x + radius); i++) {
                    for (int j = (int) (y - radius) + 1; j < (int) (y + radius); j++) {
                        for (int k = (int) (z - radius) + 1; k < (int) (z + radius); k++) {
                            dx = x - i;
                            dy = y - j;
                            dz = z - k;
                            if (dx * dx + dy * dy + dz * dz < rSq) {
                                Block b = w.getBlockAt(i, j, k);
                                if ((matType == null || matType == b.getType())
                                        && (!solid || b.getType().isSolid())
                                        && (!air || b.getType().isAir())
                                        && !Fabled.getSettings().getFilteredBlocks().contains(b.getType())) {
                                    blocks.add(b);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Grab blocks in a cuboid
        else {
            // Cuboid options
            double width  = (parseValues(caster, WIDTH, level, 5) - 1) / 2;
            double height = (parseValues(caster, HEIGHT, level, 5) - 1) / 2;
            double depth  = (parseValues(caster, DEPTH, level, 5) - 1) / 2;
            double x, y, z;

            for (LivingEntity t : targets) {
                Location loc = getLocation(caster, level, t);
                x = loc.getX();
                y = loc.getY();
                z = loc.getZ();

                double  yaw     = loc.getYaw();
                boolean facingZ = Math.abs(yaw) < 45 || Math.abs(yaw) > 135;
                if (!resetYaw) {
                    x = facingZ ? loc.getX() : loc.getZ();
                    z = facingZ ? loc.getZ() : loc.getX();
                }

                // Get all blocks in the area
                for (double i = x - width; i <= x + width + 0.01; i++) {
                    for (double j = y - height; j <= y + height + 0.01; j++) {
                        for (double k = z - depth; k <= z + depth + 0.01; k++) {
                            int   blockX = (int) Math.floor(resetYaw || facingZ ? i : k);
                            int   blockY = (int) Math.floor(j);
                            int   blockZ = (int) Math.floor(resetYaw || facingZ ? k : i);
                            Block b      = w.getBlockAt(blockX, blockY, blockZ);
                            if ((!solid || b.getType().isSolid())
                                    && (!air || b.getType().isAir())
                                    && !Fabled.getSettings().getFilteredBlocks().contains(b.getType())) {
                                blocks.add(b);
                            }
                        }
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;

        Material block = Material.ICE;
        try {
            block = Material.valueOf(settings.getString(BLOCK, "ICE").toUpperCase(Locale.US).replace(' ', '_'));
        } catch (Exception ex) {
            // Use default
        }
        int     ticks     = (int) (20 * parseValues(caster, SECONDS, level, 5));
        byte    data      = (byte) settings.getInt(DATA, 0);
        boolean permanent = settings.getBool(PERMANENT, false);

        // Change blocks
        List<Location> states = new ArrayList<>();
        for (Block b : getAffectedBlocks(caster, level, targets)) {
            // Increment the counter
            Location loc = b.getLocation();
            if (permanent) {
                pending.remove(loc);
                original.remove(loc);
            } else {
                if (isPending(loc)) {
                    pending.put(loc, pending.get(loc) + 1);
                } else {
                    pending.put(loc, 1);
                    original.put(loc, b.getState());
                }
            }

            states.add(b.getLocation());
            BlockState state = b.getState();
            state.setType(block);
            if (data > 0)
                state.setData(new MaterialData(block, data));
            state.update(true, false);
        }

        if (!permanent) {
            // Revert after duration
            final RevertTask task = new RevertTask(caster, states);
            task.runTaskLater(Fabled.inst(), ticks);
            tasks.computeIfAbsent(caster.getEntityId(), ArrayList::new).add(task);
        }

        return true;
    }

    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        if (preview.getBool("per-target")) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (preview.getBool("per-target-center-only", true))
                        for (LivingEntity t : targetSupplier.get())
                            ParticleHelper.play(getLocation(caster, level, t),
                                    preview,
                                    Set.of(caster),
                                    "per-target-",
                                    null);
                    else
                        for (Block block : getAffectedBlocks(caster, level, targetSupplier.get()))
                            ParticleHelper.play(block.getLocation(), preview, Set.of(caster), "per-target-",
                                    preview.getBool("per-target-" + "hitbox") ? block.getBoundingBox() : null
                            );
                }
            }.runTaskTimer(Fabled.inst(), 0, Math.max(1, preview.getInt("per-target-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }

    /**
     * Reverts block changes after a duration
     */
    private class RevertTask extends BukkitRunnable {
        private final List<Location> locs;
        private final LivingEntity   caster;

        RevertTask(final LivingEntity caster, final List<Location> locs) {
            this.caster = caster;
            this.locs = locs;
        }

        @Override
        public void run() {
            revert();
            tasks.get(caster.getEntityId()).remove(this);
        }

        private void revert() {
            for (Location loc : locs) {
                int count = pending.remove(loc);

                if (count == 1) {
                    original.remove(loc).update(true, false);
                } else {
                    pending.put(loc, count - 1);
                }
            }
        }
    }
}
