package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.SignalEmitEvent;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SignalTrigger implements Trigger<SignalEmitEvent>{
    @Override
    public String getKey() {
        return "signal";
    }

    @Override
    public Class<SignalEmitEvent> getEvent() {
        return SignalEmitEvent.class;
    }

    @Override
    public boolean shouldTrigger(SignalEmitEvent event, int level, Settings settings) {
        return Objects.equals(settings.getString("signal"), event.getSignal());
    }

    @Override
    public void setValues(SignalEmitEvent event, Map<String, Object> data) {
        List<String> arguments = event.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            String arg = arguments.get(i);
            if (i==0) data.put("api-arg", arg);
            data.put(String.format("api-arg[%d]", i), arg);
        }
    }

    @Override
    public LivingEntity getCaster(SignalEmitEvent event) {
        return event.isSelfHandling() ? event.getEmitter() : event.getReceiver();
    }

    @Override
    public LivingEntity getTarget(SignalEmitEvent event, Settings settings) {
        return settings.getBool("target", false) ? event.getReceiver() : event.getEmitter();
    }
}
