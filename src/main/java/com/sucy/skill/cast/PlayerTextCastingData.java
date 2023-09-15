package com.sucy.skill.cast;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import mc.promcteam.engine.utils.StringUT;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerTextCastingData {
    private final PlayerData player;
    private final String[]   skills = new String[9];
    private boolean          casting = false;
    private int oldSlot;

    public PlayerTextCastingData(PlayerData data) {
        this.player = data;
    }

    public void load(DataSection config) {
        if (config == null) return;
        for (int i = 0; i < skills.length; i++) {
            String skillName = config.getString(String.valueOf(i));
            remove(skillName);
            skills[i] = skillName;
            validate();
        }
    }

    public void validate() {
        for (int i = 0; i < skills.length; i++) {
            if (!isValid(skills[i])) skills[i] = null;
        }
        int castSlot = SkillAPI.getSettings().getCastSlot();
        if (skills[castSlot] != null) {
            ArrayList<String> list = new ArrayList<>(10);
            Collections.addAll(list, skills);
            for (int i = 0; i < list.size(); i++) {
                if (i != castSlot && list.get(i) == null) {
                    list.remove(i);
                    break;
                }
            }
            if (list.size() == 9) list.set(castSlot, null);
            else list.add(castSlot, null);
            for (int i = 0; i < skills.length; i++) {
                skills[i] = list.get(i);
            }
        }
    }

    public void save(DataSection config) {
        for (int i = 0; i < skills.length; i++) {
            if (skills[i] == null) continue;
            config.set(String.valueOf(i), skills[i]);
        }
    }

    private void remove(String skillName) {
        if (skillName == null) return;
        for (int i = 0; i < skills.length; i++) {
            if (skills[i] != null && skills[i].equalsIgnoreCase(skillName)) skills[i] = null;
        }
    }

    public boolean isEmpty() {
        for (String skill : skills) {
            if (skill != null) return false;
        }
        return true;
    }

    @Nullable
    public String getSkill(int slot) {
        return skills[slot];
    }

    public void assign(@Nullable String skillName, int slot) {
        if (!isValid(skillName)) return;
        if (slot == SkillAPI.getSettings().getCastSlot()) return;
        remove(skillName);
        skills[slot] = skillName;
    }

    private boolean isValid(@Nullable String skillName) {
        if (skillName == null) return true;
        if (!player.hasSkill(skillName)) return false;
        PlayerSkill playerSkill = player.getSkill(skillName);
        return playerSkill != null && playerSkill.isUnlocked() && playerSkill.getData().canCast();
    }

    public boolean onUnlock(String skillName) {
        if (skillName == null || !isValid(skillName)) return false;
        for (String skill : skills) {
            if (skill != null && skill.equalsIgnoreCase(skillName)) return false;
        }
        for (int i = 0; i < skills.length; i++) {
            if (i == SkillAPI.getSettings().getCastSlot()) continue;
            if (skills[i] == null) {
                assign(skillName, i);
                return true;
            }
        }
        return false;
    }

    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        String skillFormat = StringUT.color(SkillAPI.getSettings().getMessageFormatSkill());
        String separator = StringUT.color(SkillAPI.getSettings().getMessageFormatSeparator());
        boolean first = true;
        int i = 0;
        for (; i < 9; i++) {
            String skill = getSkill(i);
            if (skill != null) {
                if (first) first = false;
                else stringBuilder.append(separator);
                stringBuilder.append(skillFormat
                        .replace("%number%", String.valueOf(i+1))
                        .replace("%skill%", skill));
            }
        }
        return stringBuilder.toString();
    }

    public boolean isCasting() {
        return casting;
    }

    public void setState(boolean casting) {
        this.casting = casting;
        PlayerInventory inventory = player.getPlayer().getInventory();
        if (casting) {
            oldSlot = inventory.getHeldItemSlot();
            inventory.setHeldItemSlot(SkillAPI.getSettings().getCastSlot());
        } else {
            inventory.setHeldItemSlot(oldSlot);
        }
    }

    public boolean cast(int slot) {
        setState(false);
        PlayerSkill skill = player.getSkill(skills[slot]);
        if (skill != null) return player.cast(skill);
        return false;
    }
}
