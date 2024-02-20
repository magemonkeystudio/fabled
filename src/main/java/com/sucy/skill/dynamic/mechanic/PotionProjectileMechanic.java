/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.PotionProjectileMechanic
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
import com.sucy.skill.api.particle.target.EntityTarget;
import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ParticleProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.api.target.TargetHelper;
import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.task.RemoveTask;
import com.sucy.skill.task.RepeatingEntityTask;
import mc.promcteam.engine.mccore.util.VersionManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.sucy.skill.listener.MechanicListener.*;

/**
 * Heals each target
 */
public class PotionProjectileMechanic extends MechanicComponent {
    private static final Vector UP            = new Vector(0, 1, 0);
    private static final String FLAMING       = "flaming";
    private static final String VELOCITY      = "velocity";
    private static final String LIFESPAN      = "lifespan";
    private static final String SPREAD        = "spread";
    private static final String AMOUNT        = "amount";
    private static final String ANGLE         = "angle";
    private static final String HEIGHT        = "height";
    private static final String RAIN_RADIUS   = "rain-radius";
    private static final String FORWARD       = "forward";
    private static final String UPWARD        = "upward";
    private static final String RIGHT         = "right";
    private static final String USE_EFFECT    = "use-effect";
    private static final String EFFECT_KEY    = "effect-key";
    public static final  String HOMING        = "homing";
    public static final  String HOMING_TARGET = "target";
    public static final  String HOMING_DIST   = "homing-distance";
    public static final  String REMEMBER      = "remember-key";
    public static final  String CORRECTION    = "correction";
    public static final  String WALL          = "wall";

    private static final String ALLY   = "group";
    private static final String LINGER = "linger";
    private static final String COLOR  = "color";

    public static final String DURATION        = "duration";
    public static final String WAIT_TIME       = "wait-time";
    public static final String REAPPLY_DELAY   = "reapplication-delay";
    public static final String DURATION_ON_USE = "duration-on-use";
    public static final String RADIUS          = "cloud-radius";
    public static final String RADIUS_ON_USE   = "radius-on-use";
    public static final String RADIUS_PER_TICK = "radius-per-tick";
    public static final String CLOUD_PREFIX    = "cloud-";

    @Override
    public String getKey() {
        return "potion projectile";
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
        int     amount  = (int) parseValues(caster, AMOUNT, level, 1.0);
        double  speed   = parseValues(caster, VELOCITY, level, 2.0);
        boolean flaming = settings.getString(FLAMING, "false").equalsIgnoreCase("true");
        String  spread  = settings.getString(SPREAD, "cone").toLowerCase();

        // Fire from each target
        List<ThrownPotion> projectiles = new ArrayList<>();
        for (LivingEntity target : targets) {
            Location location = target.getEyeLocation();
            Vector   offset   = location.getDirection().setY(0).normalize();
            offset.multiply(parseValues(caster, FORWARD, level, 0))
                    .add(offset.clone().crossProduct(UP).multiply(parseValues(caster, RIGHT, level, 0)));
            location.add(offset).add(0, parseValues(caster, UPWARD, level, 0), 0);

            // Apply the spread type
            if (spread.equals("rain")) {
                Vector dir = new Vector(0, speed, 0);
                for (Location loc : CustomProjectile.calcRain(
                        location,
                        parseValues(caster, RAIN_RADIUS, level, 2.0),
                        parseValues(caster, HEIGHT, level, 8.0),
                        amount)) {
                    ThrownPotion p = caster.launchProjectile(ThrownPotion.class);
                    p.setVelocity(dir);
                    p.teleport(loc);
                    projectiles.add(p);
                }
            } else {
                Vector dir = location.getDirection();
                if (spread.equals("horizontal cone")) {
                    dir.setY(0);
                    dir.normalize();
                }
                List<Vector> dirs = CustomProjectile.calcSpread(dir, parseValues(caster, ANGLE, level, 30.0), amount);
                for (Vector d : dirs) {
                    ThrownPotion p = caster.launchProjectile(ThrownPotion.class);
                    p.teleport(location);
                    p.setVelocity(d.multiply(speed));
                    projectiles.add(p);
                }
            }
        }

        ItemStack itemStack = new ItemStack(
                settings.getString(LINGER, "false").equalsIgnoreCase("true") && VersionManager.isVersionAtLeast(
                        VersionManager.V1_9_0)
                        ? Material.LINGERING_POTION
                        : Material.SPLASH_POTION);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = ((PotionMeta) meta);
            potionMeta.setColor(Color.fromRGB(Integer.parseInt(settings.getString(COLOR, "#ff0000").substring(1), 16)));
            // An effect is required for an AreaEffectCloud to spawn, but application will be cancelled
            try {
                potionMeta.setBasePotionType(PotionType.INVISIBILITY);
            } catch (NoSuchMethodError e) {
                potionMeta.setBasePotionData(new PotionData(PotionType.INVISIBILITY));
            }
            itemStack.setItemMeta(meta);
        }
        for (ThrownPotion potion : projectiles) {
            potion.setItem(itemStack);
            if (flaming) potion.setFireTicks(Integer.MAX_VALUE);
            SkillAPI.setMeta(potion, POTION_PROJECTILE, this);
            SkillAPI.setMeta(potion, SKILL_LEVEL, level);
            SkillAPI.setMeta(potion, SKILL_CASTER, caster);
        }

