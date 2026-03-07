package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Keyed;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.codex.util.SoundUT;
import studio.magemonkey.codex.util.StringUT;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.shield.ShieldDisplay;
import studio.magemonkey.fabled.shield.ShieldEffect;
import studio.magemonkey.fabled.shield.ShieldManager;

import java.util.List;
import java.util.Locale;

/**
 * © 2026 VoidEdge
 * studio.magemonkey.fabled.dynamic.mechanic.ShieldMechanic
 */
public class ShieldMechanic extends MechanicComponent {
    private static final String NAME       = "name";
    private static final String CLASSIFIER = "classifier";
    private static final String AMOUNT     = "amount";
    private static final String PERCENT    = "percent";
    private static final String DURATION   = "duration";
    private static final String COLOR      = "color";
    private static final String STYLE      = "style";

    private static final String HIT_SOUND   = "hit-sound";
    private static final String BREAK_SOUND = "break-sound";

    @Override
    public String getKey() {
        return "shield";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;

        ShieldManager manager = Fabled.inst().getShieldManager();

        String name       = StringUT.color(settings.getString(NAME, "Shield"));
        String classifier = settings.getString(CLASSIFIER, "default");
        double amount     = parseValues(caster, AMOUNT, level, 5.0);
        double percent    = parseValues(caster, PERCENT, level, 1.0);
        int    ticks      = (int) (parseValues(caster, DURATION, level, 5) * 20);

        ShieldDisplay display = ShieldDisplay.ACTION_BAR;
        try {
            display = ShieldDisplay.valueOf(settings.getString("display", "ACTION_BAR")
                    .toUpperCase(Locale.US)
                    .replace(" ", "_")
                    .replace("-", "_"));
        } catch (NullPointerException | IllegalArgumentException e) {
            Fabled.inst().getLogger().warning("Invalid shield display type: " + settings.getString("display"));
        }

        BarColor color = BarColor.GREEN;
        BarStyle style = BarStyle.SOLID;
        if (display == ShieldDisplay.BOSS_BAR) {
            try {
                color = BarColor.valueOf(settings.getString(COLOR, "GREEN")
                        .toUpperCase(Locale.US));
            } catch (NullPointerException | IllegalArgumentException e) {
                Fabled.inst().getLogger().warning("Invalid boss bar color: " + settings.getString("color"));
            }

            try {
                style = BarStyle.valueOf(settings.getString(STYLE, "SOLID")
                        .replace(" ", "_")
                        .replace("-", "_")
                        .toUpperCase(Locale.US));
            } catch (NullPointerException | IllegalArgumentException e) {
                Fabled.inst().getLogger().warning("Invalid boss bar style: " + settings.getString("style"));
            }
        }

        Keyed hitSound   = null;
        Keyed breakSound = null;

        String hitStr = settings.getString(HIT_SOUND);
        if (hitStr != null && !hitStr.equalsIgnoreCase("none")) {
            try {
                hitSound = SoundUT.getSound(hitStr.toUpperCase(Locale.US).replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                Fabled.inst().getLogger().warning("Invalid hit sound: " + hitStr);
            }
        }

        String breakStr = settings.getString(BREAK_SOUND);
        if (breakStr != null && !breakStr.equalsIgnoreCase("none")) {
            try {
                breakSound = SoundUT.getSound(breakStr.toUpperCase(Locale.US).replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                Fabled.inst().getLogger().warning("Invalid break sound: " + breakStr);
            }
        }

        for (LivingEntity target : targets) {
            ShieldEffect effect =
                    new ShieldEffect(name, classifier, amount, percent);

            effect.setBarColor(color);
            effect.setBarStyle(style);
            effect.setDisplayLocation(display);

            effect.setHitSound(hitSound);
            effect.setBreakSound(breakSound);

            manager.addEffect(target, effect, ticks);
        }

        return !targets.isEmpty();
    }
}
