/**
 * SkillAPI
 * com.sucy.skill.cast.SphereIndicator
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
 * A fancier sphere indicator
 */
public class SpherePreview extends RoundPreview {
    private static final double COS_45 = Math.cos(Math.PI / 4);
    private              double radius;
    private              double sin, cos;
    private double angleStep;
    private int    particles;

    /**
     * @param radius radius of the circle
     */
    public SpherePreview(double radius) {
        if (radius == 0) {
            throw new IllegalArgumentException("Invalid radius - cannot be 0");
        }

        this.radius = Math.abs(radius);
        particles = (int) (PreviewSettings.density * radius * 2 * Math.PI);
        angleStep = PreviewSettings.animation * PreviewSettings.interval / (20 * this.radius);

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
        double startAngle = step * angleStep;

        double urs = Math.sin(startAngle);
        double urc = Math.cos(startAngle);

        double rs = urs * radius;
        double rc = urc * radius;

        // Flat circle packets
        for (int i = 0; i < particles; i++) {
            particle.instance(player, x + rs, y, z + rc);
            particle.instance(player, x + rs * urc, y + rc, z + rs * urs);
            particle.instance(player, x + (rc - rs * urs) * COS_45, y + rs * urc, z + (rc + rs * urs) * COS_45);

            double temp = rs * cos - rc * sin;
            rc = rs * sin + rc * cos;
            rs = temp;
        }
    }

    @Override
    public double getRadius() {return radius;}
}
