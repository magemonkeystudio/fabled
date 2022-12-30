package com.sucy.skill.listener.attribute;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.hook.CitizensHook;
import com.sucy.skill.listener.SkillAPIListener;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nightexpress.quantumrpg.api.event.RPGDamageEvent;
import su.nightexpress.quantumrpg.stats.items.attributes.DamageAttribute;
import su.nightexpress.quantumrpg.stats.items.attributes.DefenseAttribute;

public class RPGAttributeListener extends SkillAPIListener {

    @EventHandler
    public void rpgDamage(RPGDamageEvent event) {
        if (!(event instanceof RPGDamageEvent.Start)) return;
        // Physical Damage
        event.getDamageMap().entrySet().forEach(e -> {
            DamageAttribute attr = e.getKey();
            if (attr == null) return;
            final double originalDamage = e.getValue();
            double       damage         = e.getValue();
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (CitizensHook.isNPC(player))
                    return;

                PlayerData data = SkillAPI.getPlayerData(player);

                if (attr.getName().equals("physical")) {
                    damage = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, damage);
                } else {
                    damage = data.scaleStat("rpgdamage-" + attr.getId(), damage);
                }
                if (event.isProjectile()) {
                    damage = data.scaleStat(AttributeManager.PROJECTILE_DAMAGE, damage);
                } else {
                    damage = data.scaleStat(AttributeManager.MELEE_DAMAGE, damage);
                }
                event.getDamageMap().put(attr, damage);
            }

            // Physical Defense
            DefenseAttribute def = attr.getAttachedDefense();
            if (def == null) return;
            if (event.getVictim() instanceof Player) {
                Player player = (Player) event.getVictim();
                if (CitizensHook.isNPC(player))
                    return;

                PlayerData data = SkillAPI.getPlayerData(player);

                if (def.getName().equals("physical")) {
                    damage = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, damage);
                } else {
                    damage = data.scaleStat("rpgdefense-" + def.getId(), damage);
                }
                if (event.isProjectile()) {
                    damage = data.scaleStat(AttributeManager.PROJECTILE_DEFENSE, damage);
                } else {
                    damage = data.scaleStat(AttributeManager.MELEE_DEFENSE, damage);
                }
                event.getDefenseMap().put(def, originalDamage - damage);
            }
        });
    }

}
