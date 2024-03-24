/**
 * Fabled
 * com.promcteam.fabled.cast.PlayerCastBars
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
package com.promcteam.fabled.cast;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.api.player.PlayerSkill;
import com.promcteam.fabled.gui.tool.GUITool;
import com.promcteam.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Representation of cast bar data for a single player
 */
public class PlayerCastBars implements InventoryHolder {
    private final HashMap<Integer, String> hoverBar   = new HashMap<>();
    private final HashMap<Integer, String> instantBar = new HashMap<>();

    private PlayerView view = PlayerView.INVENTORY;

    private ItemStack[] backup;

    private final PlayerData player;

    private PlayerSkill hovered;

    private long cooldown;

    private int oldSlot;

    /**
     * @param data player data reference
     */
    public PlayerCastBars(PlayerData data) {
        this.player = data;
    }

    /**
     * Validates added skills, making sure they're still unlocked
     */
    public void validate() {
        validate(hoverBar);
        validate(instantBar);
    }

    /**
     * @return true if in the hover view, false otherwise
     */
    public boolean isHovering() {
        return view == PlayerView.HOVER_BAR;
    }

    public PlayerView getView() {
        return view;
    }

    /**
     * Marks a skill as hovered
     *
     * @param slot skill slot
     */
    private void hoverSkill(int slot) {
        if (hoverBar.containsKey(slot)) {
            hovered = this.player.getSkill(hoverBar.get(slot));
            hovered.startPreview();
        } else {
            hovered = null;
            player.setOnPreviewStop(null);
        }
    }

    /**
     * Checks the skills assigned to a bar to make sure they are still unlocked
     *
     * @param map data of the bar to validate
     */
    private void validate(HashMap<Integer, String> map) {
        map.entrySet().removeIf(entry -> {
            String skillName = entry.getValue();
            if (!player.hasSkill(skillName)) return false;
            PlayerSkill playerSkill = player.getSkill(skillName);
            return playerSkill == null || !playerSkill.isUnlocked() || !playerSkill.getData().canCast();
        });
    }

    /**
     * Restores the players inventory after
     * viewing one of the related views
     */
    public void restore() {
        if (view == PlayerView.INVENTORY) {
            return;
        }

        // Update organizer data
        if (view == PlayerView.ORGANIZER) {
            reset();

            ItemStack[] contents = player.getPlayer().getInventory().getContents();
            update(contents, hoverBar, 0);
            update(contents, instantBar, 27);
        }

        // Restore player's items
        player.getPlayer().getInventory().setContents(backup);
        view = PlayerView.INVENTORY;
        player.getPlayer().getInventory().setHeldItemSlot(oldSlot);
        player.setOnPreviewStop(null);
    }

    /**
     * Opens the cast bar organizer GUI
     *
     * @param player player to open for
     * @return true if opened
     */
    public boolean showOrganizer(Player player) {
        if (view != PlayerView.INVENTORY) {
            return false;
        }

        view = PlayerView.ORGANIZER;
        backup = player.getInventory().getContents();

        // Set up player inventory for the different bars
        ItemStack[] playerContents = new ItemStack[36];
        fill(playerContents, hoverBar, 0);
        fill(playerContents, instantBar, 27);
        int castSlot = Fabled.getSettings().getCastSlot();
        playerContents[castSlot] = Fabled.getSettings().getHoverItem();
        playerContents[castSlot + 27] = Fabled.getSettings().getInstantItem();

        // Make the inventory for unused skills
        Set<String> unused   = getUnused();
        Inventory   inv      = player.getServer().createInventory(this, 54);
        ItemStack[] contents = new ItemStack[54];
        int         i        = 0;
        int         j        = 9;
        for (String skill : unused) {
            if (i < contents.length) {
                contents[i++] = makeIndicator(skill);
            } else if (j < 27) {
                playerContents[j++] = makeIndicator(skill);
            }
        }

        // Apply layouts and open the view
        player.getInventory().setContents(playerContents);
        inv.setContents(contents);
        oldSlot = player.getInventory().getHeldItemSlot();
        new BukkitRunnable() {
            @Override
            public void run() {player.openInventory(inv);}
        }.runTask(Fabled.inst());

        return true;
    }

    /**
     * Fills the contents with the skills in a cast bar
     *
     * @param contents contents to add to
     * @param bar      cast bar data
     * @param index    index to start at
     */
    private void fill(ItemStack[] contents, HashMap<Integer, String> bar, int index) {
        for (Map.Entry<Integer, String> entry : bar.entrySet()) {
            contents[index + entry.getKey()] = makeIndicator(entry.getValue());
        }
    }

    /**
     * Updates the layout for a cast bar
     *
     * @param contents customizer GUI contents
     * @param bar      bar data to update
     * @param index    starting index
     */
    private void update(ItemStack[] contents, HashMap<Integer, String> bar, int index) {
        int castSlot = Fabled.getSettings().getCastSlot();
        for (int i = 0; i < 9; i++) {
            if (i == castSlot) {
                continue;
            }
            if (contents[i + index] == null) {
                continue;
            }
            ItemMeta meta = contents[i + index].getItemMeta();
            if (meta == null) {
                continue;
            }
            List<String> lore = meta.getLore();
            if (lore == null) {
                continue;
            }
            String      skillName = lore.get(lore.size() - 1);
            PlayerSkill skill     = this.player.getSkill(skillName);
            if (skill != null) {
                bar.put(i, skillName);
            }
        }
    }

    /**
     * Creates an indicator for use in the skill organize display
     *
     * @param skill skill to display
     * @return makes a skill indicator, appending the skill name to the end for identification
     */
    private ItemStack makeIndicator(String skill) {
        if (skill == null) {
            return null;
        }
        ItemStack item = Fabled.getSkill(skill).getIndicator(this.player.getSkill(skill), true);
        ItemMeta  meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(skill);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return GUITool.markCastItem(item);
    }

