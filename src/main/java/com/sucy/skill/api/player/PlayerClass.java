package com.sucy.skill.api.player;

import com.sucy.skill.api.classes.RPGClass;
import lombok.RequiredArgsConstructor;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.enums.PointSource;
import studio.magemonkey.fabled.api.event.PlayerExperienceLostEvent;

/**
 * Represents a player's class data
 *
 * @deprecated use {@link studio.magemonkey.fabled.api.player.PlayerClass} instead
 */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class PlayerClass {
    private final studio.magemonkey.fabled.api.player.PlayerClass _data;

    public studio.magemonkey.fabled.api.player.PlayerClass getRealClass() {
        return _data;
    }

    /**
     * <p>Retrieves the data of the player owning this class.</p>
     *
     * @return data of owning player
     */
    public PlayerData getPlayerData() {
        return new PlayerData(_data.getPlayerData());
    }

    /**
     * <p>Retrieves the generic data for the class.</p>
     *
     * @return generic data for the class
     */
    public RPGClass getData() {
        return new RPGClass(_data.getData());
    }

    /**
     * <p>Retrieves the experience of the class towards the next level.</p>
     * <p>This should not ever be higher than the required experience.</p>
     *
     * @return the current experience of the class towards the next level
     */
    public double getExp() {
        return _data.getExp();
    }

    /**
     * Sets the current experience for the player
     *
     * @param exp experience to set to
     */
    public void setExp(double exp) {
        _data.setExp(exp);
    }

    /**
     * <p>Retrieves the required experience to level up to the next level.</p>
     *
     * @return the current required experience
     */
    public int getRequiredExp() {
        return _data.getRequiredExp();
    }

    /**
     * <p>Retrieves the total amount of experience the player has accumulated
     * for this class since professing as it.</p>
     *
     * @return total accumulated experience for the class
     */
    public double getTotalExp() {
        return _data.getTotalExp();
    }

    /**
     * <p>Retrieves the current level of the class.</p>
     * <p>This should never be less than 1 or greater than the maximum level.</p>
     *
     * @return current level of the class
     */
    public int getLevel() {
        return _data.getLevel();
    }

    /**
     * Sets the level for the class
     *
     * @param level level to set to
     */
    public void setLevel(int level) {
        _data.setLevel(level);
    }

    /**
     * <p>Retrieves the number of skill points the class has currently available.</p>
     * <p>This should never be a negative number.</p>
     *
     * @return number of available skill points
     */
    public int getPoints() {
        return _data.getPoints();
    }

    /**
     * <p>Sets the amount of points the player's class has without
     * launching an event.</p>
     * <p>This cannot be less than 0.</p>
     * <p>This is used primarily for initialization. You should generally
     * use givePoints(int, PointSource) instead.</p>
     *
     * @param amount number of points to set it to
     */
    public void setPoints(int amount) {
        _data.setPoints(amount);
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                Functional Methods                 //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * <p>Checks whether the class has reached the max level.</p>
     *
     * @return true if max level, false otherwise
     */
    public boolean isLevelMaxed() {
        return _data.isLevelMaxed();
    }

    /**
     * Retrieves the amount of health this class provides the player
     *
     * @return health provided for the player by this class
     */
    public double getHealth() {
        return _data.getHealth();
    }

    /**
     * Retrieves the amount of mana this class provides the player
     *
     * @return mana provided for the player by this class
     */
    public double getMana() {
        return _data.getMana();
    }

    /**
     * <p>Gives skill points to be used for the class.</p>
     * <p>The number of points cannot be negative.</p>
     * <p>This calls an event that can be cancelled or have the number
     * of points modified.</p>
     * <p>This treats the points as coming from the source "SPECIAL".</p>
     *
     * @param amount amount of points to give
     * @throws java.lang.IllegalArgumentException if the points are less than 1
     */
    public void givePoints(int amount) {
        givePoints(amount, PointSource.SPECIAL);
    }

    /**
     * <p>Gives skill points to be used for the class.</p>
     * <p>The number of points cannot be negative.</p>
     * <p>This calls an event that can be cancelled or have the number
     * of points modified.</p>
     *
     * @param amount amount of points to give
     * @param source source of the points
     * @throws java.lang.IllegalArgumentException if the points are less than 1
     */
    public void givePoints(int amount, PointSource source) {
        _data.givePoints(amount, source);
    }

    /**
     * Uses points from the player for skill upgrades.
     *
     * @param amount amount of points to use
     */
    public void usePoints(int amount) {
        _data.usePoints(amount);
    }

    /**
     * <p>Gives experience to the class under the context of the experience source.</p>
     * <p>This will also check for leveling up after the experience is added.</p>
     * <p>If the class does not normally receive experience from the source,
     * it will still launch an experience event, just it will start off as
     * cancelled in case it should still be given in select circumstances.</p>
     *
     * @param amount amount of experience to give
     * @param source type of the source of the experience
     */
    public void giveExp(double amount, ExpSource source) {
        giveExp(amount, source, true);
    }

    /**
     * <p>Gives experience to the class under the context of the experience source.</p>
     * <p>This will also check for leveling up after the experience is added.</p>
     * <p>If the class does not normally receive experience from the source,
     * or the player is already max level, it will still launch an experience event,
     * just it will start off as cancelled in case it should still be given in select circumstances.</p>
     *
     * @param amount      amount of experience to give
     * @param source      type of the source of the experience
     * @param showMessage whether to show the configured message if enabled
     */
    public void giveExp(double amount, ExpSource source, boolean showMessage) {
        _data.giveExp(amount, source, showMessage);
    }

    /**
     * Causes the player to lose experience
     * This will launch a {@link PlayerExperienceLostEvent} event before taking the experience.
     *
     * @param amount      percent of experience to lose
     * @param percent     whether to take the amount as a percentage
     * @param changeLevel whether to lower the level if the exp lost exceeds the current exp,
     *                    or to cap at 0 exp and keep the current level
     */
    public void loseExp(double amount, boolean percent, boolean changeLevel) {
        _data.loseExp(amount, percent, changeLevel);
    }

    /**
     * Causes the player to lose experience
     * This will launch a {@link PlayerExperienceLostEvent} event before taking the experience.
     *
     * @param percent percent of experience to lose
     */
    public void loseExp(double percent) {loseExp(percent, true, false);}

    /**
     * <p>Gives levels to the player's class, leveling it up.</p>
     * <p>The amount of levels must be a positive number.</p>
     * <p>This will launch a level event for the gained levels.</p>
     *
     * @param amount amount of levels to give
     * @throws java.lang.IllegalArgumentException when the level amount is less than 1
     */
    public void giveLevels(int amount) {
        _data.giveLevels(amount);
    }

    public void loseLevels(int amount) {
        _data.loseLevels(amount);
    }
}
