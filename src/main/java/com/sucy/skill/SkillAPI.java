package com.sucy.skill;

import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.CommentedLanguageConfig;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.data.Settings;
import studio.magemonkey.fabled.exception.FabledNotEnabledException;
import studio.magemonkey.fabled.manager.ComboManager;
import studio.magemonkey.fabled.manager.IAttributeManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;

/**
 * SkillAPI compatibility class for 3rd party plugins hooking into SkillAPI.
 * This likely is no longer a complete implementation of the original SkillAPI
 * class, but we will do our best to provide the necessary methods for
 * compatibility.
 *
 * @deprecated use {@link Fabled} instead
 */
@Deprecated
public interface SkillAPI {
    /**
     * Checks whether Fabled has all its
     * data loaded and running.
     *
     * @return true if loaded and set up, false otherwise
     * @deprecated use {@link Fabled#isLoaded()}
     */
    @Deprecated(forRemoval = true)
    static boolean isLoaded() {
        return Fabled.isLoaded();
    }

    /**
     * This method no longer returns an instance of the _SkillAPI_ plugin, but instead
     * returns an instanceof the _Fabled_ plugin. We've sort of wrapped the two together
     * to provide increased compatibility, but this method will eventually be removed.
     *
     * @return Fabled singleton if available
     * @throws FabledNotEnabledException if Fabled isn't enabled
     * @deprecated use {@link Fabled#inst()}
     */
    @Deprecated(forRemoval = true)
    static Fabled inst() {
        return Fabled.inst();
    }

    /**
     * Retrieves the settings data controlling Fabled
     *
     * @return Fabled settings data
     * @deprecated use {@link Fabled#getSettings()}
     */
    @Deprecated(forRemoval = true)
    static Settings getSettings() {
        return Fabled.getSettings();
    }

    /**
     * Retrieves the language file data for Fabled
     *
     * @return Fabled language file data
     * @deprecated use {@link Fabled#getLanguage()}
     */
    @Deprecated(forRemoval = true)
    static CommentedLanguageConfig getLanguage() {
        return Fabled.getLanguage();
    }

    /**
     * Retrieves the manager for click cast combos
     *
     * @return click combo manager
     * @deprecated use {@link Fabled#getComboManager()}
     */
    @Deprecated(forRemoval = true)
    static ComboManager getComboManager() {
        return Fabled.getComboManager();
    }

    /**
     * Retrieves the attribute manager for Fabled
     *
     * @return attribute manager
     * @deprecated use {@link Fabled#getAttributeManager()}
     */
    @Deprecated(forRemoval = true)
    static IAttributeManager getAttributeManager() {
        return Fabled.getAttributeManager();
    }

    /**
     * Retrieves a skill by name. If no skill is found with the name, null is
     * returned instead.
     *
     * @param name name of the skill
     * @return skill with the name or null if not found
     * @deprecated use {@link Fabled#getSkill(String)}
     */
    @Deprecated(forRemoval = true)
    static Skill getSkill(String name) {
        return Fabled.getSkill(name);
    }

    /**
     * Retrieves the registered skill data for Fabled. It is recommended that you
     * don't edit this map. Instead, use "addSkill" and "addSkills" instead.
     *
     * @return the map of registered skills
     * @deprecated use {@link Fabled#getSkills()}
     */
    @Deprecated(forRemoval = true)
    static Map<String, Skill> getSkills() {
        return Fabled.getSkills();
    }

    /**
     * Checks whether a skill is registered.
     *
     * @param name name of the skill
     * @return true if registered, false otherwise
     * @deprecated use {@link Fabled#isSkillRegistered(String)}
     */
    @Deprecated(forRemoval = true)
    static boolean isSkillRegistered(String name) {
        return Fabled.isSkillRegistered(name);
    }

    /**
     * Checks whether a skill is registered
     *
     * @param skill the skill to check
     * @return true if registered, false otherwise
     * @deprecated use {@link Fabled#isSkillRegistered(PlayerSkill)}
     */
    @Deprecated(forRemoval = true)
    static boolean isSkillRegistered(PlayerSkill skill) {
        return Fabled.isSkillRegistered(skill);
    }

    /**
     * Checks whether a skill is registered
     *
     * @param skill the skill to check
     * @return true if registered, false otherwise
     * @deprecated use {@link Fabled#isSkillRegistered(Skill)}
     */
    @Deprecated(forRemoval = true)
    static boolean isSkillRegistered(Skill skill) {
        return Fabled.isSkillRegistered(skill);
    }

