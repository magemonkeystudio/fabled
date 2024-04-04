/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.LaunchMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Launches the target in a given direction relative to their forward direction
 */
public class LaunchMechanic extends MechanicComponent {
    private static final String FORWARD  = "forward";
    private static final String UPWARD   = "upward";
    private static final String RIGHT    = "right";
    private static final String RELATIVE = "relative";
    private static final String RESET_Y  = "reset-y";
    private              Vector up       = new Vector(0, 1, 0);

    @Override
    public String getKey() {
        return "launch";
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
        if (targets.isEmpty()) {
            return false;
        }

        boolean resetY   = settings.getBool(RESET_Y, true);
        double  forward  = parseValues(caster, FORWARD, level, 0);
        double  upward   = parseValues(caster, UPWARD, level, 0);
        double  right    = parseValues(caster, RIGHT, level, 0);
        String  relative = settings.getString(RELATIVE, "target").toLowerCase();
        for (LivingEntity target : targets) {
            final Vector dir;
            if (relative.equals("caster")) {
                dir = caster.getLocation().getDirection();
            } else if (relative.equals("between")) {
                dir = target.getLocation().toVector().subtract(caster.getLocation().toVector());
            } else {
                dir = target.getLocation().getDirection();
            }

            if (resetY) dir.setY(0);
            dir.normalize();

            final Vector nor = dir.clone().crossProduct(up);
            dir.multiply(forward);
            dir.add(nor.multiply(right)).setY(dir.getY() + upward);

            target.setVelocity(dir);
        }
        return !targets.isEmpty();
    }
}
