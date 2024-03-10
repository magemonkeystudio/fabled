/**
 * SkillAPI
 * com.sucy.skill.listener.BindListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.KeyPressEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import mc.promcteam.engine.utils.DataUT;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A listener that handles casting skills through binds. This shouldn't be
 * use by other plugins as it is handled by the API.
 */
public class BindListener extends SkillAPIListener {
    public static final NamespacedKey SKILLS_KEY = new NamespacedKey(SkillAPI.inst(), "bound_skills");

    private static final Map<Player, Integer> indexes = new HashMap<>();

    @Override
    public void init() {
        MainListener.registerJoin(this::init);
        MainListener.registerClear(this::cleanup);
        for (Player player : Bukkit.getOnlinePlayers()) init(player);
    }

    @Override
    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) cleanup(player);
    }

    public void init(Player player) {
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) return;
        ItemStack itemStack = getHeldItem(player.getInventory());
        if (itemStack == null) return;
        List<PlayerSkill> boundSkills = getBoundSkills(itemStack, SkillAPI.getPlayerData(player));
        if (boundSkills.isEmpty()) return;
        boundSkills.get(getIndex(player, boundSkills.size())).startPreview();
    }

    public void cleanup(Player player) {
        indexes.remove(player);
        SkillAPI.getPlayerData(player).setOnPreviewStop(null);
    }

    @Nullable
    public static ItemStack getHeldItem(PlayerInventory inventory) {
        ItemStack heldItem = inventory.getItemInMainHand();
        if (heldItem.getItemMeta() == null) heldItem = inventory.getItemInOffHand();
        if (heldItem.getItemMeta() == null) return null;
        return heldItem;
    }

    public static List<String> getBoundSkills(@NotNull ItemStack itemStack) {
        String[] array = Objects.requireNonNull(itemStack.getItemMeta())
                .getPersistentDataContainer()
                .get(SKILLS_KEY, DataUT.STRING_ARRAY);
        List<String> list = new ArrayList<>();
        if (array != null)
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        return list;
    }

    public static void setBoundSkills(@NotNull ItemStack itemStack, @Nullable List<String> boundSkills) {
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        if (boundSkills == null || boundSkills.isEmpty()) meta.getPersistentDataContainer().remove(SKILLS_KEY);
        else {
            String[] array = new String[boundSkills.size()];
            for (int i = 0; i < array.length; i++) array[i] = boundSkills.get(i);
            meta.getPersistentDataContainer().set(SKILLS_KEY, DataUT.STRING_ARRAY, array);
        }
        itemStack.setItemMeta(meta);
    }

    public static List<PlayerSkill> getBoundSkills(@NotNull ItemStack itemStack, @NotNull PlayerData playerData) {
        List<String> skillNames = getBoundSkills(itemStack);
        return skillNames.stream()
                .map(playerData::getSkill)
                .filter(skill -> skill != null && skill.getLevel() > 0)
                .collect(Collectors.toList());
    }

    public static int getIndex(Player player, int boundSkills) {
        return (indexes.getOrDefault(player, 0) % boundSkills + boundSkills) % boundSkills;
    }

    /**
     * Handles interact events to check when a player right clicks with
     * a bound item to cast a skill.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(KeyPressEvent event) {
        Player player = event.getPlayer();
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) return;
        PlayerData playerData = SkillAPI.getPlayerData(player);
        ItemStack  heldItem   = getHeldItem(player.getInventory());
        if (heldItem == null) return;

        List<PlayerSkill> boundSkills = getBoundSkills(heldItem, playerData);
        if (boundSkills.isEmpty()) return;
        int index = getIndex(player, boundSkills.size());

        switch (event.getKey()) {
            case LEFT -> {
                playerData.cast(boundSkills.get(index));
            }
            case RIGHT -> {
                playerData.setOnPreviewStop(null);
                index = (index + 1) % boundSkills.size();
                indexes.put(player, index);
                boundSkills.get(index).startPreview();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        indexes.remove(player);
        PlayerData playerData = SkillAPI.getPlayerData(player);
        playerData.setOnPreviewStop(null);
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) return;

        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        if (heldItem != null && heldItem.getItemMeta() != null) {
            List<PlayerSkill> boundSkills = getBoundSkills(heldItem, playerData);
            if (boundSkills.isEmpty()) return;
            boundSkills.get(0).startPreview();
        }
    }
}
