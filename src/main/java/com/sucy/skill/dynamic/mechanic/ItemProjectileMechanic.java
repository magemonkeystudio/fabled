/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ItemProjectileMechanic
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
import com.sucy.skill.api.projectile.ItemProjectile;
import com.sucy.skill.api.projectile.ParticleProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.api.util.ItemStackReader;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Launches a projectile using an item as its visual that applies child components upon landing
 */
public class ItemProjectileMechanic extends MechanicComponent implements ProjectileCallback {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String ALLY     = "group";
    private static final String WALLS    = "walls";
    private static final String VELOCITY = "velocity";
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
        return "item projectile";
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

        // Get other common values
        double  speed    = parseValues(caster, VELOCITY, level, 3.0);
        int     amount   = (int) parseValues(caster, AMOUNT, level, 1.0);
        String  spread   = settings.getString(SPREAD, "cone").toLowerCase();
        boolean ally     = settings.getString(ALLY, "enemy").equalsIgnoreCase("ally");
        boolean walls    = settings.getBool(WALLS, true);
        int     lifespan = (int) (parseValues(caster, LIFESPAN, level, 9999) * 20);

        final Settings copy = new Settings(settings);
        copy.set(ParticleProjectile.SPEED, parseValues(caster, ParticleProjectile.SPEED, level, 1), 0);
        copy.set(ParticleHelper.POINTS_KEY, parseValues(caster, ParticleHelper.POINTS_KEY, level, 1), 0);
        copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);
        copy.set(ItemProjectile.HOMING_DIST, parseValues(caster, ItemProjectile.HOMING_DIST, level, 20), 0);
        copy.set(ItemProjectile.CORRECTION, parseValues(caster, ItemProjectile.CORRECTION, level, 0.2), 0);

        // Fire from each target
        for (LivingEntity target : targets) {
            Location location = target.getEyeLocation();
            Vector offset = location.getDirection().setY(0).normalize();
            offset.multiply(parseValues(caster, FORWARD, level, 0))
                    .add(offset.clone().crossProduct(UP).multiply(parseValues(caster, RIGHT, level, 0)));
            location.add(offset).add(0, parseValues(caster, UPWARD, level, 0), 0);

            // Apply the spread type
            List<ItemProjectile> list;
            if (spread.equals("rain")) {
                list = ItemProjectile.rain(caster,
                        level,
                        location,
                        copy,
                        item,
                        parseValues(caster, RADIUS, level, 2.0),
                        parseValues(caster, HEIGHT, level, 8.0),
                        speed,
                        amount,
                        this,
                        lifespan,
                        walls);
            } else {
                Vector dir = location.getDirection();
                if (spread.equals("horizontal cone")) {
                    dir.setY(0);
                    dir.normalize();
                }
                dir.multiply(speed);
                double angle = parseValues(caster, ANGLE, level, 30.0);
                list = ItemProjectile.spread(
                        caster,
                        level,
                        dir,
                        location,
                        copy,
                        item,
                        angle,
                        amount,
                        this,
                        lifespan,
                        walls
                );
            }

            // Set metadata for when the callback happens
            for (ItemProjectile p : list) {
                SkillAPI.setMeta(p, LEVEL, level);
                p.setAllyEnemy(ally, !ally);
            }

            if (settings.getBool(USE_EFFECT, false)) {
                EffectPlayer player = new EffectPlayer(settings);
                for (CustomProjectile p : list) {
                    player.start(
                            new FollowTarget(p),
                            settings.getString(EFFECT_KEY, skill.getName()),
                            9999,
                            level,
                            true);
                }
            }
        }

        return !targets.isEmpty();
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
        ArrayList<LivingEntity> targets = new ArrayList<>();
        targets.add(hit);
        executeChildren(projectile.getShooter(),
                SkillAPI.getMetaInt(projectile, LEVEL),
                targets,
                skill.isForced(projectile.getShooter()));
        projectile.setCallback(null);
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
                boolean ally   = settings.getString(ALLY, "enemy").equalsIgnoreCase("ally");
                int     lifespan = (int) (parseValues(caster, LIFESPAN, level, 9999) * 20);

                final Settings copy = new Settings(settings);
                copy.set(ParticleProjectile.SPEED, parseValues(caster, ParticleProjectile.SPEED, level, 1), 0);
                copy.set(ParticleHelper.POINTS_KEY, parseValues(caster, ParticleHelper.POINTS_KEY, level, 1), 0);
                copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);
                copy.set(ParticleProjectile.RADIUS, 0.125, 0);
                copy.set(ParticleProjectile.GRAVITY, -0.04, 0);
                copy.set(ParticleProjectile.DRAG, 0.02, 0);
                copy.set(ParticleProjectile.PERIOD, preview.getInt("path-steps", 2));
                copy.set(ItemProjectile.HOMING_DIST, parseValues(caster, ItemProjectile.HOMING_DIST, level, 20), 0);
                copy.set(ItemProjectile.CORRECTION, parseValues(caster, ItemProjectile.CORRECTION, level, 0.2), 0);

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
                    Location location = target.getEyeLocation();
                    Vector offset = location.getDirection().setY(0).normalize();
                    offset.multiply(parseValues(caster, FORWARD, level, 0))
                            .add(offset.clone().crossProduct(UP).multiply(parseValues(caster, RIGHT, level, 0)));
                    location.add(offset).add(0, parseValues(caster, UPWARD, level, 0), 0);

                    // Apply the spread type
                    if (spread.equals("rain")) {
                        list.addAll(ParticleProjectile.rain(caster,
                                level,
                                location,
                                copy,
                                parseValues(caster, RADIUS, level, 2.0),
                                parseValues(caster, HEIGHT, level, 8.0),
                                amount,
                                callback,
                                lifespan));
                    } else {
                        Vector dir = location.getDirection();
                        if (spread.equals("horizontal cone")) {
                            dir.setY(0);
                            dir.normalize();
                        }
                        list.addAll(ParticleProjectile.spread(
                                caster,
                                level,
                                dir,
                                location,
                                copy,
                                parseValues(caster, ANGLE, level, 30.0),
                                amount,
                                callback,
                                lifespan
                        ));
                    }

                    for (ParticleProjectile p : list) {
                        SkillAPI.setMeta(p, LEVEL, level);
                        p.setAllyEnemy(ally, !ally);
                    }

                    Consumer<Location> onStep = preview.getBool("path")
                            ? loc -> new ParticleSettings(preview, "path-").instance(caster, loc.getX(), loc.getY(), loc.getZ())
                            : loc -> {};
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
