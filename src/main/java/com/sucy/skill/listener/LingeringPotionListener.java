package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.mechanic.PotionProjectileMechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;

import static com.sucy.skill.listener.MechanicListener.*;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.listener.LingeringPotionListener
 */
public class LingeringPotionListener extends SkillAPIListener {

    @EventHandler
    public void onLingerSplash(LingeringPotionSplashEvent event) {
        PotionProjectileMechanic mechanic = (PotionProjectileMechanic) SkillAPI.getMeta(event.getEntity(), POTION_PROJECTILE);
        if (mechanic != null) {
            SkillAPI.setMeta(event.getAreaEffectCloud(), POTION_PROJECTILE, mechanic);
            event.getAreaEffectCloud().setMetadata(SKILL_LEVEL, event.getEntity().getMetadata(SKILL_LEVEL).get(0));
            event.getAreaEffectCloud().setMetadata(SKILL_CASTER, event.getEntity().getMetadata(SKILL_CASTER).get(0));
        }
    }

    @EventHandler
    public void onLinger(AreaEffectCloudApplyEvent event) {
        PotionProjectileMechanic mechanic = (PotionProjectileMechanic) SkillAPI.getMeta(event.getEntity(), POTION_PROJECTILE);
        if (mechanic != null) {
            mechanic.callback(event.getEntity(), event.getAffectedEntities());
            event.getAffectedEntities().clear();
        }
    }
}
