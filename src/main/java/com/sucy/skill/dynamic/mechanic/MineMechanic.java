/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.BlockMechanic
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
import com.sucy.skill.api.particle.ParticleHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Mechanic that destroys a selection of blocks at the location of the target
 */
public class MineMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String MATERIALS = "materials";
    private static final String DROP      = "drop";
    private static final String TOOL      = "tool";
    private static final String SHAPE     = "shape";
    private static final String RADIUS    = "radius";
    private static final String WIDTH     = "width";
    private static final String HEIGHT    = "height";
    private static final String DEPTH     = "depth";
    private static final String FORWARD   = "forward";
    private static final String UPWARD    = "upward";
    private static final String RIGHT     = "right";

    @Override
    public String getKey() {
        return "mine";
    }

    private Location getLocation(LivingEntity caster, int level, LivingEntity target) {
        double forward = parseValues(caster, FORWARD, level, 0);
        double upward  = parseValues(caster, UPWARD, level, 0);
        double right   = parseValues(caster, RIGHT, level, 0);
        Location loc = target.getLocation();
        Vector   dir = target.getLocation().getDirection().setY(0).normalize();
        Vector   nor = dir.clone().crossProduct(UP);
        loc.add(dir.multiply(forward).add(nor.multiply(right)));
        loc.add(0, upward, 0);
        return loc;
    }

    private Map<LivingEntity, List<Block>> getAffectedBlocks(LivingEntity caster, int level, List<LivingEntity> targets) {
        boolean sphere = settings.getString(SHAPE, "sphere").equalsIgnoreCase("sphere");
        Set<String> materials = new HashSet<>();
        boolean     any;
        boolean     origin    = false;
        {
            List<String> materialList = settings.getStringList(MATERIALS);
            any = materialList.stream().anyMatch(material -> material.equalsIgnoreCase("any"));
            if (!any) {
                origin = materialList.stream().anyMatch(material -> material.equalsIgnoreCase("origin"));
                for (String material : materialList) {
                    materials.add(material.toUpperCase().replace(' ', '_'));
                }
            }
        }



        Map<LivingEntity, List<Block>> blockMap = new HashMap<>();
        World                          w        = caster.getWorld();

        // Determine blocks to be mined
        if (sphere) {
            double radius = parseValues(caster, RADIUS, level, 2);
            double x, y, z, dx, dy, dz;
            double rSq    = radius * radius;
            for (LivingEntity t : targets) {
                List<Block> blocks = new ArrayList<>();
                blockMap.put(t, blocks);
                Location loc = getLocation(caster, level, t);

                x = loc.getBlockX();
                y = loc.getBlockY();
                z = loc.getBlockZ();
                String originMaterial = loc.getBlock().getType().name();

                for (int i = (int) (x - radius) + 1; i < (int) (x + radius); i++) {
                    for (int j = (int) (y - radius) + 1; j < (int) (y + radius); j++) {
                        for (int k = (int) (z - radius) + 1; k < (int) (z + radius); k++) {
                            dx = x - i;
                            dy = y - j;
                            dz = z - k;
                            if (dx * dx + dy * dy + dz * dz < rSq) {
                                Block    b            = w.getBlockAt(i, j, k);
                                Material material     = b.getType();
                                String   materialName = material.name();
                                if (!SkillAPI.getSettings().getFilteredBlocks().contains(material) && !b.isLiquid() &&
                                        (any || (origin && materialName.equals(originMaterial)) || materials.contains(
                                                materialName))) {
                                    blocks.add(b);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            double width  = (parseValues(caster, WIDTH, level, 5) - 1) / 2;
            double height = (parseValues(caster, HEIGHT, level, 5) - 1) / 2;
            double depth  = (parseValues(caster, DEPTH, level, 5) - 1) / 2;
            double x, y, z;

            for (LivingEntity t : targets) {
                List<Block> blocks = new ArrayList<>();
                blockMap.put(t, blocks);
                Location loc = getLocation(caster, level, t);

                double  yaw     = loc.getYaw();
                boolean facingZ = Math.abs(yaw) < 45 || Math.abs(yaw) > 135;

                x = facingZ ? loc.getX() : loc.getZ();
                y = loc.getY();
                z = facingZ ? loc.getZ() : loc.getX();
                String originMaterial = loc.getBlock().getType().name();

                for (double i = x - width; i <= x + width + 0.01; i++) {
                    for (double j = y - height; j <= y + height + 0.01; j++) {
                        for (double k = z - depth; k <= z + depth + 0.01; k++) {
                            Block    b            = w.getBlockAt((int) Math.floor(facingZ ? i : k),
                                    (int) Math.floor(j),
                                    (int) Math.floor(facingZ ? k : i));
                            Material material     = b.getType();
                            String   materialName = material.name();
                            if (!SkillAPI.getSettings().getFilteredBlocks().contains(material) && !b.isLiquid() &&
                                    (any || (origin && materialName.equals(originMaterial)) || materials.contains(
                                            materialName))) {
                                blocks.add(b);
                            }
                        }
                    }
                }
            }
        }
        return blockMap;
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;

        Map<LivingEntity, List<Block>> blockMap = getAffectedBlocks(caster, level, targets);
        boolean drop   = settings.getBool(DROP, true);

        // Mine blocks
        boolean success = false;
        if (drop) {
            String    toolString = settings.getString(TOOL, "CASTER").toUpperCase().replace(' ', '_');
            boolean   targetTool = toolString.equals("TARGET");
            ItemStack tool       = null;
            if (toolString.equals("CASTER")) {
                EntityEquipment equipment = caster.getEquipment();
                if (equipment != null) {
                    tool = equipment.getItemInMainHand();
                }
            } else if (!targetTool) {
                try {
                    tool = new ItemStack(Material.valueOf(toolString));
                } catch (IllegalArgumentException ignored) {
                }
            }
            for (Map.Entry<LivingEntity, List<Block>> entry : blockMap.entrySet()) {
                if (targetTool) {
                    tool = null;
                    EntityEquipment equipment = entry.getKey().getEquipment();
                    if (equipment != null) {
                        tool = equipment.getItemInMainHand();
                    }
                }
                if (tool == null) {
                    tool = new ItemStack(Material.AIR);
                }
                List<Block> blocks = entry.getValue();
                success = success || !blocks.isEmpty();
                for (Block block : blocks) {
                    block.breakNaturally(tool);
                }
            }
        } else {
            for (List<Block> blocks : blockMap.values()) {
                success = success || !blocks.isEmpty();
                for (Block block : blocks) {
                    BlockData blockState = block.getBlockData();
                    block.setType(blockState instanceof Waterlogged && ((Waterlogged) blockState).isWaterlogged() ?
                            Material.WATER :
                            Material.AIR);
                }
            }
        }
        return success;
    }

    @Override
    public void playPreview(List<Runnable> onPreviewStop, Player caster, int level, List<LivingEntity> targets) {
        if (preview.getBool("per-target")) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (preview.getBool("per-target-center-only", true))
                        for (LivingEntity t : targets)
                            ParticleHelper.play(getLocation(caster, level, t), preview, Set.of(caster), "per-target-", null);
                    else {
                        Map<LivingEntity, List<Block>> blockMap = getAffectedBlocks(caster, level, targets);
                        for (List<Block> blocks : blockMap.values()) {
                            for (Block block : blocks) {
                                ParticleHelper.play(block.getLocation(), preview, Set.of(caster), "per-target-",
                                        preview.getBool("per-target-"+"hitbox") ? block.getBoundingBox() : null
                                );
                            }
                        }
                    }
                }
            }.runTaskTimer(SkillAPI.inst(),0, Math.max(1, preview.getInt("per-target-"+"period", 5)));
            onPreviewStop.add(task::cancel);
        }
    }
}
