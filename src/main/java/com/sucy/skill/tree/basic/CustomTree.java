package com.sucy.skill.tree.basic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.gui.tool.GUIData;
import com.sucy.skill.gui.tool.GUIType;
import mc.promcteam.engine.mccore.config.parse.DataSection;

import java.util.List;

public class CustomTree extends InventoryTree {
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree
     */
    public CustomTree(SkillAPI api, RPGClass tree) {
        super(api, tree);
    }

    @Override
    protected void arrange(List<Skill> skills) throws SkillTreeException {
        skillSlots.clear();
        DataSection section = SkillAPI.getConfig("gui").getConfig().getSection(GUIType.SKILL_TREE.getPrefix()+tree.getName());
        if (section == null) { return; }

        int rows = Math.max(1, Math.min(section.getInt(GUIData.ROWS, 3), 6));
        DataSection slotsSection = section.getSection(GUIData.SLOTS);
        if (slotsSection == null) { return; }
        for (String key : slotsSection.keys()) {
            int page = Integer.parseInt(key);
            DataSection pageSection = slotsSection.getSection(key);
            for (String skillName : pageSection.keys()) {
                Skill skill = SkillAPI.getSkill(skillName);
                if (skill == null) { continue; }
                skillSlots.put(pageSection.getInt(skillName)+(page-1)*rows*9, skill);
            }
        }
    }
}
