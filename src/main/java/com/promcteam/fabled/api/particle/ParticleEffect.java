/**
 * Fabled
 * com.promcteam.fabled.api.particle.ParticleEffect
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.api.particle;

import com.promcteam.fabled.api.particle.direction.DirectionHandler;
import com.promcteam.fabled.api.particle.direction.Directions;
import com.promcteam.fabled.api.particle.direction.XZHandler;
import com.promcteam.fabled.data.Matrix3D;
import com.promcteam.fabled.data.Point2D;
import com.promcteam.fabled.data.Point3D;
import com.promcteam.fabled.data.formula.Formula;
import com.promcteam.fabled.data.formula.IValue;
import com.promcteam.fabled.data.formula.value.CustomValue;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

/**
 * A particle effect that can be played
 */
public class ParticleEffect {
    private static final XZHandler flatRot = (XZHandler) Directions.byName("XZ");

    private final PolarSettings    shape;
    private final PolarSettings    animation;
    private final ParticleSettings particle;
    private final IValue           size;
    private final IValue           animSize;

    private final DirectionHandler shapeDir;
    private final DirectionHandler animDir;

    private final boolean  withRotation;
    private final double   initialRotation;
    private final Matrix3D rotMatrix;

    private final String name;

    private final int interval;
    private final int view;

    /**
     * @param shape     shape formula details
     * @param animation motion animation formula details
     * @param particle  settings of the particle to use
     * @param shapeDir  shape orientation
     * @param animDir   animation orientation
     * @param size      formula string for shape size
     * @param animSize  formula string for animation size
     * @param interval  time between animation frames in ticks
     * @param viewRange range in blocks players can see the effect from
     */
    public ParticleEffect(
            String name,
            PolarSettings shape,
            PolarSettings animation,
            ParticleSettings particle,
            DirectionHandler shapeDir,
            DirectionHandler animDir,
            String size,
            String animSize,
            int interval,
            int viewRange,
            boolean withRotation,
            double initialRotation) {
        this.name = name;
        this.shape = shape;
        this.animation = animation;
        this.withRotation = withRotation;
        this.initialRotation = Math.toRadians(initialRotation);

        if (this.initialRotation != 0) {
            double cos = Math.cos(this.initialRotation);
            double sin = Math.sin(this.initialRotation);
            rotMatrix = new Matrix3D(
                    cos, 0, sin,
                    0, 1, 0,
                    -sin, 0, cos
            );
        } else
            rotMatrix = null;

        this.size = new Formula(
                size,
                new CustomValue("t"),
                new CustomValue("p"),
                new CustomValue("c"),
                new CustomValue("s"),
                new CustomValue("x"),
                new CustomValue("y"),
                new CustomValue("z"),
                new CustomValue("v")
        );
        this.particle = particle;
        this.shapeDir = shapeDir;
        this.animDir = animDir;
        this.animSize = new Formula(
                animSize,
                new CustomValue("t"),
                new CustomValue("p"),
                new CustomValue("c"),
                new CustomValue("s"),
                new CustomValue("x"),
                new CustomValue("y"),
                new CustomValue("z"),
                new CustomValue("v")
        );
        this.interval = interval;
        this.view = viewRange;

        int points = shape.getPoints(shapeDir).length;
        animation.getPoints(animDir);
    }

    /**
     * @return name of the effect
     */
    public String getName() {
        return name;
    }

    /**
     * @return time between each frame in ticks
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Plays the effect
     *
     * @param loc   location to play at
     * @param frame frame of the animation to play
     * @param level level of the effect
     */
    public void play(Location loc, int frame, int level) {
        frame = frame % animation.getSteps();
        try {
            int       next        = (frame + 1) * animation.getCopies();
            Point3D[] animPoints  = animation.getPoints(animDir);
            Point3D[] shapePoints = shape.getPoints(shapeDir);
            Point2D[] trig        = animation.getTrig(frame);

            Point2D cs = trig[0];
            double  t  = animation.getT(frame);
            double  p  = (double) frame / animation.getSteps();

            int j = 0;

            World       world   = Objects.requireNonNull(loc.getWorld());
            Set<Player> players = ParticleHelper.filterPlayers(world.getPlayers(), loc, view);

            Particle effect = this.particle.type;
            int      count  = this.particle.amount;
            double   dx     = this.particle.dx;
            double   dy     = this.particle.dy;
            double   dz     = this.particle.dz;
            float    speed  = this.particle.speed;
            Object   object = this.particle.object;

            for (int i = frame * this.animation.getCopies(); i < next; ++i) {
                Point3D p1       = animPoints[i];
                double  animSize = this.animSize.compute(t, p, cs.x, cs.y, p1.x, p1.y, p1.z, level);

                for (Point3D p2 : shapePoints) {
                    double size = this.size.compute(t, p, cs.x, cs.y, p2.x, p2.y, p2.z, level);
                    if (initialRotation != 0) p2 = flatRot.rotateAboutY(p2, rotMatrix);
                    if (withRotation) {
                        double yaw = Math.toRadians(-loc.getYaw());
                        p2 = flatRot.rotateAboutY(p2, yaw);
                    }
                    double x = p1.x * animSize + this.animDir.rotateX(p2, trig[j]) * size + loc.getX();
                    double y = p1.y * animSize + this.animDir.rotateY(p2, trig[j]) * size + loc.getY();
                    double z = p1.z * animSize + this.animDir.rotateZ(p2, trig[j]) * size + loc.getZ();

                    players.forEach(player ->
                            player.spawnParticle(effect, x, y, z, count, dx, dy, dz, speed, object));
                }
                ++j;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
