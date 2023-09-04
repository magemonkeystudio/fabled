/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerSkillSlot
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
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
package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.gui.tool.GUITool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Handles the skill slot for casting when bars are disabled
 */
public class PlayerSkillSlot {
    private final ArrayList<PlayerSkill> skills = new ArrayList<PlayerSkill>();
    private       int                    index  = 0;
    private       PlayerData             player;

    /**
     * Initializes the skill slot for the given player
     *
     * @param data data of the player
     */
    public void init(PlayerData data) {
        this.player = data;
        this.index = 0;
        this.skills.clear();

        for (PlayerSkill skill : data.getSkills())
            if (skill.getData().canCast() && skill.isUnlocked())
                skills.add(skill);
    }

    /**
     * Gets the current item that should be displayed in the skill slot
     *
     * @return item display
     */
    public ItemStack getDisplay() {
        return skills.isEmpty() ?
                SkillAPI.getSettings().getCastItem()
                : GUITool.markCastItem(skills.get(index).getData().getIndicator(skills.get(index), true));
    }

    /**
     * Adds a skill to the available skills, if castable
     *
     * @param skill skill to add
     */
    public void unlock(PlayerSkill skill) {
        if (skill.isUnlocked() && skill.getData().canCast())
            skills.add(skill);
    }

    /**
     * Clears a specified skill, if available
     *
     * @param skill skill to clear
     */
    public void clear(PlayerSkill skill) {
        if (skill.getData().canCast()) {
            skills.remove(skill);
            index = Math.max(Math.min(index, skills.size() - 1), 0);
        }
    }

    /**
     * Clears all available skills
     */
    public void clearAll() {
        skills.clear();
        index = 0;
    }

    /**
     * Updates the displayed item for the player
     *
     * @param player player to update for
     */
    public void updateItem(Player player) {
        if (player != null)
            player.getInventory().setItem(SkillAPI.getSettings().getCastSlot(), getDisplay());
    }

    /**
     * Activates the skill slot, casting the hovered item
     */
    public void activate() {
        if (!skills.isEmpty())
            player.cast(skills.get(index));
    }

    /**
     * Cycles to the next skill
     */
    public void next() {
        if (!skills.isEmpty()) {
            index = (index + 1) % skills.size();
            updateItem(player.getPlayer());
        }
    }

    /**
     * Cycles to the previous skill
     */
    public void prev() {
        if (!skills.isEmpty()) {
            index = (index + skills.size() - 1) % skills.size();
            updateItem(player.getPlayer());
        }
    }
}
