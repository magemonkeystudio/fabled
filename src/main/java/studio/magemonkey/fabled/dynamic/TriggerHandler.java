package studio.magemonkey.fabled.dynamic;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ManaCost;
import studio.magemonkey.fabled.api.event.DynamicTriggerEvent;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.dynamic.trigger.ChatTrigger;
import studio.magemonkey.fabled.dynamic.trigger.Trigger;
import studio.magemonkey.fabled.dynamic.trigger.TriggerComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static studio.magemonkey.fabled.dynamic.ComponentRegistry.getExecutor;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.TriggerHandler
 */
public class TriggerHandler implements Listener {

    private final Map<Integer, Integer> active = new HashMap<>();

    private final DynamicSkill     skill;
    @Getter
    private final String           key;
    @Getter
    private final Trigger<?>       trigger;
    private final TriggerComponent component;

    public TriggerHandler(final DynamicSkill skill,
                          final String key,
                          final Trigger<?> trigger,
                          final TriggerComponent component) {

        Objects.requireNonNull(skill, "Must provide a skill");
        Objects.requireNonNull(key, "Must provide a key");
        Objects.requireNonNull(trigger, "Must provide a trigger");
        Objects.requireNonNull(component, "Must provide a component");

        this.skill = skill;
        this.key = key;
        this.trigger = trigger;
        this.component = component;
    }

    public EffectComponent getComponent() {
        return component;
    }

    public void init(final LivingEntity entity, final int level) {
        active.put(entity.getEntityId(), level);
    }

    public void cleanup(final LivingEntity entity) {
        active.remove(entity.getEntityId());
        component.cleanUp(entity);
    }

    /**
     * Registers needed events for the skill, ignoring any unused events for efficiency
     *
     * @param plugin plugin reference
     */
    public void register(final Fabled plugin) {
        plugin.getServer()
                .getPluginManager()
                .registerEvent(trigger.getEvent(),
                        this,
                        EventPriority.HIGHEST,
                        getExecutor(trigger),
                        (Plugin) plugin,
                        !trigger.getEvent().getTypeName().equals("org.bukkit.event.player.PlayerInteractEvent")
                                && !trigger.getEvent()
                                .getTypeName()
                                .contains("PlayerSwapHandItemsEvent"));

    }

    <T extends Event> void apply(final T event, final Trigger<T> trigger) {

        final LivingEntity caster = trigger.getCaster(event);
        if (caster == null || !active.containsKey(caster.getEntityId())) {
            return;
        }

        final int level = active.get(caster.getEntityId());
        if (!trigger.shouldTrigger(event, level, component.settings)) {
            return;
        }

        if (trigger instanceof ChatTrigger) {
            // KNOWN CAVEAT: ChatTrigger is called Asynchronously. In order to properly call
            // child components, we need to make subsequent calls _synchronously_.
            // This effectively means that ChatTriggers won't be able to cancel the original
            // AsyncPlayerChatEvent.
            Bukkit.getScheduler()
                    .runTask((Plugin) Fabled.inst(),
                            () -> {
                                Bukkit.getPluginManager()
                                        .callEvent(new DynamicTriggerEvent(caster,
                                                this.skill,
                                                event,
                                                trigger.getKey()));

                                final LivingEntity target = trigger.getTarget(event, component.settings);
                                trigger.setValues(event, DynamicSkill.getCastData(caster));
                                trigger(caster, target, level);

                                trigger.postProcess(event, skill);
                            });
        } else {
            Bukkit.getPluginManager()
                    .callEvent(new DynamicTriggerEvent(caster,
                            this.skill,
                            event,
                            trigger.getKey()));

            final LivingEntity target = trigger.getTarget(event, component.settings);
            trigger.setValues(event, DynamicSkill.getCastData(caster));
            trigger(caster, target, level);

            if (event instanceof Cancellable) {
                skill.applyCancelled((Cancellable) event);
            }
            trigger.postProcess(event, skill);
        }
    }

    boolean trigger(final LivingEntity user, final LivingEntity target, final int level) {
        if (user == null || target == null || component.isRunning() || !Fabled.getSettings().isValidTarget(target)) {
            return false;
        }

        if (user instanceof Player) {
            final PlayerData  data  = Fabled.getData((Player) user);
            final PlayerSkill skill = data.getSkill(this.skill.getName());
            final boolean     cd    = component.getSettings().getBool("cooldown", false);
            final boolean     mana  = component.getSettings().getBool("mana", false);

            if ((cd || mana) && !data.check(skill, cd, mana)) {
                return false;
            }

            // TODO Make sure that FALSE is appropriate here.
            if (component.trigger(user, target, level, false)) {
                if (cd) {
                    skill.startCooldown();
                }
                if (mana) {
                    data.useMana(skill.getManaCost(), ManaCost.SKILL_CAST);
                }

                return true;
            } else {
                return false;
            }
        } else {
            return component.trigger(user, target, level, false);
        }
    }
}
