package com.sucy.skill.api.classes;

import com.sucy.skill.api.player.PlayerData;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.ReadOnlySettings;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.data.Click;
import studio.magemonkey.fabled.data.GroupSettings;
import studio.magemonkey.fabled.tree.basic.InventoryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a class in the RPG system
 * @deprecated use {@link studio.magemonkey.fabled.api.classes.FabledClass} instead
 */
@AllArgsConstructor
@Deprecated(forRemoval = true)
public class RPGClass {
    private final FabledClass root;

    public String getName() {
        return root.getName();
    }

    public String getPrefix() {
        return root.getPrefix();
    }

    public void setPrefix(String prefix) {
        root.setPrefix(prefix);
    }

    public ChatColor getPrefixColor() {
        return root.getPrefixColor();
    }

    public InventoryTree getSkillTree() {
        return root.getSkillTree();
    }

    public String getGroup() {
        return root.getGroup();
    }

    public GroupSettings getGroupSettings() {
        return root.getGroupSettings();
    }

    public boolean hasParent() {
        return root.hasParent();
    }

    public RPGClass getParent() {
        return new RPGClass(root.getParent());
    }

    public RPGClass getRoot() {
        return new RPGClass(root.getRoot());
    }

    public ItemStack getIcon() {
        return root.getIcon();
    }

    public Map<String, Skill> getSkillMap() {
        return root.getSkillMap();
    }

    public ItemStack getIcon(PlayerData playerData) {
        return root.getIcon(playerData.getActual());
    }

    public boolean isAllowed(final Player player) {
        return root.isAllowed(player);
    }

    public ItemStack getToolIcon() {
        return root.getToolIcon();
    }

    public String getActionBarText() {
        return root.getActionBarText();
    }

    public void setActionBarText(String actionBarText) {
        root.setActionBarText(actionBarText);
    }

    public boolean hasActionBarText() {
        return root.hasActionBarText();
    }

    public boolean receivesExp(ExpSource source) {
        return root.receivesExp(source);
    }

    public int getMaxLevel() {
        return root.getMaxLevel();
    }

    public int getRequiredExp(int level) {
        return root.getRequiredExp(level);
    }

    public double getHealth(int level) {
        return root.getHealth(level);
    }

    public double getBaseHealth() {
        return root.getBaseHealth();
    }

    public double getHealthScale() {
        return root.getHealthScale();
    }

    public double getMana(int level) {
        return root.getMana(level);
    }

    public double getBaseMana() {
        return root.getBaseMana();
    }

    public double getManaScale() {
        return root.getManaScale();
    }

    public int getAttribute(String key, int level) {
        return root.getAttribute(key, level);
    }

    public ReadOnlySettings getSettings() {
        return root.getSettings();
    }

    public String getManaName() {
        return root.getManaName();
    }

    public List<Skill> getSkills() {
        return root.getSkills();
    }

    public List<Skill> getSkills(boolean includeParents) {
        return root.getSkills(includeParents);
    }

    public boolean hasManaRegen() {
        return root.hasManaRegen();
    }

    public double getManaRegen() {
        return root.getManaRegen();
    }

    public void setManaRegen(double manaRegen) {
        root.setManaRegen(manaRegen);
    }

    public ArrayList<RPGClass> getOptions() {
        ArrayList<RPGClass> classes = new ArrayList<>();
        for (FabledClass fClass : root.getOptions()) {
            classes.add(new RPGClass(fClass));
        }
        return classes;
    }

    public boolean canUse(final Material type) {
        return root.canUse(type);
    }

    public void addSkill(String name) {
        root.addSkill(name);
    }

    public void addSkills(String... names) {
        root.addSkills(names);
    }

    public void setAllowedExpSources(ExpSource... sources) {
        root.setAllowedExpSources(sources);
    }

    public void allowExpSource(ExpSource source) {
        root.allowExpSource(source);
    }

    public void disallowExpSource(ExpSource source) {
        root.disallowExpSource(source);
    }

    public void save(DataSection config) {
        root.save(config);
    }

    public void softSave(DataSection config) {
        root.softSave(config);
    }

    public void load(DataSection config) {
        root.load(config);
    }

    public void reloadSkillTree() {
        root.reloadSkillTree();
    }

    public void arrange() {
        root.arrange();
    }

    public boolean canStartCombo(Click click, @Nullable ItemStack itemStack) {
        return root.canStartCombo(click, itemStack);
    }
}
