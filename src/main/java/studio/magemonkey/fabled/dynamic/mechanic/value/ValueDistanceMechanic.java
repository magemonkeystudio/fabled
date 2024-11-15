package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.value.ValueDistanceMechanic
 */
public class ValueDistanceMechanic extends MechanicComponent {
    private static final String KEY  = "key";
    private static final String SAVE = "save";

    @Override
    public String getKey() {
        return "value distance";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (!settings.has(KEY) || !(caster instanceof Player)) {
            return false;
        }

        String   key  = settings.getString(KEY);
        CastData data = DynamicSkill.getCastData(caster);
        data.put(key, targets.get(0).getLocation().distance(caster.getLocation()));
        if (settings.getBool(SAVE, false))
            Fabled.getData((OfflinePlayer) caster).setPersistentData(key, data.getRaw(key));
        return true;
    }
}
