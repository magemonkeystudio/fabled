package studio.magemonkey.fabled.hook.mimic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.level.ExpLevelConverter;
import studio.magemonkey.fabled.Fabled;

public class FabledConverter implements ExpLevelConverter {

    @Nullable
    private static FabledConverter instance;

    public static @NotNull ExpLevelConverter getInstance() {
        if (instance == null) {
            instance = new FabledConverter();
        }
        return instance;
    }

    @Override
    public double getExpToReachLevel(int level) {
        return getExpToReachNextLevel(level - 1);
    }

    @Override
    public double getExpToReachNextLevel(int level) {
        return level > 0 ? Fabled.getSettings().getRequiredExp(level) : -1.0;
    }
}
