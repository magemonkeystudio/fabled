/**
 * Fabled
 * studio.magemonkey.fabled.listener.BarListener
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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import studio.magemonkey.codex.util.InventoryUtil;
import studio.magemonkey.codex.util.ItemUT;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.*;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkillBar;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.ItemSerializer;
import studio.magemonkey.fabled.gui.handlers.SkillHandler;
import studio.magemonkey.fabled.hook.CitizensHook;

import java.util.*;

/**
 * Handles interactions with skill bars. This shouldn't be
 * used by other plugins as it is handled by the API.
 */
public class CastCombatListener extends FabledListener {
    private final Map<UUID, ItemStack[]> backup = new HashMap<>();

    private final Set<UUID> ignored = new HashSet<>();

    private final int slot = Fabled.getSettings().getCastSlot();

    @Override
    public void init() {
        MainListener.registerJoin(this::init);
        MainListener.registerClear(this::handleClear);
        Bukkit.getOnlinePlayers().forEach(this::init);
    }

    @Override
    public void cleanup() {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> Fabled.getSettings().isWorldEnabled(player.getWorld()))
                .forEach(this::cleanup);
    }

    private void init(Player player) {
        if (!Fabled.getSettings().isWorldEnabled(player.getWorld())) return;

        PlayerData data = Fabled.getData(player);
        backup.put(player.getUniqueId(), new ItemStack[9]);

        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            PlayerInventory inv  = player.getInventory();
            ItemStack       item = inv.getItem(slot);
            inv.setItem(slot, Fabled.getSettings().getCastItem());
            if (item != null && item.getType() != Material.AIR) {
                for (ItemStack overflow : inv.addItem(item).values())
                    player.getWorld().dropItemNaturally(player.getLocation(), overflow);
            }
            inv.getItem(slot).setAmount(1);

            int playerSlot = player.getInventory().getHeldItemSlot();
            int tries      = 0;
            while (!data.getSkillBar().isWeaponSlot(playerSlot) || slot == playerSlot) {
                playerSlot = (playerSlot + 1) % 9;
                if (++tries > 9) {
                    Fabled.inst()
                            .getLogger()
                            .warning(
                                    "You appear to have casting bars enabled, but don't have a slot for the player to equip a weapon. We're disabling cast bars until this is resolved.");
                    //Fabled.getSettings().setCastBars(false);
                    break; // It's unknown that kind of behavior this will cause... but we need to break the loop sometime.
                }
            }
            if (playerSlot != player.getInventory().getHeldItemSlot()) {
                player.getInventory().setHeldItemSlot(playerSlot);
            }
        }
    }

    private void cleanup(Player player) {
        ignored.remove(player.getUniqueId());
        PlayerData     data    = Fabled.getData(player);
        PlayerSkillBar bar     = data.getSkillBar();
        if (bar.isSetup()) toggle(player);
        player.getInventory().setItem(slot, null);
        backup.remove(player.getUniqueId());
    }

    private void toggle(Player player) {
        ItemStack[] items = backup.get(player.getUniqueId());
        if (items == null) items = new ItemStack[9];

        ItemStack[] temp = new ItemStack[9];
        PlayerData  data = Fabled.getData(player);
        for (int i = 0; i < items.length; i++) {
            if (i == slot || (data.getSkillBar().isSetup() && !data.getSkillBar().isWeaponSlot(i))) continue;

            temp[i] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
        }
        backup.put(player.getUniqueId(), temp);

        if (data.getSkillBar().isSetup()) data.getSkillBar().clear(player);
        else data.getSkillBar().setup(player);

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            player.getInventory().setItem(i, items[i]);
        }
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld())) cleanup(event.getPlayer());
    }

    /**
     * Manages setting up and clearing the skill bar when a player changes professions
     *
     * @param event event details
     */
    @EventHandler
    public void onProfess(PlayerClassChangeEvent event) {
        Player p = event.getPlayerData().getPlayer();

        if (event.getPreviousClass() != null && event.getNewClass() == null) {
            PlayerSkillBar bar = event.getPlayerData().getSkillBar();
            bar.reset();
            bar.clear(p);
        }
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event) {
        if (!event.getUnlockedSkill().getData().canCast() || event.getPlayerData().getPlayer() == null) return;

        event.getPlayerData().getSkillBar().unlock(event.getUnlockedSkill());
    }

    /**
     * Updates the skill bar when a skill is upgraded
     *
     * @param event event details
     */
    @EventHandler
    public void onUpgrade(final PlayerSkillUpgradeEvent event) {
        final Player player = event.getPlayerData().getPlayer();
        if (player != null && event.getPlayerData().getSkillBar().isSetup())
            Fabled.schedule(() -> event.getPlayerData().getSkillBar().update(player), 0);
    }

    /**
     * Updates a player's skill bar when downgrading a skill to level 0
     *
     * @param event event details
     */
    @EventHandler
    public void onDowngrade(final PlayerSkillDowngradeEvent event) {
        if (event.getPlayerData().getSkillBar().isSetup()) {
            Fabled.schedule(() -> Fabled.getData(event.getPlayerData().getPlayer())
                    .getSkillBar()
                    .update(event.getPlayerData().getPlayer()), 1);
        }
    }

    /**
     * Handles assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler
    public void onAssign(final InventoryClickEvent event) {
        if (event.getAction() != InventoryAction.HOTBAR_MOVE_AND_READD || !(event.getInventory()
                .getHolder() instanceof SkillHandler)) return;

        final PlayerData data = Fabled.getData((Player) event.getWhoClicked());
        if (!data.getSkillBar().isSetup() || data.getSkillBar().isWeaponSlot(event.getHotbarButton())) return;

        final SkillHandler handler = (SkillHandler) event.getInventory().getHolder();
        final Skill        skill   = handler.get(event.getSlot());
        if (skill != null && skill.canCast())
            data.getSkillBar().assign(data.getSkill(skill.getName()), event.getHotbarButton());
    }

    /**
     * Clears the skill bar on death
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) return;
        if (CitizensHook.isNPC(event.getEntity()) || !Fabled.getSettings()
                .isWorldEnabled(event.getEntity().getWorld()) || event.getEntity()
                .getWorld()
                .getGameRuleValue("keepInventory")
                .equals("true")) return;

        // This is important because if other plugins are using the death event, they may
        // be modifying drops, or doing something with the items the player has in their inventory
        Player     player = event.getEntity();
        PlayerData data   = Fabled.getData(player);
        if (data.getSkillBar().isSetup()) {
            player.setMetadata("skill-bar-setup", new FixedMetadataValue(Fabled.inst(), true));
            for (int i = 0; i < 9; i++) {
                if (!data.getSkillBar().isWeaponSlot(i)) event.getDrops().remove(player.getInventory().getItem(i));
            }
            data.getSkillBar().clear(player);
        }
        event.getDrops().remove(player.getInventory().getItem(slot));
        player.getInventory().setItem(slot, null);

        ItemStack[] hidden = backup.get(player.getUniqueId());
        if (hidden != null) {
            Arrays.stream(hidden).filter(Objects::nonNull).forEach(item -> event.getDrops().add(item));
            backup.put(player.getUniqueId(), new ItemStack[9]);
        }
    }

    /**
     * If the DeathEvent was cancelled, we should re-init the skill bar back
     * into their inventory if it was previously set up.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void postDeath(PlayerDeathEvent event) {
        if (CitizensHook.isNPC(event.getEntity())
                || event instanceof Cancellable && !((Cancellable) event).isCancelled()) return;

        Player player = event.getEntity();
        boolean wasSetup =
                player.hasMetadata("skill-bar-setup") && player.getMetadata("skill-bar-setup").get(0).asBoolean();
        if (!wasSetup) return;

        player.removeMetadata("skill-bar-setup", Fabled.inst());
        PlayerData data = Fabled.getData(player);
        player.getInventory().setItem(slot, Fabled.getSettings().getCastItem());
        data.getSkillBar().setup(player);
    }

    /**
     * Sets the skill bar back up on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        if (!event.getPlayer().getWorld().getGameRuleValue("keepInventory").equals("true")) init(event.getPlayer());
    }

    @EventHandler
    public void onDupe(InventoryClickEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getWhoClicked().getWorld())) return;

        if (event.getSlot() == slot && event.getSlotType() == InventoryType.SlotType.QUICKBAR || (
                (event.getAction() == InventoryAction.HOTBAR_SWAP
                        || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                        && event.getHotbarButton() == slot)) {
            event.setCancelled(true);
        }
    }

    /**
     * Event for assigning skills to the skill bar
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onToggle(InventoryClickEvent event) {
        // Must click on an active skill bar
        PlayerData           data     = Fabled.getData((Player) event.getWhoClicked());
        final PlayerSkillBar skillBar = data.getSkillBar();
        if (!skillBar.isSetup()) return;

        if ((event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) && (
                !skillBar.isWeaponSlot(event.getHotbarButton()) || !skillBar.isWeaponSlot(event.getSlot()))) {
            event.setCancelled(true);
            return;
        }


        // Prevent moving skill icons
        int slot = event.getSlot();
        if (event.getSlot() < 9 && event.getRawSlot() > InventoryUtil.getTopInventory(event).getSize()) {
            if (event.getSlot() == this.slot) event.setCancelled(true);
            else if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT)
                event.setCancelled(!skillBar.isWeaponSlot(slot));
            else if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) && (
                    !skillBar.isWeaponSlot(slot) || (skillBar.isWeaponSlot(slot) && (event.getCurrentItem() == null
                            || event.getCurrentItem().getType() == Material.AIR)))) {
                event.setCancelled(true);
                skillBar.toggleSlot(slot);
            } else if (event.getAction().name().startsWith("DROP")) event.setCancelled(!skillBar.isWeaponSlot(slot));
        }
    }

    /**
     * Ignores the next cast upon changing worlds due to the forced slot
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeWorldPre(PlayerChangedWorldEvent event) {
        PlayerData data       = Fabled.getData(event.getPlayer());
        boolean    enabled    = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        boolean    wasEnabled = Fabled.getSettings().isWorldEnabled(event.getFrom());
        if (data.hasClass() && data.getSkillBar().isSetup() && enabled) ignored.add(event.getPlayer().getUniqueId());
        if (!enabled && wasEnabled) cleanup(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        boolean enabled    = Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld());
        boolean wasEnabled = Fabled.getSettings().isWorldEnabled(event.getFrom());

        if (enabled && !wasEnabled) init(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeAccount(PlayerAccountChangeEvent event) {
        if (event.getPreviousAccount().getSkillBar().isSetup()) {
            toggle(event.getPreviousAccount().getPlayer());
        }
    }

    /**
     * Applies skill bar actions when pressing the number keys
     *
     * @param event event details
     */
    @EventHandler
    public void onCast(PlayerItemHeldEvent event) {
        if (!Fabled.getSettings().isWorldEnabled(event.getPlayer().getWorld())) return;

        if (event.getNewSlot() == slot) {
            event.setCancelled(true);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) toggle(event.getPlayer());
            return;
        }

        // Must be a skill slot when the bar is set up
        PlayerData     data = Fabled.getData(event.getPlayer());
        PlayerSkillBar bar  = data.getSkillBar();
        if (!bar.isWeaponSlot(event.getNewSlot()) && bar.isSetup()) {
            event.setCancelled(true);
            if (ignored.remove(event.getPlayer().getUniqueId())) return;
            bar.apply(event.getNewSlot());
        }
    }

    /**
     * Clears or sets up the skill bar upon changing from or to creative mode
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeMode(PlayerGameModeChangeEvent event) {
        // Clear on entering creative mode
        final PlayerData data = Fabled.getData(event.getPlayer());
        if (event.getNewGameMode() == GameMode.CREATIVE && data.getSkillBar().isSetup()) toggle(data.getPlayer());
    }

    private void handleClear(final Player player) {
        backup.put(player.getUniqueId(), new ItemStack[9]);
        PlayerSkillBar skillBar = Fabled.getData(player).getSkillBar();
        if (skillBar.isSetup()) skillBar.update(player);
        player.getInventory().setItem(slot, Fabled.getSettings().getCastItem());
    }
}
