/**
 * Fabled
 * com.promcteam.fabled.api.classes.RPGClass
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.api.classes;

import com.promcteam.codex.CodexEngine;
import com.promcteam.codex.mccore.config.parse.DataSection;
import com.promcteam.codex.mccore.util.TextFormatter;
import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.ReadOnlySettings;
import com.promcteam.fabled.api.Settings;
import com.promcteam.fabled.api.enums.ExpSource;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.api.skills.Skill;
import com.promcteam.fabled.api.util.Data;
import com.promcteam.fabled.data.Click;
import com.promcteam.fabled.data.GroupSettings;
import com.promcteam.fabled.data.Permissions;
import com.promcteam.fabled.gui.tool.IconHolder;
import com.promcteam.fabled.log.LogType;
import com.promcteam.fabled.log.Logger;
import com.promcteam.fabled.tree.basic.InventoryTree;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a template for a class used in the RPG system. This is
 * the class to extend when creating your own classes.
 */
public abstract class FabledClass implements IconHolder {
    private static final String                   SKILLS        = "skills";
    private static final String                   PARENT        = "parent";
    private static final String                   NAME          = "name";
    private static final String                   PREFIX        = "prefix";
    private static final String                   ACTION_BAR    = "action-bar";
    private static final String                   GROUP         = "group";
    private static final String                   MANA          = "mana";
    private static final String                   MAX           = "max-level";
    private static final String                   EXP           = "exp-source";
    private static final String                   REGEN         = "mana-regen";
    private static final String                   PERM          = "needs-permission";
    private static final String                   ATTR          = "attributes";
    private static final String                   OLD_TREE      = "tree";
    private static final String                   TREE          = "skill-tree";
    private static final String                   BLACKLIST     = "blacklist";
    /**
     * The settings for your class. This will include the
     * health and mana scaling for the class.
     */
    protected final      Settings                 settings      = new Settings();
    private final        Map<String, Skill>       skillMap      = new HashMap<>();
    private final        List<Skill>              skills        = new ArrayList<>();
    private final        Set<Material>            blacklist     = new HashSet<>();
    private final        Map<Click, ComboStarter> comboStarters = new HashMap<>();

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Constructors                    //
    //                                                   //
    ///////////////////////////////////////////////////////
    private final ReadOnlySettings readOnlySettings = new ReadOnlySettings(settings);
    /**
     * Whether the class requires permissions
     * in order to be professed into
     */
    @Getter
    protected     boolean          needsPermission;
    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Accessor Methods                  //
    //                                                   //
    ///////////////////////////////////////////////////////
    protected     String           actionBar        = "";
    private       InventoryTree    skillTree;
    private       String           parent;
    private       ItemStack        icon;
    private       TreeType         tree;
    private       String           name;
    private       String           prefix;
    private       String           group;
    private       String           mana;
    private       int              maxLevel;
    private       int              expSources;
    private       double           manaRegen;

    /**
     * Initializes a class template that does not profess from other
     * classes but is rather a starting class.
     *
     * @param name     name of the class
     * @param icon     icon representing the class in menus
     * @param maxLevel max level the class can reach
     */
    protected FabledClass(String name, ItemStack icon, int maxLevel) {
        this(name, icon, maxLevel, null, null);
    }

    /**
     * Initializes a class template that can profess from the parent
     * class when that class reaches its max level.
     *
     * @param name     name of the class
     * @param icon     icon representing the class in menus
     * @param maxLevel max level the class can reach
     * @param parent   parent class to profess from
     */
    protected FabledClass(String name, ItemStack icon, int maxLevel, String parent) {
        this(name, icon, maxLevel, null, parent);
    }

