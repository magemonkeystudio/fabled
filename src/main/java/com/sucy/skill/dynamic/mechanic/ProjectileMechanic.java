/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ProjectileMechanic
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
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.RemoveTask;
import com.sucy.skill.task.RepeatingEntityTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Heals each target
 */
public class ProjectileMechanic extends MechanicComponent {
    private static final Vector                                       UP                 = new Vector(0, 1, 0);
    private static final String                                       PROJECTILE         = "projectile";
    private static final String                                       OVERRIDE_ITEM      = "override-item";
    private static final String                                       MATERIAL           = "material";
    private static final String                                       ENCHANTED          = "enchanted";
    private static final String                                       DURABILITY         = "durability";
    private static final String                                       CMD                = "custom-model-data";
    private static final String                                       FLAMING            = "flaming";
    private static final String                                       COST               = "cost";
    private static final String                                       VELOCITY           = "velocity";
    private static final String                                       LIFESPAN           = "lifespan";
    private static final String                                       SPREAD             = "spread";
    private static final String                                       AMOUNT             = "amount";
    private static final String                                       ANGLE              = "angle";
    private static final String                                       HEIGHT             = "height";
    private static final String                                       RADIUS             = "rain-radius";
    private static final String                                       FORWARD            = "forward";
    private static final String                                       UPWARD             = "upward";
    private static final String                                       RIGHT              = "right";
    private static final String                                       USE_EFFECT         = "use-effect";
    private static final String                                       EFFECT_KEY         = "effect-key";
    public static final  String                                       HOMING             = "homing";
    public static final  String                                       HOMING_TARGET      = "target";
    public static final  String                                       HOMING_DIST        = "homing-distance";
    public static final  String                                       REMEMBER           = "remember-key";
    public static final  String                                       CORRECTION         = "correction";
    public static final  String                                       WALL               = "wall";
    private static final HashMap<String, Class<? extends Projectile>> PROJECTILES        =
            new HashMap<String, Class<? extends Projectile>>() {{
                put("arrow", Arrow.class);
                put("egg", Egg.class);
                put("ghast fireball", LargeFireball.class);
                put("snowball", Snowball.class);
                put("fishing hook", FishHook.class);
            }};
    private static final HashMap<String, Material>                    MATERIALS          =
            new HashMap<String, Material>() {{
                put("arrow", Material.ARROW);
                put("egg", Material.EGG);
                put("snowball", snowBall());
            }};
    private static final Class<Enum<?>>                               PICKUP_STATUS_ENUM = null;

    private static Class<? extends Projectile> getProjectileClass(String projectileName) {
        StringBuilder conditionedName = new StringBuilder();
        for (String word : projectileName.split(" ")) {
            conditionedName.append(word.substring(0, 1).toUpperCase(Locale.US)).append(word.substring(1).toLowerCase());
        }
        try {
            return (Class<? extends Projectile>) Class.forName("org.bukkit.entity." + conditionedName);
        } catch (ClassNotFoundException e) {
            return PROJECTILES.get(projectileName);
        }
    }

    private static Material snowBall() {
        for (Material material : Material.values()) {
            if (material.name().startsWith("SNOW") && material.name().endsWith("BALL")) {
                return material;
            }
        }
        return Material.SNOW;
    }

