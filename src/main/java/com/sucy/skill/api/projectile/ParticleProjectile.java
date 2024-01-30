/**
 * SkillAPI
 * com.sucy.skill.api.projectile.ParticleProjectile
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
package com.sucy.skill.api.projectile;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.ParticleProjectileExpireEvent;
import com.sucy.skill.api.event.ParticleProjectileHitEvent;
import com.sucy.skill.api.event.ParticleProjectileLandEvent;
import com.sucy.skill.api.event.ParticleProjectileLaunchEvent;
import com.sucy.skill.api.particle.ParticleHelper;
import com.sucy.skill.api.target.TargetHelper;
import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A fake projectile that plays particles along its path
 */
public class ParticleProjectile extends CustomProjectile {
    /**
     * Settings key for the projectile speed
     */
    public static final String SPEED = "velocity";
    /**
     * Settings key for the minimum projectile steps per meter
     */
    public static final String STEPS = "steps";
    /**
     * Settings key for the gravity the projectile is subjected to
     */
    public static final String GRAVITY = "gravity";
    /**
     * Settings key for the drag rate the projectile is subjected to
     */
    public static final String DRAG = "drag";
    /**
     * Settings key for the projectile's period for playing particles
     */
    public static final String PERIOD = "period";
    public static final String HOMING             = "homing";
    public static final String HOMING_TARGET      = "target";
    public static final String HOMING_DIST        = "homing-distance";
    public static final String REMEMBER           = "remember-key";
    public static final String CORRECTION         = "correction";
    public static final String WALL               = "wall";
    public static final String RADIUS             = "collision-radius";
    /**
     * Settings key for the projectile's frequency of playing particles
     * @deprecated unintuitively named, now PERIOD is used instead
     */
    @Deprecated
    public static final String LEGACY_FREQUENCY = "frequency";

    /**
     * Settings key for the projectile's effective gravity
     */

    private static final String PIERCE = "pierce";

    private       Location               loc;
    private       Vector                 vel;
    private       int                    life;
    private final int                    steps;
    private final double                 radius;
    private final double                 gravity;
    private final double                 drag;
    private final int                    particlePeriod;
    private       int                    count;
    private final boolean                pierce;
    protected     Consumer<Location>     onStep;
    protected     Supplier<LivingEntity> homing;
    protected     double                 correction;

    /**
     * Constructor
     *
     * @param shooter  entity that shot the projectile
     * @param level    level to use for scaling the speed
     * @param loc      initial location of the projectile
     * @param settings settings for the projectile
     */
    public ParticleProjectile(LivingEntity shooter, int level, Location loc, Settings settings, int lifespan) {
        super(shooter, settings);

        this.loc = loc;
        this.vel = loc.getDirection().multiply(settings.getAttr(SPEED, level, 1.0));
        this.life = lifespan;
        this.steps = settings.getInt(STEPS, 2);
        this.radius = settings.getAttr(RADIUS, level, 1.5);
        this.gravity = settings.getAttr(GRAVITY, level, -0.04);
        this.drag = settings.getAttr(DRAG, 0, 0.02);

        this.particlePeriod = settings.getInt(PERIOD, (int) (40 * settings.getDouble(LEGACY_FREQUENCY, 0.05)));
        this.pierce = settings.getBool(PIERCE, false);

        if (settings.getBool(HOMING, false)) {
            String target = settings.getString(HOMING_TARGET, "nearest");
            final Comparator<LivingEntity> comparator = Comparator.comparingDouble(o -> o.getLocation().distanceSquared(getLocation()));
            if (target.equalsIgnoreCase("remember target")) {
                homing = () -> {
                    Object data = DynamicSkill.getCastData(getShooter()).getRaw(ParticleProjectile.this.settings.getString(REMEMBER, "target"));
                    if (data == null) return null;
                    try {
                        return ((List<LivingEntity>) data).stream()
                                .filter(tar -> ParticleProjectile.this.settings.getBool(WALL, false) || !TargetHelper.isObstructed(getLocation(), tar.getEyeLocation()))
                                .min(comparator)
                                .orElse(null);
                    } catch (ClassCastException e) {
                        return null;
                    }
                };
            } else {
                homing = () -> Nearby.getLivingNearby(getLocation(), ParticleProjectile.this.settings.getAttr(HOMING_DIST, 0, 20)).stream()
                        .filter(tar -> {
                            if (tar == getShooter()) return false;
                            if (!SkillAPI.getSettings().isValidTarget(tar)) return false;
                            boolean ally = SkillAPI.getSettings().isAlly(getShooter(), tar);
                            if (ally && !ParticleProjectile.this.ally) return false;
                            if (!ally && !ParticleProjectile.this.enemy) return false;
                            return true;
                        })
                        .filter(tar -> ParticleProjectile.this.settings.getBool(WALL, false) || !TargetHelper.isObstructed(getLocation(), tar.getEyeLocation()))
                        .min(comparator)
                        .orElse(null);
            }
            this.correction = settings.getAttr(CORRECTION, 0, 0.2);
        }

        Bukkit.getPluginManager().callEvent(new ParticleProjectileLaunchEvent(this));
    }