    /**
     * Shows the hover cast bar to the player
     *
     * @param player player to show to
     */
    public boolean showHoverBar(Player player) {
        if (show(player, PlayerView.HOVER_BAR, hoverBar)) {
            hoverSkill(player.getInventory().getHeldItemSlot());
            if (hovered != null) hovered.startPreview();
            return true;
        }
        return false;
    }

    /**
     * Shows the instant bar to the player
     *
     * @param player player to show to
     */
    public boolean showInstantBar(Player player) {
        return show(player, PlayerView.INSTANT_BAR, instantBar);
    }

    /**
     * Shows a cast bar to the player if requirements are met
     *
     * @param player player to show
     * @param view   view related to the bar
     * @param bar    bar data
     */
    private boolean show(Player player, PlayerView view, HashMap<Integer, String> bar) {
        long left = System.currentTimeMillis() - cooldown - Fabled.getSettings().getCastCooldown();
        if (this.view != PlayerView.INVENTORY || bar.isEmpty() || left < 0) {
            return false;
        }

        this.view = view;
        backup = player.getInventory().getContents();

        ItemStack[] contents = new ItemStack[36];
        makeContents(bar, contents, 0);
        player.getInventory().setContents(contents);
        player.getInventory().setItem(Fabled.getSettings().getCastSlot(), Fabled.getSettings().getCastItem());

        return true;
    }

    /**
     * Handles changing to a different weapon slot
     *
     * @param event event details
     */
    public boolean handle(PlayerItemHeldEvent event) {
        switch (view) {
            case INSTANT_BAR:
                if (instantBar.containsKey(event.getNewSlot())) {
                    player.cast(instantBar.get(event.getNewSlot()));
                    cooldown = System.currentTimeMillis();
                }
                restore();
                event.setCancelled(true);
                return true;

            case HOVER_BAR:
                hoverSkill(event.getNewSlot());
                return true;

            case INVENTORY:
                oldSlot = event.getPreviousSlot();
                return false;
        }

        return false;
    }

    /**
     * Handles a click event when in certain views
     *
     * @param player the player doing the interaction
     */
    public boolean handleInteract(Player player) {
        switch (view) {
            case INSTANT_BAR:
                restore();
                return true;

            case HOVER_BAR:
                if (hoverBar.containsKey(player.getInventory().getHeldItemSlot())) {
                    this.player.cast(hoverBar.get(player.getInventory().getHeldItemSlot()));
                    cooldown = System.currentTimeMillis();
                }
                restore();
                return true;
        }
        return false;
    }

    /**
     * Handles when the player opens an inventory
     */
    public void handleOpen() {
        if (view == PlayerView.HOVER_BAR || view == PlayerView.INSTANT_BAR) {
            restore();
        }
    }

    /**
     * Adds an unlocked skill to the skill bars
     *
     * @param skill skill to add
     */
    public void unlock(PlayerSkill skill) {
        if (!addTo(hoverBar, skill)) {
            addTo(instantBar, skill);
        }
        validate();
    }

    /**
     * Adds a skill to the first open slot in the bar
     *
     * @param bar   bar to add to
     * @param skill skill to add
     * @return true if added, false if no room
     */
    private boolean addTo(HashMap<Integer, String> bar, PlayerSkill skill) {
        for (int i = 0; i < 9; i++) {
            if (!bar.containsKey(i)) {
                add(bar, skill.getData().getName(), i);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets a skill to the bar
     *
     * @param bar   bar to set to
     * @param skill skill to set
     * @param slot  slot to set to
     */
    private void add(HashMap<Integer, String> bar, String skill, int slot) {
        bar.put(slot, skill);
    }

    /**
     * Resets the layout and populates the unused list
     * with all available skills
     */
    public void reset() {
        instantBar.clear();
        hoverBar.clear();
    }

    private HashSet<String> getUnused() {
        HashSet<String> unused = new HashSet<>();
        for (PlayerSkill skill : player.getSkills()) {
            if (skill.isUnlocked() && skill.getData().canCast()) {
                String name = skill.getData().getName();
                if (!hoverBar.containsValue(name) && !instantBar.containsValue(name)) {
                    unused.add(name);
                }
            }
        }

        return unused;
    }

    /**
     * Makes the contents for one of the views
     *
     * @param slots    slots to use
     * @param contents where to store the results
     * @param offset   starting index to add to
     */
    private void makeContents(HashMap<Integer, String> slots, ItemStack[] contents, int offset) {
        for (Map.Entry<Integer, String> slot : slots.entrySet()) {
            PlayerSkill skill = this.player.getSkill(slot.getValue());
            if (skill != null) {
                contents[offset + slot.getKey()] = GUITool.markCastItem(skill.getData().getIndicator(skill, true));
            }
        }
    }

    /**
     * Loads data from the config
     *
     * @param config config data
     * @param hover  whether it's for the hover bar
     */
    public void load(DataSection config, boolean hover) {
        if (config == null) {
            return;
        }

        HashMap<Integer, String> bar = hover ? hoverBar : instantBar;
        for (String key : config.keys()) {
            add(bar, key, config.getInt(key));
        }
        validate(bar);
    }

    /**
     * Saves data to the config
     *
     * @param config config data
     * @param hover  whether it's for the hover bar
     */
    public void save(DataSection config, boolean hover) {
        HashMap<Integer, String> bar = hover ? hoverBar : instantBar;
        for (Map.Entry<Integer, String> entry : bar.entrySet()) {
            config.set(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Added to satisfy InventoryHolder, though doesn't do anything
     *
     * @return null
     */
    @Override
    public Inventory getInventory() {
        return null;
    }
}
