package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class BlockBreakTrigger implements Trigger<BlockBreakEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "BLOCK_BREAK";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BlockBreakEvent> getEvent() {
        return BlockBreakEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final BlockBreakEvent event, final int level, final Settings settings) {
        final List<String> types = settings.getStringList("material");
        return types.isEmpty() || types.contains("Any")
                || types.stream()
                .anyMatch(mat -> event.getBlock().getType().name().equalsIgnoreCase(mat.replace(' ', '_')));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final BlockBreakEvent event, final Map<String, Object> data) {
        data.put("api-block-type", event.getBlock().getType().name());
        data.put("api-block-loc", event.getBlock().getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final BlockBreakEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final BlockBreakEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
