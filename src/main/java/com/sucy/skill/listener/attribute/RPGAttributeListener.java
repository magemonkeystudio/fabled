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

        // Scale damages
        event.getDamageMap().entrySet().forEach(damageEntry -> {
            DamageAttribute damageAttribute = damageEntry.getKey();
            if (damageAttribute == null) {return;}
            double damage = damageEntry.getValue();
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (CitizensHook.isNPC(player)) {return;}

                PlayerData data = SkillAPI.getPlayerData(player);

                if (damageAttribute.getName().equals("physical")) {
                    damage = data.scaleStat(AttributeManager.PHYSICAL_DAMAGE, damage);
                } else {
                    damage = data.scaleStat("rpgdamage-" + damageAttribute.getId(), damage);
                }
                if (event.isProjectile()) {
                    damage = data.scaleStat(AttributeManager.PROJECTILE_DAMAGE, damage);
                } else {
                    damage = data.scaleStat(AttributeManager.MELEE_DAMAGE, damage);
                }
                event.getDamageMap().put(damageAttribute, damage);
            }
        });

        // Scale defenses
        event.getDefenseMap().entrySet().forEach(defenseEntry -> {
            DefenseAttribute defenseAttribute = defenseEntry.getKey();
            if (defenseAttribute == null) {return;}
            double defense = defenseEntry.getValue();
            if (event.getVictim() instanceof Player) {
                Player player = (Player) event.getVictim();
                if (CitizensHook.isNPC(player)) {return;}

                PlayerData data = SkillAPI.getPlayerData(player);

                if (defenseAttribute.getName().equals("physical")) {
                    defense = data.scaleStat(AttributeManager.PHYSICAL_DEFENSE, defense);
                } else {
                    defense = data.scaleStat("rpgdefense-" + defenseAttribute.getId(), defense);
                }
                if (event.isProjectile()) {
                    defense = data.scaleStat(AttributeManager.PROJECTILE_DEFENSE, defense);
                } else {
                    defense = data.scaleStat(AttributeManager.MELEE_DEFENSE, defense);
                }
                event.getDefenseMap().put(defenseAttribute, defense);
            }
        });
    }
}
