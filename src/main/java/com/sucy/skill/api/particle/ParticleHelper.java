/**
 * SkillAPI
 * com.sucy.skill.api.particle.Particle
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.api.particle;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.enums.Direction;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Simplified particle utility compared to MCCore's
 */
public final class ParticleHelper {

    public static final String ARRANGEMENT_KEY    = "arrangement";
    public static final String POINTS_KEY         = "particles";
    public static final String LEVEL              = "level";
    public static final String PARTICLE_KEY       = "particle";
    public static final String MATERIAL_KEY       = "material";
    public static final String CMD_KEY            = "type";
    public static final String DURABILITY_KEY     = "durability";
    public static final String RADIUS_KEY         = "radius";
    public static final String AMOUNT_KEY         = "amount";
    public static final String DIRECTION_KEY      = "direction";
    public static final String VISIBLE_RADIUS_KEY = "visible-radius";
    public static final String DX_KEY             = "dx";
    public static final String DY_KEY             = "dy";
    public static final String DZ_KEY             = "dz";
    public static final String SPEED_KEY          = "speed";
    public static final String DATA_KEY           = "data";
    public static final String DUST_COLOR         = "dust-color";
    public static final String FINAL_DUST_COLOR   = "final-dust-color";
    public static final String DUST_SIZE          = "dust-size";

    private static final Random random = new Random();

    private ParticleHelper() {}

    public static Particle getFromKey(String particleKey) {
        return Particle.valueOf(particleKey.toUpperCase().replace(' ', '_'));
    }

    /**
     * Plays particles about the given location using the given settings
     */
    public static void play(Location loc,
                            Settings settings,
                            @Nullable Set<Player> viewers,
                            String particleKey,
                            @Nullable BoundingBox targetHitbox) {
        double   visibleRadius = settings.getDouble(particleKey + VISIBLE_RADIUS_KEY, 25);
        Particle particle      =
                ParticleHelper.getFromKey(settings.getString(particleKey + PARTICLE_KEY, "Villager happy"));
        int      amount        = settings.getInt(particleKey + AMOUNT_KEY, 1);
        double   dx            = settings.getDouble(particleKey + DX_KEY, 0);
        double   dy            = settings.getDouble(particleKey + DY_KEY, 0);
        double   dz            = settings.getDouble(particleKey + DZ_KEY, 0);
        float    speed         = (float) settings.getDouble(particleKey + SPEED_KEY, 0.1);
        Object   object        = makeObject(particle, settings, particleKey);

        String arrangement = settings.getString(particleKey + ARRANGEMENT_KEY, "").toLowerCase();
        int    level       = settings.getInt(particleKey + LEVEL, 1);
        int    points      = (int) settings.getAttr(particleKey + POINTS_KEY, 0, 20);
        if (targetHitbox != null) {
            switch (arrangement) {
                case "sphere":
                case "hemisphere":
                    loc.add(0, targetHitbox.getHeight() / 2, 0);
                    break;
                default:
            }
        }
        switch (arrangement) {
            case "circle":
                fillCircle(loc,
                        settings,
                        level,
                        points,
                        visibleRadius,
                        particle,
                        amount,
                        dx,
                        dy,
                        dz,
                        speed,
                        object,
                        particleKey,
                        viewers);
                break;
            case "sphere":
                fillSphere(loc,
                        settings,
                        level,
                        points,
                        visibleRadius,
                        particle,
                        amount,
                        dx,
                        dy,
                        dz,
                        speed,
                        object,
                        particleKey,
                        viewers);
                break;
            case "hemisphere":
                fillHemisphere(loc,
                        settings,
                        level,
                        points,
                        visibleRadius,
                        particle,
                        amount,
                        dx,
                        dy,
                        dz,
                        speed,
                        object,
                        particleKey,
                        viewers);
                break;
            default:
                if (viewers == null)
                    viewers = filterPlayers(Objects.requireNonNull(loc.getWorld()).getPlayers(), loc, visibleRadius);
                viewers.forEach(
                        player -> player.spawnParticle(particle, loc, amount, dx, dy, dz, speed, object));
                break;
        }
    }


    /**
     * Plays particles about the given location using the given settings
     */
    public static void play(Location loc, Settings settings) {
        play(loc, settings, null, "", null);
    }

    /**
     * Plays several of a particle type randomly within a circle
     */
    public static void fillCircle(
            Location loc, Settings settings, int level, int points, double visibleRadius,
            Particle particle, int amount, double dx, double dy, double dz, float speed, Object object,
            String particleKey, @Nullable Set<Player> viewers) {
        double       radius       = settings.getAttr(particleKey + RADIUS_KEY, level, 3.0);
        World        world        = Objects.requireNonNull(loc.getWorld());
        List<Player> worldPlayers = world.getPlayers();
        double       rSquared     = radius * radius;
        double       twoRadius    = radius * 2;

        Location temp  = loc.clone();
        int      index = 0;

        Direction direction = null;
        if (settings.has(particleKey + DIRECTION_KEY)) {
            try {
                direction = Direction.valueOf(settings.getString(particleKey + DIRECTION_KEY));
            } catch (Exception ex) { /* Use default value */ }
        }
        if (direction == null) {
            direction = Direction.XZ;
        }

        // Play the particles
        while (index < points) {
            if (direction == Direction.XY || direction == Direction.XZ) {
                temp.setX(loc.getX() + random.nextDouble() * twoRadius - radius);
            }
            if (direction == Direction.XY || direction == Direction.YZ) {
                temp.setY(loc.getY() + random.nextDouble() * twoRadius - radius);
            }
            if (direction == Direction.XZ || direction == Direction.YZ) {
                temp.setZ(loc.getZ() + random.nextDouble() * twoRadius - radius);
            }

            if (temp.distanceSquared(loc) > rSquared) {
                continue;
            }

            if (viewers == null) viewers = filterPlayers(worldPlayers, temp, visibleRadius);
            viewers.forEach(player -> player.spawnParticle(particle, temp, amount, dx, dy, dz, speed, object));
            index++;
        }
    }