    /**
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    @Override
    public Location getLocation() {
        return loc;
    }

    /**
     * Handles expiring due to range or leaving loaded chunks
     */
    @Override
    protected Event expire() {
        return new ParticleProjectileExpireEvent(this);
    }

    /**
     * Handles landing on terrain
     */
    @Override
    protected Event land() {
        return new ParticleProjectileLandEvent(this);
    }

    /**
     * Handles hitting an entity
     *
     * @param entity entity the projectile hit
     */
    @Override
    protected Event hit(LivingEntity entity) {
        return new ParticleProjectileHitEvent(this, entity);
    }

    /**
     * @return true if passing through a solid block, false otherwise
     */
    @Override
    protected boolean landed() {
        return TargetHelper.isSolid(getLocation().getBlock().getType());
    }

    /**
     * @return squared radius for colliding
     */
    @Override
    protected double getCollisionRadius() {
        return radius;
    }

    /**
     * @return velocity of the projectile
     */
    @Override
    public Vector getVelocity() {
        return vel;
    }

    /**
     * Teleports the projectile to a location
     *
     * @param loc location to teleport to
     */
    public void teleport(Location loc) {
        this.loc = loc;
    }

    /**
     * Sets the velocity of the projectile
     *
     * @param vel new velocity
     */
    @Override
    public void setVelocity(Vector vel) {
        this.vel = vel;
    }

    /**
     * Passes the current projectile's location to the consumer every step of the way
     * @param onStep the consumer the location is passed to
     */
    public void setOnStep(Consumer<Location> onStep) {
        this.onStep = onStep;
    }

    /**
     * Updates the projectiles position and checks for collisions
     */
    @Override
    public void run() {
        vel.setX(vel.getX()-drag*vel.getX());
        vel.setY(vel.getY()-drag*vel.getY()+gravity);
        vel.setZ(vel.getZ()-drag*vel.getZ());

        if (homing != null) {
            LivingEntity target = homing.get();
            if (target != null) {
                Vector acceleration = target.getBoundingBox().getCenter().subtract(getLocation().toVector())
                        .normalize().multiply(settings.getAttr(SPEED, 0, 1.0)).subtract(vel);
                double length = acceleration.length();
                acceleration.multiply(1.0/length).multiply(Math.min(length, correction));
                vel.add(acceleration);
            }
        }

        // Go through multiple steps to avoid tunneling
        double speed = vel.length();
        int steps = (int) Math.round(speed * this.steps);
        Vector stepVector = vel.clone().multiply(1.0/steps);
        for (int i = 0; i < steps; i++) {
            loc.add(stepVector);

            // Particle along path
            count++;
            if (count >= particlePeriod) {
                count = 0;
                if (onStep == null) ParticleHelper.play(loc, settings);
                else onStep.accept(loc);
            }

            if (!isTraveling()) return;

            if (!checkCollision(pierce)) break;
        }


        // Lifespan
        life--;
        if (life <= 0) {
            if (settings.getBool("on-expire")) callback.callback(this, null);
            cancel();
            Bukkit.getPluginManager().callEvent(new ParticleProjectileExpireEvent(this));
        }
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter   entity shooting the projectiles
     * @param level     level to use for scaling the speed
     * @param direction the center direction of the spread
     * @param loc       location to shoot from
     * @param settings  settings to use when firing
     * @param angle     angle of the spread
     * @param amount    number of projectiles to fire
     * @param callback  optional callback for when projectiles hit
     * @return list of fired projectiles
     */
    public static List<ParticleProjectile> spread(LivingEntity shooter,
                                                       int level,
                                                       Vector direction,
                                                       Location loc,
                                                       Settings settings,
                                                       double angle,
                                                       int amount,
                                                       ProjectileCallback callback,
                                                       int lifespan) {
        List<Vector>             dirs = calcSpread(direction, angle, amount);
        List<ParticleProjectile> list = new ArrayList<>();
        for (Vector dir : dirs) {
            Location l = loc.clone();
            l.setDirection(dir);
            ParticleProjectile p = new ParticleProjectile(shooter, level, l, settings, lifespan);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter  entity shooting the projectiles
     * @param level    level to use for scaling the speed
     * @param center   the center location to rain on
     * @param settings settings to use when firing
     * @param radius   radius of the circle
     * @param height   height above the center location
     * @param amount   number of projectiles to fire
     * @param callback optional callback for when projectiles hit
     * @return list of fired projectiles
     */
    public static List<ParticleProjectile> rain(LivingEntity shooter,
                                                     int level,
                                                     Location center,
                                                     Settings settings,
                                                     double radius,
                                                     double height,
                                                     int amount,
                                                     ProjectileCallback callback,
                                                     int lifespan) {
        Vector                        vel  = new Vector(0, 1, 0);
        List<Location>           locs = calcRain(center, radius, height, amount);
        List<ParticleProjectile> list = new ArrayList<>();
        for (Location l : locs) {
            l.setDirection(vel);
            ParticleProjectile p = new ParticleProjectile(shooter, level, l, settings, lifespan);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }
}
