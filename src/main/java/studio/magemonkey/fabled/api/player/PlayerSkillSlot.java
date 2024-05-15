/**
 * Fabled
 * studio.magemonkey.fabled.api.player.PlayerSkillSlot
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.api.player;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.gui.tool.GUITool;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Handles the skill slot for casting when bars are disabled
 */
public class PlayerSkillSlot {
    private final ArrayList<PlayerSkill> skills   = new ArrayList<PlayerSkill>();
    private       int                    index    = 0;
    private       PlayerData             player;
    private       boolean                hovering = false;

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
        setHovering(player.getPlayer().getInventory().getHeldItemSlot() == Fabled.getSettings().getCastSlot());
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
     * Updates the displayed item for the player
     *
     * @param player player to update for
     */
    public void updateItem(Player player) {
        if (player != null) {
            PlayerData playerData = Fabled.getData(player);
            if (skills.isEmpty()) {
                player.getInventory()
                        .setItem(Fabled.getSettings().getCastSlot(),
                                GUITool.markCastItem(Fabled.getSettings().getCastItem()));
                playerData.setOnPreviewStop(null);
            } else {
                PlayerSkill playerSkill = skills.get(index);
                if (hovering) playerSkill.startPreview();
                player.getInventory().setItem(Fabled.getSettings().getCastSlot(),
                        GUITool.markCastItem(playerSkill.getData().getIndicator(skills.get(index), true)));
            }
        }
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

    public void setHovering(boolean hovering) {
        this.hovering = true;
        if (hovering && !skills.isEmpty()) skills.get(index).startPreview();
        else player.setOnPreviewStop(null);
    }
}
