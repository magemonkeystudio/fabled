/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.target.ConeTarget
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.particle.ParticleSettings;
import studio.magemonkey.fabled.api.target.TargetHelper;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Supplier;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class ConeTarget extends TargetComponent {
    private static final String ANGLE   = "angle";
    private static final String RANGE   = "range";
    private static final String RESET_Y = "reset-y";


    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        super.playPreview(onPreviewStop, caster, level, targetSupplier);

        if (preview.getBool("triangle", false)) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    ParticleSettings particleSettings = new ParticleSettings(preview, "triangle-");
                    double angle =
                            parseValues(caster, ANGLE, level, 90.0) * Math.PI / 360; // Intentional division by 2
                    double range   = parseValues(caster, RANGE, level, 5.0);
                    double density = preview.getDouble("triangle-" + "density", 1);

                    double rStep = 1 / range / density;

                    for (LivingEntity target : targetSupplier.get()) {
                        Location origin    = target.getEyeLocation();
                        Vector   direction = origin.getDirection();

                        Location altDirection = origin.clone();
                        altDirection.setPitch((origin.getPitch() + 135) % 180 - 90); // Move pitch 45° without overflow
                        Vector perpendicular = altDirection.getDirection().crossProduct(direction);

                        Vector directionStep = direction.clone().multiply(rStep);
                        double startDistance = preview.getDouble("triangle-start-distance", 2);
                        origin.add(direction.clone().multiply(startDistance));
                        for (double rLocation = startDistance; rLocation <= range; rLocation += rStep) {
                            double radius = rLocation * Math.tan(angle);
                            Vector vector = perpendicular.clone().multiply(radius);
                            particleSettings.instance(caster,
                                    origin.getX() + vector.getX(),
                                    origin.getY() + vector.getY(),
                                    origin.getZ() + vector.getZ());
                            particleSettings.instance(caster,
                                    origin.getX() - vector.getX(),
                                    origin.getY() - vector.getY(),
                                    origin.getZ() - vector.getZ());

                            origin.add(directionStep);
                        }
                    }
                }
            }.runTaskTimer(Fabled.inst(), 0, Math.max(1, preview.getInt("triangle-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }

        if (preview.getBool("cone", false)) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    ParticleSettings particleSettings = new ParticleSettings(preview, "cone-");
                    double angle =
                            parseValues(caster, ANGLE, level, 90.0) * Math.PI / 360; // Intentional division by 2
                    double range   = parseValues(caster, RANGE, level, 5.0);
                    double density = preview.getDouble("cone-" + "density", 1);

                    double rStep = 1 / range / density;

                    for (LivingEntity target : targetSupplier.get()) {
                        Location origin    = target.getEyeLocation();
                        Vector   direction = origin.getDirection();

                        Location altDirection = origin.clone();
                        altDirection.setPitch((origin.getPitch() + 135) % 180 - 90); // Move pitch 45° without overflow
                        Vector perpendicular = altDirection.getDirection().crossProduct(direction);

                        Vector directionStep = direction.clone().multiply(rStep);
                        double startDistance = preview.getDouble("cone-start-distance", 2);
                        origin.add(direction.clone().multiply(startDistance));
                        for (double rLocation = startDistance; rLocation <= range; rLocation += rStep) {
                            double radius = rLocation * Math.tan(angle);

                            Vector radiusVec = perpendicular.clone().multiply(radius);
                            double angleStep = 1 / radius / density;
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
            }.runTaskTimer(Fabled.inst(), 0, Math.max(1, preview.getInt("cone-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<LivingEntity> getTargets(LivingEntity caster, int level, List<LivingEntity> targets) {
        double range = parseValues(caster, RANGE, level, 3.0);
        double angle = parseValues(caster, ANGLE, level, 90.0);
        return determineTargets(caster,
                level,
                targets,
                t -> TargetHelper.getConeTargets(t, angle, range, settings.getBool(RESET_Y, true)));
    }

    @Override
    public String getKey() {
        return "cone";
    }
}