    public static void fillCircle(
            Location loc, Settings settings, int level, int points, double visibleRadius,
            Particle particle, int amount, double dx, double dy, double dz, float speed, Object object) {
        fillCircle(loc, settings, level, points, visibleRadius, particle, amount, dx, dy, dz, speed, object, "", null);
    }

    /**
     * Randomly plays particle effects within the sphere
     */
    public static void fillSphere(
            Location loc, Settings settings, int level, int points, double visibleRadius,
            Particle particle, int amount, double dx, double dy, double dz, float speed, Object object,
            String particleKey, @Nullable Set<Player> viewers) {
        double       radius       = settings.getAttr(particleKey + RADIUS_KEY, level, 3.0);
        World        world        = Objects.requireNonNull(loc.getWorld());
        List<Player> worldPlayers = world.getPlayers();
        double       rSquared     = radius * radius;
        double       twoRadius    = radius * 2;

        Location temp  = loc.clone();
        int      index = 0;

        // Play the particles
        while (index < points) {
            temp.setX(loc.getX() + random.nextDouble() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextDouble() * twoRadius - radius);
            temp.setZ(loc.getZ() + random.nextDouble() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) {
                continue;
            }

            if (viewers == null) viewers = filterPlayers(worldPlayers, temp, visibleRadius);
            viewers.forEach(
                    player -> player.spawnParticle(particle, temp, amount, dx, dy, dz, speed, object));
            index++;
        }
    }

    public static void fillSphere(
            Location loc, Settings settings, int level, int points, double visibleRadius,
            Particle particle, int amount, double dx, double dy, double dz, float speed, Object object) {
        fillSphere(loc, settings, level, points, visibleRadius, particle, amount, dx, dy, dz, speed, object, "", null);
    }

    /**
     * Randomly plays particle effects within the hemisphere
     */
    public static void fillHemisphere(
            Location loc, Settings settings, int level, int points, double visibleRadius,
            Particle particle, int amount, double dx, double dy, double dz, float speed, Object object,
            String particleKey, @Nullable Set<Player> viewers) {
        double       radius       = settings.getAttr(particleKey + RADIUS_KEY, level, 3.0);
        World        world        = Objects.requireNonNull(loc.getWorld());
        List<Player> worldPlayers = world.getPlayers();
        double       rSquared     = radius * radius;
        double       twoRadius    = radius * 2;

        Location temp  = loc.clone();
        int      index = 0;

        // Play the particles
        while (index < points) {
            temp.setX(loc.getX() + random.nextDouble() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextDouble() * radius);
            temp.setZ(loc.getZ() + random.nextDouble() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) {
                continue;
            }

            if (viewers == null) viewers = filterPlayers(worldPlayers, temp, visibleRadius);
            viewers.forEach(
                    player -> player.spawnParticle(particle, temp, amount, dx, dy, dz, speed, object));
            index++;
        }
    }

    public static void fillHemisphere(
            Location loc, Settings settings, int level, int points, double visibleRadius,
            Particle particle, int amount, double dx, double dy, double dz, float speed, Object object) {
        fillHemisphere(loc,
                settings,
                level,
                points,
                visibleRadius,
                particle,
                amount,
                dx,
                dy,
                dz,
                speed,
                object,
                "",
                null);
    }

    public static Object makeObject(Particle particle, Settings settings, String particleKey) {
        return switch (particle) {
            case SCULK_CHARGE -> Float.parseFloat(settings.getString(particleKey + DATA_KEY, "0"));
            default -> makeObject(particle,
                    Material.valueOf(settings.getString(particleKey + MATERIAL_KEY, "DIRT")
                            .toUpperCase()
                            .replace(" ", "_")),
                    settings.getInt(particleKey + CMD_KEY, 0),
                    settings.getInt(particleKey + DURABILITY_KEY, 0),
                    Color.fromRGB(Integer.parseInt(settings.getString(particleKey + DUST_COLOR, "#FF0000").substring(1),
                            16)),
                    Color.fromRGB(Integer.parseInt(settings.getString(particleKey + FINAL_DUST_COLOR, "#FF0000")
                            .substring(1), 16)),
                    (float) settings.getDouble(particleKey + DUST_SIZE, 1));
        };
    }

    public static Object makeObject(Particle particle, Settings settings) {
        return makeObject(particle, settings, "");
    }

    public static Object makeObject(Particle particle,
                                    Material material,
                                    int cmd,
                                    int durability,
                                    Color dustColor,
                                    Color toColor,
                                    float dustSize) {
        Object object = null;
        switch (particle) {
            case REDSTONE:
                object = new Particle.DustOptions(dustColor, dustSize);
                break;
            case ITEM_CRACK:
                ItemStack item = new ItemStack(material);
                ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                meta.setCustomModelData(cmd);
                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(durability);
                }
                item.setItemMeta(meta);
                object = item;
                break;
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
            case BLOCK_MARKER:
                object = material.createBlockData();
                break;
            case DUST_COLOR_TRANSITION:
                object = new Particle.DustTransition(dustColor, toColor, dustSize);
                break;
        }
        return object;
    }

    public static Set<Player> filterPlayers(Collection<Player> players, Location location, double visibleRadius) {
        visibleRadius = visibleRadius * visibleRadius;
        Set<Player> result = new HashSet<>();
        for (Player player : players) {
            if (location.distanceSquared(player.getLocation()) <= visibleRadius) {
                result.add(player);
            }
        }
        return result;
    }
}