        if (settings.getBool(USE_EFFECT, false)) {
            EffectPlayer player = new EffectPlayer(settings);
            for (Projectile p : projectiles) {
                player.start(
                        new EntityTarget(p),
                        settings.getString(EFFECT_KEY, skill.getName()),
                        Integer.MAX_VALUE,
                        level,
                        true);
            }
        }

        if (settings.getBool(HOMING, false)) {
            String                             target = settings.getString(HOMING_TARGET, "nearest");
            Function<Projectile, LivingEntity> homing;
            if (target.equalsIgnoreCase("remember target")) {
                homing = (proj) -> {
                    Object data = Objects.requireNonNull(DynamicSkill.getCastData((LivingEntity) proj.getShooter()))
                            .getRaw(settings.getString(REMEMBER, "target"));
                    if (data == null) return null;
                    try {
                        return ((List<LivingEntity>) data).stream()
                                .filter(tar -> settings.getBool(WALL, false)
                                        || !TargetHelper.isObstructed(proj.getLocation(), tar.getEyeLocation()))
                                .min(Comparator.comparingDouble(o -> o.getLocation()
                                        .distanceSquared(proj.getLocation())))
                                .orElse(null);
                    } catch (ClassCastException e) {
                        return null;
                    }
                };
            } else {
                homing = (proj) -> Nearby.getLivingNearby(proj.getLocation(), settings.getAttr(HOMING_DIST, 0, 20))
                        .stream()
                        .filter(tar -> {
                            if (tar == proj.getShooter()) return false;
                            return SkillAPI.getSettings().isValidTarget(tar);
                        })
                        .filter(tar -> settings.getBool(WALL, false) || !TargetHelper.isObstructed(proj.getLocation(),
                                tar.getEyeLocation()))
                        .min(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(proj.getLocation())))
                        .orElse(null);
            }
            double correction = settings.getAttr(CORRECTION, 0, 0.2);

