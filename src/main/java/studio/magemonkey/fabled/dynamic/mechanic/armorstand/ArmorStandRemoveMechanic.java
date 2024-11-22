package studio.magemonkey.fabled.dynamic.mechanic.armorstand;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.armorstand.ArmorStandData;
import studio.magemonkey.fabled.api.armorstand.ArmorStandManager;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;

/**
 * Removes an armor stand
 */
public class ArmorStandRemoveMechanic extends MechanicComponent {
    private static final String KEY = "key";

    @Override
    public String getKey() {
        return "armor stand remove";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key = settings.getString(KEY, skill.getName());

        for (LivingEntity target : targets) {
            ArmorStandData data = ArmorStandManager.getArmorStandData(target);
            if (data == null) continue;
            data.remove(key);
        }
        return !targets.isEmpty();
    }
}
