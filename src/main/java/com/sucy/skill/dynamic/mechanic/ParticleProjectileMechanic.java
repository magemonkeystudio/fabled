/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ParticleProjectileMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.particle.EffectPlayer;
import com.sucy.skill.api.particle.ParticleHelper;
import com.sucy.skill.api.particle.ParticleSettings;
import com.sucy.skill.api.particle.target.FollowTarget;
import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ParticleProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Heals each target
 */
public class ParticleProjectileMechanic extends MechanicComponent implements ProjectileCallback {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String GROUP    = "group";
    private static final String LIFESPAN = "lifespan";
    private static final String SPREAD   = "spread";
    private static final String AMOUNT   = "amount";
    private static final String ANGLE    = "angle";
    private static final String HEIGHT   = "height";
    private static final String RADIUS   = "rain-radius";
    private static final String LEVEL    = "skill_level";
    private static final String FORWARD  = "forward";
    private static final String UPWARD   = "upward";
    private static final String RIGHT    = "right";

    private static final String USE_EFFECT = "use-effect";
    private static final String EFFECT_KEY = "effect-key";

    @Override
    public String getKey() {
        return "particle projectile";
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
        // Get common values
        int     amount = (int) parseValues(caster, AMOUNT, level, 1.0);
        String  spread = settings.getString(SPREAD, "cone").toLowerCase();
        boolean ally   = settings.getString(GROUP, "enemy").equalsIgnoreCase("ally");
        settings.set("level", level);
        int life = (int) (parseValues(caster, LIFESPAN, level, settings.getDouble(LIFESPAN, 2)) * 20);

        final Settings copy = new Settings(settings);
        copy.set(ParticleProjectile.GRAVITY, parseValues(caster, ParticleProjectile.GRAVITY, level, -0.04), 0);
        copy.set(ParticleProjectile.DRAG, parseValues(caster, ParticleProjectile.DRAG, level, 0.02), 0);
        copy.set(ParticleProjectile.SPEED, parseValues(caster, ParticleProjectile.SPEED, level, 1), 0);
        copy.set(ParticleHelper.POINTS_KEY, parseValues(caster, ParticleHelper.POINTS_KEY, level, 1), 0);
        copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);

        // Fire from each target
        for (LivingEntity target : targets) {
            Location loc = target.getLocation();

            // Apply the spread type
            ArrayList<ParticleProjectile> list;
            if (spread.equals("rain")) {
                double radius = parseValues(caster, RADIUS, level, 2.0);
                double height = parseValues(caster, HEIGHT, level, 8.0);
                list = ParticleProjectile.rain(caster, level, loc, copy, radius, height, amount, this, life);
            } else {
                Vector dir = target.getLocation().getDirection();

                double right   = parseValues(caster, RIGHT, level, 0);
                double upward  = parseValues(caster, UPWARD, level, 0);
                double forward = parseValues(caster, FORWARD, level, 0);

                Vector looking = dir.clone().setY(0).normalize();
                Vector normal  = looking.clone().crossProduct(UP);
                looking.multiply(forward).add(normal.multiply(right));

                if (spread.equals("horizontal cone")) {
                    dir.setY(0);
                    dir.normalize();
                }
                double angle = parseValues(caster, ANGLE, level, 30.0);
                list = ParticleProjectile.spread(
                        caster,
                        level,
                        dir,
                        loc.add(looking).add(0, upward + 0.5, 0),
                        copy,
                        angle,
                        amount,
                        this,
                        life
                );
            }

            // Set metadata for when the callback happens
            for (ParticleProjectile p : list) {
                SkillAPI.setMeta(p, LEVEL, level);
                p.setAllyEnemy(ally, !ally);
            }

            if (settings.getBool(USE_EFFECT, false)) {
                EffectPlayer player = new EffectPlayer(settings);
                for (CustomProjectile p : list) {
                    player.start(
                            new FollowTarget(p),
                            settings.getString(EFFECT_KEY, skill.getName()),
                            life,
                            level,
                            true);
                }
            }
        }

