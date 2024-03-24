package com.promcteam.fabled.listener;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.particle.ParticleHelper;
import com.promcteam.fabled.api.particle.ParticleSettings;
import com.promcteam.fabled.dynamic.mechanic.PotionProjectileMechanic;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.Locale;

import static com.promcteam.fabled.listener.MechanicListener.*;

/**
 * Fabled © 2023
 * com.promcteam.fabled.listener.LingeringPotionListener
 */
public class LingeringPotionListener extends FabledListener {

    @EventHandler
    public void onLingerSplash(LingeringPotionSplashEvent event) {
        PotionProjectileMechanic mechanic =
                (PotionProjectileMechanic) Fabled.getMeta(event.getEntity(), POTION_PROJECTILE);
        if (mechanic != null) {
            MetadataValue levelMetadata = event.getEntity().getMetadata(SKILL_LEVEL).get(0);
            int           level         = levelMetadata.asInt();
            Settings      settings      = mechanic.getSettings();

            AreaEffectCloud aec = event.getAreaEffectCloud();
            aec.setDuration((int) settings.getAttr(PotionProjectileMechanic.DURATION, level, 30) * 20);
            aec.setWaitTime((int) settings.getAttr(PotionProjectileMechanic.WAIT_TIME, level, 0.5) * 20);
            aec.setReapplicationDelay((int) settings.getAttr(PotionProjectileMechanic.REAPPLY_DELAY, level, 1) * 20);
            aec.setDurationOnUse((int) settings.getAttr(PotionProjectileMechanic.DURATION_ON_USE, level, 0) * 20);
            aec.setRadius((float) settings.getAttr(PotionProjectileMechanic.RADIUS, level, 3));
            aec.setRadiusOnUse((float) settings.getAttr(PotionProjectileMechanic.RADIUS_ON_USE, level, -0.5));
            aec.setRadiusPerTick((float) settings.getAttr(PotionProjectileMechanic.RADIUS_PER_TICK, level, -0.01) / 20);
            Particle particle = ParticleHelper.getFromKey(settings.getString(
                    PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.PARTICLE_KEY, "Spell mob"));
            aec.setParticle(particle, ParticleHelper.makeObject(particle,
                    Material.valueOf(settings.getString(
                                    PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.MATERIAL_KEY, "Dirt")
                            .toUpperCase(Locale.US)
                            .replace(" ", "_")),
                    settings.getInt(PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.DATA_KEY, 0),
                    settings.getInt(PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.DURABILITY_KEY, 0),
                    Color.fromRGB(Integer.parseInt(settings.getString(
                                    PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.DUST_COLOR, "#FF0000")
                            .substring(1), 16)),
                    Color.fromRGB(Integer.parseInt(settings.getString(
                                    PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.FINAL_DUST_COLOR, "#FF0000")
                            .substring(1), 16)),
                    (float) settings.getDouble(PotionProjectileMechanic.CLOUD_PREFIX + ParticleSettings.DUST_SIZE, 1)));

            Fabled.setMeta(aec, POTION_PROJECTILE, mechanic);
            aec.setMetadata(SKILL_LEVEL, levelMetadata);
            aec.setMetadata(SKILL_CASTER, event.getEntity().getMetadata(SKILL_CASTER).get(0));
        }
    }

    @EventHandler
    public void onLinger(AreaEffectCloudApplyEvent event) {
        PotionProjectileMechanic mechanic =
                (PotionProjectileMechanic) Fabled.getMeta(event.getEntity(), POTION_PROJECTILE);
        if (mechanic != null) {
            event.setCancelled(true);
            mechanic.callback(event.getEntity(), event.getAffectedEntities());
        }
    }
}
