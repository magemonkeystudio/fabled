/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.condition.LoreCondition
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
package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LoreCondition extends ConditionComponent {
    private static final String REGEX  = "regex";
    private static final String STRING = "str";

    private Predicate<String> test;

    @Override
    public String getKey() {
        return "lore";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        final boolean regex = settings.getString(REGEX, "false").toLowerCase().equals("true");
        final String  str   = settings.getString(STRING, "");
        if (regex) {
            final Pattern pattern = Pattern.compile(str);
            test = line -> pattern.matcher(line).find();
        } else {
            test = line -> line.contains(str);
        }
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final EntityEquipment items = target.getEquipment();
        if (items == null || items.getItemInMainHand() == null || !items.getItemInMainHand().hasItemMeta()) {
            return false;
        }

        final List<String> lore = items.getItemInMainHand().getItemMeta().getLore();
        return lore != null && lore.stream().anyMatch(test);
    }
}
