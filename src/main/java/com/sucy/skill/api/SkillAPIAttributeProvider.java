package com.sucy.skill.api;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import lombok.RequiredArgsConstructor;
import mc.promcteam.engine.registry.attribute.AttributeProvider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SkillAPIAttributeProvider implements AttributeProvider {
    private final SkillAPI plugin;

    @Override
    public double scaleAttribute(String name, LivingEntity entity, double value) {
        if (!(entity instanceof Player)) return value;

        Player     player = (Player) entity;
        PlayerData data   = plugin.getPlayerData(player);
        if (data == null) return value;

        return data.scaleStat(name, value);
    }
}
