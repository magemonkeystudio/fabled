package com.sucy.skill.hook.mimic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.classes.BukkitClassSystem;

import java.util.List;
import java.util.stream.Collectors;

public class ProSkillApiClassSystem extends BukkitClassSystem {

    public ProSkillApiClassSystem(@NotNull Player player) {
        super(player);
    }

    @NotNull
    @Override
    public List<String> getClasses() {
        return getPlayerData().getClasses()
                .stream()
                .map(playerClass -> playerClass.getData().getName())
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public String getPrimaryClass() {
        PlayerClass mainClass = getPlayerData().getMainClass();
        return mainClass != null ? mainClass.getData().getName() : null;
    }

    private PlayerData getPlayerData() {
        return SkillAPI.getPlayerData(getPlayer());
    }
}
