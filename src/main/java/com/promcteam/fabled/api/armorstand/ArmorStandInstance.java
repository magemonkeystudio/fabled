package com.promcteam.fabled.api.armorstand;

import com.promcteam.fabled.Fabled;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@AllArgsConstructor
@RequiredArgsConstructor
public class ArmorStandInstance {
    private static final Vector       UP = new Vector(0, 1, 0);
    private final        ArmorStand   armorStand;
    private final        LivingEntity target;
    private final        boolean      follow;
    private              double       forward;
    private              double       upward;
    private              double       right;

    /**
     * @return true if the instance is still valid
     */
    public boolean isValid() {
        return target.isValid() && armorStand.isValid();
    }

    /**
     * Removes the armor stand
     */
    public void remove() {
        Bukkit.getScheduler().runTask(Fabled.inst(), armorStand::remove);
    }

    /**
     * Ticks the armor stand
     */
    public void tick() {
        if (follow) {
            Bukkit.getScheduler().runTask(Fabled.inst(), () -> {
                boolean sameWorld = armorStand.getWorld().equals(target.getWorld());

                Location loc  = target.getLocation().clone();
                Vector   dir  = loc.getDirection().setY(0).normalize();
                Vector   side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

                if (!sameWorld) {
                    boolean chunkLoaded = armorStand.getLocation().getChunk().isLoaded();
                    if (!chunkLoaded) {
                        // If the armor stand is in an unloaded chunk, we can't teleport it
                        armorStand.getLocation().getChunk().load();
                    }
                }
                armorStand.teleport(loc);
            });
        }
    }
}
