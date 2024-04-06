/**
 * Fabled
 * studio.magemonkey.fabled.api.particle.ParticleImage
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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

import studio.magemonkey.fabled.data.Point3D;
import studio.magemonkey.fabled.data.formula.Formula;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * A particle effect image that can be played
 */
@RequiredArgsConstructor
public class ParticleImage implements IParticleEffect {
    @Getter
    private final String             name;
    private final Color[][]          colors;
    private final Point3D[][]        points;
    private final Formula            particleSizeFormula;
    @Getter
    private final int                interval;
    private final int                iterationsPerFrame;
    private final int                view;
    private final boolean            withRotation;
    private final TimeBasedTransform transform;

    /**
     * Plays the effect
     *
     * @param loc   location to play at
     * @param frame frame of the animation to play
     * @param level level of the effect
     */
    public void play(Location loc, int frame, int level) {
        if (loc == null || loc.getWorld() == null) return;

        World       world   = loc.getWorld();
        Set<Player> players = ParticleHelper.filterPlayers(world.getPlayers(), loc, view);

        if (players.isEmpty()) return;

        int       gifFrame      = iterationsPerFrame == 0 ? 1 : frame / iterationsPerFrame;
        Point3D[] framePoints   = points[gifFrame % points.length];
        Color[]   colors        = this.colors[gifFrame % this.colors.length];
        Point3D[] displayPoints = framePoints;
        if (transform != null) {
            displayPoints = transform.apply(displayPoints, loc, withRotation, frame, level);
        }

        float particleSize = (float) particleSizeFormula.compute(frame, level);

        for (int i = 0; i < displayPoints.length; i++) {
            Point3D point = displayPoints[i];
            Color   color = colors[i];
            if (color == null) continue;

            Location location = loc.clone().add(point.x, point.y, point.z);
            for (Player player : players) {
                if (player.getLocation().distance(location) > view) continue;

                player.spawnParticle(Particle.REDSTONE,
                        location,
                        1,
                        0,
                        0,
                        0,
                        0,
                        new Particle.DustOptions(color, particleSize));
            }
        }
    }
}
