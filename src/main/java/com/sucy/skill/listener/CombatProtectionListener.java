package com.sucy.skill.listener;

import com.sucy.skill.api.DefaultCombatProtection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class CombatProtectionListener extends SkillAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkDamage(DefaultCombatProtection.FakeEntityDamageByEntityEvent event) {
        event.setExternallyCancelled(event.isCancelled());
        event.setCancelled(true);
    }

}
