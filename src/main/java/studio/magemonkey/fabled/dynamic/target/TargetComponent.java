package studio.magemonkey.fabled.dynamic.target;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.target.TargetHelper;
import studio.magemonkey.fabled.dynamic.ComponentType;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.EffectComponent;
import studio.magemonkey.fabled.dynamic.TempEntity;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.target.TargetComponent
 */
public abstract class TargetComponent extends EffectComponent {

    private static final   String ALLY         = "group";
    private static final   String WALL         = "wall";
    private static final   String CASTER       = "caster";
    protected static final String MAX          = "max";
    private static final   String INVULNERABLE = "invulnerable";

    boolean       everyone;
    boolean       allies;
    boolean       throughWall;
    boolean       invulnerable;
    IncludeCaster self;

    @Override
    public ComponentType getType() {
        return ComponentType.TARGET;
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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        final List<LivingEntity> list = getTargets(caster, level, targets);
        return (!list.isEmpty() && executeChildren(caster, level, list, force));
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);

        final String group = settings.getString(ALLY, "enemy").toLowerCase();
        everyone = group.equals("both");
        allies = group.equals("ally");
        throughWall = settings.getString(WALL, "false").equalsIgnoreCase("true");
        invulnerable = settings.getString(INVULNERABLE, "false").equalsIgnoreCase("true");
        self = IncludeCaster.valueOf(settings.getString(CASTER, "false").toUpperCase(Locale.US).replace(' ', '_'));
    }

    abstract List<LivingEntity> getTargets(final LivingEntity caster,
                                           final int level,
                                           final List<LivingEntity> targets);

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        Supplier<List<LivingEntity>> supplier = () -> getTargets(caster, level, targetSupplier.get());
        super.playPreview(onPreviewStop, caster, level, supplier);
        playChildrenPreviews(onPreviewStop, caster, level, supplier);
    }

    List<LivingEntity> determineTargets(final LivingEntity caster,
                                        final int level,
                                        final List<LivingEntity> from,
                                        final Function<LivingEntity, List<LivingEntity>> conversion) {

        final double max = parseValues(caster, MAX, level, 99);

        final List<LivingEntity> list = new ArrayList<>();
        from.forEach(target -> {
            final List<LivingEntity> found = conversion.apply(target);
            int                      count = 0;
            for (LivingEntity entity : found) {
                if (count >= max) break;
                if (isValidTarget(caster, target, entity) || (self.equals(IncludeCaster.IN_AREA) && caster == entity)) {
                    list.add(entity);
                    count++;
                }
            }
        });
        if (self.equals(IncludeCaster.TRUE)) list.add(caster);
        return list;
    }

    boolean isValidTarget(final LivingEntity caster, final LivingEntity from, final LivingEntity target) {
        if (Fabled.getMeta(target, MechanicListener.ARMOR_STAND) != null) return false;
        if (target instanceof TempEntity) return true;
        if (target.isInvulnerable() && !invulnerable) return false;
        if (target instanceof Player && (((Player) target).getGameMode() == GameMode.SPECTATOR
                || ((Player) target).getGameMode() == GameMode.CREATIVE)) return false;

        return target != caster && Fabled.getSettings().isValidTarget(target) && (throughWall
                || !TargetHelper.isObstructed(from.getEyeLocation(), target.getEyeLocation()))
                && (everyone || allies == Fabled.getSettings().isAlly(caster, target));
    }

    public enum IncludeCaster {
        TRUE, FALSE, IN_AREA
    }
}
