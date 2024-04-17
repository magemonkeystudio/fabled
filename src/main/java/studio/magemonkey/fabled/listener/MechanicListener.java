/**
 * Fabled
 * studio.magemonkey.fabled.listener.MechanicListener
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

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.event.FlagApplyEvent;
import studio.magemonkey.fabled.api.event.FlagExpireEvent;
import studio.magemonkey.fabled.api.event.PlayerExperienceGainEvent;
import studio.magemonkey.fabled.api.event.PlayerLandEvent;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.projectile.ItemProjectile;
import studio.magemonkey.fabled.dynamic.mechanic.*;
import studio.magemonkey.fabled.hook.DisguiseHook;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.hook.VaultHook;
import studio.magemonkey.fabled.task.RemoveTask;
import studio.magemonkey.codex.mccore.util.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Field;
import java.util.*;

/**
 * The listener for handling events related to dynamic mechanics
 */
public class MechanicListener extends FabledListener {
    public static final String SUMMON_DAMAGE     = "sapiSumDamage";
    public static final String P_CALL            = "pmCallback";
    public static final String NO_FIRE           = "noFire";
    public static final String POTION_PROJECTILE = "potionProjectile";
    public static final String ITEM_PROJECTILE   = "itemProjectile";
    public static final String SKILL_LEVEL       = "skill_level";
    public static final String SKILL_CASTER      = "caster";
    public static final String SPEED_KEY         = "sapiSpeedKey";
    public static final String DISGUISE_KEY      = "sapiDisguiseKey";
    public static final String ARMOR_STAND       = "asMechanic";
    public static final String DAMAGE_CAUSE      = "damageCause";

    private static final HashMap<UUID, Double> flying = new HashMap<UUID, Double>();
    private static       Map<UUID, Double>     exempt = new HashMap<>();

    /**
     * Cleans up listener data on shutdown
     */
    @Override
    public void cleanup() {
        flying.clear();
    }

