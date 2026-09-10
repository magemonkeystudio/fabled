package studio.magemonkey.fabled.api.displayentity;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DisplayEntityData {
    private final HashMap<String, DisplayEntityInstance> displayEntities = new HashMap<>();
    private final LivingEntity                           target;

    /**
     * @param target target of the display entities
     */
    public DisplayEntityData(LivingEntity target) {
        this.target = target;
    }

    /**
     * @return true if should keep the data, false otherwise
     */
    public boolean isValid() {
        return !displayEntities.isEmpty() && target.isValid();
    }

    /**
     * Fetches an active display entity instance by key
     *
     * @param key display entity key
     * @return active instance or null if not found
     */
    public DisplayEntityInstance getDisplayEntity(String key) {
        return displayEntities.get(key);
    }

    /**
     * Registers a display entity instance under the given key
     *
     * @param instance display entity instance
     * @param key      key to register under
     */
    public void register(DisplayEntityInstance instance, String key) {
        DisplayEntityInstance old = displayEntities.put(key, instance);
        if (old != null) old.remove();
    }

    /**
     * Ticks each display entity for the target
     */
    public void tick() {
        Iterator<DisplayEntityInstance> iterator = displayEntities.values().iterator();
        while (iterator.hasNext()) {
            DisplayEntityInstance instance = iterator.next();
            if (instance.isValid()) {
                instance.tick();
            } else {
                instance.remove();
                iterator.remove();
            }
        }
    }

    /**
     * Returns the key associated with the given instance
     *
     * @param instance the instance to look up
     * @return key or null if not found
     */
    public String getKey(DisplayEntityInstance instance) {
        for (Map.Entry<String, DisplayEntityInstance> entry : displayEntities.entrySet()) {
            if (entry.getValue() == instance) return entry.getKey();
        }
        return null;
    }

    /**
     * Removes and unregisters all display entities for this target
     */
    public void remove() {
        displayEntities.values().forEach(DisplayEntityInstance::remove);
        displayEntities.clear();
    }

    /**
     * Removes and unregisters the display entity with the given key
     *
     * @param key key of the display entity to remove
     */
    public void remove(String key) {
        DisplayEntityInstance instance = displayEntities.get(key);
        if (instance != null) instance.remove();
        displayEntities.remove(key);
    }

    /**
     * Removes and unregisters the given display entity instance
     *
     * @param instance instance to remove
     */
    public void remove(DisplayEntityInstance instance) {
        instance.remove();
        String key = getKey(instance);
        if (key != null) displayEntities.remove(key);
    }
}
