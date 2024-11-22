/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.target.SingleTarget
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
package studio.magemonkey.fabled.dynamic.target;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.particle.ParticleSettings;
import studio.magemonkey.fabled.api.target.TargetHelper;

import java.util.List;
import java.util.function.Supplier;

/**
 * Applies child components to the closest linear entity of each of the
 * provided targets.
 */
public class SingleTarget extends TargetComponent {
    private static final String RANGE     = "range";
    private static final String TOLERANCE = "tolerance";

    /**
     * {@inheritDoc}
     */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        double range     = parseValues(caster, RANGE, level, 5.0);
        double tolerance = parseValues(caster, TOLERANCE, level, 4.0);
        return determineTargets(caster, level, targets, t -> {
            final LivingEntity target = TargetHelper.getLivingTarget(t, range, tolerance);
            return target == null ? ImmutableList.of() : ImmutableList.of(target);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        super.playPreview(onPreviewStop, caster, level, targetSupplier);

        if (preview.getBool("line", false)) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    ParticleSettings particleSettings = new ParticleSettings(preview, "line-");
                    double           range            = parseValues(caster, RANGE, level, 5.0);
                    double           density          = preview.getDouble("line-" + "density", 1);

                    double rStep = 1 / range / density;

                    for (LivingEntity target : targetSupplier.get()) {
                        Location origin    = target.getEyeLocation();
                        Vector   direction = origin.getDirection();

                        Vector directionStep = direction.clone().multiply(rStep);
                        double startDistance = preview.getDouble("line-start-distance", 2);
                        origin.add(direction.clone().multiply(startDistance));
                        for (double rLocation = startDistance; rLocation <= range; rLocation += rStep) {
                            particleSettings.instance(caster, origin.getX(), origin.getY(), origin.getZ());
                            origin.add(directionStep);
                        }
                    }
                }
            }.runTaskTimer((Plugin) Fabled.inst(), 0, Math.max(1, preview.getInt("line-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }

        if (preview.getBool("cylinder", false)) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    ParticleSettings particleSettings = new ParticleSettings(preview, "cylinder-");
                    double           range            = parseValues(caster, RANGE, level, 5.0);
                    double           radius           = parseValues(caster, TOLERANCE, level, 0);
                    double           density          = preview.getDouble("cylinder-" + "density", 1);

                    double rStep     = 1 / range / density;
                    double angleStep = 1 / radius / density;

                    for (LivingEntity target : targetSupplier.get()) {
                        Location origin    = target.getEyeLocation();
                        Vector   direction = origin.getDirection();

                        Location altDirection = origin.clone();
                        altDirection.setPitch((origin.getPitch() + 135) % 180 - 90); // Move pitch 45° without overflow
                        Vector radiusVec = altDirection.getDirection().crossProduct(direction).multiply(radius);

                        Vector directionStep = direction.clone().multiply(rStep);
                        double startDistance = preview.getDouble("cylinder-start-distance", 2);
                        origin.add(direction.clone().multiply(startDistance));
                        for (double rLocation = startDistance; rLocation <= range; rLocation += rStep) {
                            for (double totalAngle = 0; totalAngle <= Math.PI + 0.1; totalAngle += angleStep) {
                                Vector vector = radiusVec.clone().rotateAroundNonUnitAxis(direction, totalAngle);
                                particleSettings.instance(caster,
                                        origin.getX() + vector.getX(),
                                        origin.getY() + vector.getY(),
                                        origin.getZ() + vector.getZ());
                                particleSettings.instance(caster,
                                        origin.getX() - vector.getX(),
                                        origin.getY() - vector.getY(),
                                        origin.getZ() - vector.getZ());
                            }

                            origin.add(directionStep);
                        }
                    }
                }
            }.runTaskTimer((Plugin) Fabled.inst(), 0, Math.max(1, preview.getInt("cylinder-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }

    @Override
    public String getKey() {
        return "single";
    }
}
