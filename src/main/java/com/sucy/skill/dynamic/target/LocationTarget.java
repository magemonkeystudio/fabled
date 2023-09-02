/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.LocationTarget
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
package com.sucy.skill.dynamic.target;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.cast.PreviewSettings;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends TargetComponent {
    private static final String RANGE    = "range";
    private static final String ENTITIES = "entities";
    private static final String FLUIDS   = "fluids";
    private static final String PASSABLE = "passable";
    private static final String CENTER   = "center";

    //LEGACY
    private static final String GROUND = "ground";

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPreview(Player caster, final int level, final LivingEntity target, int step) {
        final LivingEntity loc = getTargets(caster, level, ImmutableList.of(target)).get(0);
        switch (previewType) {
            case DIM_2:
                circlePreview.playParticles(caster, PreviewSettings.particle, loc.getLocation().add(0, 0.1, 0), step);
                break;
            case DIM_3:
                spherePreview.playParticles(caster, PreviewSettings.particle, loc.getLocation().add(0, 0.1, 0), step);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<LivingEntity> getTargets(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {
        final double  range    = parseValues(caster, RANGE, level, 5.0);
        final boolean entities = settings.getBool(ENTITIES, true);
        final boolean fluids   = settings.getBool(FLUIDS, false);
        final boolean passable = settings.has(PASSABLE) ? settings.getBool(PASSABLE) : settings.getBool(GROUND, true);
        final boolean center   = settings.getBool(CENTER, false);
        return determineTargets(caster,
                level,
                targets,
                t -> getTargetLoc(caster, t, range, entities, fluids, passable, center));
    }

    private List<LivingEntity> getTargetLoc(
            LivingEntity caster,
            LivingEntity t,
            final double range,
            final boolean entities,
            final boolean fluids,
            final boolean passable,
            final boolean center) {
        World              world         = t.getWorld();
        Location           startLocation = t.getEyeLocation();
        Vector             direction     = startLocation.getDirection();
        FluidCollisionMode fluidMode     = fluids ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER;
        RayTraceResult rayTrace = entities ?
                world.rayTrace(startLocation, direction, range, fluidMode, !passable, 0, entity -> entity != caster) :
                world.rayTraceBlocks(startLocation, direction, range, fluidMode, !passable);

        Location location;
        if (rayTrace == null) {
            location = startLocation.add(direction.multiply(range));
        } else {
            Block hitBlock = rayTrace.getHitBlock();
            location = center && hitBlock != null ?
                    hitBlock.getLocation() :
                    rayTrace.getHitPosition().toLocation(world);
        }
        location.setDirection(direction);
        if (center) {
            center(location);
        }
        return ImmutableList.of(new TempEntity(location));
    }

    private void center(Location location) {
        location.setX(location.getBlockX() + 0.5);
        location.setY(location.getBlockY() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
    }

    @Override
    public String getKey() {
        return "location";
    }
}
