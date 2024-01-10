/**
 * SkillAPI
 * com.sucy.skill.manager.AttributeManager
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.manager;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.gui.tool.GUIData;
import com.sucy.skill.gui.tool.GUIPage;
import com.sucy.skill.gui.tool.GUITool;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import lombok.Getter;
import mc.promcteam.engine.mccore.config.CommentedConfig;
import mc.promcteam.engine.mccore.config.parse.DataSection;

import java.util.*;

/**
 * Handles loading and accessing individual
 * attributes from the configuration.
 */
public class AttributeManager implements IAttributeManager {
    // Keys for supported stat modifiers
    public static final String HEALTH             = "health";
    public static final String MANA               = "mana";
    public static final String MANA_REGEN         = "mana-regen";
    public static final String PHYSICAL_DAMAGE    = "physical-damage";
    public static final String MELEE_DAMAGE       = "melee-damage";
    public static final String PROJECTILE_DAMAGE  = "projectile-damage";
    public static final String PHYSICAL_DEFENSE   = "physical-defense";
    public static final String MELEE_DEFENSE      = "melee-defense";
    public static final String PROJECTILE_DEFENSE = "projectile-defense";
    public static final String SKILL_DAMAGE       = "skill-damage";
    public static final String SKILL_DEFENSE      = "skill-defense";
    public static final String MOVE_SPEED         = "move-speed";
    public static final String ATTACK_SPEED       = "attack-speed";
    public static final String ARMOR              = "armor";
    public static final String LUCK               = "luck";
    public static final String ARMOR_TOUGHNESS    = "armor-toughness";
    public static final String EXPERIENCE         = "exp";
    public static final String HUNGER             = "hunger";
    public static final String HUNGER_HEAL        = "hunger-heal";
    public static final String COOLDOWN           = "cooldown";
    public static final String KNOCKBACK_RESIST   = "knockback-resist";

    /**
     *  Unsafe getter for the attribute data.
     *  <p>
     *  Do not use this method or modify it's return value unless
     *  you know exactly what you are doing.
     */
    @Getter
    private final Map<String, ProAttribute>       attributes  = new LinkedHashMap<>();
    private final Map<String, ProAttribute>       lookup      = new HashMap<>();
    private final Map<String, List<ProAttribute>> byStat      = new HashMap<>();
    private final Map<String, List<ProAttribute>> byComponent = new HashMap<>();

    /**
     * Sets up the attribute manager, loading the attribute
     * data from the configuration. This is handled by SkillAPI
     * automatically so other plugins should not instantiate
     * this class.
     *
     * @param api SkillAPI reference
     */
    public AttributeManager(SkillAPI api) {
        load(api);
    }

    /**
     * Retrieves an attribute template
     *
     * @param key attribute key
     * @return template for the attribute
     */
    public ProAttribute getAttribute(String key) {
        return lookup.get(key.toLowerCase());
    }

    public List<ProAttribute> forStat(final String key) {
        return byStat.get(key.toLowerCase());
    }

    public List<ProAttribute> forComponent(final EffectComponent component, final String key) {
        return byComponent.get(component.getKey() + "-" + key.toLowerCase());
    }
    public Set<String> getKeys() {
        return attributes.keySet();
    }
    public Set<String> getLookupKeys() {
        return lookup.keySet();
    }

    public String normalize(String key) {
        final ProAttribute proAttribute = lookup.get(key.toLowerCase());
        if (proAttribute == null) {
            throw new IllegalArgumentException("Invalid attribute - " + key);
        }
        return proAttribute.getKey();
    }

    /**
     * Loads attribute data from the config
     *
     * @param api SkillAPI reference
     */
    private void load(SkillAPI api) {
        CommentedConfig config = new CommentedConfig(api, "attributes");
        config.saveDefaultConfig();

        DataSection data = config.getConfig();
        Logger.log(LogType.ATTRIBUTE_LOAD, 1, "Loading attributes...");
        for (String key : data.keys()) {
            Logger.log(LogType.ATTRIBUTE_LOAD, 2, "  - " + key);
            ProAttribute proAttribute = new ProAttribute(data.getSection(key), key);
            attributes.put(proAttribute.getKey(), proAttribute);
            lookup.put(proAttribute.getKey(), proAttribute);
            lookup.put(proAttribute.getName().toLowerCase(), proAttribute);
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

    public void addByComponent(String key, ProAttribute proAttribute) {
        byComponent.computeIfAbsent(key, k -> new ArrayList<>()).add(proAttribute);
    }

    public void addByStat(String key, ProAttribute proAttribute) {
        byStat.computeIfAbsent(key, k -> new ArrayList<>()).add(proAttribute);
    }
}
