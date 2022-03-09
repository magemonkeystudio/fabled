/**
 * SkillAPI
 * com.sucy.skill.cast.ArcIndicator
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
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
package com.sucy.skill.cast;

import com.sucy.skill.api.particle.ParticleSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Represents a preview indicator for showing the direction of projectiles to fire
 */
public class ProjectilePreview extends Preview {
    private double speed;
    private double gravity;
    private double tBase;

    /**
     * @param speed   speed of the projectile
     * @param gravity gravity of the projectile
     */
    public ProjectilePreview(double speed, double gravity) {
        this.speed = speed;
        this.gravity = gravity;
        this.tBase = 3/this.speed;
    }

    /**
     * Creates the packets for the indicator, adding them to the list
     *
     * @param particle particle type to use
     * @param step     animation step
     * @throws Exception
     */
    @Override
    public void playParticles(Player player, ParticleSettings particle, Location location, int step) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        Vector direction = location.getDirection();

        double px = x+direction.getX()*tBase;
        double py = y+direction.getY()*tBase-gravity*tBase*tBase;
        double pz = z+direction.getZ()*tBase;

        particle.instance(player, px, py, pz);
    }

    public double getSpeed() { return speed; }

    public double getGravity() { return gravity; }
}
