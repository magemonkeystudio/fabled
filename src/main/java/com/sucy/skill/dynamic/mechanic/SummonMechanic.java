/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.WolfMechanic
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

import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.task.RemoveTask;
import mc.promcteam.engine.mccore.util.TextFormatter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Summons a mob to the target location
 */
public class SummonMechanic extends MechanicComponent {
    private static final String TYPE   = "type";
    private static final String HEALTH = "health";
    private static final String NAME   = "name";
    private static final String AMOUNT = "amount";

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
        cleanUp(caster);

        final Player player = (Player) caster;

        double health = parseValues(player, HEALTH, level, 10.0);
        String type   = settings.getString(TYPE, "Zombie");
        String name =
                TextFormatter.colorString(filter(caster,
                        null,
                        settings.getString(NAME, "").replace("{player}", player.getName())));
        double amount = parseValues(player, AMOUNT, level, 1.0);

        EntityType entType = EntityType.valueOf(type.replace(" ", "_").toUpperCase(Locale.US));

        List<LivingEntity> wolves = new ArrayList<>();
        for (LivingEntity target : targets) {
            for (int i = 0; i < amount; i++) {
                Entity entity = target.getWorld().spawn(target.getLocation(), entType.getEntityClass());
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).setMaxHealth(health);
                    ((LivingEntity) entity).setHealth(health);

                    List<LivingEntity> owner = new ArrayList<>(1);
                    owner.add(player);
                    DynamicSkill.getCastData((LivingEntity) entity).put("api-owner", owner);

                    wolves.add((LivingEntity) entity);
                }


                if (!name.isBlank()) {
                    entity.setCustomName(name);
                    entity.setCustomNameVisible(true);
                }
            }
        }

        // Apply children to the wolves
        if (!wolves.isEmpty()) {
            executeChildren(player, level, wolves, force);
            return true;
        }
        return false;
    }

    @Override
    public String getKey() {
        return "summon";
    }
}
