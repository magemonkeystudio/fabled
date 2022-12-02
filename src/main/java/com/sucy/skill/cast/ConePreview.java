/**
 * SkillAPI
 * com.sucy.skill.cast.ConeIndicator
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

public class ConePreview extends Preview {
    private double arc, radius;
    private double sin, cos;
    private double rSin, rCos;
    private double offset;
    private double angleOffset;

    /**
     * @param arc    angle of the cone in radians
     * @param radius radius of the cone
     */
    public ConePreview(double arc, double radius) {
        if (radius == 0) {
            throw new IllegalArgumentException("Invalid radius - cannot be 0");
        }

        this.arc = arc;
        this.radius = Math.abs(radius);
        double perimeter = radius * arc + 2 * radius;
        int    particles = (int) (PreviewSettings.density * perimeter);

        offset = perimeter / particles;
        angleOffset = offset / radius;
        sin = Math.sin(angleOffset);
        cos = Math.cos(angleOffset);
        rSin = Math.sin(arc / 2);
        rCos = Math.cos(arc / 2);
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

        double yaw = location.getYaw() - Math.PI / 180;
        double fx  = Math.sin(yaw);
        double fz  = Math.cos(yaw);
        yaw = yaw + Math.PI / 4;

        double base = (PreviewSettings.animation * 0.05 * step) % offset;

        // Offset angle for animation
        double startAngle = ((radius - base) % offset) / radius;
        double ii         = Math.sin(startAngle + yaw) * radius;
        double jj         = Math.cos(startAngle + yaw) * radius;

        // Packets along the edges
        make(player, particle, x, y, z, base, fx * rCos + fz * rSin, fz * rCos - fx * rSin);
        make(player, particle, x, y, z, offset - base, fx * rCos - fz * rSin, fx * rSin + fz * rCos);

        // Packets around the curve
        while (startAngle < arc) {
            particle.instance(player, x + ii, y, z + jj);

            double temp = ii * cos - jj * sin;
            jj = ii * sin + jj * cos;
            ii = temp;

            startAngle += angleOffset;
        }
    }

    private void make(Player player, ParticleSettings particle, double x, double y, double z, double pos, double rfx, double rfz) {
        while (pos <= radius) {
            particle.instance(player, x + pos * rfx, y, z + pos * rfz);
            pos += offset;
        }
    }

    public double getArc() {return arc;}

    public double getRadius() {return radius;}
}
