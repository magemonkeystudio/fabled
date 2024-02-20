package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.CastData;
import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class BlockPlaceTrigger implements Trigger<BlockPlaceEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "BLOCK_PLACE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BlockPlaceEvent> getEvent() {
        return BlockPlaceEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final BlockPlaceEvent event, final int level, final Settings settings) {
        final List<String> types = settings.getStringList("material");
        return types.isEmpty() || types.contains("Any")
                || types.stream()
                .anyMatch(mat -> event.getBlock().getType().name().equalsIgnoreCase(mat.replace(' ', '_')));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final BlockPlaceEvent event, final CastData data) {
        data.put("api-block-type", event.getBlock().getType().name());
        data.put("api-block-loc", event.getBlock().getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final BlockPlaceEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final BlockPlaceEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
