/**
 * Fabled
 * studio.magemonkey.fabled.api.util.BuffData
 * <p>
 * The MIT License (MIT)
 * <p>
 * © 2026 VoidEdge
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
package studio.magemonkey.fabled.api.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.BuffExpiredEvent;
import studio.magemonkey.fabled.log.LogType;
import studio.magemonkey.fabled.log.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents buffs set on an entity
 */
public class BuffData {
    private final Map<String, Map<String, Buff>> buffs = new HashMap<>();

    private final LivingEntity entity;

    /**
     * Initializes new buff data for the entity
     *
     * @param entity entity to initialize for
     */
    public BuffData(LivingEntity entity) {
        this.entity = entity;
    }

    public double getMultiplier(final BuffType buffType, final String category) {
        return category == null || category.isEmpty() ? getMultiplier(buffType.getLocalizedName())
                : getMultiplier(buffType.getLocalizedName(), buffType.getLocalizedName() + "_" + category);
    }

    public double getFlatBonus(final BuffType buffType, final String category) {
        return category == null || category.isEmpty() ? getFlatBonus(buffType.getLocalizedName())
                : getFlatBonus(buffType.getLocalizedName(), buffType.getLocalizedName() + "_" + category);
    }

    /**
     * Adds a buff to the buff collection. If a buff already exists with the same
     * key, it will be overwritten.
     *
     * @param type  type of buff to add
     * @param buff  buff details
     * @param ticks how long to apply the buff for
     *
     * @deprecated Buff types are more abstract now, so {@link BuffData#addBuff(String, Buff, int)} should be preferred
     */
    @Deprecated
    public void addBuff(final BuffType type, final Buff buff, final int ticks) {
        addBuff(type.getLocalizedName(), buff, ticks);
    }

    /**
     * Adds a buff to the buff collection. If a buff already exists with the same
     * key, it will be overwritten.
     *
     * @param type  type of buff to add
     * @param buff  buff details
     * @param ticks how long to apply the buff for
     */
    public void addBuff(final String type, final Buff buff, final int ticks) {
        doAddBuff(type, buff, ticks);
    }

    /**
     * Adds a buff to the buff collection. If a buff already exists with the same
     * key, it will be overwritten.
     *
     * @param type     type of buff to add
     * @param category sub category of the type to apply (e.g. damage classification)
     * @param buff     buff details
     * @param ticks    how long to apply the buff for
     *
     * @deprecated Buff types are more abstract now, so {@link BuffData#addBuff(String, Buff, int)} should be preferred
     */
    @Deprecated
    public void addBuff(final BuffType type, final String category, final Buff buff, final int ticks) {
        addBuff(type.getLocalizedName(), category, buff, ticks);
    }

    /**
     * Adds a buff to the buff collection. If a buff already exists with the same
     * key, it will be overwritten.
     *
     * @param type     type of buff to add
     * @param category sub category of the type to apply (e.g. damage classification)
     * @param buff     buff details
     * @param ticks    how long to apply the buff for
     */
    public void addBuff(final String type, final String category, final Buff buff, final int ticks) {
        doAddBuff(type + (category != null ? "_" + category : ""), buff, ticks);
    }

    private void doAddBuff(final String type, final Buff buff, final int ticks) {
        final Map<String, Buff> typeBuffs = buffs.computeIfAbsent(type, t -> new HashMap<>());
        final Buff              conflict  = typeBuffs.remove(buff.getKey());
        if (conflict != null) conflict.task.cancel();

        typeBuffs.put(buff.getKey(), buff);
        buff.task = Fabled.schedule(new BuffTask(type, buff.getKey()), ticks);
    }

    /**
     * @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead
     */
    @Deprecated
    public void addDamageBuff(Buff buff, int ticks) {
        addBuff(BuffType.DAMAGE.getLocalizedName(), buff, ticks);
    }

    /**
     * @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead
     */
    @Deprecated
    public void addDefenseBuff(Buff buff, int ticks) {
        addBuff(BuffType.DEFENSE.getLocalizedName(), buff, ticks);
    }

    /**
     * @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead
     */
    @Deprecated
    public void addSkillDamageBuff(Buff buff, int ticks) {
        addBuff(BuffType.SKILL_DAMAGE.getLocalizedName(), buff, ticks);
    }

    /**
     * @deprecated use {@link BuffData#addBuff(BuffType, Buff, int)} instead
     */
    @Deprecated
    public void addSkillDefenseBuff(Buff buff, int ticks) {
        addBuff(BuffType.SKILL_DEFENSE.getLocalizedName(), buff, ticks);
    }

    /**
     * Applies all buffs of the given type to the specified value
     *
     * @param type  type of buff to apply
     * @param value value to modify
     * @return value after all buff applications
     */
    public double apply(final String type, final double value) {
        return doApply(value, type);
    }

    /**
     * Applies all buffs of the given type to the specified value
     *
     * @param type  type of buff to apply
     * @param value value to modify
     * @return value after all buff applications
     *
     * @deprecated use {@link BuffData#apply(String, double)} instead
     */
    @Deprecated
    public double apply(final BuffType type, final double value) {
        return apply(type.getLocalizedName(), value);
    }

