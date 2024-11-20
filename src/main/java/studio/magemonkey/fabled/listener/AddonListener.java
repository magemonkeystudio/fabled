package studio.magemonkey.fabled.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.event.PlayerCastSkillEvent;
import studio.magemonkey.fabled.api.event.PlayerExperienceGainEvent;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.hook.WorldGuardHook;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Series of fixes/improvements created by EvrimSen and touched up by Eniripsa96
 * <p>
 * See https://www.spigotmc.org/resources/addonforskillapi.55857/ for extra features
 */
public class AddonListener extends FabledListener {
    private static final Set<UUID> IGNORE_CASTING = new HashSet<>();

    /**
     * Cancels damage between friendly classes
     *
     * @param event damage event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerHit(final EntityDamageByEntityEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())
                || DefaultCombatProtection.isFakeDamageEvent(event)) {
            return;
        }

        final LivingEntity damager = ListenerUtil.getDamager(event);
        if (event.getEntity() instanceof Player && damager instanceof Player) {
            final PlayerData attackerData = Fabled.getData((Player) damager);
            final PlayerData defenderData = Fabled.getData((Player) event.getEntity());

            for (final String group : Fabled.getGroups()) {
                final boolean     friendly = Fabled.getSettings().getGroupSettings(group).isFriendly();
                final PlayerClass attacker = attackerData.getClass(group);
                final PlayerClass defender = defenderData.getClass(group);
                if (friendly && attacker != null && defender != null
                        && attacker.getData().getRoot() == defender.getData().getRoot()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Tracks when a player changes worlds for avoiding accidental skill casts
     *
     * @see AddonListener#onSkillUse(PlayerCastSkillEvent)
     */
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        startIgnoring(e.getPlayer());
    }

    /**
     * Tracks when a player joins for avoiding accidental skill casts
     *
     * @see AddonListener#onSkillUse(PlayerCastSkillEvent)
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        startIgnoring(e.getPlayer());
    }

    private void startIgnoring(final Player player) {
        if (!Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            return;
        }

        final UUID uuid = player.getUniqueId();
        IGNORE_CASTING.add(uuid);
        Fabled.schedule(() -> IGNORE_CASTING.remove(uuid), 40);
    }

    /**
     * Cancels skill casts shortly after changing worlds or joining the server.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillUse(final PlayerCastSkillEvent e) {
        if (IGNORE_CASTING.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        } else if (PluginChecker.isWorldGuardActive()) {
            if (WorldGuardHook.getRegionIds(e.getPlayer().getLocation()).stream()
                    .anyMatch(id -> Fabled.getSettings().areSkillsDisabledForRegion(id))) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * Cancels skill casts shortly after changing worlds or joining the server.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExpGain(final PlayerExperienceGainEvent e) {
        if (PluginChecker.isWorldGuardActive()) {
            if (WorldGuardHook.getRegionIds(e.getPlayerData().getPlayer().getLocation()).stream()
                    .anyMatch(id -> Fabled.getSettings().isExpDisabledForRegion(id))) {
                e.setCancelled(true);
            }
        }
    }
}
