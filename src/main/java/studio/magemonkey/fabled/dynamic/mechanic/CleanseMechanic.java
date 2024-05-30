/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.CleanseMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.dynamic.mechanic;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.units.qual.N;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.api.util.StatusFlag;

import javax.naming.Name;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Cleanses a target of negative potion or status effects
 */
public class CleanseMechanic extends MechanicComponent {
    private static final Set<PotionEffectType> POTIONS = ImmutableSet.of(
            PotionEffectType.BLINDNESS,
            PotionEffectType.getByKey(NamespacedKey.minecraft("nausea")), // CONFUSION/NAUSEA
            PotionEffectType.HUNGER,
            PotionEffectType.LEVITATION,
            PotionEffectType.POISON,
            PotionEffectType.getByKey(NamespacedKey.minecraft("slowness")), // SLOW/SLOWNESS
            PotionEffectType.getByKey(NamespacedKey.minecraft("mining_fatigue")), // SLOW_DIGGING/MINING_FATIGUE
            PotionEffectType.UNLUCK,
            PotionEffectType.WEAKNESS,
            PotionEffectType.WITHER
    );

    private static final String STATUS = "status";
    private static final String POTION = "potion";

    @Override
    public String getKey() {
        return "cleanse";
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
        boolean     worked    = false;
        Set<String> statusSet = new HashSet<>();
        for (String string : settings.getStringList(STATUS)) {
            if (string.equalsIgnoreCase("All")) {
                for (String status : StatusFlag.NEGATIVE) {
                    statusSet.add(status);
                }
                break;
            }
            statusSet.add(string.toLowerCase());
        }
        Set<PotionEffectType> potionSet = new HashSet<>();
        for (String string : settings.getStringList(POTION)) {
            if (string.equalsIgnoreCase("All")) {
                potionSet.addAll(POTIONS);
                break;
            }
            try {
                potionSet.add(Objects.requireNonNull(PotionEffectType.getByName(string.toLowerCase()
                        .replace(' ', '_'))));
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }

        for (LivingEntity target : targets) {
            for (String status : statusSet) {
                if (FlagManager.hasFlag(target, status)) {
                    FlagManager.removeFlag(target, status);
                    worked = true;
                }
            }
            for (PotionEffectType type : potionSet) {
                if (target.hasPotionEffect(type)) {
                    target.removePotionEffect(type);
                    worked = true;
                }
            }
        }
        return worked;
    }
}
