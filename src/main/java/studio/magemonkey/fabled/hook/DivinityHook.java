package studio.magemonkey.fabled.hook;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.codex.util.DataUT;
import studio.magemonkey.divinity.manager.effects.main.AdjustStatEffect;
import studio.magemonkey.divinity.stats.EntityStats;
import studio.magemonkey.divinity.stats.items.ItemStats;
import studio.magemonkey.divinity.stats.items.api.ItemLoreStat;
import studio.magemonkey.divinity.stats.items.attributes.api.TypedStat;
import studio.magemonkey.fabled.api.enums.Operation;

import java.util.function.DoubleUnaryOperator;

public class DivinityHook {
    private static final NamespacedKey KEY_MODULE  = NamespacedKey.fromString("prorpgitems:qrpg_item_module");
    private static final NamespacedKey KEY_MODULE2 = NamespacedKey.fromString("quantumrpg:qrpg_item_module");

    public static boolean isDivinity(ItemStack item) {
        String data = DataUT.getStringData(item, KEY_MODULE2);
        if (data == null) data = DataUT.getStringData(item, KEY_MODULE);

        return data != null;
    }

    /**
     * Clamps a StatMechanic modifier amount so the resulting stat total does not exceed
     * Divinity's configured cap for the given stat key.
     * <p>
     * Returns the original amount unchanged if the stat is unknown, has no cap (-1),
     * or the operation is unrecognized.
     * <p>
     * NOTE: this method must only be called when Divinity is confirmed active.
     *
     * @param key       Divinity stat key (e.g. "critical_rate")
     * @param operation Fabled Operation (ADD_NUMBER or MULTIPLY_PERCENTAGE)
     * @param amount    Modifier amount from the mechanic
     * @param player    Target player
     * @return clamped amount that will not push the stat past its Divinity cap
     */
    public static double clampStatAmount(String key, Operation operation, double amount, Player player) {
        TypedStat.Type statType = TypedStat.Type.getByName(key);
        if (statType == null) return amount;

        TypedStat stat = ItemStats.getStat(statType);
        if (stat == null) return amount;

        double cap = stat.getCapability();
        if (cap < 0) return amount; // unlimited cap

        double currentValue = EntityStats.get(player).getItemStat(statType, true);

        switch (operation) {
            case ADD_NUMBER:
                // Clamp: currentValue + amount <= cap  →  amount <= cap - currentValue
                return Math.min(amount, Math.max(0, cap - currentValue));
            case MULTIPLY_PERCENTAGE:
                // New total = currentValue * amount; clamp: amount <= cap / currentValue
                // Ensure we don't reduce the stat (amount >= 1.0)
                if (currentValue <= 0) return amount;
                if (currentValue >= cap) return 1.0; // already at/above cap, no-op multiplier
                return Math.max(1.0, Math.min(amount, cap / currentValue));
            default:
                return amount;
        }
    }

    /**
     * Applies a temporary stat modifier to a non-player entity via Divinity's AdjustStatEffect.
     * Returns the effect as Object so callers don't need to reference Divinity types directly.
     * Only call when Divinity is confirmed active.
     *
     * @param seconds negative value = permanent
     * @return the created AdjustStatEffect, or null if stat key is unknown / operation invalid
     */
    public static Object applyStatToMob(LivingEntity target, LivingEntity caster,
                                        String key, String operation, double amount, double seconds) {
        ItemLoreStat<?> stat = ItemStats.getAttribute(key);
        if (stat == null) return null;

        DoubleUnaryOperator operator;
        switch (operation) {
            case "ADD_NUMBER":
                operator = v -> v + amount;
                break;
            case "MULTIPLY_PERCENTAGE":
                operator = v -> v * amount;
                break;
            default:
                return null;
        }

        AdjustStatEffect effect =
                new AdjustStatEffect.Builder(seconds)
                        .withCaster(caster)
                        .withAdjust(stat, operator)
                        .build();
        effect.applyTo(target);
        return effect;
    }

    /**
     * Removes a stat effect previously returned by {@link #applyStatToMob} from the target's EntityStats.
     * Only call when Divinity is confirmed active.
     */
    public static void removeStatFromMob(LivingEntity target, Object effect) {
        if (effect == null) return;
        EntityStats.get(target).removeEffect((AdjustStatEffect) effect);
    }
}
