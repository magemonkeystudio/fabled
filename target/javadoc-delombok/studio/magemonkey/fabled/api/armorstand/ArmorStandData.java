package studio.magemonkey.fabled.api.armorstand;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArmorStandData {
    private final HashMap<String, ArmorStandInstance> armorStands = new HashMap<>();
    private final LivingEntity                        target;

    /**
     * @param target target of the armor stands
     */
    public ArmorStandData(LivingEntity target) {
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
     * @return active armor stand or null if not found
     */
    public ArmorStandInstance getArmorStands(String key) {
        return armorStands.get(key);
    }

    public void register(ArmorStandInstance armorStand, String key) {
        ArmorStandInstance oldArmorStand = armorStands.put(key, armorStand);
        if (oldArmorStand != null) oldArmorStand.remove();
    }

    /**
     * Ticks each armor stand for the target
     */
    public void tick() {
        Iterator<ArmorStandInstance> iterator = armorStands.values().iterator();
        while (iterator.hasNext()) {
            ArmorStandInstance armorStand = iterator.next();
            if (armorStand.isValid()) {
                armorStand.tick();
            } else {
                armorStand.remove();
                iterator.remove();
            }
        }
    }

    public String getKey(ArmorStandInstance armorStand) {
        for (Map.Entry<String, ArmorStandInstance> entry : armorStands.entrySet()) {
            String             key   = entry.getKey();
            ArmorStandInstance value = entry.getValue();
            if (value == armorStand) return key;
        }
        return null;
    }

    /**
     * Removes and unregisters all armor stands for this target
     */
    public void remove() {
        armorStands.values().forEach(ArmorStandInstance::remove);
        armorStands.clear();
    }

    public void remove(String key) {
        ArmorStandInstance armorStand = armorStands.get(key);
        if (armorStand != null) armorStand.remove();
        armorStands.remove(key);
    }

    public void remove(ArmorStandInstance armorStand) {
        armorStand.remove();
        String key = getKey(armorStand);
        if (key != null) armorStands.remove(key);
    }
}
