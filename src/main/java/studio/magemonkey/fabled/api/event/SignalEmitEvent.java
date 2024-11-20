package studio.magemonkey.fabled.api.event;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.skills.Skill;

import java.util.List;

@Getter
public class SignalEmitEvent extends Event {

    private static final HandlerList  handlers = new HandlerList();
    private final        Skill        skill;
    private final        LivingEntity emitter;
    private final        LivingEntity receiver;
    private final        String       signal;
    private final        List<Object> arguments;
    private final        boolean      selfHandling;

    /**
     * Event to call custom signal
     *
     * @param skill        skill used to emit signal
     * @param emitter      entity that emitted signal
     * @param receiver     entity that will receive signal
     * @param signal       signal name and encrypted arguments
     * @param arguments    arguments use for handling signal
     * @param selfHandling whether the signal is handling by the emitter or by the receiver
     */
    public SignalEmitEvent(Skill skill,
                           LivingEntity emitter,
                           LivingEntity receiver,
                           String signal,
                           List<Object> arguments,
                           boolean selfHandling) {
        this.skill = skill;
        this.emitter = emitter;
        this.receiver = receiver;
        this.signal = signal;
        this.arguments = arguments;
        this.selfHandling = selfHandling;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
