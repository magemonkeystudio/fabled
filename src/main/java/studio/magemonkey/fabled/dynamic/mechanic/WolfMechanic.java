/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.WolfMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.skills.PassiveSkill;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.task.RemoveTask;
import studio.magemonkey.codex.mccore.util.TextFormatter;
import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.*;

/**
 * Applies a flag to each target
 */
public class WolfMechanic extends MechanicComponent {
    public static final  String SKILL_META = "sapi_wolf_skills";
    public static final  String LEVEL      = "sapi_wolf_level";
    private static final String COLOR      = "color";
    private static final String HEALTH     = "health";
    private static final String SECONDS    = "seconds";
    private static final String NAME       = "name";
    private static final String DAMAGE     = "damage";
    private static final String SKILLS     = "skills";
    private static final String AMOUNT     = "amount";
    private static final String SITTING    = "sitting";

    private final Map<Integer, RemoveTask> tasks = new HashMap<>();

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
        if (!(caster instanceof Player)) {
            return false;
        }

        cleanUp(caster);

        final Player player = (Player) caster;

        String color  = settings.getString(COLOR);
        double health = parseValues(player, HEALTH, level, 10.0);
        String name =
                TextFormatter.colorString(filter(caster,
                        null,
                        settings.getString(NAME, "").replace("{player}", player.getName())));
        double       damage  = parseValues(player, DAMAGE, level, 3.0);
        double       amount  = parseValues(player, AMOUNT, level, 1.0);
        boolean      sitting = settings.getString(SITTING, "false").equalsIgnoreCase("true");
        List<String> skills  = settings.getStringList(SKILLS);

        DyeColor dye = null;
        if (color != null) {
            try {
                dye = DyeColor.valueOf(color.toUpperCase(Locale.US));
            } catch (Exception ex) { /* Invalid color */ }
        }

        double             seconds = parseValues(player, SECONDS, level, 10.0);
        int                ticks   = (int) (seconds * 20);
        List<LivingEntity> wolves  = new ArrayList<>();
        for (LivingEntity target : targets) {
            for (int i = 0; i < amount; i++) {
                Wolf wolf = target.getWorld().spawn(target.getLocation(), Wolf.class);
                wolf.setOwner(player);
                wolf.setMaxHealth(health);
                wolf.setHealth(health);
                wolf.setSitting(sitting);
                Fabled.setMeta(wolf, MechanicListener.SUMMON_DAMAGE, damage);

                List<LivingEntity> owner = new ArrayList<>(1);
                owner.add(player);
                DynamicSkill.getCastData(wolf).put("api-owner", owner);

                if (dye != null) {
                    wolf.setCollarColor(dye);
                }
                if (name.length() > 0) {
                    wolf.setCustomName(name);
                    wolf.setCustomNameVisible(true);
                }

                // Setup skills
                for (String skillName : skills) {
                    Skill skill = Fabled.getSkill(skillName);
                    if (skill instanceof PassiveSkill) {
                        ((PassiveSkill) skill).initialize(wolf, level);
                    }
                }
                Fabled.setMeta(wolf, SKILL_META, skills);
                Fabled.setMeta(wolf, LEVEL, level);

                wolves.add(wolf);
            }
        }

        final RemoveTask task = new RemoveTask(wolves, ticks);
        tasks.put(caster.getEntityId(), task);

        // Apply children to the wolves
        if (wolves.size() > 0) {
            executeChildren(player, level, wolves, force);
            return true;
        }
        return false;
    }

    @Override
    public String getKey() {
        return "wolf";
    }

    @Override
    public void cleanUp(final LivingEntity caster) {
        final RemoveTask task = tasks.remove(caster.getEntityId());
        if (task != null) {
            task.cancel();
            task.run();
        }
    }
}
