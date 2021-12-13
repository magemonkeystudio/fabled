package com.sucy.skill.hook.mimic;

import com.sucy.skill.SkillAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.level.ExpLevelConverter;

public class ProSkillApiConverter implements ExpLevelConverter {

    @Nullable
    private static ProSkillApiConverter instance;

    public static @NotNull ExpLevelConverter getInstance() {
        if (instance == null) {
            instance = new ProSkillApiConverter();
        }
        return instance;
    }

    @Override
    public double getExpToReachLevel(int level) {
        return getExpToReachNextLevel(level - 1);
    }

    @Override
    public double getExpToReachNextLevel(int level) {
        return level > 0 ? SkillAPI.getSettings().getRequiredExp(level) : -1.0;
    }
}
