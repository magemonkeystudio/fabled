package com.sucy.skill.api.armorstand;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.ArmorStandTask;
import com.sucy.skill.thread.MainThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorStandManager {
    private static final Map<LivingEntity, ArmorStandData> instances = new ConcurrentHashMap<>();

    /**
     * Registers the armor stand repeated task, and searches for rogue armor to remove them
     */
    public static void init() {
        MainThread.register(new ArmorStandTask());
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(ArmorStand.class).forEach(as -> {
            if (SkillAPI.getMeta(as, MechanicListener.ARMOR_STAND) != null) as.remove();
        }));
    }

    /**
     * Removes all armor stand instances
     *
     */
    public static void cleanUp() {
        instances.values().forEach(ArmorStandData::remove);
        instances.clear();
    }

    /**
     * Clears armor stands for a given entity
     *
     * @param target target to clear for
     */
    public static void clear(LivingEntity target) {
        instances.remove(target);
    }

    /**
     * Gets the armor stand data for the given target
     *
     * @param target target to get the data for
     * @return armor stand data for the target or null if doesn't exist
     */
    public static ArmorStandData getArmorStandData(LivingEntity target) {
        return instances.get(target);
    }

    /**
     * Fetches an active armor stand for a given target
     *
     * @param target target to get the armor stand for
     * @param key    armor stand key
     *
     * @return active armor stand or null if not found
     */
    public static ArmorStandInstance getArmorStand(LivingEntity target, String key) {
        if (!instances.containsKey(target)) { return null; }
        return instances.get(target).getArmorStands(key);
    }

    /**
     * Registers an active armor stand for the given target
     *
     * @param armorStand armor stand to register
     * @param target target to register the armor stand for
     * @param key    armor stand key
     */
    public static void register(ArmorStandInstance armorStand, LivingEntity target, String key) {
        if (!instances.containsKey(target)) { instances.put(target, new ArmorStandData(target)); }
        instances.get(target).register(armorStand, key);
    }

    /**
     * Ticks all active armor stands
     */
    public static void tick() {
        Iterator<ArmorStandData> iterator = instances.values().iterator();
        while (iterator.hasNext()) {
            ArmorStandData data = iterator.next();
            if (data.isValid()) { data.tick(); } else {
                data.remove();
                iterator.remove();
            }
        }
    }
}