    /**
     * Checks for landing on the ground
     *
     * @param event event details
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;

        boolean inMap = flying.containsKey(event.getPlayer().getUniqueId());
        if (inMap == isOnGround(event.getTo())) {
            if (inMap) {
                double maxHeight = flying.remove(event.getPlayer().getUniqueId());
                Bukkit.getPluginManager()
                        .callEvent(new PlayerLandEvent(event.getPlayer(),
                                maxHeight - event.getPlayer().getLocation().getY()));
            } else
                flying.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation().getY());
        } else if (inMap) {
            double y = flying.get(event.getPlayer().getUniqueId());
            flying.put(event.getPlayer().getUniqueId(), Math.max(y, event.getPlayer().getLocation().getY()));
        }
    }

    public boolean isOnGround(Location loc) {
        loc = loc.clone();
        Set<Block> blocksUnderneath = new HashSet<>();
        double     dx               = loc.getX() % 1;
        if (dx < 0) dx += 1;
        double dz = loc.getZ() % 1;
        if (dz < 0) dz += 1;

        blocksUnderneath.add(loc.getBlock());
        blocksUnderneath.add(loc.getBlock().getRelative(BlockFace.DOWN));
        if (fuzzyEquals(loc.getY() % 1, 0.5))
            loc.subtract(0, 0.5, 0);
        loc.subtract(0, 0.07, 0);
        if (dx < 0.3) {
            blocksUnderneath.add(loc.clone().subtract(0.31, 0, 0).getBlock());
            if (dz < 0.3) {
                blocksUnderneath.add(loc.clone().subtract(0.31, 0, 0.31).getBlock());
            } else if (dz > 0.7) {
                blocksUnderneath.add(loc.clone().subtract(0.31, 0, -0.31).getBlock());
            }
        }

        if (dz < 0.3) {
            blocksUnderneath.add(loc.clone().subtract(0, 0, 0.31).getBlock());
        }

        if (dx > 0.7) {
            blocksUnderneath.add(loc.clone().add(0.31, 0, 0).getBlock());
            if (dz > 0.7) {
                blocksUnderneath.add(loc.clone().add(0.31, 0, 0.31).getBlock());
            } else if (dz < 0.3) {
                blocksUnderneath.add(loc.clone().add(0.31, 0, -0.31).getBlock());
            }
        }

        if (dz > 0.7) {
            blocksUnderneath.add(loc.clone().add(0, 0, 0.31).getBlock());
        }

        Location finalLoc = loc.clone();
        return blocksUnderneath.stream()
                .anyMatch(b -> {
                    boolean     solid = !b.isPassable();
                    BoundingBox box   = b.getBoundingBox();
                    box.expandDirectional(0, isTaller(b) ? 0.5 : 0, 0);
                    boolean bounded = isIntersecting(box, finalLoc);

                    return solid && bounded;
                });
    }

    private boolean isTaller(Block b) {
        Material type    = b.getType();
        String   typeStr = type.toString();
        return typeStr.contains("WALL") || typeStr.contains("FENCE");
    }

    private boolean isIntersecting(BoundingBox box, Location loc) {
        boolean xContains = box.getMinX() <= loc.getX() && loc.getX() <= box.getMaxX() || fuzzyEquals(box.getMinX(),
                loc.getX(),
                0.3) || fuzzyEquals(box.getMaxX(), loc.getX(), 0.3);
        boolean yContains = box.getMinY() <= loc.getY() && loc.getY() <= box.getMaxY();
        boolean zContains = box.getMinZ() <= loc.getZ() && loc.getZ() <= box.getMaxZ() || fuzzyEquals(box.getMinZ(),
                loc.getZ(),
                0.3) || fuzzyEquals(box.getMaxZ(), loc.getZ(), 0.3);

        return xContains && yContains && zContains;
    }

    private boolean fuzzyEquals(double input, double expected) {
        return fuzzyEquals(input, expected, 0.07);
    }

    private boolean fuzzyEquals(double input, double expected, double epsilon) {
        return Math.abs(input - expected) < epsilon;
    }

    /**
     * Resets walk speed and clears them from the map when quitting
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        flying.remove(event.getPlayer().getUniqueId());
        event.getPlayer().setWalkSpeed(0.2f);
    }

    /**
     * Applies effects when specific flag keys are set
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onApply(FlagApplyEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getFlag().startsWith("perm:") && PluginChecker.isVaultPermissionsActive())
                VaultHook.addPermission((Player) event.getEntity(), event.getFlag().substring(5));
        }
    }

    /**
     * Clears speed modifiers when the flag expires
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpire(FlagExpireEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getFlag().startsWith("perm:") && PluginChecker.isVaultPermissionsActive())
                VaultHook.removePermission((Player) event.getEntity(), event.getFlag().substring(5));
            else if (event.getFlag().startsWith(SPEED_KEY + ":")) {
                Player player = (Player) event.getEntity();
                UUID   uuid   = UUID.fromString(event.getFlag().split(":")[1]);

                PlayerData data = Fabled.getPlayerData(player);
                data.removeStatModifier(uuid, false);
                data.updateWalkSpeed(player);
            }
        }
        if (event.getFlag().equals(DISGUISE_KEY))
            DisguiseHook.removeDisguise(event.getEntity());
    }

    /**
     * Applies projectile callbacks when landing on the ground
     *
     * @param event event details
     */
    @EventHandler
    public void onLand(final ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata(P_CALL))
            Fabled.schedule(() -> {
                final Object obj = Fabled.getMeta(event.getEntity(), P_CALL);
                if (obj != null)
                    ((ProjectileMechanic) obj).callback(event.getEntity(), null);
            }, 1);
    }

    /**
     * Prevent item projectiles from being absorbed by hoppers
     *
     * @param event event details
     */
    @EventHandler
    public void onItemPickup(final InventoryPickupItemEvent event) {
        final Object meta = Fabled.getMeta(event.getItem(), ITEM_PROJECTILE);
        if (meta != null) {
            event.setCancelled(true);
            ((ItemProjectile) meta).applyLanded();
        }
    }

    /**
     * Used for experience mechanic
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExperienceGain(PlayerExperienceGainEvent event) {
        Player player = event.getPlayerData().getPlayer();
        if (event.isCancelled()
                && event.getSource() == ExpSource.PLUGIN
                && exempt.containsKey(player.getUniqueId())
                && exempt.get(player.getUniqueId()) == event.getExp()) {
            event.setCancelled(false);
        }
    }

    public static void addExemptExperience(Player player, double amount) {
        exempt.put(player.getUniqueId(), amount);
    }

    /**
     * Stop explosions of projectiles fired from skills
     *
     * @param event event details
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity().hasMetadata(P_CALL))
            event.setCancelled(true);
    }

    /**
     * Applies projectile and lightning callbacks when striking an enemy
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity  = event.getEntity();
        if (damager instanceof Projectile) {
            Projectile p = (Projectile) damager;
            if (p.hasMetadata(P_CALL) && entity instanceof LivingEntity) {
                ((ProjectileMechanic) Fabled.getMeta(p, P_CALL))
                        .callback(p, (LivingEntity) entity);
                event.setCancelled(true);
            }
        } else if (damager instanceof LightningStrike && damager.hasMetadata(P_CALL)
                && entity instanceof LivingEntity) {
            double damage = Objects.requireNonNull((LightningMechanic.Callback) Fabled.getMeta(damager, P_CALL))
                    .execute((LivingEntity) entity);
            if (damage <= 0) {
                event.setCancelled(true);
            } else {
                event.setDamage(damage);
            }
        }
    }

    @EventHandler
    public void combust(EntityCombustByEntityEvent event) {
        if (event.getCombuster() != null && event.getCombuster().hasMetadata(NO_FIRE))
            event.setCancelled(true);
    }

    @EventHandler
    public void blockIgnite(BlockIgniteEvent event) {
        if (event.getIgnitingEntity() != null && event.getIgnitingEntity().hasMetadata(NO_FIRE))
            event.setCancelled(true);
    }

    /**
     * Handles when summoned monsters deal damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onSummonDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata(SUMMON_DAMAGE))
            VersionManager.setDamage(event, Fabled.getMetaDouble(event.getDamager(), SUMMON_DAMAGE));
    }

    /**
     * Handles when a potion projectile hits things
     *
     * @param event event details
     */
    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        if (event.getEntity().hasMetadata(POTION_PROJECTILE)) {
            event.setCancelled(true);
            ((PotionProjectileMechanic) Fabled.getMeta(event.getEntity(), POTION_PROJECTILE))
                    .callback(event.getEntity(), event.getAffectedEntities());
            event.getAffectedEntities().clear();
        }
    }

    /**
     * Can't break blocks from block mechanics
     *
     * @param event event details
     */
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (BlockMechanic.isPending(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ArmorStand && Fabled.getMeta(entity, ARMOR_STAND) != null) {
            event.setCancelled(true);
        } else if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)
                && entity.hasMetadata(FireMechanic.META_KEY)) {
            event.setDamage(Fabled.getMetaDouble(entity, FireMechanic.META_KEY));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageCause(EntityDamageEvent event) {
        Entity              entity       = event.getEntity();
        List<MetadataValue> metadataList = entity.getMetadata(DAMAGE_CAUSE);
        if (metadataList.isEmpty()) {
            return;
        }
        Object metadataValue = metadataList.get(0).value();
        if (!(metadataValue instanceof EntityDamageEvent.DamageCause)) {
            return;
        }
        if (event.getCause() != metadataValue) {
            try {
                Field causeField = EntityDamageEvent.class.getDeclaredField("cause");
                causeField.setAccessible(true);
                causeField.set(event, metadataValue);
            } catch (Exception e) {
                new UnsupportedOperationException("Failed to change DamageCause", e).printStackTrace();
            }
        }
        entity.removeMetadata(DAMAGE_CAUSE, Fabled.inst());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        if (entity.hasMetadata(FireMechanic.META_KEY)) {
            // Clears old FireMechanic data before combusting again
            Fabled.removeMeta(entity, FireMechanic.META_KEY);
        }
    }

    /**
     * Cancels interactions with  armor stands corresponding to an Armor Stand Mechanic
     *
     * @param event event details
     */
    @EventHandler
    public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
        Entity entity = event.getRightClicked();
        if (Fabled.getMeta(entity, ARMOR_STAND) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(WolfMechanic.SKILL_META)
                    || entity.hasMetadata(ARMOR_STAND)) {
                entities.add(entity);
            }
        }
        if (!entities.isEmpty()) {
            new RemoveTask(entities, 1);
        }
    }
}
