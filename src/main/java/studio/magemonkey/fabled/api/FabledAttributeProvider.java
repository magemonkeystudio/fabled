package studio.magemonkey.fabled.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.registry.attribute.AttributeProvider;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;

public class FabledAttributeProvider implements AttributeProvider {
    @Override
    public double scaleAttribute(String name, LivingEntity entity, double value) {
        if (!(entity instanceof Player)) return value;

        Player     player = (Player) entity;
        PlayerData data   = Fabled.getData(player);
        if (data == null) return value;

        return data.scaleStat(name, value);
    }
}