    /**
     * Initializes a class template that can profess from the parent
     * class when that class reaches its max level. The group is
     * the category for the class which determines which classes
     * can be professed into simultaneously. Classes in the same
     * group will not be able to both be professed into at the same
     * time while classes in different groups are able to. For example,
     * a class "Warrior" in the "class" group and an "Elf" class in the
     * "race" group can both be professed as by a player at the same
     * time, giving the player the stats and skills from both.
     *
     * @param name     name of the class
     * @param icon     icon representing the class in menus
     * @param maxLevel max level the class can reach
     * @param group    class group
     * @param parent   parent class to profess from
     */
    protected FabledClass(String name, ItemStack icon, int maxLevel, String group, String parent) {
        this.parent = parent;
        this.icon = icon;
        this.name = name;
        this.prefix = name;
        this.group = group == null ? "class" : group.toLowerCase();
        this.mana = "Mana";
        this.maxLevel = maxLevel;
        this.tree = DefaultTreeType.REQUIREMENT;

        setAllowedExpSources(ExpSource.MOB, ExpSource.COMMAND, ExpSource.QUEST);

        if (this instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) this, Fabled.inst());
        }
    }

    /**
     * Retrieves the name of the class
     *
     * @return class name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the prefix of the class
     *
     * @return class prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix for the class
     *
     * @param prefix class prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Retrieves the color of the class's prefix
     *
     * @return prefix color
     */
    public ChatColor getPrefixColor() {
        String colors = ChatColor.getLastColors(prefix);
        if (colors.length() < 2) {
            return ChatColor.WHITE;
        }
        return ChatColor.getByChar(colors.charAt(1));
    }

    /**
     * Retrieves the skill tree representing the class skills
     *
     * @return class skill tree
     */
    public InventoryTree getSkillTree() {
        return skillTree;
    }

    /**
     * Retrieves the group this class falls in
     *
     * @return class group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Retrieves the settings for the class's group
     *
     * @return settings for the class's group
     */
    public GroupSettings getGroupSettings() {
        return Fabled.getSettings().getGroupSettings(group);
    }

    /**
     * Checks whether the class professes from another class
     *
     * @return true if professes from another class, false otherwise
     */
    public boolean hasParent() {
        return getParent() != null;
    }

    /**
     * Retrieves the parent of this class
     *
     * @return parent of the class or null if none
     */
    public FabledClass getParent() {
        return Fabled.getClass(parent);
    }

    public FabledClass getRoot() {
        FabledClass root = this;
        while (root.parent != null) root = root.getParent();
        return root;
    }

    /**
     * Retrieves the icon representing this class for menus
     *
     * @return icon representation of the class
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * @return map of skills for use in menus
     */
    public Map<String, Skill> getSkillMap() {
        if (skillMap.isEmpty()) {
            FabledClass current = this;
            while (current != null) {
                for (Skill skill : current.skills)
                    skillMap.put(skill.getName().toLowerCase(), skill);
                current = current.getParent();
            }
        }
        return skillMap;
    }

    /**
     * Retrieves the icon representing this class for menus
     *
     * @param data player to get the icon for
     * @return icon representation of the class
     */
    @Override
    public ItemStack getIcon(PlayerData data) {
        return getIcon();
    }

    @Override
    public boolean isAllowed(final Player player) {
        return !needsPermission
                || player.hasPermission(Permissions.CLASS)
                || player.hasPermission(Permissions.CLASS + "." + name.toLowerCase().replace(" ", "-"));
    }

    /**
     * Gets the indicator for the class for the GUI tools
     *
     * @return GUI tool indicator
     */
    public ItemStack getToolIcon() {
        ItemStack item     = new ItemStack(icon.getType());
        ItemMeta  iconMeta = icon.getItemMeta();
        if (iconMeta != null) {
            ItemMeta     meta = item.getItemMeta();
            List<String> lore = iconMeta.hasLore() ? iconMeta.getLore() : new ArrayList<>();
            if (iconMeta.hasDisplayName()) lore.add(0, iconMeta.getDisplayName());

            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * @return text to display in the action bar for the class (nullable)
     */
    public String getActionBarText() {
        return actionBar;
    }

    public void setActionBarText(String text) {
        actionBar = text;
    }

    public boolean hasActionBarText() {
        return actionBar.trim().length() > 0;
    }

    /**
     * Checks whether the class receives experience
     * from the given source
     *
     * @param source source of experience to check
     * @return true if receives experience from the source, false otherwise
     */
    public boolean receivesExp(ExpSource source) {
        return (expSources & source.getId()) != 0;
    }

    /**
     * Retrieves the max level in which this class can reach
     *
     * @return max level this class can reach
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Setting Methods                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Retrieves the required amount of experience this class need to level
     *
     * @param level current level of the class
     * @return required amount of experience to reach the next level
     */
    public int getRequiredExp(int level) {
        return Fabled.getSettings().getRequiredExp(level);
    }

    /**
     * Retrieves the amount of max health this class provides
     *
     * @param level current level of the class
     * @return amount of max health the class provides
     */
    public double getHealth(int level) {
        return settings.getAttr(ClassAttribute.HEALTH, level);
    }

    /**
     * Retrieves the base amount of health for the class
     *
     * @return base amount of health for the class
     */
    public double getBaseHealth() {
        return settings.getBase(ClassAttribute.HEALTH);
    }

    /**
     * Retrieves the amount of health gained per level for the class
     *
     * @return health gained per level
     */
    public double getHealthScale() {
        return settings.getScale(ClassAttribute.HEALTH);
    }

    /**
     * Retrieves the amount of max mana this class provides
     *
     * @param level current level of the class
     * @return amount of max mana the class provides
     */
    public double getMana(int level) {
        return settings.getAttr(ClassAttribute.MANA, level);
    }

    /**
     * Retrieves the base amount of mana for the class
     *
     * @return base amount of mana for the class
     */
    public double getBaseMana() {
        return settings.getBase(ClassAttribute.MANA);
    }

    /**
     * Retrieves the amount of mana gained per level for the class
     *
     * @return mana gained per level
     */
    public double getManaScale() {
        return settings.getScale(ClassAttribute.MANA);
    }

    /**
     * Gets the class attribute amount for the given level
     *
     * @param key   attribute key
     * @param level class level
     * @return attribute amount
     */
    public int getAttribute(String key, int level) {
        return (int) settings.getAttr(key, level, 0);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                    IO Methods                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Retrieves the settings for the class in a read-only format
     *
     * @return settings for the class in a read-only format
     */
    public ReadOnlySettings getSettings() {
        return readOnlySettings;
    }

    /**
     * Retrieves the alias for mana this class uses
     *
     * @return mana alias for the class
     */
    public String getManaName() {
        return mana;
    }

    /**
     * Sets the mana alias for the class
     *
     * @param name mana alias
     */
    public void setManaName(String name) {
        mana = name;
    }

    /**
     * Retrieves the list of skills this class provides a player
     *
     * @return list of skills provided by the class
     */
    public List<Skill> getSkills() {
        return getSkills(true);
    }

    /**
     * Retrieves the list of skills this class provides a player
     *
     * @param includeParent Whether to include the parent skills or not
     * @return list of skills provided by the class
     */
    public List<Skill> getSkills(boolean includeParent) {
        List<Skill> skills = new ArrayList<>();
        skills.addAll(this.skills);
        if (hasParent() && includeParent) skills.addAll(getParent().getSkills());
        return skills;
    }

    /**
     * Checks whether this class has mana regeneration
     *
     * @return true if has mana regeneration, false otherwise
     */
    public boolean hasManaRegen() {
        return manaRegen > 0;
    }

    /**
     * Retrieves the amount of mana regeneration this class has
     *
     * @return mana regeneration per update or a non-positive number if no regeneration
     */
    public double getManaRegen() {
        return manaRegen;
    }

    /**
     * Sets the amount of mana regen this class has
     *
     * @param amount amount of mana regen
     */
    public void setManaRegen(double amount) {
        this.manaRegen = amount;
    }

    /**
     * Retrieves the list of child classes that the player has
     * as options to profess into upon reaching max level.
     *
     * @return list of child classes
     */
    public ArrayList<FabledClass> getOptions() {
        ArrayList<FabledClass> list = new ArrayList<>();
        for (FabledClass c : Fabled.getClasses().values())
            if (c.getParent() == this)
                list.add(c);
        return list;
    }

    public boolean canUse(final Material type) {
        return !blacklist.contains(type);
    }

    /**
     * Adds a skill to the class by name. This will not add it to the
     * skill tree or to players who are already professed as the class.
     *
     * @param name name of the skill
     */
    public void addSkill(String name) {
        Skill skill = Fabled.getSkill(name);
        if (skill != null) {
            skills.add(skill);
        } else {
            Logger.invalid("Class \"" + this.name + "\" tried to add an invalid skill - \"" + name + "\"");
        }
    }

    /**
     * Adds multiple skills to the class by name. This will not add it to
     * the skill tree or to players who are already professed as the class.
     *
     * @param names names of the skills
     */
    public void addSkills(String... names) {
        for (String name : names) {
            addSkill(name);
        }
    }

    /**
     * Sets the experience sources this class can receive experience from.
     *
     * @param sources allowed sources of experience
     */
    public void setAllowedExpSources(ExpSource... sources) {
        expSources = 0;
        for (ExpSource source : sources) {
            allowExpSource(source);
        }
    }

    /**
     * Adds an experience source to the list of allowed sources for the class.
     *
     * @param source allowed source of experience
     */
    public void allowExpSource(ExpSource source) {
        expSources |= source.getId();
    }

    /**
     * Removes an experience source from the list of allowed
     * sources for the class.
     *
     * @param source disallowed source of experience
     */
    public void disallowExpSource(ExpSource source) {
        expSources &= (~source.getId());
    }

    /**
     * Saves the class template data to the config
     *
     * @param config config to save to
     */
    public void save(DataSection config) {
        config.set(NAME, name);
        config.set(ACTION_BAR, actionBar.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(PREFIX, prefix.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(GROUP, group);
        config.set(MANA, mana.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(MAX, maxLevel);
        config.set(PARENT, parent);
        config.set(PERM, needsPermission);
        settings.save(config.createSection(ATTR));
        config.set(REGEN, manaRegen);
        config.set(TREE, tree.toString());
        config.set(BLACKLIST, new ArrayList<Material>(blacklist));

        ArrayList<String> skillNames = new ArrayList<String>();
        for (Skill skill : skills) {
            skillNames.add(skill.getName());
        }
        config.set(SKILLS, skillNames);

        Data.serializeIcon(icon, config);
        config.set(EXP, expSources);

        DataSection comboStartersSection = config.createSection("combo-starters");
        for (Map.Entry<Click, ComboStarter> entry : comboStarters.entrySet()) {
            DataSection  dataSection  = comboStartersSection.createSection(entry.getKey().getKey());
            ComboStarter comboStarter = entry.getValue();
            dataSection.set("inverted", comboStarter.blacklist);
            dataSection.set("whitelist", comboStarter.itemTypes);
        }
    }

    /**
     * Saves some of the class template data to the config, avoiding
     * overwriting any existing data.
     *
     * @param config config to save to
     */
    public void softSave(DataSection config) {
        boolean neededOnly = config.keys().size() > 0;
        if (!neededOnly) {
            save(config);
        }
    }

    /**
     * Loads class template data from the configuration
     *
     * @param config config to load from
     */
    public void load(DataSection config) {
        parent = config.getString(PARENT);
        icon = Data.parseIcon(config);
        name = config.getString(NAME, name);

        ItemMeta iconMeta = icon.getItemMeta();
        if (iconMeta != null && !iconMeta.hasDisplayName()) {
            iconMeta.setDisplayName(name);
            icon.setItemMeta(iconMeta);
        }

        actionBar = TextFormatter.colorString(config.getString(ACTION_BAR, ""));
        prefix = TextFormatter.colorString(config.getString(PREFIX, prefix));
        group = config.getString(GROUP, "class");
        mana = TextFormatter.colorString(config.getString(MANA, mana));
        maxLevel = config.getInt(MAX, maxLevel);
        expSources = config.getInt(EXP, expSources);
        manaRegen = config.getDouble(REGEN, manaRegen);
        needsPermission = config.getString(PERM, needsPermission + "").equalsIgnoreCase("true");
        String skillTree = config.getString(TREE);
        if (skillTree == null) { // Class is using old trees, load it as a custom tree to avoid losing customization
            tree = DefaultTreeType.CUSTOM;
            config.remove(OLD_TREE);
        } else {
            tree = DefaultTreeType.getByName(skillTree);
        }
        for (final String type : config.getList(BLACKLIST)) {
            if (type.isEmpty()) continue;
            final Material mat = Material.matchMaterial(type.toUpperCase(Locale.US).replace(' ', '_'));
            if (mat != null) {
                blacklist.add(mat);
            } else {
                Logger.invalid(type + " is not a valid material for class " + name);
            }
        }

        settings.load(config.getSection(ATTR));

        if (config.isList(SKILLS)) {
            skills.clear();
            for (String name : config.getList(SKILLS)) {
                Skill skill = Fabled.getSkill(name);
                if (skill != null) {
                    skills.add(skill);
                } else Logger.invalid("Invalid skill for class " + this.name + " - " + name);
            }
        }

        comboStarters.clear();
        DataSection section = config.getSection("combo-starters");
        if (section != null) {
            for (String key : section.keys()) {
                Click click = Click.getByName(key);
                if (click == null) continue;
                DataSection subSection = section.getSection(key);
                if (subSection != null) comboStarters.put(click, new ComboStarter(subSection));
            }
        }

        this.skillTree = this.tree.getTree(Fabled.inst(), this);
    }

    public void reloadSkillTree() {
        this.skillTree = this.tree.getTree(Fabled.inst(), this);
        arrange();
    }

    /**
     * Arranges the skill tree for the class
     */
    public void arrange() {
        try {
            Logger.log(LogType.REGISTRATION, 2, "Arranging for \"" + name + "\" - " + skills.size() + " skills");
            this.skillTree.arrange();
        } catch (Exception ex) {
            Logger.invalid("Failed to arrange skill tree for class \"" + name + "\" - " + ex.getMessage());
        }
    }

    public boolean canStartCombo(Click click, @Nullable ItemStack itemStack) {
        ComboStarter comboStarter = comboStarters.get(click);
        if (comboStarter == null) return true;
        return comboStarter.isAllowed(itemStack);
    }

    private static class ComboStarter {
        private final List<String> itemTypes;
        private final boolean      blacklist;

        public ComboStarter(DataSection dataSection) {
            List<String> itemTypes = new ArrayList<>();
            for (String itemType : dataSection.getList("whitelist")) {
                if (!itemTypes.contains(itemType)) itemTypes.add(itemType);
            }
            this.itemTypes = List.copyOf(itemTypes);
            this.blacklist = dataSection.getBoolean("inverted", false);
        }

        public boolean isAllowed(@Nullable ItemStack itemStack) {
            boolean contains = false;
            for (String itemType : this.itemTypes) {
                if (CodexEngine.getEngine().getItemManager().isCustomItemOfId(itemStack, itemType)) {
                    contains = true;
                    break;
                }
            }
            return contains != this.blacklist;
        }
    }
}
