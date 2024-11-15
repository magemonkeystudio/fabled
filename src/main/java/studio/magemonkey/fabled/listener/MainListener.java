/**
 * Fabled
 * studio.magemonkey.fabled.listener.MainListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package studio.magemonkey.fabled.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.data.io.PlayerLoader;
import studio.magemonkey.fabled.api.DefaultCombatProtection;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.event.PhysicalDamageEvent;
import studio.magemonkey.fabled.api.event.PlayerLevelUpEvent;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.BuffManager;
import studio.magemonkey.fabled.api.util.Combat;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.data.Permissions;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.ImmunityMechanic;
import studio.magemonkey.fabled.gui.tool.GUITool;
import studio.magemonkey.fabled.hook.CitizensHook;
import studio.magemonkey.fabled.manager.ClassBoardManager;

import java.util.*;
import java.util.function.Consumer;

/**
 * The main listener for Fabled that handles general mechanics
 * such as loading/clearing data, controlling experience gains, and
 * enabling/disabling passive abilities.
 */
public class MainListener extends FabledListener {
    public static final  Map<UUID, BukkitTask>  loadingPlayers = new HashMap<>();
    private static final List<Consumer<Player>> JOIN_HANDLERS  = new ArrayList<>();
    private static final List<Consumer<Player>> CLEAR_HANDLERS = new ArrayList<>();

    public static void registerJoin(final Consumer<Player> joinHandler) {
        JOIN_HANDLERS.add(joinHandler);
    }

    public static void registerClear(final Consumer<Player> joinHandler) {
        CLEAR_HANDLERS.add(joinHandler);
    }

    /**
     * Unloads a player's data from the server
     *
     * @param player player to unload
     */
    public static void unload(Player player) {
        if (CitizensHook.isNPC(player))
            return;

        boolean skipSaving = false;
        if (loadingPlayers.containsKey(player.getUniqueId())) {
            loadingPlayers.remove(player.getUniqueId()).cancel();
            skipSaving = true;
        }

        PlayerData data = Fabled.getData(player);
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            data.setOnPreviewStop(null);
            data.record(player);
            data.stopSkills(player);
        }

        FlagManager.clearFlags(player);
        BuffManager.clearData(player);
        Combat.clearData(player);
        DynamicSkill.clearCastData(player);

