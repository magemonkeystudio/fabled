package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.FlagApplyEvent;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlagTrigger implements Trigger<FlagApplyEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "FLAG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<FlagApplyEvent> getEvent() {
        return FlagApplyEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final FlagApplyEvent event, final int level, final Settings settings) {
        final List<String> flags       = settings.getStringList("flags");
        final boolean      inverted    = settings.getBool("inverted", false);
        final double       minDuration = settings.getDouble("min-duration", 0);
        final int          ticks       = (int) (minDuration * 20);

        // Return false if the flag duration is shorter than the required minimum
        if (event.getTicks() < ticks) {
            return false;
        }

        final LivingEntity entity = getCaster(event);
        return (flags.isEmpty()
                || flags.contains("Any")
                || flags.stream().anyMatch(f -> filterFlag(entity, f).equals(event.getFlag()))) != inverted;
    }

    /**
     * Resolves dynamic variables in a flag name using the given entity's cast data.
     * Mirrors EffectComponent#filter — entity acts as both caster and target.
     * Note: for cross-entity skills {player} resolves to the entity that received
     * the flag, not the original caster.
     */
    private String filterFlag(final LivingEntity entity, String text) {
        CastData data  = DynamicSkill.getCastData(entity);
        Pattern  pat   = Pattern.compile("\\{[^{}]+}");
        Matcher  match = pat.matcher(text);
        while (match.find()) {
            String key = match.group().substring(1, match.group().length() - 1);
            if (data.contains(key))            text = text.replace(match.group(), data.get(key));
            else if (key.equals("player"))     text = text.replace(match.group(), entity.getName());
            else if (key.equals("target"))     text = text.replace(match.group(), entity.getName());
            else if (key.equals("targetUUID")) text = text.replace(match.group(), entity.getUniqueId().toString());
            match = pat.matcher(text);
        }
        return text;
    }

    /**
     * {@inheritDoc}
     */
    // Adds flag duration as a usable Value
    @Override
    public void setValues(final FlagApplyEvent event, final CastData data) {
        data.put("api-" + event.getFlag() + "-duration", event.getTicks());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final FlagApplyEvent event) {
        return event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final FlagApplyEvent event, final Settings settings) {
        return event.getEntity();
    }

}
