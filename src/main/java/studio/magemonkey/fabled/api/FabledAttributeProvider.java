package studio.magemonkey.fabled.api;

import studio.magemonkey.codex.registry.attribute.AttributeProvider;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class FabledAttributeProvider implements AttributeProvider {
    private final Fabled plugin;

    @Override
    public double scaleAttribute(String name, LivingEntity entity, double value) {
        if (!(entity instanceof Player)) return value;

        Player     player = (Player) entity;
        PlayerData data   = Fabled.getPlayerData(player);
        if (data == null) return value;

        return data.scaleStat(name, value);
    }
}