    /**
     * Retrieves a class by name. If no skill is found with the name, null is
     * returned instead.
     *
     * @param name name of the class
     * @return class with the name or null if not found
     * @deprecated use {@link Fabled#getClass(String)}
     */
    @Deprecated(forRemoval = true)
    static FabledClass getClass(String name) {
        return Fabled.getClass(name);
    }

    /**
     * Retrieves the registered class data for Fabled. It is recommended that you
     * don't edit this map. Instead, use "addClass" and "addClasses" instead.
     *
     * @return the map of registered skills
     * @deprecated use {@link Fabled#getClasses()}
     */
    @Deprecated(forRemoval = true)
    static Map<String, FabledClass> getClasses() {
        return Fabled.getClasses();
    }

    /**
     * Retrieves a list of base classes that don't profess from another class
     *
     * @return the list of base classes
     * @deprecated use {@link Fabled#getBaseClasses(String)}
     */
    @Deprecated(forRemoval = true)
    static List<FabledClass> getBaseClasses(String group) {
        return Fabled.getBaseClasses(group);
    }

    /**
     * Checks whether a class is registered.
     *
     * @param name name of the class
     * @return true if registered, false otherwise
     * @deprecated use {@link Fabled#isClassRegistered(String)}
     */
    @Deprecated(forRemoval = true)
    static boolean isClassRegistered(String name) {
        return Fabled.isClassRegistered(name);
    }

    /**
     * Checks whether a class is registered.
     *
     * @param playerClass the class to check
     * @return true if registered, false otherwise
     * @deprecated use {@link Fabled#isClassRegistered(PlayerClass)}
     */
    @Deprecated(forRemoval = true)
    static boolean isClassRegistered(PlayerClass playerClass) {
        return Fabled.isClassRegistered(playerClass);
    }

    /**
     * Checks whether a class is registered.
     *
     * @param fabledClass the class to check
     * @return true if registered, false otherwise
     * @deprecated use {@link Fabled#isClassRegistered(FabledClass)}
     */
    @Deprecated(forRemoval = true)
    static boolean isClassRegistered(FabledClass fabledClass) {
        return Fabled.isClassRegistered(fabledClass);
    }

    /**
     * Retrieves the active class data for the player. If no data is found for the
     * player, a new set of data will be created and returned.
     *
     * @param player player to get the data for
     * @return the class data of the player
     * @deprecated use {@link Fabled#getPlayerData(OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static PlayerData getPlayerData(OfflinePlayer player) {
        return Fabled.getPlayerData(player);
    }

    /**
     * Loads the data for a player when they join the server. This is handled
     * by the API and doesn't need to be used elsewhere unless you want to
     * load a player's data without them logging on. This should be run
     * asynchronously since it is loading configuration files.
     *
     * @param player player to load the data for
     * @deprecated use {@link Fabled#loadPlayerData(OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static PlayerAccounts loadPlayerData(OfflinePlayer player) {
        return Fabled.loadPlayerData(player);
    }

    /**
     * Used to fake player data until SQL data is loaded when both SQL and the SQL delay are enabled.
     * This should not be used by other plugins. If the player data already exists, this does nothing.
     *
     * @param player player to fake data for
     * @deprecated use {@link Fabled#initFakeData(OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static void initFakeData(final OfflinePlayer player) {
        Fabled.initFakeData(player);
    }

    /**
     * Do not use this method outside onJoin. This will delete any progress a player
     * has made since joining.
     *
     * @deprecated use {@link Fabled#reloadPlayerData(Player)} (OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static void reloadPlayerData(final Player player) {
        Fabled.reloadPlayerData(player);
    }

    /**
     * Saves all player data to the configs. This
     * should be called asynchronously to avoid problems
     * with the main server loop.
     *
     * @deprecated use {@link Fabled#saveData()}
     */
    @Deprecated(forRemoval = true)
    static void saveData() {
        Fabled.saveData();
    }

    /**
     * Checks whether Fabled currently has loaded data for the
     * given player. This returning false doesn't necessarily mean the
     * player doesn't have any data at all, just not data that is
     * currently loaded.
     *
     * @param player player to check for
     * @return true if data has loaded, false otherwise
     * @deprecated use {@link Fabled#hasPlayerData(OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static boolean hasPlayerData(OfflinePlayer player) {
        return Fabled.hasPlayerData(player);
    }

    /**
     * Unloads player data from memory, saving it to the config
     * first and then removing it from the map.
     *
     * @param player player to unload data for
     * @deprecated use {@link Fabled#unloadPlayerData(OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static void unloadPlayerData(final OfflinePlayer player) {
        Fabled.unloadPlayerData(player);
    }

    /**
     * Unloads player data from memory, saving it to the config
     * first and then removing it from the map.
     *
     * @param player     player to unload data for
     * @param skipSaving whether to skip saving the data
     * @deprecated use {@link Fabled#unloadPlayerData(OfflinePlayer, boolean)}
     */
    @Deprecated(forRemoval = true)
    static void unloadPlayerData(final OfflinePlayer player, final boolean skipSaving) {
        Fabled.unloadPlayerData(player, skipSaving);
    }