    public boolean isActive(final BuffType type) {
        final Map<String, Buff> typeBuffs = buffs.get(type.getLocalizedName());
        return typeBuffs != null;
    }

    /**
     * Clears buffs by the given type
     *
     * @param type type of buff
     */
    public void clearByType(final BuffType type) {
        Map<String, Buff> buffType = buffs.get(type.getLocalizedName());
        if (buffType == null) return;
        for (final Buff buff : buffType.values()) {
            buff.task.cancel();
        }
        buffs.remove(type.getLocalizedName());
    }

    /**
     * Applies all buffs of the given type to the specified value
     *
     * @param type     type of buff to apply
     * @param category sub category of the buff type to apply (e.g. damage classification)
     * @param value    value to modify
     * @return value after all buff applications
     */
    public double apply(final BuffType type, final String category, final double value) {
        return category == null || category.isEmpty() ? doApply(value, type.getLocalizedName())
                : doApply(value, type.getLocalizedName(), type.getLocalizedName() + "_" + category);
    }

    private double doApply(final double value, final String... types) {

        // Ignore zeroed out values that shouldn't get buffs
        if (value <= 0) return value;

        double multiplier = 1;
        double bonus      = 0;
        Logger.log(LogType.BUFF, 1, "Buffs:");
        for (final String type : types) {
            final Map<String, Buff> typeBuffs = buffs.get(type);
            if (typeBuffs == null) {
                continue;
            }

            for (final Buff buff : typeBuffs.values()) {
                if (buff.isPercent()) {
                    Logger.log(LogType.BUFF, 1, "  - x" + buff.getValue());
                    multiplier *= buff.getValue();
                } else {
                    Logger.log(LogType.BUFF, 1, "  - +" + buff.getValue());
                    bonus += buff.getValue();
                }
            }
        }
        double result = Math.max(0, value * multiplier + bonus);
        Logger.log(LogType.BUFF, 1, "Result: x" + multiplier + ", +" + bonus + ", " + value + " -> " + result);

        // Negatives aren't well received by bukkit, so return 0 instead
        if (multiplier <= 0) return 0;

        return result;
    }

    private double getFlatBonus(final String... types) {
        double bonus = 0;
        for (final String type : types) {
            for (final Buff buff : buffs.getOrDefault(type, Collections.emptyMap()).values()) {
                if (!buff.isPercent()) {
                    bonus += buff.getValue();
                }
            }
        }
        // Negatives aren't well received by bukkit, so return 0 instead
        return bonus;
    }

    private double getMultiplier(final String... types) {
        double multiplier = 1;
        for (final String type : types) {
            for (final Buff buff : buffs.getOrDefault(type, Collections.emptyMap()).values()) {
                if (buff.isPercent()) {
                    multiplier *= buff.getValue();
                }
            }
        }
        // Negatives aren't well received by bukkit, so return 0 instead
        return Math.max(0, multiplier);
    }

    /**
     * @deprecated use {@link BuffData#apply(String, double)} instead
     */
    @Deprecated
    public double modifyDealtDamage(double damage) {
        return apply(BuffType.DAMAGE.getLocalizedName(), damage);
    }

    /**
     * @deprecated use {@link BuffData#apply(String, double)} instead
     */
    @Deprecated
    public double modifyTakenDamage(double damage) {
        return apply(BuffType.DEFENSE.getLocalizedName(), damage);
    }

    /**
     * @deprecated use {@link BuffData#apply(String, double)} instead
     */
    @Deprecated
    public double modifySkillDealtDamage(double damage) {
        return apply(BuffType.SKILL_DAMAGE.getLocalizedName(), damage);
    }

    /**
     * @deprecated use {@link BuffData#apply(String, double)} instead
     */
    @Deprecated
    public double modifySkillTakenDamage(double damage) {
        return apply(BuffType.SKILL_DEFENSE.getLocalizedName(), damage);
    }

    /**
     * Clears all buffs on the entity and stops associated tasks.
     */
    public void clear() {
        for (final Map<String, Buff> typeBuffs : buffs.values()) {
            for (final Buff buff : typeBuffs.values()) {
                buff.task.cancel();
            }
        }
        buffs.clear();
        BuffManager.clearData(entity);
    }

    private class BuffTask extends BukkitRunnable {
        private final String type;
        private final String key;

        BuffTask(final String type, final String key) {
            this.type = type;
            this.key = key;
        }

        @Override
        public void run() {
            if (!entity.isValid() || entity.isDead()) {
                BuffManager.clearData(entity);
                return;
            }

            final Map<String, Buff> typeBuffs = buffs.get(type);
            typeBuffs.remove(key);
            // Clean up buff data if the entity doesn't hold onto any buffs
            if (typeBuffs.isEmpty()) {
                buffs.remove(type);
                if (buffs.isEmpty()) {
                    BuffManager.clearData(entity);
                }
            }
            BuffExpiredEvent event = new BuffExpiredEvent(entity, typeBuffs.get(type), this.type);
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
