package com.promcteam.fabled.manager;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.api.util.DamageLoreRemover;
import com.promcteam.fabled.api.util.Data;
import com.promcteam.fabled.data.formula.Formula;
import com.promcteam.fabled.data.formula.value.CustomValue;
import com.promcteam.fabled.dynamic.ComponentType;
import com.promcteam.fabled.dynamic.EffectComponent;
import com.promcteam.fabled.gui.tool.IconHolder;
import com.promcteam.fabled.log.LogType;
import com.promcteam.fabled.log.Logger;
import lombok.Getter;
import lombok.Setter;
import com.promcteam.codex.mccore.config.parse.DataSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A single attribute template
 */
public class ProAttribute implements IconHolder {
    private static final String DISPLAY   = "display";
    private static final String GLOBAL    = "global";
    private static final String CONDITION = "condition";
    private static final String MECHANIC  = "mechanic";
    private static final String TARGET    = "target";
    private static final String STATS     = "stats";
    private static final String MAX       = "max";
    private static final String COSTBASE  = "cost_base";
    private static final String COSTMOD   = "cost_modifier";

    /**
     * Retrieves the config key of the attribute
     *
     * @return config key of the attribute
     */
    // Attribute description
    @Getter
    private String    key;
    private String    display;
    private ItemStack icon;
    /**
     * Retrieves the max amount the attribute can be raised to
     *
     * @return max attribute amount
     */
    @Getter
    private int       max;
    /**
     * Retrieves the starting cost of the attribute upgrade.
     *
     * @return costBase amount
     */
    @Getter
    private int       costBase;
    /**
     * Retrieves the raw additional cost of the attribute upgrade.
     * It should be converted to int e.g. using (int) Math.floor function.
     *
     * @return costModifier
     */
    @Getter
    @Setter
    private double    costModifier;

    // Dynamic global modifiers
    private Map<ComponentType, Map<String, AttributeValue[]>> dynamicModifiers = new EnumMap<>(ComponentType.class);

    // General stat modifiers
    private Map<String, Formula> statModifiers = new HashMap<>();

    /**
     * Creates a new attribute, loading the settings from the given
     * config data.
     *
     * @param data config data to load from
     * @param key  the key the attribute was labeled under
     */
    public ProAttribute(DataSection data, String key) {
        this.key = key.toLowerCase();
        this.display = data.getString(DISPLAY, key);
        this.icon = Data.parseIcon(data);
        this.max = data.getInt(MAX, 999);
        // iomatix: base_cost and the modifier
        // e.g. per 0.3 increase -> 0.3=>0, 0.6=>0, 0.9=>0, 1.2=>1 (the first additional cost point) etc.
        this.costBase = data.getInt(COSTBASE, 1);
        this.costModifier = data.getDouble(COSTMOD, 0.0);

        // Load dynamic global settings
        DataSection globals = data.getSection(GLOBAL);
        if (globals != null) {
            loadGroup(globals.getSection(CONDITION), ComponentType.CONDITION);
            loadGroup(globals.getSection(MECHANIC), ComponentType.MECHANIC);
            loadGroup(globals.getSection(TARGET), ComponentType.TARGET);
        }

        // Load stat settings
        DataSection stats = data.getSection(STATS);
        if (stats != null) {
            for (String stat : stats.keys()) {
                loadStatModifier(stats, stat);
            }
        }
    }

    /**
     * Retrieves the name for the attribute
     *
     * @return name of the attribute
     */
    public String getName() {
        return display;
    }

