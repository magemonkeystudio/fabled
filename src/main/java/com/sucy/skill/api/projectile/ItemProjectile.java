/**
 * SkillAPI
 * com.sucy.skill.api.projectile.ItemProjectile
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
import com.sucy.skill.api.event.ItemProjectileExpireEvent;
import com.sucy.skill.api.event.ItemProjectileHitEvent;
import com.sucy.skill.api.event.ItemProjectileLandEvent;
import com.sucy.skill.api.event.ItemProjectileLaunchEvent;
import com.sucy.skill.api.target.TargetHelper;
import com.sucy.skill.api.util.DamageLoreRemover;
import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static com.sucy.skill.listener.MechanicListener.ITEM_PROJECTILE;

/**
 * <p>Represents a projectile that uses an item as the actual projectile.</p>
 */
public class ItemProjectile extends CustomProjectile {
    private static final String NAME          = "SkillAPI#";
    public static final  String HOMING        = "homing";
    public static final  String HOMING_TARGET = "target";
    public static final  String HOMING_DIST   = "homing-distance";
    public static final  String REMEMBER      = "remember-key";
    public static final  String CORRECTION    = "correction";
    public static final  String WALL          = "wall";
    public static final  String RADIUS        = "collision-radius";

    private static int NEXT = 0;

    private final Item                   item;
    private       int                    life;
    private final boolean                walls;
    private final double                 speed;
    protected     Supplier<LivingEntity> homing;
    protected     double                 correction;
    protected     double                 radius;

