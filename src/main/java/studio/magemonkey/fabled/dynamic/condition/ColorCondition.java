package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.material.Colorable;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.Locale;

/**
 * Checks if the target ({@link Colorable}) has the specified color.
 * Colorable entities at the time of writing are {@link Sheep} and {@link org.bukkit.entity.Shulker}
 */
public class ColorCondition extends ConditionComponent {
    private static final String COLOR = "color";

    private DyeColor color;

    @Override
    public String getKey() {return "color";}

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        color = DyeColor.valueOf(settings.getString(COLOR, "white").toUpperCase(Locale.US));
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        return target instanceof Colorable && ((Colorable) target).getColor().equals(color);
    }
}
