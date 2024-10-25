package studio.magemonkey.fabled.hook;

import org.bukkit.entity.Entity;

/**
 * Handles checking whether an entity is an NPC
 */
public class CitizensHook {
    public static boolean isNPC(Entity entity) {
        return entity.getClass().getName().equals("PlayerNPC");
    }
}
