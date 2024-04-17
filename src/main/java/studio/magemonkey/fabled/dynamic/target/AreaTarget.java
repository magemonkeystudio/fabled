/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.target.AreaTarget
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

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.particle.ParticleSettings;
import studio.magemonkey.fabled.api.util.Nearby;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Applies child components to the closest all nearby entities around
 * each of the current targets.
 */
public class AreaTarget extends TargetComponent {
    private static final String RADIUS = "radius";
    private static final String RANDOM = "random";

    /**
     * {@inheritDoc}
     */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        final double  radius = parseValues(caster, RADIUS, level, 3.0);
        final boolean random = settings.getBool(RANDOM, false);
        return determineTargets(caster, level, targets, t -> shuffle(Nearby.getLivingNearby(t, radius, true), random));
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

        if (preview.getBool("circle", false)) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    ParticleSettings particleSettings = new ParticleSettings(preview, "circle-");
                    double           radius           = parseValues(caster, RADIUS, level, 3.0);
                    double           density          = preview.getDouble("circle-density", 1);
                    double           angle            = 1 / radius / density;
                    double           halfPi           = Math.PI / 2;
                    Vector           direction        = new Vector(radius, 0, 0);

                    for (LivingEntity target : targetSupplier.get()) {
                        Location center = target.getLocation();
                        for (double totalAngle = 0; totalAngle <= halfPi + 0.1; totalAngle += angle) {
                            Vector dir = direction.clone().rotateAroundY(totalAngle);
                            particleSettings.instance(caster,
                                    center.getX() + dir.getX(),
                                    center.getY() + dir.getY(),
                                    center.getZ() + dir.getZ());
                            particleSettings.instance(caster,
                                    center.getX() + dir.getX(),
                                    center.getY() + dir.getY(),
                                    center.getZ() - dir.getZ());
                            particleSettings.instance(caster,
                                    center.getX() - dir.getX(),
                                    center.getY() + dir.getY(),
                                    center.getZ() + dir.getZ());
                            particleSettings.instance(caster,
                                    center.getX() - dir.getX(),
                                    center.getY() + dir.getY(),
                                    center.getZ() - dir.getZ());
                        }
                    }
                }
            }.runTaskTimer(Fabled.inst(), 0, Math.max(1, preview.getInt("circle-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }

        if (preview.getBool("sphere", false)) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    ParticleSettings particleSettings = new ParticleSettings(preview, "sphere-");

                    double radius  = parseValues(caster, RADIUS, level, 3.0);
                    double density = preview.getDouble("sphere-density", 1);
                    double zAngle  = 1 / radius / density;
                    double halfPi  = Math.PI / 2;

                    for (LivingEntity target : targetSupplier.get()) {
                        Location center    = target.getLocation();
                        Vector   direction = new Vector(radius, 0, 0);
                        for (double totalZAngle = 0; totalZAngle <= halfPi; totalZAngle += zAngle) {
                            Vector dir    = direction.clone().rotateAroundZ(totalZAngle);
                            double yAngle = 1 / (radius * Math.cos(totalZAngle)) / density;
                            for (double totalYAngle = 0; totalYAngle <= halfPi; totalYAngle += yAngle) {
                                Vector loc = dir.clone().rotateAroundY(totalYAngle);
                                particleSettings.instance(caster,
                                        center.getX() + loc.getX(),
                                        center.getY() + loc.getY(),
                                        center.getZ() + loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() + loc.getX(),
                                        center.getY() + loc.getY(),
                                        center.getZ() - loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() + loc.getX(),
                                        center.getY() - loc.getY(),
                                        center.getZ() + loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() + loc.getX(),
                                        center.getY() - loc.getY(),
                                        center.getZ() - loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() - loc.getX(),
                                        center.getY() + loc.getY(),
                                        center.getZ() + loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() - loc.getX(),
                                        center.getY() + loc.getY(),
                                        center.getZ() - loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() - loc.getX(),
                                        center.getY() - loc.getY(),
                                        center.getZ() + loc.getZ());
                                particleSettings.instance(caster,
                                        center.getX() - loc.getX(),
                                        center.getY() - loc.getY(),
                                        center.getZ() - loc.getZ());
                            }
                        }
                    }
                }
            }.runTaskTimer(Fabled.inst(), 0, Math.max(1, preview.getInt("sphere-" + "period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }

    @Override
    public String getKey() {
        return "area";
    }

    private List<LivingEntity> shuffle(final List<LivingEntity> targets, final boolean random) {
        if (!random) return targets;

        final List<LivingEntity> list = new ArrayList<>();
        while (!targets.isEmpty()) {
            list.add(targets.remove(Fabled.RANDOM.nextInt(targets.size())));
        }
        return list;
    }
}
