/**
 * Fabled
 * com.promcteam.fabled.api.util.Nearby
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.api.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Fetches nearby entities by going through possible chunks
 * instead of all entities in a world
 */
public class Nearby {
    /**
     * Gets entities nearby a location using a given radius
     *
     * @param loc    location centered around
     * @param radius radius to get within
     * @return nearby entities
     */
    public static List<Entity> getNearby(Location loc, double radius) {
        List<Entity> result = new ArrayList<Entity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        radius *= radius;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (entity.getLocation().distanceSquared(loc) < radius)
                        result.add(entity);

        return result;
    }

    /**
     * Fetches entities nearby a location using a given radius
     *
     * @param loc    location centered around
     * @param radius radius to get within
     * @return nearby entities
     */
    public static List<LivingEntity> getLivingNearby(Location loc, double radius) {
        return getLivingNearby(null, loc, radius, false);
    }

    public static List<LivingEntity> getLivingNearby(Location loc, double radius, boolean includeCaster) {
        return getLivingNearby(null, loc, radius, includeCaster);
    }

    public static List<LivingEntity> getLivingNearby(World world, BoundingBox boundingBox) {
        return getLivingNearby(null, world, boundingBox, false);
    }

    public static List<LivingEntity> getLivingNearby(World world, BoundingBox boundingBox, boolean includeCaster) {
        return getLivingNearby(null, world, boundingBox, includeCaster);
    }

    private static List<LivingEntity> getLivingNearby(Entity source,
                                                      Location loc,
                                                      double radius,
                                                      boolean includeCaster) {
        List<LivingEntity> result    = new ArrayList<>();
        Map<UUID, Double>  distances = new HashMap<>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        radius *= radius;
        World world = Objects.requireNonNull(loc.getWorld());

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : world.getChunkAt(i, j).getEntities())
                    if ((includeCaster || entity != source)
                            && entity instanceof LivingEntity
                            && entity.getWorld() == loc.getWorld()
                            && entity.getLocation().distanceSquared(loc) < radius) {
                        result.add((LivingEntity) entity);
                        distances.put(entity.getUniqueId(), entity.getLocation().distanceSquared(loc));
                    }

        return result.stream().sorted(
                Comparator.comparingDouble(entity -> distances.get(entity.getUniqueId()))
        ).collect(Collectors.toList());
    }

    private static List<LivingEntity> getLivingNearby(Entity source,
                                                      World world,
                                                      BoundingBox boundingBox,
                                                      boolean includeCaster) {
        List<LivingEntity> result    = new ArrayList<>();
        Map<UUID, Double>  distances = new HashMap<>();

        int minX = (int) (boundingBox.getMinX()) >> 4;
        int maxX = (int) (boundingBox.getMaxX()) >> 4;
        int minZ = (int) (boundingBox.getMinZ()) >> 4;
        int maxZ = (int) (boundingBox.getMaxZ()) >> 4;

        Location loc = boundingBox.getCenter().toLocation(world);

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : world.getChunkAt(i, j).getEntities())
                    if ((includeCaster || entity != source)
                            && entity instanceof LivingEntity
                            && entity.getWorld() == world
                            && entity.getBoundingBox().overlaps(boundingBox)) {
                        result.add((LivingEntity) entity);
                        distances.put(entity.getUniqueId(), entity.getLocation().distanceSquared(loc));
                    }

        return result.stream().sorted(
                Comparator.comparingDouble(entity -> distances.get(entity.getUniqueId()))
        ).collect(Collectors.toList());
    }

    /**
     * Gets entities nearby a location using a given radius
     *
     * @param entity entity to get nearby ones for
     * @param radius radius to get within
     * @return nearby entities
     */
    public static List<Entity> getNearby(Entity entity, double radius) {
        return getNearby(entity.getLocation(), radius);
    }

    /**
     * Fetches entities nearby a location using a given radius
     *
     * @param entity entity to get nearby ones for
     * @param radius radius to get within
     * @return nearby entities
     */
    public static List<LivingEntity> getLivingNearby(Entity entity, double radius) {
        return getLivingNearby(entity, entity.getLocation(), radius, false);
    }

    public static List<LivingEntity> getLivingNearby(Entity entity, double radius, boolean includeCaster) {
        return getLivingNearby(entity, entity.getLocation(), radius, includeCaster);
    }

    public static List<Entity> getNearbyBox(Location loc, double radius) {
        List<Entity> result = new ArrayList<Entity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (boxDistance(entity.getLocation(), loc) < radius)
                        result.add(entity);

        return result;
    }

    public static List<LivingEntity> getLivingNearbyBox(Location loc, double radius) {
        List<LivingEntity> result = new ArrayList<LivingEntity>();

        int minX = (int) (loc.getX() - radius) >> 4;
        int maxX = (int) (loc.getX() + radius) >> 4;
        int minZ = (int) (loc.getZ() - radius) >> 4;
        int maxZ = (int) (loc.getZ() + radius) >> 4;

        for (int i = minX; i <= maxX; i++)
            for (int j = minZ; j <= maxZ; j++)
                for (Entity entity : loc.getWorld().getChunkAt(i, j).getEntities())
                    if (entity instanceof LivingEntity && boxDistance(entity.getLocation(), loc) < radius)
                        result.add((LivingEntity) entity);

        return result;
    }

    private static double boxDistance(Location loc1, Location loc2) {
        return Math.max(Math.max(Math.abs(loc1.getX() - loc2.getX()), Math.abs(loc1.getY() - loc2.getY())),
                Math.abs(loc1.getZ() - loc2.getZ()));
    }
}