    /**
     * Retrieves the icon for the attribute
     *
     * @return icon of the attribute
     */
    @Override
    public ItemStack getIcon(PlayerData data) {
        ItemStack item     = new ItemStack(icon.getType());
        ItemMeta  iconMeta = icon.getItemMeta();
        ItemMeta  meta     = item.getItemMeta();
        if (meta != null && iconMeta != null) {
            meta.setDisplayName(filter(data, iconMeta.getDisplayName()));
            List<String> iconLore = iconMeta.getLore();
            List<String> lore = iconLore != null
                    ? iconLore.stream().map(iconLine -> filter(data, iconLine)).collect(Collectors.toList())
                    : new ArrayList<>();

            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(((Damageable) iconMeta).getDamage());
            }

            if (iconMeta.hasCustomModelData()) {
                meta.setCustomModelData(iconMeta.getCustomModelData());
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return DamageLoreRemover.removeAttackDmg(item);
    }

    @Override
    public boolean isAllowed(final Player player) {
        return true;
    }

    /**
     * Filters a line of the icon according to the player data
     *
     * @param data player data to use
     * @param text line of text to filter
     * @return filtered line
     */
    private String filter(PlayerData data, String text) {
        return text
                .replace("{amount}", "" + data.getInvestedAttributeStage(key)) // iomatix: it's a stage now.
                .replace("{max}",
                        String.valueOf(this.getMax())) // iomatix: the limit, to verify: is it conflicting with skills' {max} ?
                .replace("{total}", "" + data.getAttribute(key))
                .replace("{cost}", "" + data.getAttributeUpCost(key))
                .replace("{invested}", "" + data.getInvestedAttribute(key)) // iomatix: it's an old {amount}
                .replace("{ap}", "" + data.getAttributePoints()); // iomatix: current value of last attr points

    }

    /**
     * @return icon for the attribute for use in the GUI editor
     */
    public ItemStack getToolIcon() {
        ItemStack icon     = new ItemStack(this.icon.getType());
        ItemMeta  meta     = icon.getItemMeta();
        ItemMeta  iconMeta = this.icon.getItemMeta();
        if (meta == null || iconMeta == null) {
            return icon;
        }
        meta.setDisplayName(key);
        List<String> lore = iconMeta.hasLore()
                ? iconMeta.getLore()
                : null;
        if (lore == null) {
            lore = new ArrayList<>();
        }
        if (iconMeta.hasDisplayName())
            lore.add(0, iconMeta.getDisplayName());
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    /**
     * Modifies a dynamic condition's value
     *
     * @param component component to modify for
     * @param key       key of the value to modify
     * @param value     base value
     * @param amount    amount of attribute points
     * @return modified value
     */
    public double modify(EffectComponent component, String key, double value, int amount) {
        key = component.getKey() + "-" + key.toLowerCase();
        final Map<String, AttributeValue[]> map = dynamicModifiers.get(component.getType());
        if (map.containsKey(key)) {
            AttributeValue[] list = map.get(key);
            for (AttributeValue attribValue : list) {
                if (attribValue.passes(component)) {
                    return attribValue.apply(value, amount);
                }
            }
        }
        return value;
    }

    /**
     * Modifies a stat value
     *
     * @param key    key of the stat
     * @param base   base value of the stat
     * @param amount amount of attribute points
     * @return modified stat value
     */
    public double modifyStat(String key, double base, int amount) {
        if (statModifiers.containsKey(key)) {
            return statModifiers.get(key).compute(base, amount);
        }
        return base;
    }

    /**
     * Loads a dynamic group globals settings into the given map
     *
     * @param data config data to load from
     * @param type the component type to load for
     */
    private void loadGroup(DataSection data, ComponentType type) {
        if (data == null) {
            return;
        }

        final Map<String, AttributeValue[]> target = dynamicModifiers.computeIfAbsent(type, t -> new HashMap<>());
        for (String key : data.keys()) {
            final String lower = key.toLowerCase();
            Logger.log(LogType.ATTRIBUTE_LOAD, 2, "    SkillMod: " + key);
            final String           value    = data.getString(key);
            final String[]         formulas = value.split("\\|");
            final AttributeValue[] values   = new AttributeValue[formulas.length];
            int                    i        = 0;
            for (final String formula : formulas) {
                values[i++] = new AttributeValue(formula);
            }
            target.put(lower, values);
            Fabled.getAttributeManager().addByComponent(lower, this);
        }
    }

    /**
     * Loads a stat modifier from the config data
     *
     * @param data config data to load from
     * @param key  key of the stat modifier
     */
    private void loadStatModifier(DataSection data, String key) {
        if (data.has(key)) {
            Logger.log(LogType.ATTRIBUTE_LOAD, 2, "    StatMod: " + key);
            statModifiers.put(
                    key,
                    new Formula(data.getString(key, "v"), new CustomValue("v"), new CustomValue("a")));

            Fabled.getAttributeManager().addByStat(key, this);
        }
    }
}