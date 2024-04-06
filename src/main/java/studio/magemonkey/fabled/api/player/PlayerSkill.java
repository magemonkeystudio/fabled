/**
 * Fabled
 * studio.magemonkey.fabled.api.player.PlayerSkill
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
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
package studio.magemonkey.fabled.api.player;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.SkillStatus;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.manager.AttributeManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * Represents player-specific data for a skill such as the player's
 * current level for the skill, the cooldown, and other related data.
 */
public final class PlayerSkill {

    private Skill       skill;
    private PlayerData  player;
    private PlayerClass parent;
    @Getter
    @Setter
    private long        cooldown;
    private int         level;
    private boolean     external;

    /**
     * Constructs a new PlayerSkill. You should not need to use
     * this constructor as it is provided by the API. Get instances
     * through the PlayerData object.
     *
     * @param player owning player data
     * @param skill  skill template
     * @param parent owning player class
     */
    public PlayerSkill(PlayerData player, Skill skill, PlayerClass parent) {
        this.player = player;
        this.skill = skill;
        this.parent = parent;
        this.external = false;
    }

    /**
     * Constructs a new PlayerSkill. You should not need to use
     * this constructor as it is provided by the API. Get instances
     * through the PlayerData object.
     *
     * @param player   owning player data
     * @param skill    skill template
     * @param parent   owning player class
     * @param external whether the skill was added by an external plugin
     */
    public PlayerSkill(PlayerData player, Skill skill, PlayerClass parent, boolean external) {
        this.player = player;
        this.skill = skill;
        this.parent = parent;
        this.external = external;
    }

    /**
     * Checks whether the skill is currently unlocked
     * for the player. This requires the skill to be at least
     * level 1.
     *
     * @return true if unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return level > 0;
    }

    /**
     * Retrieves the template data for this skill.
     *
     * @return skill template data
     */
    public Skill getData() {
        return skill;
    }

    /**
     * Retrieves the owning player class.
     *
     * @return owning player class
     */
    public PlayerClass getPlayerClass() {
        return parent;
    }

    /**
     * Retrieves the owning player's data.
     *
     * @return owning player's data
     */
    public PlayerData getPlayerData() {
        return player;
    }

    /**
     * Retrieves the material this skill is currently bound to.
     *
     * @return the current material bound to or null if not bound
     */
    public Material getBind() {
        return null;
    }

    /**
     * Retrieves the current level the player has the skill at
     *
     * @return current skill level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Retrieves whether the skill was added by an external plugin
     *
     * @return whether the skill was added by an external plugin
     */
    public boolean isExternal() {return external;}

    /**
     * Retrieves the cost to upgrade the skill to the next level
     *
     * @return cost to upgrade the skill to the next level
     */
    public int getCost() {
        return skill.getCost(level);
    }

    /**
     * @return total invested cost in the skill
     */
    public int getInvestedCost() {
        int total = 0;
        for (int i = 0; i < level; i++)
            total += skill.getCost(i);
        return total;

        /* Could assume the linearly scaling cost, but API allows overrides
        int x0 = skill.getCost(0);
        int dx = skill.getCost(1) - x0;
        return (x0 - dx) * level + dx * (level - 1) * level / 2;
        */
    }

    /**
     * @return mana cost to use the skill
     */
    public double getManaCost() {
        return skill.getManaCost(level);
    }

    /**
     * Retrieves the level requirement of the skill to get to the next level
     *
     * @return the level requirement to get to the next level
     */
    public int getLevelReq() {
        return skill.getLevelReq(level);
    }

    /**
     * Checks whether the skill is currently on cooldown
     *
     * @return true if on cooldown, false otherwise
     */
    public boolean isOnCooldown() {
        return cooldown > System.currentTimeMillis();
    }

    /**
     * Checks whether the skill is at its maximum level
     *
     * @return true if at max level, false otherwise
     */
    public boolean isMaxed() {
        return level >= skill.getMaxLevel();
    }

    /**
     * Gets the current cooldown of the skill in seconds.
     *
     * @return current cooldown in seconds or 0 if not on cooldown
     */
    public int getCooldownLeft() {
        if (isOnCooldown()) {
            return (int) ((cooldown - System.currentTimeMillis() + 999) / 1000);
        } else {
            return 0;
        }
    }

    /**
     * Retrieves the current ready status of the skill which could
     * be on cooldown, missing mana, or ready.
     *
     * @return the ready status of the skill
     */
    public SkillStatus getStatus() {

        // See if it is on cooldown
        if (isOnCooldown()) {
            return SkillStatus.ON_COOLDOWN;
        }

        // If mana is enabled, check to see if the player has enough
        if (Fabled.getSettings().isManaEnabled()
                && player.getMana() < skill.getManaCost(level)) {

            return SkillStatus.MISSING_MANA;
        }

        // The skill is available when both off cooldown and when there's enough mana
        return SkillStatus.READY;
    }

    /**
     * Sets the level of the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param level new level of the skill
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Adds levels to the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param amount number of levels to add
     */
    public void addLevels(int amount) {
        this.level = Math.min(this.level + amount, skill.getMaxLevel());
    }

    /**
     * Sets the bind material of the skill
     *
     * @param mat new bind material
     */
    @Deprecated
    public void setBind(Material mat) {}

    /**
     * Reverts the skill back to level 0, locking it from
     * casting and refunding invested skill points
     */
    public void revert() {
        parent.givePoints(getInvestedCost());
        level = 0;
    }

    /**
     * Starts the cooldown of the skill
     */
    public void startCooldown() {
        long cd = (long) player.scaleStat(AttributeManager.COOLDOWN, skill.getCooldown(level) * 1000L);
        cooldown = System.currentTimeMillis() + cd;
    }

    /**
     * Refreshes the cooldown of the skill, allowing the
     * player to cast the skill again.
     */
    public void refreshCooldown() {
        cooldown = 0;
    }

    /**
     * Subtracts from the current cooldown time, shortening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to subtract from the cooldown
     */
    public void subtractCooldown(double seconds) {
        addCooldown(-seconds);
    }

    /**
     * Adds to the current cooldown time, lengthening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to add to the cooldown
     */
    public void addCooldown(double seconds) {
        if (isOnCooldown())
            cooldown += (int) (seconds * 1000);
        else
            cooldown = System.currentTimeMillis() + (int) (seconds * 1000);
    }

    /**
     * Starts the skill preview effects
     */
    public void startPreview() {
        skill.playPreview(player, level);
    }
}
