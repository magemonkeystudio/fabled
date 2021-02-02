package com.sucy.skill.api.target;

import com.sucy.skill.hook.DisguiseHook;
import com.sucy.skill.hook.PluginChecker;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.utilities.reflection.FakeBoundingBox;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public abstract class TargetHelper {


    /**
     * <p>Number of pixels that end up displaying about 1 degree of vision in the client window</p>
     * <p>Not really useful since you can't get the client's window size, but I added it in case
     * it becomes useful sometime</p>
     */
    private static final int PIXELS_PER_DEGREE = 35;

    /**
     * <p>Gets all entities the player is looking at within the range</p>
     * <p>Has a little bit of tolerance to make targeting easier</p>
     *
     * @param source living entity to get the targets of
     * @param range  maximum range to check
     *
     * @return all entities in the player's vision line
     */
    public static List<LivingEntity> getLivingTargets(LivingEntity source, double range)
    {
        return getLivingTargets(source, range, 4);
    }

    /**
     * <p>Gets all entities the player is looking at within the range using
     * the given tolerance.</p>
     *
     * @param source    living entity to get the targets of
     * @param range     maximum range to check
     * @param tolerance tolerance of the line calculation
     *
     * @return all entities in the player's vision line
     */
    public static List<LivingEntity> getLivingTargets(LivingEntity source, double range, double tolerance)
    {
        List<Entity> list = source.getNearbyEntities(range, range, range);
        TreeMap<Double, LivingEntity> targets = new TreeMap<>();

        Location location = source.getEyeLocation();
        Vector origin = location.toVector();
        AABB.Ray3D ray = new AABB.Ray3D(location);

        for (Entity entity : list)
        {
            if (!isInFront(source, entity) || !(entity instanceof LivingEntity)) continue;

            AABB aabb = getAABB(entity);
            aabb.expand(tolerance);
            AABB.Vec3D collision = aabb.intersectsRay(ray, 0, range);
            if (collision != null) {
                targets.put(new Vector(collision.x, collision.y, collision.z).distance(origin), (LivingEntity) entity);
            }
        }
        return new ArrayList<>(targets.values());
    }

    /**
     * <p>Gets the entity the player is looking at</p>
     * <p>Has a little bit of tolerance to make targeting easier</p>
     *
     * @param source living entity to get the target of
     * @param range  maximum range to check
     *
     * @return entity player is looing at or null if not found
     */
    public static LivingEntity getLivingTarget(LivingEntity source, double range)
    {
        return getLivingTarget(source, range, 4);
    }

    /**
     * <p>Gets the entity the player is looking at</p>
     * <p>Has a little bit of tolerance to make targeting easier</p>
     *
     * @param source    living entity to get the target of
     * @param range     maximum range to check
     * @param tolerance tolerance of the line calculation
     *
     * @return entity player is looking at or null if not found
     */
    public static LivingEntity getLivingTarget(LivingEntity source, double range, double tolerance)
    {
        List<LivingEntity> targets = getLivingTargets(source, range, tolerance);
        if (targets.size() == 0) return null;
        return targets.get(0);
    }

    /**
     * Gets the targets in a cone
     *
     * @param source entity to get the targets for
     * @param arc    arc angle of the cone
     * @param range  range of the cone
     *
     * @return list of targets
     */
    public static List<LivingEntity> getConeTargets(LivingEntity source, double arc, double range)
    {
        List<LivingEntity> targets = new ArrayList<>();
        List<Entity> list = source.getNearbyEntities(range, range, range);
        if (arc <= 0) return targets;

        // Initialize values
        Location sourceLocation = source.getEyeLocation();
        Vector dir = sourceLocation.getDirection();
        dir.setY(0);
        double cos = Math.cos(arc * Math.PI / 180);
        double cosSq = cos * cos;

        // Get the targets in the cone
        for (Entity entity : list)
        {
            if (entity instanceof LivingEntity)
            {

                // Greater than 360 degrees is all targets
                if (arc >= 360)
                {
                    targets.add((LivingEntity) entity);
                }

                // Otherwise, select targets based on dot product
                else
                {
                    Vector relative = entity.getLocation().clone().add(0, getHeight(entity)*0.5,0).subtract(sourceLocation).toVector();
                    relative.setY(0);
                    double dot = relative.dot(dir);
                    double value = dot * dot / relative.lengthSquared();
                    if (arc < 180 && dot > 0 && value >= cosSq) targets.add((LivingEntity) entity);
                    else if (arc >= 180 && (dot > 0 || dot <= cosSq)) targets.add((LivingEntity) entity);
                }
            }
        }

        return targets;
    }

    /**
     * Checks if the entity is in front of the entity
     *
     * @param entity entity to check for
     * @param target target to check against
     *
     * @return true if the target is in front of the entity
     */
    public static boolean isInFront(Entity entity, Entity target)
    {

        // Get the necessary vectors
        Vector facing = entity.getLocation().getDirection();
        Vector relative = target.getLocation().clone().add(0,getHeight(entity)*0.5,0).subtract(entity.getLocation()).toVector();

        // If the dot product is positive, the target is in front
        return facing.dot(relative) >= 0;
    }

    /**
     * Checks if the entity is in front of the entity restricted to the given angle
     *
     * @param entity entity to check for
     * @param target target to check against
     * @param angle  angle to restrict it to (0-360)
     *
     * @return true if the target is in front of the entity
     */
    public static boolean isInFront(Entity entity, Entity target, double angle)
    {
        if (angle <= 0) return false;
        if (angle >= 360) return true;

        // Get the necessary data
        double dotTarget = Math.cos(angle);
        Vector facing = entity.getLocation().getDirection();
        Vector relative = target.getLocation().clone().add(0,getHeight(entity)*0.5,0).subtract(entity.getLocation()).toVector().normalize();

        // Compare the target dot product with the actual result
        return facing.dot(relative) >= dotTarget;
    }

    /**
     * Checks if the target is behind the entity
     *
     * @param entity entity to check for
     * @param target target to check against
     *
     * @return true if the target is behind the entity
     */
    public static boolean isBehind(Entity entity, Entity target)
    {
        return !isInFront(entity, target);
    }

    /**
     * Checks if the entity is behind the player restricted to the given angle
     *
     * @param entity entity to check for
     * @param target target to check against
     * @param angle  angle to restrict it to (0-360)
     *
     * @return true if the target is behind the entity
     */
    public static boolean isBehind(Entity entity, Entity target, double angle)
    {
        if (angle <= 0) return false;
        if (angle >= 360) return true;

        // Get the necessary data
        double dotTarget = Math.cos(angle);
        Vector facing = entity.getLocation().getDirection();
        Vector relative = entity.getLocation().clone().add(0,getHeight(entity)*0.5,0).subtract(target.getLocation()).toVector().normalize();

        // Compare the target dot product and the actual result
        return facing.dot(relative) >= dotTarget;
    }

    /**
     * Checks whether or not the line between the two points is obstructed
     *
     * @param loc1 first location
     * @param loc2 second location
     *
     * @return the location of obstruction or null if not obstructed
     */
    public static boolean isObstructed(Location loc1, Location loc2)
    {
        if (loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ())
        {
            return false;
        }
        Vector slope = loc2.clone().subtract(loc1).toVector();
        int steps = (int) (slope.length() * 4) + 1;
        slope.multiply(1.0 / steps);
        Location temp = loc1.clone();
        for (int i = 0; i < steps; i++)
        {
            temp.add(slope);
            if (temp.getBlock().getType().isSolid())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves an open location along the line for teleporting or linear targeting
     *
     * @param loc1        start location of the path
     * @param loc2        end location of the path
     * @param throughWall whether or not going through walls is allowed
     *
     * @return the farthest open location along the path
     */
    public static Location getOpenLocation(Location loc1, Location loc2, boolean throughWall)
    {
        // Special case
        if (loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ())
        {
            return loc1;
        }

        // Common data
        Vector slope = loc2.clone().subtract(loc1).toVector();
        int steps = (int) (slope.length() * 4) + 1;
        slope.multiply(1.0 / steps);

        // Going through walls starts at the end and traverses backwards
        if (throughWall)
        {
            Location temp = loc2.clone();
            while (temp.getBlock().getType().isSolid() && steps > 0)
            {
                temp.subtract(slope);
                steps--;
            }
            temp.setX(temp.getBlockX() + 0.5);
            temp.setZ(temp.getBlockZ() + 0.5);
            temp.setY(temp.getBlockY() + 1);
            return temp;
        }

        // Not going through walls starts at the beginning and traverses forward
        else
        {
            Location temp = loc1.clone();
            while (!temp.getBlock().getType().isSolid() && steps > 0)
            {
                temp.add(slope);
                steps--;
            }
            temp.subtract(slope);
            temp.setX(temp.getBlockX() + 0.5);
            temp.setZ(temp.getBlockZ() + 0.5);
            temp.setY(temp.getBlockY() + 1);
            return temp;
        }
    }

    public static AABB getAABB(Entity entity) {
        AABB.Vec3D origin = AABB.Vec3D.fromLocation(entity.getLocation());
        try {
            if (PluginChecker.isDisguiseActive() && DisguiseAPI.isDisguised(entity) && DisguiseAPI.getDisguise(entity).isModifyBoundingBox()) {
                FakeBoundingBox boundingBox = DisguiseHook.getFakeBoundingBox(entity);
                return new AABB(origin.add(new AABB.Vec3D(-boundingBox.getX(), 0, -boundingBox.getZ())), origin.add(new AABB.Vec3D(boundingBox.getX(), boundingBox.getY(), boundingBox.getZ())));
            }
        } catch (NullPointerException ignored) {}
        double halfWidth = entity.getWidth()/2;
        return new AABB(origin.add(new AABB.Vec3D(-halfWidth, 0, -halfWidth)), origin.add(new AABB.Vec3D(halfWidth, entity.getHeight(), halfWidth)));
    }

    public static double getHeight(Entity entity) {
        return (PluginChecker.isDisguiseActive() && DisguiseAPI.isDisguised(entity)) ? DisguiseHook.getFakeBoundingBox(entity).getY() : entity.getHeight();
    }
}