/**
 * Fabled
 * studio.magemonkey.fabled.listener.ListenerUtil
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package studio.magemonkey.fabled.listener;

import studio.magemonkey.codex.util.Reflex;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

/**
 * Helper class for listeners
 */
public class ListenerUtil {
    /**
     * Retrieves a damager from an entity damage event which will get the
     * shooter of projectiles if it was a projectile hitting them or
     * converts the Entity damager to a LivingEntity if applicable.
     *
     * @param event event to grab the damager from
     * @return LivingEntity damager of the event or null if not found
     */
    public static LivingEntity getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            return (LivingEntity) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity) {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }

    public static Inventory getClickedInventory(InventoryClickEvent event) {
        int           slot = event.getRawSlot();
        InventoryView view = event.getView();
        if (slot < 0) {
            return null;
        } else if ((view.getTopInventory() != null) && (slot < view.getTopInventory().getSize())) {
            return view.getTopInventory();
        } else {
            return view.getBottomInventory();
        }
    }

    /**
     * Gets a simple name of the entity
     *
     * @param entity entity to get the name of
     * @return simple name of the entity
     */
    public static String getName(Entity entity) {
        String name = entity.getClass().getSimpleName().toLowerCase().replace("craft", "");
        if (entity.getType().name().equals("SKELETON")) {
            try {
                Object type = Reflex.getMethod(entity, "getSkeletonType").invoke(entity);
                if (Reflex.getMethod(type, "name").invoke(type).equals("WITHER")) {
                    name = "wither" + name;
                }
            } catch (Exception ex) { /* Wither skeletons don't exist */ }
        } else if (entity.getType().name().equals("GUARDIAN")) {
            try {
                if ((Boolean) Reflex.getMethod(entity, "isElder").invoke(entity)) {
                    name = "elder" + name;
                }
            } catch (Exception ex) { /* Shouldn't error out */ }
        }
        return name;
    }
}