        return targets.size() > 0;
    }

    /**
     * The callback for the projectiles that applies child components
     *
     * @param projectile projectile calling back for
     * @param hit        the entity hit by the projectile, if any
     */
    @Override
    public void callback(CustomProjectile projectile, LivingEntity hit) {
        if (hit == null) {
            hit = new TempEntity(projectile.getLocation());
        }
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(hit);
        executeChildren(projectile.getShooter(),
                SkillAPI.getMetaInt(projectile, LEVEL),
                targets,
                skill.isForced(projectile.getShooter()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(List<Runnable> onPreviewStop, Player caster, int level, Supplier<List<LivingEntity>> targetSupplier) {
        List<LivingEntity> targets = new ArrayList<>();

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                targets.clear();

                int     amount = (int) parseValues(caster, AMOUNT, level, 1.0);
                String  spread = settings.getString(SPREAD, "cone").toLowerCase();
                boolean ally   = settings.getString(GROUP, "enemy").equalsIgnoreCase("ally");
                int life = (int) (parseValues(caster, LIFESPAN, level, settings.getDouble(LIFESPAN, 2)) * 20);

                final Settings copy = new Settings(settings);
                copy.set(ParticleProjectile.GRAVITY, parseValues(caster, ParticleProjectile.GRAVITY, level, -0.04), 0);
                copy.set(ParticleProjectile.DRAG, parseValues(caster, ParticleProjectile.DRAG, level, 0.02), 0);
                copy.set(ParticleProjectile.SPEED, parseValues(caster, ParticleProjectile.SPEED, level, 1), 0);
                copy.set(ParticleProjectile.PERIOD, preview.getInt("path-steps", 2));

                ProjectileCallback callback = (projectile, hit) -> {
                    if (hit == null) hit = new TempEntity(projectile.getLocation());
                    targets.add(hit);
                    if (preview.getBool("per-target")) {
                        ParticleHelper.play(hit.getLocation(), preview, Set.of(caster), "per-target-",
                                preview.getBool("per-target-" + "hitbox") ? hit.getBoundingBox() : null);
                    }
                };

                List<ParticleProjectile> list = new ArrayList<>();
                // Fire from each target
                for (LivingEntity target : targetSupplier.get()) {
                    Location loc = target.getLocation();

                    // Apply the spread type
                    if (spread.equals("rain")) {
                        double radius = parseValues(caster, RADIUS, level, 2.0);
                        double height = parseValues(caster, HEIGHT, level, 8.0);
                        list.addAll(ParticleProjectile.rain(caster, level, loc, copy, radius, height, amount, callback, life));
                    } else {
                        Vector dir = target.getLocation().getDirection();

                        double right   = parseValues(caster, RIGHT, level, 0);
                        double upward  = parseValues(caster, UPWARD, level, 0);
                        double forward = parseValues(caster, FORWARD, level, 0);

                        Vector looking = dir.clone().setY(0).normalize();
                        Vector normal  = looking.clone().crossProduct(UP);
                        looking.multiply(forward).add(normal.multiply(right));

                        if (spread.equals("horizontal cone")) {
                            dir.setY(0);
                            dir.normalize();
                        }
                        double angle = parseValues(caster, ANGLE, level, 30.0);
                        list.addAll(ParticleProjectile.spread(
                                caster,
                                level,
                                dir,
                                loc.add(looking).add(0, upward + 0.5, 0),
                                copy,
                                angle,
                                amount,
                                callback,
                                life
                        ));
                    }

                    for (ParticleProjectile p : list) {
                        SkillAPI.setMeta(p, LEVEL, level);
                        p.setAllyEnemy(ally, !ally);
                    }

                    Consumer<Location> onStep = preview.getBool("path")
                            ? location -> new ParticleSettings(preview, "path-").instance(caster, location.getX(), location.getY(), location.getZ())
                            : location -> {};
                    for (ParticleProjectile p : list) p.setOnStep(onStep);

                    for (ParticleProjectile p : list) {
                        while (p.isValid()) {
                            p.run();
                        }
                    }
                }
            }
        }.runTaskTimer(SkillAPI.inst(),0, Math.max(1, preview.getInt("period", 5)));
        onPreviewStop.add(task::cancel);

        playChildrenPreviews(onPreviewStop, caster, level, () -> targets);
    }
}
