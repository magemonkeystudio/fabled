/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.SoundMechanic
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

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.codex.util.SoundUT;
import studio.magemonkey.fabled.log.Logger;

import java.util.List;
import java.util.Locale;

/**
 * Plays a particle effect
 */
public class SoundMechanic extends MechanicComponent {
    private static final String SOUND  = "sound";
    private static final String SOUND2 = "newsound";
    private static final String CUSTOM = "custom";
    private static final String VOLUME = "volume";
    private static final String PITCH  = "pitch";

    @Override
    public String getKey() {
        return "sound";
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
        if (targets.size() == 0) {
            return false;
        }

        String type =
                settings.getString(SOUND, settings.getString(SOUND2, "")).toUpperCase(Locale.US).replace(" ", "_");
        try {
            String sound =
                    type.equals("CUSTOM") ? settings.getString(CUSTOM, "")
                            : SoundUT.getSound(type).getKey().toString();
            float volume = (float) parseValues(caster, VOLUME, level, 100.0) / 100;
            float pitch  = (float) parseValues(caster, PITCH, level, 0.0);

            volume = Math.max(0, volume);
            pitch = Math.min(2, Math.max(0.5f, pitch));

            for (LivingEntity target : targets) {
                target.getWorld().playSound(target.getLocation(), sound, volume, pitch);
            }
            return targets.size() > 0;
        } catch (Exception ex) {
            Logger.invalid("Invalid sound type: " + type);
            return false;
        }
    }
}
