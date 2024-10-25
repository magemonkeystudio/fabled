/**
 * Fabled
 * studio.magemonkey.fabled.tree.basic.RequirementTree
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
package studio.magemonkey.fabled.tree.basic;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.exception.SkillTreeException;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.gui.tool.GUIType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tree implementation based on requirement chains
 */
public class RequirementTree extends InventoryTree {
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public RequirementTree(Fabled api, FabledClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @throws SkillTreeException
     */
    @Override
    public void arrange(List<Skill> skills) throws SkillTreeException {
        skillSlots.clear();

        // Organize skills into chained and unchained
        List<Skill> chained   = new ArrayList<>();
        List<Skill> unchained = new ArrayList<>();
        for (Skill skill : skills) {
            if (isChained(skills, skill)) {
                chained.add(skill);
            } else {
                unchained.add(skill);
            }
        }

        // Determine the widths for each group
        int unchainedWidth = (unchained.size() + 5) / 6;
        int chainedWidth   = 7 - unchainedWidth;
        if (unchainedWidth == 0) {
            chainedWidth = 8;
        }
        if (unchainedWidth > 0) {
            height = (unchained.size() + unchainedWidth - 1) / unchainedWidth;
        }

        // Fill in the unchained group
        int index = 0;
        unchained.sort(comparator);
        for (Skill skill : unchained) {
            int x = index % unchainedWidth;
            int y = index / unchainedWidth;
            index++;
            skillSlots.put(x + y * 9, skill);
        }

        // Fill in the chained group
        HashMap<Skill, Integer> tier     = new HashMap<>();
        HashMap<Skill, Integer> prevTier = new HashMap<>();
        int                     row      = 0;
        index = 0;

        do {
            // Get the next tier of skills
            tier.clear();
            for (Skill skill : chained) {
                boolean hasSkillReq = skill.getSkillReq() != null && Fabled.isSkillRegistered(skill.getSkillReq());
                if ((!hasSkillReq && prevTier.size() == 0)) {
                    tier.put(skill, index++);
                } else if (hasSkillReq && prevTier.containsKey(Fabled.getSkill(skill.getSkillReq()))) {
                    tier.put(skill, prevTier.get(Fabled.getSkill(skill.getSkillReq())));
                }
            }

            // Fill in the tier
            int filled = 0;
            for (int i = 0; i < index; i++) {
                for (Map.Entry<Skill, Integer> entry : tier.entrySet()) {
                    if (entry.getValue() == i) {
                        int x = filled % chainedWidth + unchainedWidth + 1;
                        int y = filled / chainedWidth + row;
                        filled++;
                        skillSlots.put(x + y * 9, entry.getKey());
                    }
                }
            }

            // Move the current tier to the previous tier
            prevTier.clear();
            for (Map.Entry<Skill, Integer> entry : tier.entrySet()) {
                prevTier.put(entry.getKey(), entry.getValue());
            }

            // Increment the row
            row += (tier.size() + chainedWidth - 1) / chainedWidth;
        }
        while (tier.size() > 0);

        if (row + 1 > height) {
            height = row + 1;
        }

        height = Math.max(1,
                Math.min(Fabled.getConfig("gui")
                        .getConfig()
                        .getInt(GUIType.SKILL_TREE.getPrefix() + tree.getName() + ".rows", height), 6));
    }

    /**
     * Checks whether the skill is attached to a chain
     *
     * @param skills skill list to check in
     * @param skill  skill to check for
     * @return true if attached, false otherwise
     */
    private boolean isChained(List<Skill> skills, Skill skill) {
        if (Fabled.getSkill(skill.getSkillReq()) != null) {
            return true;
        }
        for (Skill s : skills) {
            if (Fabled.getSkill(s.getSkillReq()) == skill) {
                return true;
            }
        }
        return false;
    }
}