        player.setDisplayName(player.getName());
        player.setWalkSpeed(0.2f);
        Fabled.unloadPlayerData(player, skipSaving);
    }

    public static void init(final Player player) {
        final PlayerData data = Fabled.getData(player);
        data.init(player);
        GUITool.removeCastItems(player);
        JOIN_HANDLERS.forEach(handler -> handler.accept(player));
    }

    @Override
    public void cleanup() {
        JOIN_HANDLERS.clear();
    }

    /**
     * Starts passives and applies class data when a player logs in.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("NPC") || !Fabled.getSettings().isWorldEnabled(player.getWorld()))
            return;

        final int delay = Fabled.getSettings().getSqlDelay();
        if (Fabled.getSettings().isUseSql() && delay > 0) {
            final BukkitTask task = Fabled.schedule(() -> {
                try {
                    PlayerLoader.loadPlayer(player);
                    init(player);
                } finally {
                    loadingPlayers.remove(event.getPlayer().getUniqueId());
                }
            }, delay);
            loadingPlayers.put(event.getPlayer().getUniqueId(), task);
        } else {
            init(player);
        }
    }

    /**
     * Saves player data when they log out and stops passives
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        unload(event.getPlayer());
    }

    /**
     * Stops passives an applies death penalties when a player dies.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        FlagManager.clearFlags(event.getEntity());
        BuffManager.clearData(event.getEntity());
        DynamicSkill.clearCastData(event.getEntity());

        if (CitizensHook.isNPC(event.getEntity())) return;

        PlayerData data = Fabled.getData(event.getEntity());
        if (data.hasClass() && Fabled.getSettings().isWorldEnabled(event.getEntity().getWorld())) {
            data.stopSkills(event.getEntity());
            if (!Fabled.getSettings().shouldIgnoreExpLoss(event.getEntity().getWorld())) {
                data.loseExp();
            }
            if (event.getEntity() instanceof Player) {
                data.getExtraData().clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(final EntityDeathEvent event) {
        DynamicSkill.clearCastData(event.getEntity());
        FlagManager.clearFlags(event.getEntity());
        BuffManager.clearData(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnload(final ChunkUnloadEvent event) {
        for (final Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                final LivingEntity livingEntity = (LivingEntity) entity;
                DynamicSkill.clearCastData(livingEntity);
                FlagManager.clearFlags(livingEntity);
                BuffManager.clearData(livingEntity);
            }
        }
    }

    /**
     * Handles experience when a block is broken
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        Player player = event.getPlayer();
        if (Fabled.getSettings().isUseOrbs() && player != null && Fabled.getSettings()
                .isWorldEnabled(player.getWorld()))
            Fabled.getData(player).giveExp(event.getExpToDrop(), ExpSource.BLOCK_BREAK);
    }

    /**
     * Handles experience when ore is smelted in a furnace
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSmelt(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        if (Fabled.getSettings().isUseOrbs() && player != null && Fabled.getSettings()
                .isWorldEnabled(player.getWorld()))
            Fabled.getData(player).giveExp(event.getExpToDrop(), ExpSource.SMELT);
    }

    /**
     * Handles experience when a Bottle o' Enchanting breaks
     *
     * @param event event details
     */
    @EventHandler
    public void onExpBottleBreak(ExpBottleEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player) || !Fabled.getSettings()
                .isWorldEnabled(((Player) event.getEntity().getShooter()).getWorld()))
            return;

        Player player = (Player) event.getEntity().getShooter();
        if (CitizensHook.isNPC(player))
            return;

        if (Fabled.getSettings().isUseOrbs())
            Fabled.getData(player).giveExp(event.getExperience(), ExpSource.EXP_BOTTLE);
    }

    /**
     * Prevents experience orbs from modifying the level bar when it
     * is used for displaying class level.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpChange(PlayerExpChangeEvent event) {
        // Prevent it from changing the level bar when that is being used to display class level
        if (!Fabled.getSettings().getLevelBar().equalsIgnoreCase("none")
                && event.getPlayer().hasPermission(Permissions.EXP)
                && Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld())) {
            event.setAmount(0);
        }
    }

    /**
     * Handles updating level displays for players
     *
     * @param event event details
     */
    @EventHandler
    public void onLevelUp(final PlayerLevelUpEvent event) {
        if (Fabled.getSettings().isShowClassLevel()) {
            ClassBoardManager.updateLevel(event.getPlayerData());
        }
    }

    /**
     * Starts passive abilities again after respawning
     *
     * @param event event details
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        PlayerData data = Fabled.getData(event.getPlayer());
        if (data.hasClass() && Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld())) {
            data.startPassives(event.getPlayer());
            data.updateScoreboard();
        }
    }

    /**
     * Damage type immunities
     *
     * @param event event details
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity && FlagManager.hasFlag((LivingEntity) event.getEntity(),
                "immune:" + event.getCause().name())) {
            double multiplier = Fabled.getMetaDouble(event.getEntity(), ImmunityMechanic.META_KEY);
            if (multiplier <= 0)
                event.setCancelled(true);
            else
                event.setDamage(event.getDamage() * multiplier);
        }
    }

    /**
     * Cancels food damaging the player when the bar is being used
     * for GUI features instead of normal hunger.
     *
     * @param event event details
     */
    @EventHandler
    public void onStarve(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION
                && !Fabled.getSettings().getFoodBar().equalsIgnoreCase("none")) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels saturation heal
     *
     * @param event event details
     */
    @EventHandler
    public void onSaturationHeal(EntityRegainHealthEvent event) {
        String foodBar = Fabled.getSettings().getFoodBar().toLowerCase();
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                && Fabled.getSettings().isBlockSaturation()
                && (foodBar.equals("mana") || foodBar.equals("exp"))) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels EntityDamageByEntity events when the attacker and defender
     * are not allowed to attack each other. This prevents the following method
     * to trigger a PhysicalDamageEvent
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void allyCheck(EntityDamageByEntityEvent event) {
        if (DefaultCombatProtection.isFakeDamageEvent(event)
                || event.getClass().getSimpleName().equals("DamageCheckEvent") // Don't cause StackOverflow with MMOLib
        ) return;

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player &&
                !Fabled.getSettings()
                        .canAttack((Player) event.getDamager(), (Player) event.getEntity(), event.getCause())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fakeDamageClear(EntityDamageByEntityEvent event) {
        if (!DefaultCombatProtection.isFakeDamageEvent(event)) return;

        DefaultCombatProtection.externallyCancelled.put(event, event.isCancelled());
        event.setCancelled(true);
    }

    /**
     * Launches physical damage events to differentiate skill damage from physical damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPhysicalDamage(EntityDamageByEntityEvent event) {
        if (Skill.isSkillDamage()
                || DefaultCombatProtection.isFakeDamageEvent(event)
                || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM
                || !(event.getEntity() instanceof LivingEntity)
                || event.getDamage() <= 0) {
            return;
        }

        PhysicalDamageEvent e = new PhysicalDamageEvent(ListenerUtil.getDamager(event),
                (LivingEntity) event.getEntity(),
                event.getDamage(),
                event.getDamager() instanceof Projectile);
        Bukkit.getPluginManager().callEvent(e);
        event.setDamage(e.getDamage());
        if (e.isCancelled()) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles marking players as in combat
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombat(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM
                || !(event.getEntity() instanceof LivingEntity)) return;

        if (event.getEntity() instanceof Player) {
            Combat.applyCombat((Player) event.getEntity());
        }

        LivingEntity damager = ListenerUtil.getDamager(event);
        if (damager instanceof Player) {
            Combat.applyCombat((Player) damager);
        }
    }

    /**
     * Applies or removes Fabled features from a player upon switching worlds
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldChangePre(PlayerChangedWorldEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        boolean oldEnabled = Fabled.getSettings().isWorldEnabled(event.getFrom());
        boolean newEnabled = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        if (!oldEnabled || newEnabled) return;

        PlayerData data = Fabled.getData(event.getPlayer());
        data.clearAllModifiers();
        data.stopSkills(event.getPlayer());
        ClassBoardManager.clear(event.getPlayer());
        if (Fabled.getSettings().isModifyHealth()) {
            event.getPlayer().setMaxHealth(Fabled.getSettings().getDefaultHealth());
            event.getPlayer().setHealth(Fabled.getSettings().getDefaultHealth());
        }

        if (!Fabled.getSettings().getLevelBar().equalsIgnoreCase("none")) {
            event.getPlayer().setLevel(0);
            event.getPlayer().setExp(0);
        }

        if (!Fabled.getSettings().getFoodBar().equalsIgnoreCase("none")) {
            event.getPlayer().setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) return;

        boolean oldEnabled = Fabled.getSettings().isWorldEnabled(event.getFrom());
        boolean newEnabled = Fabled.getSettings().isWorldEnabled(player.getWorld());

        if (newEnabled) {
            if (oldEnabled) Fabled.getData(player)
                    .updateHealth(player); // Fixes some hybrid servers resetting max health to 20 after world change
            else init(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            return;

        if (event.getMessage().equals("/clear")) {
            handleClear(event.getPlayer());
        } else if (event.getMessage().startsWith("/clear ")) {
            handleClear(Bukkit.getPlayer(event.getMessage().substring(7)));
        }
    }

    @EventHandler
    public void onCommand(final ServerCommandEvent event) {
        if (event.getCommand().startsWith("clear ")) {
            handleClear(Bukkit.getPlayer(event.getCommand().substring(6)));
        }
    }

    private void handleClear(final Player player) {
        if (player != null) {
            Fabled.schedule(() -> {
                final PlayerData data = Fabled.getData(player);
                data.getEquips().update(player);

                CLEAR_HANDLERS.forEach(handler -> handler.accept(player));
            }, 1);
        }
    }
}
