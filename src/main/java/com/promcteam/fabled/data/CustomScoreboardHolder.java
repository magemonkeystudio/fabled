package com.promcteam.fabled.data;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.util.PlaceholderUtil;
import com.promcteam.codex.mccore.scoreboard.StatHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomScoreboardHolder implements StatHolder {
    private List<String> format;
    private UUID         id;

    public CustomScoreboardHolder(Player player) {
        this.id = player.getUniqueId();
        format = Fabled.getSettings().getScoreboardFormat();
    }

    @Override
    public List<String> getNames() {
        Player player = Bukkit.getPlayer(id);
        List<String> formattedLines = PlaceholderUtil.colorizeAndReplace(format, player)
                .stream()
//                .map(s -> {
//                    return
//                            s.length() > 16 ? s.substring(0, 16) :
//                            s;
//                })
                .collect(Collectors.toList());

        return formattedLines;
    }

    @Override
    public List<Integer> getValues() {
        // Return a list just counting down from to zero from the number of lines
        List<Integer> values = new ArrayList<>();
        for (int i = format.size(); i > 0; i--) {
            values.add(i);
        }
        return values;
    }
}
