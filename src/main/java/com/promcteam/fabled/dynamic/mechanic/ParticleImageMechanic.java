package com.promcteam.fabled.dynamic.mechanic;

import com.promcteam.fabled.api.particle.EffectImage;
import com.promcteam.fabled.api.particle.target.EntityTarget;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class ParticleImageMechanic extends MechanicComponent {
    private static final String DURATION = "duration";
    private static final String KEY      = "effect-key";

    @Override
    public String getKey() {
        return "particle image";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key      = settings.getString(KEY, skill.getName());
        int    duration = (int) (20 * parseValues(caster, DURATION, level, 5));

        EffectImage image = new EffectImage(settings);
        for (LivingEntity target : targets)
            image.start(new EntityTarget(target), key, duration, level);

        return !targets.isEmpty();
    }
}
