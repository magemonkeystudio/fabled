/**
 * Fabled
 * studio.magemonkey.fabled.tree.basic.LevelTree
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Root class for tree implementations based on levels</p>
 * <p>This is still in development to make it work as intended</p>
 */
public abstract class LevelTree extends InventoryTree {
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public LevelTree(Fabled api, FabledClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     * @throws SkillTreeException
     */
    @Override
    protected void arrange(List<Skill> skills) throws SkillTreeException {
        skillSlots.clear();

        // Get the max level
        int maxLevel = 1;
        for (Skill skill : skills) {
            if (skill.getLevelReq(0) > maxLevel) {
                maxLevel = skill.getLevelReq(0);
            }
        }

        // Break it up into tiers
        int scale = getTierLimit() > 0 ? (maxLevel + getTierLimit() - 1) / getTierLimit() : 1;
        skills.sort(levelComparator);
        HashMap<Integer, List<Skill>> tiers = new HashMap<>();
        int                           tier  = 0;
        while (skills.size() > 0) {
            List<Skill> list = new ArrayList<>();
            tiers.put(tier++, list);
            int max   = tier * scale;
            int count = 0;

            while (skills.size() > 0 && (getPerTierLimit() < 0 || count++ < getPerTierLimit())
                    && skills.get(0).getLevelReq(0) <= max) {
                list.add(skills.remove(0));
            }
        }

        // Arrange the tree
        for (int i = 0; i < tier; i++) {
            List<Skill> list     = tiers.get(i);
            int         maxIndex = 0;

            for (int k = 0; k < i; k++) {
                List<Skill> prevList = tiers.get(k);
                for (int j = 0; j < prevList.size(); j++) {
                    Skill prevSkill = prevList.get(j);
                    for (int l = 0; l < list.size(); l++) {
                        Skill nextSkill = list.get(l);
                        if (nextSkill.getSkillReq() != null
                                && nextSkill.getSkillReq().equalsIgnoreCase(prevSkill.getName())) {
                            list.remove(l);
                            int index = Math.min(Math.max(maxIndex, j), list.size());
                            maxIndex = Math.max(maxIndex + 1, j);
                            list.add(index, nextSkill);
                        }
                    }
                }
            }
            for (int j = 0; j < list.size(); j++) {
                int index;
                if (getPerTierLimit() == 8) { // Vertical tree
                    index = j + i * 9;
                } else {
                    index = j * 9 + i;
                }
                skillSlots.put(index, list.get(j));
                if (index / 9 + 1 > height) {
                    height = index / 9 + 1;
                }
            }
        }
        height = Math.max(1,
                Math.min(Fabled.getConfig("gui")
                        .getConfig()
                        .getInt(GUIType.SKILL_TREE.getPrefix() + tree.getName() + ".rows", height), 6));
    }

    /**
     * Maximum number of skills per tier allowed
     *
     * @return number of skills per tier
     */
    protected abstract int getPerTierLimit();

    /**
     * @return maximum number of tiers allowed
     */
    protected abstract int getTierLimit();

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
