/**
 * Fabled
 * studio.magemonkey.fabled.api.particle.ParticleSettings
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package studio.magemonkey.fabled.api.particle;

import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.Settings;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Settings for playing a particle
 */
public class ParticleSettings {
    public static final String
            PARTICLE_KEY     = "particle",
            MATERIAL_KEY     = "material",
            DURABILITY_KEY   = "durability",
            DATA_KEY         = "type",
            AMOUNT_KEY       = "amount",
            DX_KEY           = "dx",
            DY_KEY           = "dy",
            DZ_KEY           = "dz",
            SPEED_KEY        = "speed",
            DUST_COLOR       = "dust-color",
            FINAL_DUST_COLOR = "final-dust-color",
            DUST_SIZE        = "dust-size";

    // Particle type
    public final Particle type;

    // Offset values
    public final float dx, dy, dz;

    // Particle speed
    public final float speed;

    // Particle amount
    public final int amount;

    public final Object object;

    /**
     * Sets up a particle that requires material cmd
     *
     * @param type     particle type
     * @param dx       DX value
     * @param dy       DY value
     * @param dz       DZ value
     * @param speed    particle speed
     * @param amount   particle amount
     * @param material material to use
     * @param cmd      material cmd value
     */
    public ParticleSettings(
            Particle type,
            float dx,
            float dy,
            float dz,
            float speed,
            int amount,
            Material material,
            int cmd,
            int durability,
            Color dustColor,
            Color toColor,
            float dusSize
    ) {
        this.type = type;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.speed = speed;
        this.amount = amount;
        this.object = ParticleHelper.makeObject(type, material, cmd, durability, dustColor, toColor, dusSize);
    }

    /**
     * Loads a particle setup from config data
     *
     * @param config config data to load from
     */
    public ParticleSettings(DataSection config) {
        this.type = ParticleHelper.getFromKey(config.getString(PARTICLE_KEY, "Villager happy"));
        this.dx = config.getFloat(DX_KEY, 0);
        this.dy = config.getFloat(DY_KEY, 0);
        this.dz = config.getFloat(DZ_KEY, 0);
        this.speed = config.getFloat(SPEED_KEY, 1);
        this.amount = config.getInt(AMOUNT_KEY, 1);
        this.object = ParticleHelper.makeObject(type,
                Material.valueOf(config.getString(MATERIAL_KEY, "Dirt").toUpperCase(Locale.US).replace(" ", "_")),
                config.getInt(DATA_KEY, 0),
                config.getInt(DURABILITY_KEY, 0),
                Color.fromRGB(Integer.parseInt(config.getString(DUST_COLOR, "#FF0000").substring(1), 16)),
                Color.fromRGB(Integer.parseInt(config.getString(FINAL_DUST_COLOR, "#FF0000").substring(1), 16)),
                (float) config.getDouble(DUST_SIZE, 1));
    }

    /**
     * Loads a particle setup from config data
     *
     * @param config config data to load from
     * @param prefix string to look for at the beginning of each config key
     */
    public ParticleSettings(Settings config, String prefix) {
        this.type = ParticleHelper.getFromKey(config.getString(prefix + PARTICLE_KEY, "Villager happy"));
        this.dx = (float) config.getDouble(prefix + DX_KEY, 0);
        this.dy = (float) config.getDouble(prefix + DY_KEY, 0);
        this.dz = (float) config.getDouble(prefix + DZ_KEY, 0);
        this.speed = (float) config.getDouble(prefix + SPEED_KEY, 1);
        this.amount = config.getInt(prefix + AMOUNT_KEY, 1);
        this.object = ParticleHelper.makeObject(type,
                Material.valueOf(config.getString(prefix + MATERIAL_KEY, "Dirt")
                        .toUpperCase(Locale.US)
                        .replace(" ", "_")),
                config.getInt(prefix + DATA_KEY, 0),
                config.getInt(prefix + DURABILITY_KEY, 0),
                Color.fromRGB(Integer.parseInt(config.getString(prefix + DUST_COLOR, "#FF0000").substring(1), 16)),
                Color.fromRGB(Integer.parseInt(config.getString(prefix + FINAL_DUST_COLOR, "#FF0000").substring(1),
                        16)),
                (float) config.getDouble(prefix + DUST_SIZE, 1));
    }

    /**
     * Makes a new instance of the particle effect
     *
     * @param x X-axis coordinates
     * @param y Y-axis coordinates
     * @param z Z-axis coordinates
     */
    public void instance(Player player, double x, double y, double z) {
        player.spawnParticle(type, x, y, z, amount, dx, dy, dz, speed, object);
    }
}
