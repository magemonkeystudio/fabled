package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SkillCastTrigger implements Trigger<PlayerCastSkillEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "SKILL_CAST";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerCastSkillEvent> getEvent() {
        return PlayerCastSkillEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerCastSkillEvent event, final int level, final Settings settings) {
        List<String> classes = settings.getStringList("allowed-classes")
                .stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        List<String> skills = settings.getStringList("allowed-skills")
                .stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        List<String> blackClasses = classes.stream().filter(c -> c.startsWith("!"))
                .map(c -> c.substring(1))
                .collect(Collectors.toList());
        List<String> blackSkills = skills.stream().filter(c -> c.startsWith("!"))
                .map(c -> c.substring(1))
                .collect(Collectors.toList());
        classes = classes.stream().filter(c -> !c.startsWith("!")).collect(Collectors.toList());
        skills = skills.stream().filter(c -> !c.startsWith("!")).collect(Collectors.toList());

        String skillName = event.getSkill().getData().getName();
        String className = event.getPlayerData().getMainClass().getData().getName();
        if (!skills.isEmpty() && !skills.contains(skillName)
                || blackSkills.contains(skillName)) {
            return false;
        }

        if (!classes.isEmpty() && !classes.contains(className)
                || blackClasses.contains(className)) {
            return false;
        }

        boolean cancelEvent = settings.getBool("cancel");
        if (cancelEvent) {
            event.setCancelled(true);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerCastSkillEvent event, final CastData data) {
        data.put("api-skill", event.getSkill().getData().getName());
        data.put("api-mana", event.getManaCost());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerCastSkillEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerCastSkillEvent event, final Settings settings) {
        return event.getPlayer();
    }

}
