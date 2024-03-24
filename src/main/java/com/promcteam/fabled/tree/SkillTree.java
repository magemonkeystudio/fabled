/**
 * Fabled
 * com.promcteam.fabled.tree.SkillTree
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.tree;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.classes.FabledClass;
import com.promcteam.fabled.api.exception.SkillTreeException;
import com.promcteam.fabled.api.skills.Skill;
import com.promcteam.fabled.log.Logger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a skill tree that contains an arrangement of a class's skills
 * for a player to browse and level up or refund skills.
 */
public abstract class SkillTree {
    protected final Fabled      api;
    protected final FabledClass tree;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public SkillTree(Fabled api, FabledClass tree) {
        this.api = api;
        this.tree = tree;
    }

    /**
     * Checks whether the player can be shown the skill
     *
     * @param player player to check for
     * @param skill  skill to check for permissions
     * @return true if skill can be shown, false otherwise
     */
    public boolean canShow(Player player, Skill skill) {
        return !(skill.canAutoLevel(0) && skill.canAutoLevel(1) && !skill.canCast() && !Fabled.getSettings()
                .isShowingAutoSkills())
                && skill.isAllowed(player);
    }

    /**
     * Arranges the skill tree
     *
     * @throws com.promcteam.fabled.api.exception.SkillTreeException
     */
    public void arrange() throws SkillTreeException {

        // Get included skills
        ArrayList<Skill> skills = new ArrayList<>();
        for (Skill skill : tree.getSkills()) {
            if (!Fabled.isSkillRegistered(skill)) {
                Logger.invalid("Failed to add skill to tree - " + skill + ": Skill does not exist");
                continue;
            }
            if (Fabled.getSettings().isShowingAutoSkills() || skill.canCast() || !(skill.canAutoLevel(0)
                    && skill.canAutoLevel(1))) {
                skills.add(skill);
            }
        }

        // Arrange the skills
        arrange(skills);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     */
    protected abstract void arrange(List<Skill> skills) throws SkillTreeException;

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     * @return true if registered, false otherwise
     */
    public abstract boolean hasSkill(Skill skill);
}
