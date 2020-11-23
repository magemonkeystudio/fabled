package com.sucy.skill.api.armorstand;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;

public class ArmorStandData {
    private final HashMap<String, ArmorStandInstance> armorStands = new HashMap<>();
    private final LivingEntity target;

    /**
     * @param target target of the armor stands
     */
    public ArmorStandData(LivingEntity target)
    {
        this.target = target;
    }

    /**
     * @return true if should keep the data, false otherwise
     */
    public boolean isValid() {
        return armorStands.size() > 0 && target.isValid();
    }

    /**
     * Fetches an active armor stand by key
     *
     * @param key armor stand key
     *
     * @return active armor stand or null if not found
     */
    public ArmorStandInstance getArmorStands(String key) { return armorStands.get(key); }

    public void register(ArmorStandInstance armorStand, String key) {
        ArmorStandInstance oldArmorStand = armorStands.put(key, armorStand);
        if (oldArmorStand != null) oldArmorStand.remove();
    }

    /**
     * Ticks each armor stand for the target
     */
    public void tick() {
        Iterator<ArmorStandInstance> iterator = armorStands.values().iterator();
        while (iterator.hasNext())
        {
            ArmorStandInstance armorStand = iterator.next();
            if (armorStand.isValid()) {
                armorStand.tick();
            }
            else {
                armorStand.remove();
                iterator.remove();
            }
        }
    }

    /**
     * Removes and unregisters all armor stands for this target
     */
    public void remove() {
        armorStands.values().forEach(ArmorStandInstance::remove);
        armorStands.clear();
    }
}