    /**
     * Retrieves all class data for the player. This includes the active and
     * all inactive accounts the player has. If no data is found, a new set
     * of data will be created and returned.
     *
     * @param player player to get the data for
     * @return the class data of the player
     * @deprecated use {@link Fabled#getPlayerAccountData(OfflinePlayer)}
     */
    @Deprecated(forRemoval = true)
    static PlayerAccounts getPlayerAccountData(OfflinePlayer player) {
        return Fabled.getPlayerAccountData(player);
    }

    /**
     * Retrieves all the player data of Fabled. It is recommended not to
     * modify this map. Instead, use helper methods within individual player data.
     *
     * @return all Fabled player data
     * @deprecated use {@link Fabled#getPlayerAccountData()}
     */
    @Deprecated(forRemoval = true)
    static Map<String, PlayerAccounts> getPlayerAccountData() {
        return Fabled.getPlayerAccountData();
    }

    /**
     * Retrieves the list of active class groups used by
     * registered classes
     *
     * @return list of active class groups
     * @deprecated use {@link Fabled#getGroups()}
     */
    @Deprecated(forRemoval = true)
    static List<String> getGroups() {
        return Fabled.getGroups();
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     * @deprecated use {@link Fabled#schedule(BukkitRunnable, int)}
     */
    @Deprecated(forRemoval = true)
    static BukkitTask schedule(BukkitRunnable runnable, int delay) {
        return Fabled.schedule(runnable, delay);
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     * @deprecated use {@link Fabled#schedule(BukkitRunnable, int)}
     */
    @Deprecated(forRemoval = true)
    static BukkitTask schedule(Runnable runnable, int delay) {
        return Fabled.schedule(runnable, delay);
    }

    /**
     * Schedules a repeating task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks before the first tick
     * @param period   how often to run in ticks
     * @deprecated use {@link Fabled#schedule(BukkitRunnable, int, int)}
     */
    @Deprecated(forRemoval = true)
    static BukkitTask schedule(BukkitRunnable runnable, int delay, int period) {
        return Fabled.schedule(runnable, delay, period);
    }

    /**
     * Sets a value to an entity's metadata
     *
     * @param target entity to set to
     * @param key    key to store under
     * @param value  value to store
     * @deprecated use {@link Fabled#setMeta(Metadatable, String, Object)}
     */
    @Deprecated(forRemoval = true)
    static void setMeta(Metadatable target, String key, Object value) {
        Fabled.setMeta(target, key, value);
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     * @return the stored value
     * @deprecated use {@link Fabled#getMeta(Metadatable, String)}
     */
    @Deprecated(forRemoval = true)
    static Object getMeta(Metadatable target, String key) {
        return Fabled.getMeta(target, key);
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     * @return the stored value
     * @deprecated use {@link Fabled#getMetaInt(Metadatable, String)}
     */
    @Deprecated(forRemoval = true)
    static int getMetaInt(Metadatable target, String key) {
        return Fabled.getMetaInt(target, key);
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     * @return the stored value
     * @deprecated use {@link Fabled#getMetaInt(Metadatable, String)}
     */
    @Deprecated(forRemoval = true)
    static double getMetaDouble(Metadatable target, String key) {
        return Fabled.getMetaDouble(target, key);
    }

    /**
     * Removes metadata from an entity
     *
     * @param target entity to remove from
     * @param key    key metadata was stored under
     * @deprecated use {@link Fabled#removeMeta(Metadatable, String)}
     */
    @Deprecated(forRemoval = true)
    static void removeMeta(Metadatable target, String key) {
        Fabled.removeMeta(target, key);
    }

    /**
     * Grabs a config for Fabled
     *
     * @param name config file name
     * @return config data
     * @deprecated use {@link Fabled#getConfig(String)}
     */
    @Deprecated(forRemoval = true)
    static CommentedConfig getConfig(String name) {
        return Fabled.getConfig(name);
    }
}
