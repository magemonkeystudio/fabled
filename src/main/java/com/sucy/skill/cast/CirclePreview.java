/**
 * SkillAPI
 * com.sucy.skill.cast.CircleIndicator
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

/**
 * An indicator for a circular pattern
 */
public class CirclePreview extends RoundPreview {
    private double radius;
    private double sin, cos;
    private int particles;

    /**
     * @param radius radius of the circle
     */
    public CirclePreview(double radius) {
        if (radius == 0) {
            throw new IllegalArgumentException("Invalid radius - cannot be 0");
        }

        this.radius = Math.abs(radius);
        particles = (int) (PreviewSettings.density * radius * 2 * Math.PI);

        double angle = Math.PI * 2 / particles;
        sin = Math.sin(angle);
        cos = Math.cos(angle);
    }

    /**
     * Creates the packets for the indicator, adding them to the list
     *
     * @param particle particle type to use
     * @param step     animation step
     */
    @Override
    public void playParticles(Player player, ParticleSettings particle, Location location, int step) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        // Offset angle for animation
        double startAngle = step * PreviewSettings.animation / (20 * radius);
        double ii         = Math.sin(startAngle) * radius;
        double jj         = Math.cos(startAngle) * radius;

        // Make the packets
        for (int i = 0; i < particles; i++) {
            particle.instance(player, x + ii, y, z + jj);

            double temp = ii * cos - jj * sin;
            jj = ii * sin + jj * cos;
            ii = temp;
        }
    }

    @Override
    public double getRadius() {return radius;}
}