    /**
     * <p>Constructs a new item projectile.</p>
     *
     * @param thrower      the entity throwing the projectile
     * @param loc          the location to shoot from
     * @param item         the item to represent the projectile
     * @param vel          the velocity of the projectile
     * @param collideWalls whether to consider wall collisions as the projectile landing
     */
    public ItemProjectile(LivingEntity thrower,
                          int level,
                          Location loc,
                          Settings settings,
                          ItemStack item,
                          Vector vel,
                          int lifespan,
                          boolean collideWalls) {
        super(thrower, settings);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NAME + NEXT++);
            item.setItemMeta(meta);
        }
        DamageLoreRemover.removeAttackDmg(item);

        this.item = thrower.getWorld().dropItem(loc, item);
        this.item.setVelocity(vel);
        this.speed = vel.length();
        this.item.setPickupDelay(Integer.MAX_VALUE);
        this.walls = collideWalls;
        this.life = lifespan;
        this.radius = settings.getAttr(RADIUS, level, 0.2);
        SkillAPI.setMeta(this.item, ITEM_PROJECTILE, this);

        if (settings.getBool(HOMING, false)) {
            String target = settings.getString(HOMING_TARGET, "nearest");
            final Comparator<LivingEntity> comparator =
                    Comparator.comparingDouble(o -> o.getLocation().distanceSquared(getLocation()));
            if (target.equalsIgnoreCase("remember target")) {
                homing = () -> {
                    Object data = DynamicSkill.getCastData(getShooter())
                            .getRaw(ItemProjectile.this.settings.getString(REMEMBER, "target"));
                    if (data == null) return null;
                    try {
                        return ((List<LivingEntity>) data).stream()
                                .filter(tar -> ItemProjectile.this.settings.getBool(WALL, false)
                                        || !TargetHelper.isObstructed(getLocation(), tar.getEyeLocation()))
                                .min(comparator)
                                .orElse(null);
                    } catch (ClassCastException e) {
                        return null;
                    }
                };
            } else {
                homing = () -> Nearby.getLivingNearby(getLocation(),
                                ItemProjectile.this.settings.getAttr(HOMING_DIST, 0, 20))
                        .stream()
                        .filter(tar -> {
                            if (tar == getShooter()) return false;
                            if (!SkillAPI.getSettings().isValidTarget(tar)) return false;
                            boolean ally = SkillAPI.getSettings().isAlly(getShooter(), tar);
                            if (ally && !ItemProjectile.this.ally) return false;
                            if (!ally && !ItemProjectile.this.enemy) return false;
                            return true;
                        })
                        .filter(tar -> ItemProjectile.this.settings.getBool(WALL, false) || !TargetHelper.isObstructed(
                                getLocation(),
                                tar.getEyeLocation()))
                        .min(comparator)
                        .orElse(null);
            }
            this.correction = settings.getAttr(CORRECTION, 0, 0.2);
        }

        Bukkit.getPluginManager().callEvent(new ItemProjectileLaunchEvent(this));
    }

    public Item getItem() {return item;}

    /**
     * Retrieves the location of the projectile
     *
     * @return location of the projectile
     */
    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    /**
     * Handles expiring due to range or leaving loaded chunks
     */
    @Override
    protected Event expire() {
        return land();
    }

    /**
     * Handles landing on terrain
     */
    @Override
    protected Event land() {
        return new ItemProjectileLandEvent(this);
    }

    /**
     * Handles hitting an entity
     *
     * @param entity entity the projectile hit
     */
    @Override
    protected Event hit(LivingEntity entity) {
        return new ItemProjectileHitEvent(this, entity);
    }

    private static final Vector X_AXIS = new Vector(1, 0, 0);
    private static final Vector Y_AXIS = new Vector(0, 1, 0);
    private static final Vector Z_AXIS = new Vector(0, 0, 1);

    /**
     * @return true if item is on the ground, false otherwise
     */
    @Override
    protected boolean landed() {
        if (item.isOnGround()) {
            return true;
        }
        if (walls) {
            Vector velocity = item.getVelocity();
            RayTraceResult raytrace = item.getWorld()
                    .rayTraceBlocks(item.getLocation(), velocity, velocity.length(), FluidCollisionMode.NEVER, true);
            if (raytrace == null) {
                raytrace = collideWall(velocity, X_AXIS);
            }
            if (raytrace == null) {
                raytrace = collideWall(velocity, Z_AXIS);
            }
            if (raytrace == null) {
                raytrace = collideWall(velocity, Y_AXIS);
            }
            if (raytrace != null) {
                item.teleport(raytrace.getHitPosition().toLocation(item.getWorld()));
                return true;
            }
        }
        return false;
    }

    @Nullable
    private RayTraceResult collideWall(Vector direction, Vector defaultDirection) {
        direction = direction.clone();
        direction.multiply(defaultDirection);
        if (direction.lengthSquared() == 0) {
            RayTraceResult result = collideWall(defaultDirection, defaultDirection);
            if (result == null) {
                result = collideWall(defaultDirection.clone().multiply(-1), defaultDirection);
            }
            return result;
        }
        return item.getWorld()
                .rayTraceBlocks(item.getLocation().add(0, 0.125, 0),
                        direction,
                        radius + 0.1,
                        FluidCollisionMode.NEVER,
                        true);
    }

    /**
     * @return squared radius for colliding
     */
    @Override
    protected double getCollisionRadius() {
        return radius;
    }

    @Override
    protected Vector getVelocity() {
        return item.getVelocity();
    }

    @Override
    protected void setVelocity(final Vector velocity) {
        item.setVelocity(velocity);
    }

    /**
     * <p>Updates the projectile's position.</p>
     * <p>This is for the repeating task and if you call it yourself, it
     * will move faster than it should.</p>
     */
    @Override
    public void run() {
        if (homing != null) {
            LivingEntity target = homing.get();
            if (target != null) {
                Vector acceleration = target.getBoundingBox()
                        .getCenter()
                        .subtract(this.item.getBoundingBox().getCenter())
                        .normalize()
                        .multiply(speed)
                        .subtract(item.getVelocity());
                double length = acceleration.length();
                acceleration.multiply(1.0 / length).multiply(Math.min(length, correction));
                item.setVelocity(item.getVelocity().add(acceleration));
            }
        }

        if (isTraveling()) checkCollision(false);

        life--;
        if (life <= 0) {
            if (settings.getBool("on-expire") && callback != null) callback.callback(this, null);
            cancel();
            Bukkit.getPluginManager().callEvent(new ItemProjectileExpireEvent(this));
        }
    }

    /**
     * Removes the item on cancelling the task
     */
    @Override
    public void cancel() {
        super.cancel();
        item.remove();
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter      entity shooting the projectiles
     * @param direction    the center velocity of the spread
     * @param loc          location to shoot from
     * @param item         the item to use for the projectile
     * @param angle        angle of the spread
     * @param amount       number of projectiles to fire
     * @param callback     optional callback for when projectiles hit
     * @param lifespan     maximum duration of the projectile
     * @param collideWalls whether to consider wall collisions as the projectiles landing
     * @return list of fired projectiles
     */
    public static List<ItemProjectile> spread(LivingEntity shooter,
                                              int level,
                                              Vector direction,
                                              Location loc,
                                              Settings settings,
                                              ItemStack item,
                                              double angle,
                                              int amount,
                                              ProjectileCallback callback,
                                              int lifespan,
                                              boolean collideWalls) {
        double speed = direction.length();
        direction.normalize();
        List<Vector>         dirs = calcSpread(shooter.getLocation().getDirection(), angle, amount);
        List<ItemProjectile> list = new ArrayList<>();
        for (Vector dir : dirs) {
            Vector         vel = dir.multiply(speed);
            ItemProjectile p   = new ItemProjectile(shooter, level, loc, settings, item, vel, lifespan, collideWalls);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }

    /**
     * Fires a spread of projectiles from the location.
     *
     * @param shooter      entity shooting the projectiles
     * @param center       the center location to rain on
     * @param item         the item to use for the projectile
     * @param radius       radius of the circle
     * @param height       height above the center location
     * @param speed        speed of the projectiles
     * @param amount       number of projectiles to fire
     * @param callback     optional callback for when projectiles hit
     * @param lifespan     maximum duration of the projectile
     * @param collideWalls whether to consider wall collisions as the projectiles landing
     * @return list of fired projectiles
     */
    public static List<ItemProjectile> rain(LivingEntity shooter,
                                            int level,
                                            Location center,
                                            Settings settings,
                                            ItemStack item,
                                            double radius,
                                            double height,
                                            double speed,
                                            int amount,
                                            ProjectileCallback callback,
                                            int lifespan,
                                            boolean collideWalls) {
        Vector               vel  = new Vector(0, speed, 0);
        List<Location>       locs = calcRain(center, radius, height, amount);
        List<ItemProjectile> list = new ArrayList<>();
        for (Location l : locs) {
            l.setDirection(vel);
            ItemProjectile p = new ItemProjectile(shooter, level, l, settings, item, vel, lifespan, collideWalls);
            p.setCallback(callback);
            list.add(p);
        }
        return list;
    }
}
