package studio.magemonkey.fabled.dynamic.mechanic;

import studio.magemonkey.codex.api.damage.WeaponStatsMode;
import studio.magemonkey.fabled.api.Settings;

import java.util.Locale;

/**
 * Reads how a damage mechanic combines with the caster's weapon stats.
 * <p>
 * The editor writes {@code weapon-stats} (add, replace, ignore, raw) next to the older
 * {@code ignore-divinity} boolean. {@code weapon-stats} wins when it holds one of the four values (any case,
 * surrounding spaces ignored). Otherwise, including an unknown value, the boolean decides as it always has:
 * true is IGNORE, false or absent is ADD. An unknown value is not logged and never throws.
 */
final class WeaponStatsSetting {
    static final String WEAPON_STATS    = "weapon-stats";
    static final String IGNORE_DIVINITY = "ignore-divinity";

    private WeaponStatsSetting() {}

    static WeaponStatsMode read(Settings settings) {
        String value = settings.getString(WEAPON_STATS, null);
        if (value != null) {
            switch (value.trim().toLowerCase(Locale.ROOT)) {
                case "add":
                    return WeaponStatsMode.ADD;
                case "replace":
                    return WeaponStatsMode.REPLACE;
                case "ignore":
                    return WeaponStatsMode.IGNORE;
                case "raw":
                    return WeaponStatsMode.RAW;
                default:
                    break;
            }
        }
        return settings.getBool(IGNORE_DIVINITY, false) ? WeaponStatsMode.IGNORE : WeaponStatsMode.ADD;
    }
}
