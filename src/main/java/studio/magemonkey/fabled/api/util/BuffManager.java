/**
 * Fabled
 * studio.magemonkey.fabled.api.util.BuffManager
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

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.codex.registry.BuffRegistry;
import studio.magemonkey.codex.registry.provider.BuffProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The manager for temporary entity buff data
 */
@Getter
public class BuffManager implements BuffProvider {
    private static BuffManager         instance;
    private final  Map<UUID, BuffData> data = new HashMap<>();

    public static BuffManager getInstance() {
        if (instance == null) {
            instance = new BuffManager();
        }
        return instance;
    }

    /**
     * Retrieves the buff data for an entity. This returns null if
     * no existing data is found.
     *
     * @param entity entity to retrieve the data for
     * @return the buff data for the entity
     */
    public static BuffData getBuffData(final LivingEntity entity) {
        return getBuffData(entity, true);
    }

    /**
     * Retrieves the buff data for an entity, optionally creating new data
     * if none currently exists. If set to false, this will return null
     * if no data currently exists.
     *
     * @param entity entity to get the buff data for
     * @param create whether to create new data if it doesn't exist
     * @return the buff data for an enemy
     */
    public static BuffData getBuffData(final LivingEntity entity, final boolean create) {
        if (entity == null) return null;

        Map<UUID, BuffData> data = getInstance().getData();
        if (!data.containsKey(entity.getUniqueId()) && create) {
            data.put(entity.getUniqueId(), new BuffData(entity));
        }
        return data.get(entity.getUniqueId());
    }

    /**
     * Clears the buffs for an entity
     *
     * @param entity entity to clear the buffs for
     */
    public static void clearData(final LivingEntity entity) {
        if (entity == null) return;

        Map<UUID, BuffData> data   = getInstance().getData();
        final BuffData      result = data.remove(entity.getUniqueId());
        if (result != null) {
            result.clear();
        }
    }

    /**
     * Adds an offensive buff to the entity
     *
     * @param entity entity to give the buff to
     * @param type   type of buff to add
     * @param buff   buff to add
     * @param ticks  ticks to apply the buff for
     */
    public static void addBuff(final LivingEntity entity, final BuffType type, final Buff buff, final int ticks) {
        if (entity == null) return;
        getBuffData(entity, true).addBuff(type, buff, ticks);
    }

    /**
     * Adds an offensive buff to the entity
     *
     * @param entity   entity to give the buff to
     * @param type     type of buff to add
     * @param category sub category of the buff to add
     * @param buff     buff to add
     * @param ticks    ticks to apply the buff for
     */
    public static void addBuff(final LivingEntity entity,
                               final BuffType type,
                               final String category,
                               final Buff buff,
                               final int ticks) {
        if (entity == null) return;
        getBuffData(entity, true).addBuff(type.getLocalizedName(), category, buff, ticks);
    }

    /**
     * @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead
     */
    @Deprecated
    public static void addDamageBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.DAMAGE, buff, ticks);
    }

    /**
     * @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead
     */
    @Deprecated
    public static void addDefenseBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.DEFENSE, buff, ticks);
    }

    /**
     * @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead
     */
    @Deprecated
    public static void addSkillDamageBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.SKILL_DAMAGE, buff, ticks);
    }

    /**
     * @deprecated use {@link BuffManager#addBuff(LivingEntity, BuffType, Buff, int)} instead
     */
    @Deprecated
    public static void addSkillDefenseBuff(final LivingEntity entity, final Buff buff, final int ticks) {
        addBuff(entity, BuffType.SKILL_DEFENSE, buff, ticks);
    }

    /**
     * Modifies the amount of dealt damage using damage buff
     * multipliers and bonuses.
     *
     * @param entity entity to use the data of
     * @param type   type of buffs to apply
     * @param amount base amount to modify
     * @return modified number
     *
     * @deprecated use {@link BuffRegistry#scaleValue(String, LivingEntity, double)} instead
     */
    @Deprecated(forRemoval = true, since = "1.0.4-R0.16-SNAPSHOT")
    public static double apply(final LivingEntity entity, final BuffType type, final double amount) {
        return getInstance().scaleValue(type.getLocalizedName(), entity, amount);
    }

    /**
     * Modifies the amount of dealt damage using damage buff
     * multipliers and bonuses.
     *
     * @param entity   entity to use the data of
     * @param type     type of buffs to apply
     * @param category sub category of the buffs to apply
     * @param amount   base amount to modify
     * @return modified number
     *
     * @deprecated use {@link BuffRegistry#scaleValue(String, LivingEntity, double)} instead
     */
    @Deprecated(forRemoval = true, since = "1.0.4-R0.16-SNAPSHOT")
    public static double apply(final LivingEntity entity,
                               final BuffType type,
                               final String category,
                               final double amount) {
        return getInstance().scaleValue(type.getLocalizedName() + (category == null ? "" : "_" + category),
                entity,
                amount);
    }

    @Override
    public double scaleValue(String name, LivingEntity player, double value) {
        final BuffData data = getBuffData(player, false);
        if (data != null) {
            return data.apply(name, value);
        }
        return value;
    }

    /**
     * @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead
     */
    @Deprecated
    public static double modifyDealtDamage(final LivingEntity entity, final double damage) {
        return apply(entity, BuffType.DAMAGE, damage);
    }

    /**
     * @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead
     */
    @Deprecated
    public static double modifyTakenDefense(final LivingEntity entity, final double damage) {
        return apply(entity, BuffType.DEFENSE, damage);
    }

    /**
     * @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead
     */
    @Deprecated
    public static double modifySkillDealtDamage(LivingEntity entity, double damage) {
        return apply(entity, BuffType.SKILL_DAMAGE, damage);
    }

    /**
     * @deprecated use {@link BuffManager#apply(LivingEntity, BuffType, double)} instead
     */
    @Deprecated
    public static double modifySkillTakenDefense(LivingEntity entity, double damage) {
        return apply(entity, BuffType.SKILL_DEFENSE, damage);
    }
}
