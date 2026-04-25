package studio.magemonkey.fabled.api.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.task.DisplayEntityTask;
import studio.magemonkey.fabled.thread.MainThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DisplayEntityManager {

    /**
     * Metadata key applied to every Display entity spawned by Fabled mechanics.
     * Used to prevent damage events, cancel interactions, and clean up rogue entities.
     */
    public static final String DISPLAY_ENTITY_META = "fabledDisplayEntity";

    /**
     * True when the current server version supports Display entities (1.19.4+).
     */
    private static final boolean SUPPORTED;

    static {
        boolean supported = false;
        try {
            Class.forName("org.bukkit.entity.Display");
            supported = true;
        } catch (ClassNotFoundException ignored) {
        }
        SUPPORTED = supported;
    }

    private static final Map<LivingEntity, DisplayEntityData> instances = new ConcurrentHashMap<>();

    /**
     * @return true when Display entities are supported on this server version
     */
    public static boolean isSupported() {
        return SUPPORTED;
    }

    /**
     * Registers the tick task and removes any rogue display entities left from a previous session.
     */
    public static void init() {
        if (!SUPPORTED) return;
        MainThread.register(new DisplayEntityTask());
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (Fabled.getMeta(entity, DISPLAY_ENTITY_META) != null) entity.remove();
        }));
    }

    /**
     * Removes all managed display entity instances.
     */
    public static void cleanUp() {
        if (!SUPPORTED) return;
        instances.values().forEach(DisplayEntityData::remove);
        instances.clear();
    }

    /**
     * Clears all display entities tracked for a given entity.
     *
     * @param target target entity whose display entities should be cleared
     */
    public static void clear(LivingEntity target) {
        DisplayEntityData data = instances.remove(target);
        if (data != null) data.remove();
    }

    /**
     * Returns the {@link DisplayEntityData} for the given target, or {@code null} if none exists.
     *
     * @param target target entity
     * @return display entity data or null
     */
    public static DisplayEntityData getDisplayEntityData(LivingEntity target) {
        return instances.get(target);
    }

    /**
     * Fetches an active display entity instance for the given target and key.
     *
     * @param target target entity
     * @param key    display entity key
     * @return optional display entity instance
     */
    public static Optional<DisplayEntityInstance> getDisplayEntity(LivingEntity target, String key) {
        if (!instances.containsKey(target)) return Optional.empty();
        return Optional.ofNullable(instances.get(target).getDisplayEntity(key));
    }

    /**
     * Registers a display entity instance for the given target under the given key.
     *
     * @param instance display entity instance to register
     * @param target   target entity
     * @param key      key to register under
     */
    public static void register(DisplayEntityInstance instance, LivingEntity target, String key) {
        instances.computeIfAbsent(target, DisplayEntityData::new).register(instance, key);
    }

    /**
     * Ticks all tracked display entity instances, removing expired ones.
     */
    public static void tick() {
        Iterator<DisplayEntityData> iterator = instances.values().iterator();
        while (iterator.hasNext()) {
            DisplayEntityData data = iterator.next();
            if (data.isValid()) {
                data.tick();
            } else {
                data.remove();
                iterator.remove();
            }
        }
    }

    /**
     * Marks an entity as a Fabled-managed display entity by setting metadata on it.
     *
     * @param entity the entity to tag
     */
    public static void tag(Entity entity) {
        Fabled.setMeta(entity, DISPLAY_ENTITY_META, true);
    }
}
