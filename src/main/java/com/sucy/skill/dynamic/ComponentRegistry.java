package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.condition.*;
import com.sucy.skill.dynamic.custom.CustomComponent;
import com.sucy.skill.dynamic.custom.CustomEffectComponent;
import com.sucy.skill.dynamic.custom.EditorOption;
import com.sucy.skill.dynamic.mechanic.*;
import com.sucy.skill.dynamic.mechanic.value.*;
import com.sucy.skill.dynamic.mechanic.warp.*;
import com.sucy.skill.dynamic.target.*;
import com.sucy.skill.dynamic.trigger.*;
import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.ComponentRegistry
 */
public class ComponentRegistry {

    static final Map<ComponentType, Map<String, Class<?>>> COMPONENTS = new EnumMap<>(ComponentType.class);

    static final         Map<String, Trigger<?>>        TRIGGERS  = new HashMap<>();
    private static final Map<Trigger<?>, EventExecutor> EXECUTORS = new HashMap<>();

    static {

        // Triggers
        register(new ArmorEquipTrigger());
        register(new BlockBreakTrigger());
        register(new BlockPlaceTrigger());
        register(new ChatTrigger());
        register(new ClickLeftTrigger());
        register(new ClickRightTrigger());
        register(new CrouchTrigger());
        register(new DeathTrigger());
        register(new DropItemTrigger());
        register(new EntityTargetTrigger());
        register(new EnvironmentalTrigger());
        register(new FishingBiteTrigger());
        register(new FishingFailTrigger());
        register(new FishingFishTrigger());
        register(new FishingGrabTrigger());
        register(new FishingGroundTrigger());
        register(new FishingReelTrigger());
        register(new HealTrigger());
        register(new ItemSwapTrigger());
        register(new KillTrigger());
        register(new LandTrigger());
        register(new LaunchTrigger());
        register(new MoveTrigger());
        register(new PhysicalDealtTrigger());
        register(new PhysicalTakenTrigger());
        register(new ProjectileHitTrigger());
        register(new ProjectileTickTrigger());
        register(new SkillDealtTrigger());
        register(new SkillTakenTrigger());
        register(new ShieldTrigger());
        register(new SignalTrigger());
        register(new SkillCastTrigger());
        register(new ConsumeTrigger());

        // Targets
        register(new AreaTarget());
        register(new ConeTarget());
        register(new LinearTarget());
        register(new LocationTarget());
        register(new NearestTarget());
        register(new OffsetTarget());
        register(new RememberTarget());
        register(new SelfTarget());
        register(new SingleTarget());

        // Conditions
        register(new AltitudeCondition());
        register(new ArmorCondition());
        register(new AttributeCondition());
        register(new BiomeCondition());
        register(new BlockCondition());
        register(new BurningCondition());
        register(new CastLevelCondition());
        register(new CeilingCondition());
        register(new ChanceCondition());
        register(new ClassCondition());
        register(new ClassLevelCondition());
        register(new CombatCondition());
        register(new CrouchCondition());
        register(new DirectionCondition());
        register(new ElevationCondition());
        register(new ElseCondition());
        register(new EntityTypeCondition());
        register(new FireCondition());
        register(new FlagCondition());
        register(new FoodCondition());
        register(new GroundCondition());
        register(new HealthCondition());
        register(new InventoryCondition());
        register(new ItemCondition());
        register(new LightCondition());
        register(new LoreCondition());
        register(new ManaCondition());
        register(new MoneyCondition());
        register(new MountedCondition());
        register(new MountingCondition());
        register(new MythicMobTypeCondition());
        register(new NameCondition());
        register(new OffhandCondition());
        register(new PermissionCondition());
        register(new PotionCondition());
        register(new SkillLevelCondition());
        register(new SlotCondition());
        register(new StatusCondition());
        register(new TimeCondition());
        register(new ToolCondition());
        register(new ValueCondition());
        register(new ValueTextCondition());
        register(new WaterCondition());
        register(new WeatherCondition());
        register(new WorldCondition());

        // Mechanics
        register(new ArmorMechanic());
        register(new ArmorStandMechanic());
        register(new ArmorStandPoseMechanic());
        register(new ArmorStandRemoveMechanic());
        register(new AttributeMechanic());
        register(new BlockMechanic());
        register(new BuffMechanic());
        register(new CancelEffectMechanic());
        register(new CancelMechanic());
        register(new ChannelMechanic());
        register(new CleanseMechanic());
        register(new CommandMechanic());
        register(new CooldownMechanic());
        register(new DamageMechanic());
        register(new DamageBuffMechanic());
        register(new DamageLoreMechanic());
        register(new DefenseBuffMechanic());
        register(new DelayMechanic());
        register(new DisguiseMechanic());
        register(new DurabilityMechanic());
        register(new ExperienceMechanic());
        register(new ExplosionMechanic());
        register(new FireMechanic());
        register(new FlagMechanic());
        register(new FlagClearMechanic());
        register(new FlagToggleMechanic());
        register(new FoodMechanic());
        register(new ForgetTargetsMechanic());
        register(new HealMechanic());
        register(new HealthSetMechanic());
        register(new HeldItemMechanic());
        register(new ImmunityMechanic());
        register(new InterruptMechanic());
        register(new InvisibilityMechanic());
        register(new ItemDropMechanic());
        register(new ItemMechanic());
        register(new ItemProjectileMechanic());
        register(new ItemRemoveMechanic());
        register(new LaunchMechanic());
        register(new LightningMechanic());
        register(new ManaMechanic());
        register(new MessageMechanic());
        register(new MineMechanic());
        register(new MoneyMechanic());
        register(new MountMechanic());
        register(new ParticleMechanic());
        register(new ParticleAnimationMechanic());
        register(new ParticleEffectMechanic());
        register(new ParticleProjectileMechanic());
        register(new PassiveMechanic());
        register(new PermissionMechanic());
        register(new PotionMechanic());
        register(new PotionProjectileMechanic());
        register(new ProjectileMechanic());
        register(new PurgeMechanic());
        register(new PushMechanic());
        register(new RememberTargetsMechanic());
        register(new RepeatMechanic());
        register(new SignalEmitMechanic());
        register(new SkillCastMechanic());
        register(new StatMechanic());
        register(new SoundMechanic());
        register(new StatusMechanic());
        register(new SummonMechanic());
        register(new TauntMechanic());
        register(new ThrowMechanic());
        register(new TriggerMechanic());
        register(new ValueAddMechanic());
        register(new ValueAttributeMechanic());
        register(new ValueCopyMechanic());
        register(new ValueDistanceMechanic());
        register(new ValueHealthMechanic());
        register(new ValueLoadMechanic());
        register(new ValueLocationMechanic());
        register(new ValueLoreMechanic());
        register(new ValueLoreSlotMechanic());
        register(new ValueManaMechanic());
        register(new ValueMultiplyMechanic());
        register(new ValuePlaceholderMechanic());
        register(new ValueRandomMechanic());
        register(new ValueRoundMechanic());
        register(new ValueSetMechanic());
        register(new WarpMechanic());
        register(new WarpLocMechanic());
        register(new WarpRandomMechanic());
        register(new WarpSwapMechanic());
        register(new WarpTargetMechanic());
        register(new WarpValueMechanic());
        register(new WolfMechanic());
    }

