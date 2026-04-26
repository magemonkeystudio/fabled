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
 * {@code "t*5"} rotates the entity over time.
 *
 * <p>Settings read (all plain string keys, not the {@code -base}/{@code -scale}
 * suffixed variants used by {@code parseValues}):
 * <ul>
 *   <li>{@code scale-x}, {@code scale-y}, {@code scale-z} – defaults to {@code "1"}</li>
 *   <li>{@code translate-x}, {@code translate-y}, {@code translate-z} – defaults to {@code "0"}</li>
 *   <li>{@code left-rotation-x/y/z} – degrees, defaults to {@code "0"}</li>
 *   <li>{@code right-rotation-x/y/z} – degrees, defaults to {@code "0"}</li>
 *   <li>{@code center-rotation} – boolean, defaults to {@code false}.  When {@code true},
 *       the translation is automatically adjusted each tick so that the visual centre of the
 *       display entity (the centre of its unit-cube in model space) stays at the entity's
 *       origin regardless of rotation.  The user-configured {@code translate-x/y/z} values
 *       are then treated as additive offsets from that centre.</li>
 *   <li>{@code forward}, {@code upward}, {@code right} – world-space offset formulas applied
 *       each tick relative to the <em>cast-time</em> facing direction.  A plain number
 *       ({@code "1"}) gives a static offset; a time expression ({@code "sin(t*0.1)*2"})
 *       animates the entity's world position over time.  These are independent of the
 *       {@code forward-base}/{@code forward-scale} static offset variant read by
 *       {@code parseValues}, and are evaluated in addition to it.</li>
 * </ul>
 */
public class DisplayEntityTransform {

    /**
     * Shared formula variables: index 0 = t (tick), index 1 = l (level).
     */
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

    private final boolean centerRotation;

    private final Formula offsetForward;
    private final Formula offsetUpward;
    private final Formula offsetRight;

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

        centerRotation = settings.getBool("center-rotation", false);

        offsetForward = formula(settings, "forward", "0");
        offsetUpward  = formula(settings, "upward",  "0");
        offsetRight   = formula(settings, "right",   "0");
    }

    private static Formula formula(Settings settings, String key, String defaultValue) {
        return new Formula(settings.getString(key, defaultValue), VALUES);
    }

    /**
     * Evaluates the world-space offset formulas at the given tick and level.
     *
     * <p>The returned values are dimensioned along the three axes of the
     * entity that cast the skill:
     * <ul>
     *   <li>index 0 – forward (along the caster's horizontal facing direction)</li>
     *   <li>index 1 – upward (vertical)</li>
     *   <li>index 2 – right (perpendicular, horizontal)</li>
     * </ul>
     * A formula of {@code "0"} (the default) produces no offset.  A
     * time-varying formula such as {@code "sin(t*0.1)*2"} animates the
     * entity's world position relative to the direction the caster was
     * looking <em>when the skill was cast</em>.
     *
     * @param tick  elapsed tick count (the {@code t} variable)
     * @param level skill level (the {@code l} variable)
     * @return {@code double[3]} containing {forward, upward, right} offsets
     */
    public double[] computeWorldOffset(int tick, int level) {
        return new double[]{
                offsetForward.compute(tick, level),
                offsetUpward.compute(tick, level),
                offsetRight.compute(tick, level)
        };
    }

    /**
     * Evaluates all formulas at the given tick and level and assembles a
     * {@link Transformation}.
     *
     * <p>When {@code center-rotation} is enabled the translation is automatically
     * adjusted so that the visual centre of the unit-cube model stays at the
     * entity's origin.  The formula is:
     * <pre>T_final = T_user − LR × (S × (RR × [0.5, 0.5, 0.5]))</pre>
     * where LR / RR are the left / right rotation quaternions and S is the
     * diagonal scale matrix.  This keeps the block centred at the entity
     * position for every rotation angle, including animated ones.
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

        Quaternionf leftQ  = new Quaternionf().rotationXYZ(lrX, lrY, lrZ);
        Quaternionf rightQ = new Quaternionf().rotationXYZ(rrX, rrY, rrZ);

        if (centerRotation) {
            // Compute LR × (S × (RR × [0.5, 0.5, 0.5])) and subtract it from
            // the user-supplied translation so the visual centre of the block
            // sits at the entity origin (+ user translate).
            Vector3f center = new Vector3f(0.5f, 0.5f, 0.5f);
            rightQ.transform(center);          // RR * [0.5, 0.5, 0.5]
            center.mul(sx, sy, sz);            // S  * (RR * center)
            leftQ.transform(center);           // LR * (S * (RR * center))
            tx -= center.x;
            ty -= center.y;
            tz -= center.z;
        }

        return new Transformation(
                new Vector3f(tx, ty, tz),
                leftQ,
                new Vector3f(sx, sy, sz),
                rightQ
        );
    }
}
