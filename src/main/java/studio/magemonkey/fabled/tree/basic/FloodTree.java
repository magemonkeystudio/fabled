/**
 * Fabled
 * studio.magemonkey.fabled.tree.basic.FloodTree
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
package studio.magemonkey.fabled.tree.basic;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.exception.SkillTreeException;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.gui.customization.tool.GUIType;

import java.util.Comparator;
import java.util.List;

/**
 * <p>Root class for tree implementations based on levels</p>
 * <p>This is still in development to make it work as intended</p>
 */
public class FloodTree extends InventoryTree {
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public FloodTree(Fabled api, FabledClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     * @throws studio.magemonkey.fabled.api.exception.SkillTreeException
     */
    @Override
    protected void arrange(List<Skill> skills) throws SkillTreeException {
        skillSlots.clear();
        skills.sort(levelComparator);
        int i = 0;
        for (Skill skill : skills) {
            if (i % 9 == 8) {
                i++;
            }
            skillSlots.put(i, skill);
            i++;
        }
        height = Math.max(1,
                Math.min(Fabled.getConfig("gui")
                                .getConfig()
                                .getInt(GUIType.SKILL_TREE.getPrefix() + tree.getName() + ".rows", (skills.size() + 7) / 8),
                        6));
    }

    /**
     * Comparator for skills for level trees
     */
    private static final Comparator<Skill> levelComparator = new Comparator<Skill>() {
        /**
         * Compares skills based on their stats for skill tree arrangement
         *  -> Skills with lower level requirements come first
         *  -> Then its skills with lower costs
         *  -> Then its skills alphabetically
         *
         * @param skill1 skill being compared
         * @param skill2 skill to compare to
         * @return      -1, 0, or 1
         */
        @Override
        public int compare(Skill skill1, Skill skill2) {
            return skill1.getLevelReq(0) > skill2.getLevelReq(0) ? 1
                    : skill1.getLevelReq(0) < skill2.getLevelReq(0) ? -1
                            : skill1.getCost(0) > skill2.getCost(0) ? 1
                                    : skill1.getCost(0) < skill2.getCost(0) ? -1
                                            : skill1.getName().compareTo(skill2.getName());
        }
    };
}
