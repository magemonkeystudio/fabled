/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.value.ValueRotationMechanic
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
package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;
import studio.magemonkey.fabled.dynamic.target.RememberTarget;

import java.util.List;

/**
 * Gets the degree differential between the target's vector and the source location
 */
public class ValueRotationMechanic extends MechanicComponent {
    private static final String KEY    = "key";
    private static final String SOURCE = "source";
    private static final String SAVE   = "save";

    @Override
    public String getKey() {
        return "value rotation";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;
        List<LivingEntity> potentialSources = RememberTarget.remember(caster, settings.getString(SOURCE, "_none"));
        if (potentialSources.isEmpty()) potentialSources = List.of(caster);

        String             key    = settings.getString(KEY);
        final LivingEntity source = potentialSources.get(0);
        final LivingEntity target = targets.get(0);

        if (source.equals(target)) return false;

        Location targetEyeLoc = target.getEyeLocation();
        Vector   targetDir    = target.getLocation().getDirection();
        Vector   sourceVec    = source.getLocation().subtract(targetEyeLoc).toVector();

        // Get the angle between the loc and vec (arccos(a dot b / |a| * |b|)) -- |a| is always 1
        double dot       = targetDir.dot(sourceVec);
        double magnitude = sourceVec.length();
        double angle     = Math.toDegrees(Math.acos(dot / magnitude));

        // Set the value
        CastData data = DynamicSkill.getCastData(caster);
        data.put(key, angle);
        if (settings.getBool(SAVE, false))
            Fabled.getPlayerData((OfflinePlayer) caster).setPersistentData(key, data.getRaw(key));
        return true;
    }
}
