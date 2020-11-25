package com.sucy.skill.hook;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sucy.skill.SkillAPI;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.hook.WorldGuardHook
 */
public class WorldGuardHook {

    private static Method regionMethod;

    private static Class<?> vectorClass;
    private static Constructor<?> vectorConstructor;
    private static Method applicableRegionsMethod;

    /**
     * Fetches the list of region IDs applicable to a given location
     *
     * @param loc location to get region ids for
     * @return region IDs for the location
     */
    public static List<String> getRegionIds(final Location loc) {
        try {
            return WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(loc.getWorld()))
                    .getApplicableRegionsIDs(BlockVector3.at(loc.getX(), loc.getY(),loc.getZ()));
        } catch (NoClassDefFoundError ex) {
            try {
                final WorldGuardPlugin plugin = SkillAPI.getPlugin(WorldGuardPlugin.class);
                return (List<String>) getApplicableRegionsMethod().invoke((getRegionMethod().invoke(plugin, loc.getWorld())), vectorConstructor.newInstance(loc.getX(), loc.getY(),loc.getZ()));
            } catch (final Exception e) {
                // Cannot handle world guard
                e.printStackTrace();
                return ImmutableList.of();
            }
        }
    }

    private static Method getApplicableRegionsMethod() throws Exception {
        if (applicableRegionsMethod == null) {
            vectorClass = Class.forName("com.sk89q.worldedit.Vector");
            vectorConstructor = vectorClass.getConstructor(double.class, double.class, double.class);
            applicableRegionsMethod = RegionManager.class.getMethod("getApplicableRegionsIDs", vectorClass);
        }
        return applicableRegionsMethod;
    }

    private static Method getRegionMethod() throws Exception {
        if (regionMethod == null) {
            regionMethod = WorldGuardPlugin.class.getDeclaredMethod("getRegionManager", World.class);
        }
        return regionMethod;
    }
}
