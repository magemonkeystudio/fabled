package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * A condition for dynamic skills that requires the text value to match the settings
 */
public class ValueTextCondition extends ConditionComponent {

    private static final String MODE   = "mode";
    private static final String VALUE  = "value";
    private static final String EXPECT = "expect";

    @Override
    public String getKey() {
        return "value text";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        return test(caster, level, null) && executeChildren(caster, level, targets, force);
    }

    @Override
    boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final CompareMode mode   = CompareMode.valueOf(settings.getString(MODE).toUpperCase(Locale.US));
        final Object      value  = DynamicSkill.getCastData(caster).getRaw(settings.getString(VALUE));
        final String      expect = settings.getString(EXPECT);
        if (value == null || expect == null) return false;
        return mode.compare((String) value, expect);
    }

    private enum CompareMode {
        REGEX((r, c) -> Pattern.compile(c).matcher(r).find()),
        EXACTLY(Objects::equals),
        CONTAIN(String::contains),
        START(String::startsWith),
        END(String::endsWith);

        private final BiPredicate<String, String> consumer;

        CompareMode(BiPredicate<String, String> consumer) {
            this.consumer = consumer;
        }

        public boolean compare(String raw, String con) {
            return consumer.test(raw, con);
        }
    }

}
