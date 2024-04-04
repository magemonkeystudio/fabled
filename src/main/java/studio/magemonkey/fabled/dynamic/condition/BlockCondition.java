/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.condition.BlockCondition
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
package studio.magemonkey.fabled.dynamic.condition;

import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockCondition extends ConditionComponent {
    private static final String MATERIAL = "material";
    private static final String STANDING = "standing";

    private Set<String> types;
    private boolean     negated;
    private boolean     in;

    @Override
    public String getKey() {
        return "block";
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);
        final String type = settings.getString(STANDING).toLowerCase();
        negated = type.startsWith("not");
        in = type.endsWith("in block");
        types = settings.getStringList(MATERIAL).stream()
                .map(s -> s.toUpperCase(Locale.US).replace(' ', '_'))
                .collect(Collectors.toSet());
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final Block in     = target.getLocation().getBlock();
        final Block tested = this.in ? in : in.getRelative(BlockFace.DOWN);
        return negated != types.contains(tested.getType().name());
    }
}
