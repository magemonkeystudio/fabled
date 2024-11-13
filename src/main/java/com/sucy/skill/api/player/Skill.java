package com.sucy.skill.api.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.ReadOnlySettings;

import java.util.List;

/**
 * Represents a template for a skill used in the RPG system. This is
 * the class to extend when creating your own custom skills.
 * @deprecated use {@link studio.magemonkey.fabled.api.skills.Skill} instead
 */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class Skill {
    @Getter
    private final studio.magemonkey.fabled.api.skills.Skill wrapped;

    /**
     *  Checks whether the current damage event is due to
     *  skills damaging an entity. This method is used by the API
     *  and shouldn't be used by other plugins.
     *
     * @return true if caused by a skill, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#isSkillDamage()} instead.
     */
    @Deprecated(forRemoval = true)
    public static boolean isSkillDamage() {
        return studio.magemonkey.fabled.api.skills.Skill.isSkillDamage();
    }

    /**
     * Checks whether the skill has been assigned
     * a click combination.
     *
     * @return true if has a combo, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#hasCombo()} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean hasCombo() {
        return wrapped.hasCombo();
    }

    /**
     * Checks whether the skill can automatically
     * level up to the next stage.
     *
     * @param level - the current level of the skill
     * @return true if skill can level up automatically to the next level, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#canAutoLevel(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean canAutoLevel(final int level) {
        return wrapped.canAutoLevel(level);
    }

    /**
     * Checks whether the skill has a message to display when cast.
     *
     * @return true if has a message, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#hasMessage()} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean hasMessage() {
        return wrapped.hasMessage();
    }

    /**
     * Clears the set combo for the skill.
     * Only the API should call this.
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#clearCombo()} instead.
     */
    @Deprecated(forRemoval = true)
    public void clearCombo() {
        wrapped.clearCombo();
    }

    /**
     * Checks whether the skill needs a permission for a player to use it.
     *
     * @return true if the skill requires a permission to use
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#needsPermission()} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean needsPermission() {
        return wrapped.needsPermission();
    }

    /**
     * Checks whether the skill requires another before leveling up
     *
     * @return true if the skill requires another skill, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#hasSkillReq()} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean hasSkillReq() {
        return wrapped.hasSkillReq();
    }

    /**
     * Retrieves the skill's description
     *
     * @return description of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getDescription()} instead.
     */
    @Deprecated(forRemoval = true)
    public List<String> getDescription() {
        return wrapped.getDescription();
    }

    /**
     * Retrieves the level requirement for the skill to reach the next level
     *
     * @param level current level of the skill
     * @return level requirement for the next level
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getLevelReq(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public int getLevelReq(int level) {
        return wrapped.getLevelReq(level);
    }

    /**
     * Retrieves the mana cost of the skill
     *
     * @param level current level of the skill
     * @return mana cost
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getManaCost(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public double getManaCost(int level) {
        return wrapped.getManaCost(level);
    }

    /**
     * Retrieves the cooldown of the skill in seconds
     *
     * @param level current level of the skill
     * @return cooldown
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getCooldown(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public double getCooldown(int level) {
        return wrapped.getCooldown(level);
    }

    /**
     * Checks whether a message is sent when attempting to run the skill while in cooldown
     *
     * @return true if the message is sent, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#cooldownMessage()} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean cooldownMessage() {
        return wrapped.cooldownMessage();
    }

    /**
     * Retrieves the range of the skill in blocks
     *
     * @param level current level of the skill
     * @return target range
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getRange(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public double getRange(int level) {
        return wrapped.getRange(level);
    }

    /**
     * Retrieves the skill point cost of the skill
     *
     * @param level current level of the skill
     * @return skill point cost
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getCost(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public int getCost(int level) {
        return wrapped.getCost(level);
    }

    /**
     * Retrieves the settings for the skill in a read-only format
     *
     * @return settings for the skill in a read-only format
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getSettings()} instead.
     */
    @Deprecated(forRemoval = true)
    public ReadOnlySettings getSettings() {
        return wrapped.getSettings();
    }

    /**
     * Checks whether this skill can be cast by players
     *
     * @return true if can be cast, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#canCast()} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean canCast() {
        return wrapped.canCast();
    }

    /**
     * Gets the indicator for the skill for the GUI tools
     *
     * @return GUI tool indicator
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getToolIndicator()} instead.
     */
    @Deprecated(forRemoval = true)
    public ItemStack getToolIndicator() {
        return wrapped.getToolIndicator();
    }

    /**
     * Fetches the icon for the skill for the player
     *
     * @param data player to get for
     * @return the skill icon
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getIcon(studio.magemonkey.fabled.api.player.PlayerData)} instead.
     */
    @Deprecated(forRemoval = true)
    public ItemStack getIcon(PlayerData data) {
        return wrapped.getIcon(data.getWrapped());
    }

    /**
     * Checks if the player is allowed to use the skill
     *
     * @param player player to check
     * @return true if allowed, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#isAllowed(Player)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean isAllowed(final Player player) {
        return wrapped.isAllowed(player);
    }

    /**
     * Checks if the player has the required skill dependency
     *
     * @param playerData player data to check
     * @return true if has dependency, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#hasDependency(studio.magemonkey.fabled.api.player.PlayerData)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean hasDependency(final PlayerData playerData) {
        return wrapped.hasDependency(playerData.getWrapped());
    }

    /**
     * Checks if the skill is compatible with the player's other skills
     *
     * @param playerData player data to check
     * @return true if compatible, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#isCompatible(studio.magemonkey.fabled.api.player.PlayerData)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean isCompatible(final PlayerData playerData) {
        return wrapped.isCompatible(playerData.getWrapped());
    }

    /**
     * Checks if the player has invested enough points in the skill
     *
     * @param playerData player data to check
     * @return true if invested enough, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#hasInvestedEnough(studio.magemonkey.fabled.api.player.PlayerData)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean hasInvestedEnough(final PlayerData playerData) {
        return wrapped.hasInvestedEnough(playerData.getWrapped());
    }

    /**
     * Checks if the player has enough attributes for the skill
     *
     * @param playerData player data to check
     * @return true if has enough attributes, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#hasEnoughAttributes(studio.magemonkey.fabled.api.player.PlayerData)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean hasEnoughAttributes(final PlayerData playerData) {
        return wrapped.hasEnoughAttributes(playerData.getWrapped());
    }

    /**
     * Checks if the player has a specific attribute for the skill
     *
     * @param playerData player data to check
     * @param key attribute key to check
     * @return true if has the attribute, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#checkSingleAttribute(studio.magemonkey.fabled.api.player.PlayerData, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean checkSingleAttribute(final PlayerData playerData, String key) {
        return wrapped.checkSingleAttribute(playerData.getWrapped(), key);
    }

    /**
     * Retrieves the indicator for the skill while applying filters to match
     * the player-specific data.
     *
     * @param skillData player data
     * @param brief whether to show brief information
     * @return filtered skill indicator
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getIndicator(studio.magemonkey.fabled.api.player.PlayerSkill, boolean)} instead.
     */
    @Deprecated(forRemoval = true)
    public ItemStack getIndicator(PlayerSkill skillData, boolean brief) {
        return wrapped.getIndicator(skillData.getWrapped(), brief);
    }

    /**
     * Retrieves the indicator for the skill while applying filters to match
     * @param skillData
     * @return
     */
    @Deprecated(forRemoval = true)
    public ItemStack getIndicator(PlayerSkill skillData) {
        return wrapped.getIndicator(skillData.getWrapped(), false);
    }

    /**
     * Sends the skill message if one is present from the player to entities
     * within the given radius.
     *
     * @param player player to project the message from
     * @param radius radius to include targets of the message
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#sendMessage(Player, double)} instead.
     */
    @Deprecated(forRemoval = true)
    public void sendMessage(Player player, double radius) {
        wrapped.sendMessage(player, radius);
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#damage(LivingEntity, double, LivingEntity)} instead.
     */
    @Deprecated(forRemoval = true)
    public void damage(LivingEntity target, double damage, LivingEntity source) {
        wrapped.damage(target, damage, source);
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     * @param classification type of damage to deal
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#damage(LivingEntity, double, LivingEntity, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public void damage(LivingEntity target, double damage, LivingEntity source, String classification) {
        wrapped.damage(target, damage, source, classification);
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     * @param classification type of damage to deal
     * @param knockback whether the damage should apply knockback
     * @param ignoreDivinity whether the skill's damage should use divinity's overrides
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#damage(LivingEntity, double, LivingEntity, String, boolean, boolean)} instead.
     */
    @Deprecated(forRemoval = true)
    public void damage(LivingEntity target,
                       double damage,
                       LivingEntity source,
                       String classification,
                       boolean knockback,
                       boolean ignoreDivinity) {
        wrapped.damage(target, damage, source, classification, knockback, ignoreDivinity);
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     * @param classification type of damage to deal
     * @param knockback whether the damage should apply knockback
     * @param ignoreDivinity whether the skill's damage should use divinity's overrides
     * @param cause the cause of the damage, might affect death messages
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#damage(LivingEntity, double, LivingEntity, String, boolean, boolean, EntityDamageEvent.DamageCause)} instead.
     */
    @Deprecated(forRemoval = true)
    public void damage(LivingEntity target,
                       double damage,
                       LivingEntity source,
                       String classification,
                       boolean knockback,
                       boolean ignoreDivinity,
                       EntityDamageEvent.DamageCause cause) {
        wrapped.damage(target, damage, source, classification, knockback, ignoreDivinity, cause);
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     * and keeping the damage version compatible.
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#trueDamage(LivingEntity, double, LivingEntity)} instead.
     */
    @Deprecated(forRemoval = true)
    public void trueDamage(LivingEntity target, double damage, LivingEntity source) {
        wrapped.trueDamage(target, damage, source);
    }

    /**
     * Starts the skill's preview effects.
     * Removal of any registered listeners, tasks, entities, or other
     * temporary effects should be included in a {@link Runnable}
     * passed to {@link studio.magemonkey.fabled.api.player.PlayerData#setOnPreviewStop(Runnable)}
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#playPreview(studio.magemonkey.fabled.api.player.PlayerData, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public void playPreview(PlayerData playerData, int level) {
        wrapped.playPreview(playerData.getWrapped(), level);
    }

    /**
     * Saves the skill data to the configuration, overwriting all previous data
     *
     * @param config config to save to
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#save(DataSection)} instead.
     */
    @Deprecated(forRemoval = true)
    public void save(DataSection config) {
        wrapped.save(config);
    }

    /**
     * Saves some skill data to the config, avoiding
     * overwriting any pre-existing data
     *
     * @param config config to save to
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#softSave(DataSection)} instead.
     */
    @Deprecated(forRemoval = true)
    public void softSave(DataSection config) {
        wrapped.softSave(config);
    }

    /**
     * Loads skill data from the configuration
     *
     * @param config config to load from
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#load(DataSection)} instead.
     */
    @Deprecated(forRemoval = true)
    public void load(DataSection config) {
        wrapped.load(config);
    }

    /**
     * Retrieves the configuration key for the skill
     *
     * @return configuration key for the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getKey()} instead.
     */
    @Deprecated(forRemoval = true)
    public String getKey() {
        return wrapped.getKey();
    }

    /**
     * Retrieves the indicator representing the skill for menus
     *
     * @return indicator for the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getIndicator()} instead.
     */
    @Deprecated(forRemoval = true)
    public ItemStack getIndicator() {
        return wrapped.getIndicator();
    }

    /**
     * Retrieves the name of the skill
     *
     * @return skill name
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getName()} instead.
     */
    @Deprecated(forRemoval = true)
    public String getName() {
        return wrapped.getName();
    }

    /**
     * Retrieves the descriptive type of the skill
     *
     * @return descriptive type of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getType()} instead.
     */
    @Deprecated(forRemoval = true)
    public String getType() {
        return wrapped.getType();
    }

    /**
     * Retrieves the message for the skill to display when cast.
     *
     * @return cast message of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getMessage()} instead.
     */
    @Deprecated(forRemoval = true)
    public String getMessage() {
        return wrapped.getMessage();
    }

    /**
     * Retrieves the skill required to be upgraded before this one
     *
     * @return required skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getSkillReq()} instead.
     */
    @Deprecated(forRemoval = true)
    public String getSkillReq() {
        return wrapped.getSkillReq();
    }

    /**
     * Retrieves the max level the skill can reach
     *
     * @return max skill level
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getMaxLevel()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getMaxLevel() {
        return wrapped.getMaxLevel();
    }

    /**
     * Retrieves the level of the required skill needed to be obtained
     * before this one can be upgraded.
     *
     * @return required skill level
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getSkillReqLevel()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getSkillReqLevel() {
        return wrapped.getSkillReqLevel();
    }

    /**
     * Retrieves the ID of the skill's combo
     *
     * @return combo ID
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#getCombo()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getCombo() {
        return wrapped.getCombo();
    }

    /**
     * Sets the click combo for the skill
     *
     * @param combo new combo
     * @deprecated Use {@link studio.magemonkey.fabled.api.skills.Skill#setCombo(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public void setCombo(int combo) {
        wrapped.setCombo(combo);
    }
}