    @Override
    public String getKey() {
        return "projectile";
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
        int                         amount     = (int) parseValues(caster, AMOUNT, level, 1.0);
        double                      speed      = parseValues(caster, VELOCITY, level, 2.0);
        boolean                     flaming    = settings.getString(FLAMING, "false").equalsIgnoreCase("true");
        String                      spread     = settings.getString(SPREAD, "cone").toLowerCase();
        String                      projectile = settings.getString(PROJECTILE, "arrow").toLowerCase();
        String                      cost       = settings.getString(COST, "none").toLowerCase();
        Class<? extends Projectile> type       = getProjectileClass(projectile);
        if (type == null) {
            type = Arrow.class;
        }

        // Cost to cast
        if (cost.equals("one") || cost.equals("all")) {
            Material mat = MATERIALS.get(settings.getString(PROJECTILE, "arrow").toLowerCase());
            if (mat == null || !(caster instanceof Player)) return false;
            Player player = (Player) caster;
            if (cost.equals("one") && !player.getInventory().contains(mat, 1)) {
                return false;
            }
            if (cost.equals("all") && !player.getInventory().contains(mat, amount)) {
                return false;
            }
            if (cost.equals("one")) {
                player.getInventory().removeItem(new ItemStack(mat));
            } else player.getInventory().removeItem(new ItemStack(mat, amount));
        }

        // Fire from each target
        List<Projectile> projectiles = new ArrayList<>();
        for (LivingEntity target : targets) {
            Location location = target.getEyeLocation();
            Vector   offset   = location.getDirection().setY(0).normalize();
            offset.multiply(parseValues(caster, FORWARD, level, 0))
                    .add(offset.clone().crossProduct(UP).multiply(parseValues(caster, RIGHT, level, 0)));
            location.add(offset).add(0, parseValues(caster, UPWARD, level, 0), 0);

            // Apply the spread type
            if (spread.equals("rain")) {
                Vector vel = new Vector(0, speed, 0);
                for (Location loc : CustomProjectile.calcRain(
                        location,
                        parseValues(caster, RADIUS, level, 2.0),
                        parseValues(caster, HEIGHT, level, 8.0),
                        amount)) {
                    Projectile p = caster.launchProjectile(type);
                    p.teleport(loc);
                    p.setVelocity(vel);
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
                    Projectile p = caster.launchProjectile(type);
                    p.teleport(location);
                    p.setVelocity(d.multiply(speed));
                    projectiles.add(p);
                }
            }
        }

        for (Projectile p : projectiles) {
            if (flaming) p.setFireTicks(Integer.MAX_VALUE);
            if (type.getName().contains("Arrow")) {
                p.setTicksLived(1180);
                try {
                    // Will fail under 1.12
                    try {
                        //1.14+
                        AbstractArrow arrow = (AbstractArrow) p;
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    } catch (NoClassDefFoundError e) {
                        //1.12+
                        Arrow    arrow             = (Arrow) p;
                        Class<?> pickupStatusClass = Class.forName("org.bukkit.Arrow$PickupStatus");
                        Arrow.class.getMethod("setPickupStatus", pickupStatusClass)
                                .invoke(arrow,
                                        pickupStatusClass.getMethod("valueOf", String.class)
                                                .invoke(null, "DISALLOWED"));
                    }
                } catch (NoSuchMethodError | ClassNotFoundException | NoSuchMethodException |
                         IllegalAccessException | InvocationTargetException ignored) {
                }
            }
            SkillAPI.setMeta(p, MechanicListener.SKILL_LEVEL, level);
            SkillAPI.setMeta(p, MechanicListener.P_CALL, this);
        }

        if (settings.getBool(OVERRIDE_ITEM)) {
            ItemStack itemStack = new ItemStack(Material.valueOf(settings.getString(MATERIAL, "Snowball")
                    .toUpperCase(Locale.US)
                    .replace(" ", "_")));
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                if (settings.getBool(ENCHANTED, false)) {
                    meta.addEnchant(Enchantment.DURABILITY, 1, false);
                }
                meta.setCustomModelData(settings.getInt(CMD, 0));
                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(settings.getInt(DURABILITY));
                }
                itemStack.setItemMeta(meta);
            }
            for (Projectile p : projectiles) {
                if (p instanceof ThrowableProjectile) {
                    ((ThrowableProjectile) p).setItem(itemStack);
                }
            }
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
     * @param projectile projectile calling back for
     * @param hit        the entity hit by the projectile, if any
     */
    public void callback(Projectile projectile, LivingEntity hit) {
        if (hit == null) hit = new TempEntity(projectile.getLocation());

        LivingEntity finalHit = hit;
        Bukkit.getScheduler().runTaskLater(SkillAPI.inst(), () -> {
            ArrayList<LivingEntity> targets = new ArrayList<>();
            targets.add(finalHit);
            executeChildren((LivingEntity) projectile.getShooter(),
                    SkillAPI.getMetaInt(projectile, MechanicListener.SKILL_LEVEL),
                    targets,
                    skill.isForced((LivingEntity) projectile.getShooter()));
            SkillAPI.removeMeta(projectile, MechanicListener.P_CALL);
            projectile.remove();
        }, 1L);
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
                String type     = settings.getString(PROJECTILE, "arrow").toLowerCase();
                double gravity;
                double drag;
                switch (type) {
                    // https://minecraft.fandom.com/wiki/Entity#Motion_of_entities
                    case "egg", "snowball", "splash potion", "ender pearl", "thrown exp bottle" -> {
                        gravity = -0.03;
                        drag = 0.01;
                    }
                    case "arrow", "spectral arrow", "trident" -> {
                        gravity = -0.05;
                        drag = 0.01;
                    }
                    case "llama spit" -> {
                        gravity = -0.06;
                        drag = 0.01;
                    }
                    case "fishing hook" -> {
                        gravity = -0.03;
                        drag = 0.08;
                    }
                    default -> {
                        gravity = 0;
                        drag = 0;
                    }
                }

                final Settings copy = new Settings(settings);
                copy.set(ParticleProjectile.SPEED, parseValues(caster, ParticleProjectile.SPEED, level, 1), 0);
                copy.set(ParticleHelper.POINTS_KEY, parseValues(caster, ParticleHelper.POINTS_KEY, level, 1), 0);
                copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);
                copy.set(ParticleProjectile.RADIUS, switch (type) {
                    case "arrow", "spectral arrow", "trident" -> 0.25;
                    case "dragon fireball", "fireball" -> 0.5;
                    case "shulker bullet", "small fireball", "wither skull" -> 0.15625;
                    default -> 0.125;
                }, 0);
                copy.set(ParticleProjectile.GRAVITY, gravity, 0);
                copy.set(ParticleProjectile.DRAG, drag, 0);
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
                        SkillAPI.setMeta(p, MechanicListener.SKILL_LEVEL, level);
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
