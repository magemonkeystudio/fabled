package com.sucy.skill.listener;

import com.sucy.skill.api.event.PlayerBlockDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Listener to throw custom {@link PlayerBlockDamageEvent}
 */
public class ShieldBlockListener extends SkillAPIListener {

    private final HashMap<Player, EntityDamageByEntityEvent> volatileMap = new HashMap<>();

    /**
     * Remember EntityDamageByEntityEvent in a moment
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!player.isBlocking()) return;
        volatileMap.put(player, event);
    }

    /**
     * Trigger for {@link PlayerBlockDamageEvent}
     */
    @EventHandler
    public void onStat(PlayerStatisticIncrementEvent event){
        if (!event.getStatistic().equals(Statistic.DAMAGE_BLOCKED_BY_SHIELD)) return;
        Player player = event.getPlayer();
        if (!volatileMap.containsKey(player)) return;
        Bukkit.getPluginManager().callEvent(new PlayerBlockDamageEvent(volatileMap.get(player), event));
    }
}
