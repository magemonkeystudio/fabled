/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ParticleAnimationMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.particle.ParticleHelper;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Plays a particle effect
 */
public class ParticleAnimationMechanic extends MechanicComponent {
    public static final  String WITH_ROTATION = "-with-rotation";
    private static final String FORWARD       = "forward";
    private static final String UPWARD        = "upward";
    private static final String RIGHT         = "right";
    private static final String STEPS         = "steps";
    private static final String FREQ          = "frequency";
    private static final String ANGLE         = "angle";
    private static final String START         = "start";
    private static final String DURATION      = "duration";
    private static final String H_TRANS       = "h-translation";
    private static final String V_TRANS       = "v-translation";
    private static final String H_CYCLES      = "h-cycles";
    private static final String V_CYCLES      = "v-cycles";

    @Override
    public String getKey() {
        return "particle animation";
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

        final Settings copy = new Settings(settings);
        copy.set(ParticleHelper.POINTS_KEY, parseValues(caster, ParticleHelper.POINTS_KEY, level, 1), 0);
        copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0), 0);
        copy.set("level", level);
        new ParticleTask(caster, targets, level, copy);
        return targets.size() > 0;
    }

    private class ParticleTask extends BukkitRunnable {

        private final List<LivingEntity> targets;
        private final double[]           rots;
        private final Vector             offset;
        private final Vector             dir;

        private final double forward;
        private final double right;
        private final double upward;

        private final int      steps;
        private final int      freq;
        private final int      angle;
        private final int      startAngle;
        private final int      duration;
        private final int      hc;
        private final int      vc;
        private final int      hl;
        private final int      vl;
        private final double   ht;
        private final double   vt;
        private final double   cos;
        private final Settings settings;
        private final double   sin;
        private       int      life;
        private       boolean  withRotation = false;

        ParticleTask(LivingEntity caster, List<LivingEntity> targets, int level, Settings settings) {
            this.targets = targets;
            this.settings = settings;

            this.forward = getNum(caster, FORWARD, 0);
            this.upward = getNum(caster, UPWARD, 0);
            this.right = getNum(caster, RIGHT, 0);

            this.steps = (int) getNum(caster, STEPS, 1);
            this.freq = (int) (getNum(caster, FREQ, 1.0) * 20);
            this.angle = (int) getNum(caster, ANGLE, 0);
            this.startAngle = (int) getNum(caster, START, 0);
            this.duration = steps * (int) (20 * parseValues(caster, DURATION, level, 3.0));
            this.life = 0;
            this.ht = parseValues(caster, H_TRANS, level, 0);
            this.vt = parseValues(caster, V_TRANS, level, 0);
            this.hc = (int) getNum(caster, H_CYCLES, 1);
            this.vc = (int) getNum(caster, V_CYCLES, 1);
            this.hl = duration / hc;
            this.vl = duration / vc;
            this.withRotation = settings.getBool(WITH_ROTATION);

            this.cos = Math.cos(angle * Math.PI / (180 * duration));
            this.sin = Math.sin(angle * Math.PI / (180 * duration));

            rots = new double[targets.size()];
            for (int i = 0; i < targets.size(); i++) {
                rots[i] = targets.get(i).getLocation().getYaw();
            }
            this.dir = new Vector(0, 0, 1);
            this.offset = new Vector(right, upward, forward);

            double sc = Math.cos(Math.toRadians(startAngle));
            double ss = Math.sin(Math.toRadians(startAngle));
            rotate(offset, sc, ss);
            rotate(dir, sc, ss);

            SkillAPI.schedule(this, 0, freq);
        }

        @Override
        public void run() {
            for (int i = 0; i < steps; i++) {
                // Play the effect
                int j = 0;
                for (LivingEntity target : targets) {
                    Location loc = target.getLocation();

                    // Calculate the target rotation and add that
                    double targetAngle = loc.getYaw();
                    double targetCos;
                    double targetSin;
                    if (withRotation) {
                        targetCos = Math.cos(Math.toRadians(targetAngle));
                        targetSin = Math.sin(Math.toRadians(targetAngle));
                        rotate(offset, targetCos, targetSin);

                        loc.add(offset);
                        ParticleHelper.play(loc, settings);
                        loc.subtract(offset);

                        targetCos = Math.cos(Math.toRadians(-targetAngle));
                        targetSin = Math.sin(Math.toRadians(-targetAngle));
                        rotate(offset, targetCos, targetSin);
                    } else {
                        rotate(offset, Math.cos(Math.toRadians(rots[j])), Math.sin(Math.toRadians(rots[j])));
                        loc.add(offset);
                        ParticleHelper.play(loc, settings);
                        loc.subtract(offset);

                        rotate(offset, Math.cos(Math.toRadians(-rots[j])), Math.sin(Math.toRadians(-rots[j])));
                        j += 1;
                    }
                }

                // Update the lifespan of the animation
                this.life++;

                // Apply transformations
                rotate(offset, cos, sin);
                rotate(dir, cos, sin);

                double dx = radAt(this.life) - radAt(this.life - 1);
                offset.setX(offset.getX() + dx * dir.getX());
                offset.setZ(offset.getZ() + dx * dir.getZ());
                offset.setY(upward + heightAt(this.life));
            }

            if (this.life >= this.duration) {
                cancel();
            }
        }

        private double heightAt(int step) {
            return vt * (vl - Math.abs(vl - step % (2 * vl))) / vl;
        }

        private double radAt(int step) {
            return ht * (hl - Math.abs(hl - step % (2 * hl))) / hl;
        }

        private void
        rotate(Vector vec, double cos, double sin) {
            double x = vec.getX() * cos - vec.getZ() * sin;
            vec.setZ(vec.getX() * sin + vec.getZ() * cos);
            vec.setX(x);
        }
    }
}