    public static Map<ComponentType, Map<String, Class<?>>> getComponents() {
        return Collections.unmodifiableMap(COMPONENTS);
    }

    public static Map<String, Trigger<?>> getTriggers() {
        return Collections.unmodifiableMap(TRIGGERS);
    }

    public static Map<Trigger<?>, EventExecutor> getExecutors() {
        return Collections.unmodifiableMap(EXECUTORS);
    }

    public static Trigger<?> getTrigger(final String key) {
        return TRIGGERS.get(key.toUpperCase(Locale.US).replace(' ', '_'));
    }

    static EffectComponent getComponent(final ComponentType type, final String key) {
        final Class<?> componentClass = COMPONENTS.get(type).get(key.toLowerCase());
        if (componentClass == null) {
            throw new IllegalArgumentException("Invalid component key - " + key);
        }
        try {
            return (EffectComponent) componentClass.getDeclaredConstructor().newInstance();
        } catch (final Exception ex) {
            throw new IllegalArgumentException("Invalid component - does not have a default constructor");
        }
    }

    static EventExecutor getExecutor(final Trigger<?> trigger) {
        return EXECUTORS.get(trigger);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void register(final Trigger<T> trigger) {
        String key = trigger.getKey().toUpperCase(Locale.US).replace(' ', '_');

        if (getTrigger(key) != null) {
            throw new IllegalArgumentException("Trigger with key " + key + " already exists");
        } else if (key.contains("-")) {
            throw new IllegalArgumentException(key + " is not a valid key: must not contain dashes");
        }

        TRIGGERS.put(key, trigger);
        EXECUTORS.put(trigger, (listener, event) -> {
            if (!trigger.getEvent().isInstance(event)) return;
            ((TriggerHandler) listener).apply((T) event, trigger);
        });
    }

    public static void register(final CustomEffectComponent component) {
        register((EffectComponent) component);
    }

    public static void save() {
        final StringBuilder builder = new StringBuilder("[");
        TRIGGERS.values().forEach(trigger -> append(trigger, builder));
        COMPONENTS.forEach((type, map) -> map.keySet().forEach(key -> append(getComponent(type, key), builder)));
        if (builder.length() > 2) {
            builder.replace(builder.length() - 1, builder.length(), "]");
        } else {
            builder.append(']');
        }

        final File file = new File(SkillAPI.inst().getDataFolder(), "tool-config.json");
        try (final FileOutputStream out = new FileOutputStream(file)) {
            final BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            write.write(builder.toString());
            write.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    private static void append(final Object obj, final StringBuilder builder) {
        if (!(obj instanceof CustomComponent)) {
            return;
        }

        final CustomComponent component = (CustomComponent) obj;
        builder.append("{\"type\":\"")
                .append(component.getType().name())
                .append("\",\"key\":\"")
                .append(component.getKey())
                .append("\",\"display\":\"")
                .append(component.getDisplayName())
                .append("\",\"container\":\"")
                .append(component.isContainer())
                .append("\",\"description\":\"")
                .append(component.getDescription())
                .append("\",\"options\":[");

        boolean first = true;
        for (EditorOption option : component.getOptions()) {
            if (!first) {
                builder.append(',');
            }
            first = false;

            builder.append("{\"type\":\"")
                    .append(option.type)
                    .append("\",\"key\":\"")
                    .append(option.key)
                    .append("\",\"display\":\"")
                    .append(option.name)
                    .append("\",\"description\":\"")
                    .append(option.description)
                    .append("\"");
            option.extra.forEach((key, value) -> builder.append(",\"").append(key).append("\":").append(value));
            builder.append("}");
        }

        builder.append("]},");
    }

    private static void register(final EffectComponent component) {
        COMPONENTS.computeIfAbsent(component.getType(), t -> new HashMap<>())
                .put(component.getKey().toLowerCase(), component.getClass());
    }
}
