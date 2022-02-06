package com.sucy.skill.hook.mimic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.level.BukkitLevelSystem;
import ru.endlesscode.mimic.level.ExpLevelConverter;

public class ProSkillApiLevelSystem extends BukkitLevelSystem {

    public ProSkillApiLevelSystem(@NotNull Player player) {
        super(player);
    }

    @NotNull
    @Override
    public ExpLevelConverter getConverter() {
        return ProSkillApiConverter.getInstance();
    }

    @Override
    public int getLevel() {
        PlayerClass playerClass = getPlayerClass();
        return playerClass != null ? playerClass.getLevel() : 1;
    }

    @Override
    public void setLevel(int level) {
        PlayerClass playerClass = getPlayerClass();
        if (playerClass != null) playerClass.setLevel(level);
    }

    @Override
    public double getExp() {
        PlayerClass playerClass = getPlayerClass();
        return playerClass != null ? playerClass.getExp() : 0.0;
    }

    @Override
    public void setExp(double exp) {
        PlayerClass playerClass = getPlayerClass();
        if (playerClass != null) playerClass.setExp(exp);
    }

    @Override
    public void takeLevels(int lvlAmount) {
        PlayerClass playerClass = getPlayerClass();
        if (playerClass != null) {
            int newLevel = Math.max(getLevel() - lvlAmount, 1);
            double fractionalExp = getFractionalExp(); // Save fractional exp to restore it later
            setLevel(newLevel);
            setFractionalExp(fractionalExp);
        }
    }

    @Override
    public void giveLevels(int lvlAmount) {
        PlayerClass playerClass = getPlayerClass();
        if (playerClass != null) playerClass.giveLevels(lvlAmount);
    }

    @Override
    public void takeExp(double expAmount) {
        PlayerClass playerClass = getPlayerClass();
        if (playerClass != null) {
            double percent = expAmount / playerClass.getRequiredExp();
            playerClass.loseExp(percent);
        }
    }

    @Override
    public void giveExp(double expAmount) {
        PlayerClass playerClass = getPlayerClass();
        if (playerClass != null) playerClass.giveExp(expAmount, ExpSource.SPECIAL);
    }

    private @Nullable PlayerClass getPlayerClass() {
        return SkillAPI.getPlayerData(getPlayer()).getMainClass();
    }
}