            new RepeatingEntityTask<>(projectiles, proj -> {
                LivingEntity tar = homing.apply(proj);
                if (tar != null) {
                    Vector acceleration = tar.getBoundingBox().getCenter().subtract(proj.getBoundingBox().getCenter())
                            .normalize().multiply(speed).subtract(proj.getVelocity());
                    double length = acceleration.length();
                    acceleration.multiply(1.0 / length).multiply(Math.min(length, correction));
                    proj.setVelocity(proj.getVelocity().add(acceleration));
                }
            });
        }

        new RepeatingEntityTask<>(projectiles, proj -> ParticleHelper.play(proj.getLocation(), settings));
        new RemoveTask(projectiles, (int) parseValues(caster, LIFESPAN, level, 9999) * 20) {
            @Override
            public void run() {
                super.run();
                if (settings.getBool("on-expire", false)) {
                    for (Projectile projectile1 : projectiles) {
                        callback(projectile1, null);
                    }
                }
            }
        };

        return !targets.isEmpty();
    }

    /**
     * The callback for the projectiles that applies child components
     *
     * @param entity potion effect
     * @param hit    the entity hit by the projectile, if any
     */
    public void callback(Entity entity, Collection<LivingEntity> hit) {
        List<LivingEntity> targets = new ArrayList<>(hit);
        String             group   = settings.getString(ALLY, "enemy").toLowerCase();
        boolean            both    = group.equals("both");
        boolean            ally    = group.equals("ally");
        LivingEntity       caster  = (LivingEntity) SkillAPI.getMeta(entity, SKILL_CASTER);
        int                level   = SkillAPI.getMetaInt(entity, SKILL_LEVEL);
        Location           loc     = entity.getLocation();
        for (int i = 0; i < targets.size(); i++) {
            if (!both && SkillAPI.getSettings().canAttack(caster, targets.get(i)) == ally) {
                targets.remove(i);
                i--;
            }
        }
        if (targets.isEmpty()) {
            LivingEntity locTarget = new TempEntity(loc);
            targets.add(locTarget);
        }
        executeChildren(caster, level, targets, skill.isForced(caster));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        List<LivingEntity> targets = new ArrayList<>();

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                targets.clear();

                int    amount   = (int) parseValues(caster, AMOUNT, level, 1.0);
                String spread   = settings.getString(SPREAD, "cone").toLowerCase();
                int    lifespan = (int) (parseValues(caster, LIFESPAN, level, 9999) * 20);

                final Settings copy = new Settings(settings);
                copy.set(ParticleProjectile.SPEED, parseValues(caster, ParticleProjectile.SPEED, level, 1), 0);
                copy.set(ParticleHelper.POINTS_KEY, parseValues(caster, ParticleHelper.POINTS_KEY, level, 1), 0);
                copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);
                copy.set(ParticleProjectile.RADIUS, 0.125, 0);
                copy.set(ParticleProjectile.GRAVITY, -0.03, 0);
                copy.set(ParticleProjectile.DRAG, 0.01, 0);
                copy.set(ParticleProjectile.PERIOD, preview.getInt("path-steps", 2));

                ProjectileCallback callback = (projectile, hit) -> {
                    if (hit == null) {
                        hit = new TempEntity(projectile.getLocation());
                    }
                    List<LivingEntity> hitTargets = new ArrayList<>();
                    if (settings.getBool(LINGER, false)) {
                        double   radius = parseValues(caster, RADIUS, level, 3);
                        Location loc    = projectile.getLocation();
                        hitTargets.addAll(Nearby.getLivingNearby(projectile.getLocation().getWorld(), new BoundingBox(
                                loc.getX() - radius,
                                loc.getY() + 0.6,
                                loc.getZ() - radius,
                                loc.getX() + radius,
                                loc.getY() + 1.1,
                                loc.getZ() + radius
                        )));
                    }
                    if (hitTargets.isEmpty()) {
                        hitTargets.add(hit);
                    }
                    targets.addAll(hitTargets);
                    if (preview.getBool("per-target")) {
                        for (LivingEntity target : hitTargets) {
                            ParticleHelper.play(target.getLocation(), preview, Set.of(caster), "per-target-",
                                    preview.getBool("per-target-" + "hitbox") ? target.getBoundingBox() : null);
                        }
                    }
                };

                List<ParticleProjectile> list = new ArrayList<>();
                // Fire from each target
                for (LivingEntity target : targetSupplier.get()) {
                    Location location = target.getEyeLocation();
                    Vector   offset   = location.getDirection().setY(0).normalize();
                    offset.multiply(parseValues(caster, FORWARD, level, 0))
                            .add(offset.clone().crossProduct(UP).multiply(parseValues(caster, RIGHT, level, 0)));
                    location.add(offset).add(0, parseValues(caster, UPWARD, level, 0), 0);

                    // Apply the spread type
                    if (spread.equals("rain")) {
                        list.addAll(ParticleProjectile.rain(caster,
                                level,
                                location,
                                copy,
                                parseValues(caster, RAIN_RADIUS, level, 2.0),
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
                        SkillAPI.setMeta(p, SKILL_LEVEL, level);
                        p.setAllyEnemy(true, true);
                    }

                    Consumer<Location> onStep = preview.getBool("path")
                            ? loc -> new ParticleSettings(preview, "path-").instance(caster,
                            loc.getX(),
                            loc.getY(),
                            loc.getZ())
                            : loc -> {
                            };
                    for (ParticleProjectile p : list) p.setOnStep(onStep);

                    for (ParticleProjectile p : list) {
                        while (p.isValid()) {
                            p.run();
                        }
                    }
                }
            }
        }.runTaskTimer(SkillAPI.inst(), 0, Math.max(1, preview.getInt("period", 5)));
        onPreviewStop.add(task::cancel);

        playChildrenPreviews(onPreviewStop, caster, level, () -> targets);
    }
}
