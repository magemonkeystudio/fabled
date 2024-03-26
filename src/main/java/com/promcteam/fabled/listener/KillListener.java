/**
 * Fabled
 * com.promcteam.fabled.listener.KillListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.promcteam.fabled.listener;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.enums.ExpSource;
import com.promcteam.fabled.api.event.PhysicalDamageEvent;
import com.promcteam.fabled.api.event.SkillDamageEvent;
import com.promcteam.fabled.api.event.TrueDamageEvent;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.api.util.BuffManager;
import com.promcteam.fabled.api.util.FlagManager;
import com.promcteam.fabled.data.Permissions;
import com.promcteam.codex.util.reflection.ReflectionManager;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Tracks who kills what entities and awards experience accordingly
 */
public class KillListener extends FabledListener {
    private static final String S_TYPE  = "sType";
    private static final int    SPAWNER = 0, EGG = 1;

    public static void giveExp(LivingEntity entity, Player killer, int exp) {

        // Disabled world
        if (!Fabled.getSettings().isWorldEnabled(entity.getWorld()))
            return;

        // Cancel experience when applicable
        if (entity.hasMetadata(S_TYPE)) {
            int value = Fabled.getMetaInt(entity, S_TYPE);

            // Block spawner mob experience
            if (value == SPAWNER && Fabled.getSettings().isBlockSpawner())
                return;

                // Block egg mob experience
            else if (value == EGG && Fabled.getSettings().isBlockEgg())
                return;
        }

        // Summons don't give experience
        if (entity.hasMetadata(MechanicListener.SUMMON_DAMAGE))
            return;

        if (killer != null && killer.hasPermission(Permissions.EXP)) {
            // Block creative experience
            if (killer.getGameMode() == GameMode.CREATIVE && Fabled.getSettings().isBlockCreative())
                return;

            PlayerData player = Fabled.getPlayerData(killer);

            // Give experience based on orbs when enabled
            if (Fabled.getSettings().isUseOrbs())
                player.giveExp(exp, ExpSource.MOB);

                // Give experience based on config when not using orbs
            else {
                String name  = ListenerUtil.getName(entity);
                double yield = Fabled.getSettings().getYield(name);
                player.giveExp(yield, ExpSource.MOB);
            }
        }
    }

    /**
     * Grants experience upon killing a monster and blocks experience when
     * the monster originated from a blocked source.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onKill(EntityDeathEvent event) {
        FlagManager.clearFlags(event.getEntity());
        BuffManager.clearData(event.getEntity());

        giveExp(event.getEntity(), event.getEntity().getKiller(), event.getDroppedExp());
    }

    /**
     * Marks spawned entities with how they spawned to block experience from certain methods
     *
     * @param event event details
     */
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER)
            Fabled.setMeta(event.getEntity(), S_TYPE, SPAWNER);
        else if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            Fabled.setMeta(event.getEntity(), S_TYPE, EGG);
    }

    /**
     * Keeps track of killers when dealing physical damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPhysical(PhysicalDamageEvent event) {
        if (event.getDamager() instanceof Player)
            ReflectionManager.getReflectionUtil().setKiller(event.getTarget(), (Player) event.getDamager());
    }

    /**
     * Keeps track of killers when dealing damage with skills
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpell(SkillDamageEvent event) {
        if (event.getDamager() instanceof Player)
            ReflectionManager.getReflectionUtil().setKiller(event.getTarget(), (Player) event.getDamager());
    }

    /**
     * Keeps track of killers when dealing true damage with skills
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrue(TrueDamageEvent event) {
        if (event.getDamager() instanceof Player)
            ReflectionManager.getReflectionUtil().setKiller(event.getTarget(), (Player) event.getDamager());
    }
}
