package com.sucy.skill.listener;

import com.sucy.skill.api.DefaultCombatProtection;
import com.sucy.skill.api.event.PlayerBlockDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Listener to throw custom {@link PlayerBlockDamageEvent}
 */
public class ShieldBlockListener extends SkillAPIListener {

    private final HashMap<UUID, Entity> volatileMap = new HashMap<>();

    /**
     * Remember EntityDamageByEntityEvent in a moment
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || DefaultCombatProtection.isFakeDamageEvent(event)) return;
        Player player = (Player) event.getEntity();
        if (!player.isBlocking()) return;
        UUID player_uuid = player.getUniqueId();
        volatileMap.put(player_uuid, event.getDamager());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                volatileMap.remove(player_uuid);
            }
        }, 100);
    }

    /**
     * Trigger for {@link PlayerBlockDamageEvent}
     */
    @EventHandler
    public void onStat(PlayerStatisticIncrementEvent event) {
        if (!event.getStatistic().equals(Statistic.DAMAGE_BLOCKED_BY_SHIELD)) return;
        Player player = event.getPlayer();
        if (!volatileMap.containsKey(player.getUniqueId())) return;
        Bukkit.getPluginManager().callEvent(new PlayerBlockDamageEvent(volatileMap.get(player.getUniqueId()), event));
    }
}
