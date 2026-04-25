package studio.magemonkey.fabled.api.displayentity;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.data.formula.Formula;
import studio.magemonkey.fabled.data.formula.value.CustomValue;

/**
 * Holds time-varying formula expressions for each property of a
 * {@link org.bukkit.entity.Display} entity's {@link Transformation}.
 *
 * <p>Each property accepts any expression understood by {@link Formula},
 * with two bound variables:
 * <ul>
 *   <li>{@code t} – the elapsed tick count since the entity was spawned</li>
 *   <li>{@code l} – the skill level</li>
 * </ul>
 * A plain number (e.g. {@code "1"} or {@code "2.5"}) behaves identically
 * to a static value. A level-scaled expression like {@code "1+0.5*(l-1)"}
 * replicates the old base+scale pattern. A time-driven expression like
 * {@code "t*5"} rotates the entity over time.</p>
 *
 * <p>Settings read (all plain string keys, not the {@code -base}/{@code -scale}
 * suffixed variants used by {@code parseValues}):</p>
 * <ul>
 *   <li>{@code scale-x}, {@code scale-y}, {@code scale-z} – defaults to {@code "1"}</li>
 *   <li>{@code translate-x}, {@code translate-y}, {@code translate-z} – defaults to {@code "0"}</li>
 *   <li>{@code left-rotation-x/y/z} – degrees, defaults to {@code "0"}</li>
 *   <li>{@code right-rotation-x/y/z} – degrees, defaults to {@code "0"}</li>
 * </ul>
 */
public class DisplayEntityTransform {

    /** Shared formula variables: index 0 = t (tick), index 1 = l (level). */
    private static final CustomValue[] VALUES = {
            new CustomValue("t"),
            new CustomValue("l")
    };

    private final Formula scaleX;
    private final Formula scaleY;
    private final Formula scaleZ;

    private final Formula translateX;
    private final Formula translateY;
    private final Formula translateZ;

    private final Formula leftRotX;
    private final Formula leftRotY;
    private final Formula leftRotZ;

    private final Formula rightRotX;
    private final Formula rightRotY;
    private final Formula rightRotZ;

    public DisplayEntityTransform(Settings settings) {
        scaleX = formula(settings, "scale-x", "1");
        scaleY = formula(settings, "scale-y", "1");
        scaleZ = formula(settings, "scale-z", "1");

        translateX = formula(settings, "translate-x", "0");
        translateY = formula(settings, "translate-y", "0");
        translateZ = formula(settings, "translate-z", "0");

        leftRotX = formula(settings, "left-rotation-x", "0");
        leftRotY = formula(settings, "left-rotation-y", "0");
        leftRotZ = formula(settings, "left-rotation-z", "0");

        rightRotX = formula(settings, "right-rotation-x", "0");
        rightRotY = formula(settings, "right-rotation-y", "0");
        rightRotZ = formula(settings, "right-rotation-z", "0");
    }

    private static Formula formula(Settings settings, String key, String defaultValue) {
        return new Formula(settings.getString(key, defaultValue), VALUES);
    }

    /**
     * Evaluates all formulas at the given tick and level and assembles a
     * {@link Transformation}.
     *
     * @param tick  elapsed tick count (the {@code t} variable)
     * @param level skill level (the {@code l} variable)
     * @return the computed {@link Transformation}
     */
    public Transformation compute(int tick, int level) {
        float sx = (float) scaleX.compute(tick, level);
        float sy = (float) scaleY.compute(tick, level);
        float sz = (float) scaleZ.compute(tick, level);

        float tx = (float) translateX.compute(tick, level);
        float ty = (float) translateY.compute(tick, level);
        float tz = (float) translateZ.compute(tick, level);

        float lrX = (float) Math.toRadians(leftRotX.compute(tick, level));
        float lrY = (float) Math.toRadians(leftRotY.compute(tick, level));
        float lrZ = (float) Math.toRadians(leftRotZ.compute(tick, level));

        float rrX = (float) Math.toRadians(rightRotX.compute(tick, level));
        float rrY = (float) Math.toRadians(rightRotY.compute(tick, level));
        float rrZ = (float) Math.toRadians(rightRotZ.compute(tick, level));

        return new Transformation(
                new Vector3f(tx, ty, tz),
                new Quaternionf().rotationXYZ(lrX, lrY, lrZ),
                new Vector3f(sx, sy, sz),
                new Quaternionf().rotationXYZ(rrX, rrY, rrZ)
        );
    }
}
