package studio.magemonkey.fabled.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.PlayerBlockDamageEvent;
import studio.magemonkey.fabled.dynamic.TempEntity;

/**
 * Listener to call custom {@link PlayerBlockDamageEvent}
 */
public class ShieldBlockListener extends FabledListener {

    /**
     * Remember EntityDamageByEntityEvent in a moment
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || DefaultCombatProtection.isFakeDamageEvent(event)) return;

        Player player = (Player) event.getEntity();
        if (!player.isBlocking()) return;

        @SuppressWarnings("deprecation") double blockedDamage =
                event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        if (blockedDamage == 0) return;

        PlayerBlockDamageEvent statEvent =
                buildDamageEvent(event, player, -blockedDamage);
        Bukkit.getPluginManager().callEvent(statEvent);
    }

    @NotNull
    private static PlayerBlockDamageEvent buildDamageEvent(EntityDamageByEntityEvent event,
                                                           Player player,
                                                           double blockedDamage) {
        String type   = "melee";
        Entity source = event.getDamager();
        if (source instanceof Projectile) {
            type = "projectile";
            if (source instanceof BlockProjectileSource) {
                Location locTarget = ((BlockProjectileSource) source).getBlock().getLocation();
                source = new TempEntity(locTarget);
            } else
                source = (Entity) ((Projectile) source).getShooter();
        }

        return new PlayerBlockDamageEvent(player, source, blockedDamage, type);
    }
}
