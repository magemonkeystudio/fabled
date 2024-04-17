/**
 * Fabled
 * studio.magemonkey.fabled.tree.basic.BasicVerticalTree
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

import java.util.List;

/**
 * A basic implementation of a vertically ascending skill tree
 */
public class BasicVerticalTree extends InventoryTree {
    private int width;

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public BasicVerticalTree(Fabled api, FabledClass tree) {
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

        // Arrange the skill tree
        skills.sort(comparator);
        height = 0;

        // Cycle through all skills that do not have children, put them
        // at the far left, and branch their children to the right
        for (int i = 0, size = skills.size(); i < size; i++) {
            Skill skill = skills.get(i);
            if (skill.getSkillReq() != null) {
                continue;
            }
            if (i == 8) {
                Fabled.inst()
                        .getLogger()
                        .warning(this.getClass().getSimpleName() + " for " + this.tree.getName()
                                + " would be too big and could not be completed. Try changing the tree type of the class.");
                break;
            }
            skillSlots.put(i, skill);
            width = placeChildren(skills, skill, i + 9, 0);
        }
        height = Math.max(1,
                Math.min(Fabled.getConfig("gui")
                        .getConfig()
                        .getInt(GUIType.SKILL_TREE.getPrefix() + tree.getName() + ".rows",
                                skillSlots.size() == 0 ? 1 : (skillSlots.lastKey() + 9) / 9), 6));
    }

    /**
     * Places the children of a skill to the right of it, branching downward
     *
     * @param skills skills included in the tree
     * @param skill  skill to add the children of
     * @param slot   slot ID for the first child
     * @param depth  current depth of recursion
     */
    private int placeChildren(List<Skill> skills, Skill skill, int slot, int depth) {

        // Update tree height
        if (depth + 1 > height) {
            height = depth + 1;
        }

        // Add in all children
        int width = 0;
        for (Skill s : skills) {
            if (s.getSkillReq() == null) {
                continue;
            }
            if (s.getSkillReq().equalsIgnoreCase(skill.getName())) {
                if ((slot + width) % 9 == 8) {
                    Fabled.inst()
                            .getLogger()
                            .warning(this.getClass().getSimpleName() + " for " + this.tree.getName()
                                    + " would be too big and could not be completed. Try changing the tree type of the class.");
                    break;
                }
                skillSlots.put(slot + width, s);
                width += placeChildren(skills, s, slot + width + 9, depth + 1);
            }
        }

        return Math.max(width, 1);
    }
}
