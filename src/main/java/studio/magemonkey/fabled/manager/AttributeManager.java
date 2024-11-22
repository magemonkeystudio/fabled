/**
 * Fabled
 * studio.magemonkey.fabled.manager.AttributeManager
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.manager;

import lombok.Getter;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.dynamic.EffectComponent;
import studio.magemonkey.fabled.gui.customization.tool.GUIData;
import studio.magemonkey.fabled.gui.customization.tool.GUIPage;
import studio.magemonkey.fabled.gui.customization.tool.GUITool;
import studio.magemonkey.fabled.log.LogType;
import studio.magemonkey.fabled.log.Logger;

import java.util.*;

/**
 * Handles loading and accessing individual
 * attributes from the configuration.
 */
public class AttributeManager implements IAttributeManager {
    // Keys for supported stat modifiers
    public static final String HEALTH                         = "health";
    public static final String MANA                           = "mana";
    public static final String MANA_REGEN                     = "mana-regen";
    public static final String PHYSICAL_DAMAGE                = "physical-damage";
    public static final String MELEE_DAMAGE                   = "melee-damage";
    public static final String PROJECTILE_DAMAGE              = "projectile-damage";
    public static final String PHYSICAL_DEFENSE               = "physical-defense";
    public static final String MELEE_DEFENSE                  = "melee-defense";
    public static final String PROJECTILE_DEFENSE             = "projectile-defense";
    public static final String SKILL_DAMAGE                   = "skill-damage";
    public static final String SKILL_DEFENSE                  = "skill-defense";
    public static final String MOVE_SPEED                     = "move-speed";
    public static final String ATTACK_SPEED                   = "attack-speed";
    public static final String ARMOR                          = "armor";
    public static final String LUCK                           = "luck";
    public static final String ARMOR_TOUGHNESS                = "armor-toughness";
    public static final String EXPERIENCE                     = "exp";
    public static final String HUNGER                         = "hunger";
    public static final String HUNGER_HEAL                    = "hunger-heal";
    public static final String COOLDOWN                       = "cooldown";
    public static final String KNOCKBACK_RESIST               = "knockback-resist";
    // Attributes as of in 1.21+
    // https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html
    public static final String ATTACK_DAMAGE                  = "attack-damage";
    public static final String ATTACK_KNOCKBACK               = "attack-knockback";
    public static final String FLYING_SPEED                   = "flying-speed";
    public static final String FOLLOW_RANGE                   = "follow-range";
    public static final String ABSORPTION                     = "absorption";
    public static final String SCALE                          = "scale";
    public static final String STEP_HEIGHT                    = "step-height";
    public static final String JUMP_STRENGTH                  = "jump-strength";
    public static final String GRAVITY                        = "gravity";
    public static final String SAFE_FALL_DISTANCE             = "safe-fall-distance";
    public static final String FALL_DAMAGE_MULTIPLIER         = "fall-damage-multiplier";
    public static final String BURNING_TIME                   = "burning-time";
    public static final String EXPLOSION_KNOCKBACK_RESISTANCE = "explosion-knockback-resistance";
    public static final String MOVEMENT_EFFICIENCY            = "movement-efficiency";
    public static final String OXYGEN_BONUS                   = "oxygen-bonus";
    public static final String WATER_MOVEMENT_EFFICIENCY      = "water-movement-efficiency";
    // Player only stats 1.21+
    public static final String BLOCK_INTERACTION_RANGE        = "block-interaction-range";
    public static final String ENTITY_INTERACTION_RANGE       = "entity-interaction-range";
    public static final String BLOCK_BREAK_SPEED              = "block-break-speed";
    public static final String MINING_EFFICIENCY              = "mining-efficiency";
    public static final String SNEAKING_SPEED                 = "sneaking-speed";
    public static final String SUBMERGED_MINING_SPEED         = "submerged-mining-speed";
    public static final String SWEEPING_DAMAGE_RATIO          = "sweeping-damage-ratio";

    /**
     * Unsafe getter for the attribute data.
     * <p>
     * Do not use this method or modify it's return value unless
     * you know exactly what you are doing.
     */
    @Getter
    private final Map<String, FabledAttribute>       attributes  = new LinkedHashMap<>();
    private final Map<String, FabledAttribute>       lookup      = new HashMap<>();
    private final Map<String, List<FabledAttribute>> byStat      = new HashMap<>();
    private final Map<String, List<FabledAttribute>> byComponent = new HashMap<>();

    /**
     * Retrieves an attribute template
     *
     * @param key attribute key
     * @return template for the attribute
     */
    public FabledAttribute getAttribute(String key) {
        return lookup.get(key.toLowerCase());
    }

    public List<FabledAttribute> forStat(final String key) {
        return byStat.get(key);
    }

    public List<FabledAttribute> forComponent(final EffectComponent component, final String key) {
        return byComponent.get(component.getKey() + "-" + key.toLowerCase());
    }

    @Override
    public Set<String> getKeys() {
        return attributes.keySet();
    }

    @Override
    public Set<String> getLookupKeys() {
        return lookup.keySet();
    }

    public String normalize(String key) {
        final FabledAttribute fabledAttribute = lookup.get(key.toLowerCase());
        if (fabledAttribute == null) {
            throw new IllegalArgumentException("Invalid attribute - " + key);
        }
        return fabledAttribute.getKey();
    }

    /**
     * Loads attribute data from the config
     *
     * @param api Fabled reference
     */
    public void load(Fabled api) {
        CommentedConfig config = new CommentedConfig(api, "attributes");
        config.saveDefaultConfig();

        DataSection data = config.getConfig();
        Logger.log(LogType.ATTRIBUTE_LOAD, 1, "Loading attributes...");
        for (String key : data.keys()) {
            Logger.log(LogType.ATTRIBUTE_LOAD, 2, "  - " + key);
            FabledAttribute fabledAttribute = new FabledAttribute(data.getSection(key), key);
            attributes.put(fabledAttribute.getKey(), fabledAttribute);
            lookup.put(fabledAttribute.getKey(), fabledAttribute);
            lookup.put(fabledAttribute.getName().toLowerCase(), fabledAttribute);
        }

        GUIData attribs = GUITool.getAttributesMenu();
        if (!attribs.isValid()) {
            int     i    = 0;
            GUIPage page = attribs.getPage(0);
            for (String key : attributes.keySet()) {
                if (i < 54) {
                    page.set(i++, key);
                }
            }
            attribs.resize((attributes.size() + 8) / 9);
        }
    }

    public void addByComponent(String key, FabledAttribute fabledAttribute) {
        byComponent.computeIfAbsent(key, k -> new ArrayList<>()).add(fabledAttribute);
    }

    public void addByStat(String key, FabledAttribute fabledAttribute) {
        byStat.computeIfAbsent(key, k -> new ArrayList<>()).add(fabledAttribute);
    }
}
