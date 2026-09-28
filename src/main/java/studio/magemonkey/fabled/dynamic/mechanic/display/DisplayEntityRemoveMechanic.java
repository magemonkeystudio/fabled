package studio.magemonkey.fabled.dynamic.mechanic.display;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityData;
import studio.magemonkey.fabled.api.displayentity.DisplayEntityManager;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;

/**
 * Removes a Display entity previously spawned by the {@link DisplayEntityMechanic} using its key.
 * Requires Minecraft 1.19.4 or later.
 */
public class DisplayEntityRemoveMechanic extends MechanicComponent {
    private static final String KEY = "key";

    @Override
    public String getKey() {
        return "display entity remove";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!DisplayEntityManager.isSupported()) return false;

        String key = settings.getString(KEY, skill.getName());

        for (LivingEntity target : targets) {
            DisplayEntityData data = DisplayEntityManager.getDisplayEntityData(target);
            if (data == null) continue;
            data.remove(key);
        }
        return !targets.isEmpty();
    }
}
