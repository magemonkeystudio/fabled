package com.promcteam.fabled.dynamic.trigger;

import com.promcteam.fabled.api.CastData;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.event.PlayerAttributeChangeEvent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class AttributeChangeTrigger implements Trigger<PlayerAttributeChangeEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "ATTRIBUTE_CHANGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerAttributeChangeEvent> getEvent() {
        return PlayerAttributeChangeEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerAttributeChangeEvent event, final int level, final Settings settings) {
        List<String> attrs = settings.getStringList("attr");
        if (attrs.isEmpty() || attrs.get(0).equals("Any")) return true;

        return attrs.stream().anyMatch(attr -> attr.equalsIgnoreCase(event.getAttribute()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerAttributeChangeEvent event, final CastData data) {
        data.put("api-attribute", event.getAttribute());
        data.put("api-change", event.getChange());
        data.put("api-value",
                event.getPlayerData().getInvestedAttributeStage(event.getAttribute()) + event.getChange());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerAttributeChangeEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerAttributeChangeEvent event, final Settings settings) {
        return event.getPlayer();
    }

}
