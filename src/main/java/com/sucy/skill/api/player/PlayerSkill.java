package com.sucy.skill.api.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import studio.magemonkey.fabled.api.enums.SkillStatus;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class PlayerSkill {
    private final studio.magemonkey.fabled.api.player.PlayerSkill _skill;

    public studio.magemonkey.fabled.api.player.PlayerSkill getWrapped() {
        return _skill;
    }

    /**
     * Checks whether the skill is currently unlocked
     * for the player. This requires the skill to be at least
     * level 1.
     *
     * @return true if unlocked, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#isUnlocked()} instead.
     */
    @Deprecated
    public boolean isUnlocked() {
        return _skill.isUnlocked();
    }

    /**
     * Retrieves the template data for this skill.
     *
     * @return skill template data
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getData()} instead.
     */
    @Deprecated
    public Skill getData() {
        return new Skill(_skill.getData());
    }

    /**
     * Retrieves the owning player class.
     *
     * @return owning player class
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getPlayerClass()} instead.
     */
    @Deprecated
    public PlayerClass getPlayerClass() {
        return _skill.getPlayerClass();
    }

    /**
     * Retrieves the owning player's data.
     *
     * @return owning player's data
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getPlayerData()} instead.
     */
    @Deprecated
    public PlayerData getPlayerData() {
        return _skill.getPlayerData();
    }

    /**
     * Retrieves the material this skill is currently bound to.
     *
     * @return the current material bound to or null if not bound
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getBind()} instead.
     */
    @Deprecated
    public Material getBind() {
        return _skill.getBind();
    }

    /**
     * Retrieves the current level the player has the skill at
     *
     * @return current skill level
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getLevel()} instead.
     */
    @Deprecated
    public int getLevel() {
        return _skill.getLevel();
    }

    /**
     * Retrieves whether the skill was added by an external plugin
     *
     * @return whether the skill was added by an external plugin
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#isExternal()} instead.
     */
    @Deprecated
    public boolean isExternal() {
        return _skill.isExternal();
    }

    /**
     * Retrieves the cost to upgrade the skill to the next level
     *
     * @return cost to upgrade the skill to the next level
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getCost()} instead.
     */
    @Deprecated
    public int getCost() {
        return _skill.getCost();
    }

    /**
     * @return total invested cost in the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getInvestedCost()} instead.
     */
    @Deprecated
    public int getInvestedCost() {
        return _skill.getInvestedCost();
    }

    /**
     * @return mana cost to use the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getManaCost()} instead.
     */
    @Deprecated
    public double getManaCost() {
        return _skill.getManaCost();
    }

    /**
     * Retrieves the level requirement of the skill to get to the next level
     *
     * @return the level requirement to get to the next level
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getLevelReq()} instead.
     */
    @Deprecated
    public int getLevelReq() {
        return _skill.getLevelReq();
    }

    /**
     * Checks whether the skill is currently on cooldown
     *
     * @return true if on cooldown, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#isOnCooldown()} instead.
     */
    @Deprecated
    public boolean isOnCooldown() {
        return _skill.isOnCooldown();
    }

    /**
     * Checks whether the skill is at its maximum level
     *
     * @return true if at max level, false otherwise
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#isMaxed()} instead.
     */
    @Deprecated
    public boolean isMaxed() {
        return _skill.isMaxed();
    }

    /**
     * Gets the current cooldown of the skill in seconds.
     *
     * @return current cooldown in seconds or 0 if not on cooldown
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getCooldownLeft()} instead.
     */
    @Deprecated
    public int getCooldownLeft() {
        return _skill.getCooldownLeft();
    }

    /**
     * Retrieves the current ready status of the skill which could
     * be on cooldown, missing mana, or ready.
     *
     * @return the ready status of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getStatus()} instead.
     */
    @Deprecated
    public SkillStatus getStatus() {
        return _skill.getStatus();
    }

    /**
     * Adds levels to the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param amount number of levels to add
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#addLevels(int)} instead.
     */
    @Deprecated
    public void addLevels(int amount) {
        _skill.addLevels(amount);
    }

    /**
     * Sets the bind material of the skill
     *
     * @param mat new bind material
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#setBind(Material)} instead.
     */
    @Deprecated
    public void setBind(Material mat) {
        _skill.setBind(mat);
    }

    /**
     * Reverts the skill back to level 0, locking it from
     * casting and refunding invested skill points
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#revert()} instead.
     */
    @Deprecated
    public void revert() {
        _skill.revert();
    }

    /**
     * Starts the cooldown of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#startCooldown()} instead.
     */
    @Deprecated
    public void startCooldown() {
        _skill.startCooldown();
    }

    /**
     * Refreshes the cooldown of the skill, allowing the
     * player to cast the skill again.
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#refreshCooldown()} instead.
     */
    @Deprecated
    public void refreshCooldown() {
        _skill.refreshCooldown();
    }

    /**
     * Subtracts from the current cooldown time, shortening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to subtract from the cooldown
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#subtractCooldown(double)} instead.
     */
    @Deprecated
    public void subtractCooldown(double seconds) {
        _skill.subtractCooldown(seconds);
    }

    /**
     * Adds to the current cooldown time, lengthening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to add to the cooldown
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#addCooldown(double)} instead.
     */
    @Deprecated
    public void addCooldown(double seconds) {
        _skill.addCooldown(seconds);
    }

    /**
     * Starts the skill preview effects
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#startPreview()} instead.
     */
    @Deprecated
    public void startPreview() {
        _skill.startPreview();
    }

    /**
     * Sets the level of the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param level new level of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#setLevel(int)} instead.
     */
    @Deprecated
    public void setLevel(int level) {
        _skill.setLevel(level);
    }

    /**
     * Sets the cooldown of the skill
     *
     * @param cooldown new cooldown of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#setCooldown(long)} instead.
     */
    @Deprecated
    public void setCooldown(long cooldown) {
        _skill.setCooldown(cooldown);
    }

    /**
     * Gets the cooldown of the skill
     *
     * @return the cooldown of the skill
     * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerSkill#getCooldown()} instead.
     */
    @Deprecated
    public long getCooldown() {
        return _skill.getCooldown();
    }

    public int getPoints() {
        return _skill.getPlayerData().getPoints();
    }
}