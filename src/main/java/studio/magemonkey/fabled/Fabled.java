/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package studio.magemonkey.fabled;

import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.manager.api.menu.YAMLMenu;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.CommentedLanguageConfig;
import studio.magemonkey.codex.migration.MigrationUtil;
import studio.magemonkey.codex.registry.attribute.AttributeProvider;
import studio.magemonkey.codex.registry.attribute.AttributeRegistry;
import studio.magemonkey.fabled.api.FabledAttributeProvider;
import studio.magemonkey.fabled.api.armorstand.ArmorStandManager;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.particle.EffectManager;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.data.PlayerStats;
import studio.magemonkey.fabled.data.Settings;
import studio.magemonkey.fabled.data.io.ConfigIO;
import studio.magemonkey.fabled.data.io.IOManager;
import studio.magemonkey.fabled.data.io.SQLIO;
import studio.magemonkey.fabled.dynamic.DynamicClass;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.exception.FabledNotEnabledException;
import studio.magemonkey.fabled.gui.tool.GUITool;
import studio.magemonkey.fabled.hook.PlaceholderAPIHook;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.hook.mimic.MimicHook;
import studio.magemonkey.fabled.listener.*;
import studio.magemonkey.fabled.listener.attribute.AttributeListener;
import studio.magemonkey.fabled.manager.*;
import studio.magemonkey.fabled.task.CooldownTask;
import studio.magemonkey.fabled.task.GUITask;
import studio.magemonkey.fabled.task.ManaTask;
import studio.magemonkey.fabled.task.SaveTask;
import studio.magemonkey.fabled.thread.MainThread;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>The main class of the plugin which has the accessor methods into most of the API</p>
 * <p>You can retrieve a reference to this through Bukkit the same way as any other plugin</p>
 */
public class Fabled extends SkillAPI {
    private static Fabled singleton;
    public static  Random RANDOM = new Random();

    private final Map<String, Skill>          skills  = new HashMap<>();
    private final Map<String, FabledClass>    classes = new HashMap<>();
    private final Map<String, PlayerAccounts> players = new HashMap<>();
    private final List<String>                groups  = new ArrayList<>();

    private final List<FabledListener> listeners = new ArrayList<>();

    private CommentedLanguageConfig language;
    private Settings                settings;

    private IOManager           io;
    private CmdManager          cmd;
    private ComboManager        comboManager;
    private RegistrationManager registrationManager;
    private IAttributeManager   attributeManager = new NullAttributeManager();
    private AttributeProvider   fabledProvider   = null;

    private MainThread mainThread;
    private BukkitTask manaTask;

    private boolean loaded    = false;
    private boolean disabling = false;

    public Fabled() throws IOException {
        super();
    }

    public Fabled(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) throws
            IOException {
        super(loader, description, dataFolder, file);
    }

    /**
     * Checks whether Fabled has all its
     * data loaded and running.
     *
     * @return true if loaded and set up, false otherwise
     */
    public static boolean isLoaded() {
        return singleton != null && singleton.loaded;
    }

    /**
     * @return Fabled singleton if available
     * @throws FabledNotEnabledException if Fabled isn't enabled
     */
    public static Fabled inst() {
        if (singleton == null) {
            throw new FabledNotEnabledException(
                    "Cannot use Fabled methods before it is enabled - add it to your plugin.yml as a dependency");
        }
        return singleton;
    }

    /**
     * Retrieves the settings data controlling Fabled
     *
     * @return Fabled settings data
     */
    public static Settings getSettings() {
        return inst().settings;
    }

    /**
     * Retrieves the language file data for Fabled
     *
     * @return Fabled language file data
     */
    public static CommentedLanguageConfig getLanguage() {
        return inst().language;
    }

    /**
     * Retrieves the manager for click cast combos
     *
     * @return click combo manager
     */
    public static ComboManager getComboManager() {
        return inst().comboManager;
    }

    /**
     * Retrieves the attribute manager for Fabled
     *
     * @return attribute manager
     */
    public static IAttributeManager getAttributeManager() {
        return inst().attributeManager;
    }

    /**
     * Retrieves a skill by name. If no skill is found with the name, null is
     * returned instead.
     *
     * @param name name of the skill
     * @return skill with the name or null if not found
     */
    public static Skill getSkill(String name) {
        if (name == null) {
            return null;
        }
        return inst().skills.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered skill data for Fabled. It is recommended that you
     * don't edit this map. Instead, use "addSkill" and "addSkills" instead.
     *
     * @return the map of registered skills
     */
    public static Map<String, Skill> getSkills() {
        return inst().skills;
    }

    /**
     * Checks whether a skill is registered.
     *
     * @param name name of the skill
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(String name) {
        return getSkill(name) != null;
    }

    /**
     * Checks whether a skill is registered
     *
     * @param skill the skill to check
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(PlayerSkill skill) {
        return isSkillRegistered(skill.getData().getName());
    }

    /**
     * Checks whether a skill is registered
     *
     * @param skill the skill to check
     * @return true if registered, false otherwise
     */
    public static boolean isSkillRegistered(Skill skill) {
        return isSkillRegistered(skill.getName());
    }

    /**
     * Retrieves a class by name. If no skill is found with the name, null is
     * returned instead.
     *
     * @param name name of the class
     * @return class with the name or null if not found
     */
    public static FabledClass getClass(String name) {
        if (name == null) {
            return null;
        }
        return inst().classes.get(name.toLowerCase());
    }

    /**
     * Retrieves the registered class data for Fabled. It is recommended that you
     * don't edit this map. Instead, use "addClass" and "addClasses" instead.
     *
     * @return the map of registered skills
     */
    public static Map<String, FabledClass> getClasses() {
        return inst().classes;
    }

    /**
     * Retrieves a list of base classes that don't profess from another class
     *
     * @return the list of base classes
     */
    public static List<FabledClass> getBaseClasses(String group) {
        List<FabledClass> list = new ArrayList<>();
        for (FabledClass c : singleton.classes.values()) {
            if (!c.hasParent() && c.getGroup().equals(group)) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Checks whether a class is registered.
     *
     * @param name name of the class
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(String name) {
        return getClass(name) != null;
    }

    /**
     * Checks whether a class is registered.
     *
     * @param playerClass the class to check
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(PlayerClass playerClass) {
        return isClassRegistered(playerClass.getData().getName());
    }

    /**
     * Checks whether a class is registered.
     *
     * @param fabledClass the class to check
     * @return true if registered, false otherwise
     */
    public static boolean isClassRegistered(FabledClass fabledClass) {
        return isClassRegistered(fabledClass.getName());
    }

    /**
     * Retrieves the active class data for the player. If no data is found for the
     * player, a new set of data will be created and returned.
     *
     * @param player player to get the data for
     * @return the class data of the player
     */
    public static PlayerData getData(OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        return getPlayerAccounts(player).getActiveData();
    }

    /**
     * Loads the data for a player when they join the server. This is handled
     * by the API and doesn't need to be used elsewhere unless you want to
     * load a player's data without them logging on. This should be run
     * asynchronously since it is loading configuration files.
     *
     * @param player player to load the data for
     */
    public static PlayerAccounts loadPlayerAccounts(OfflinePlayer player) {
        if (player == null) {
            return null;
        }

        // Already loaded for some reason, no need to load again
        String id = player.getUniqueId().toString().toLowerCase();
        if (inst().players.containsKey(id)) {
            return singleton.players.get(id);
        }

        // Load the data
        return doLoad(player);
    }

    private static PlayerAccounts doLoad(OfflinePlayer player) {
        // Load the data
        PlayerAccounts data = singleton.io.loadData(player);
        singleton.players.put(player.getUniqueId().toString(), data);
        return data;
    }

    /**
     * Used to fake player data until SQL data is loaded when both SQL and the SQL delay are enabled.
     * This should not be used by other plugins. If the player data already exists, this does nothing.
     *
     * @param player player to fake data for
     */
    public static void initFakeData(final OfflinePlayer player) {
        inst().players.computeIfAbsent(player.getUniqueId().toString(), id -> new PlayerAccounts(player));
    }

    /**
     * Do not use this method outside onJoin. This will delete any progress a player
     * has made since joining.
     */
    public static void reloadPlayerData(final Player player) {
        doLoad(player);
    }

    /**
     * Saves all player data to the configs. This
     * should be called asynchronously to avoid problems
     * with the main server loop.
     */
    public static void saveData() {
        inst().io.saveAll();
    }

    /**
     * Checks whether Fabled currently has loaded data for the
     * given player. This returning false doesn't necessarily mean the
     * player doesn't have any data at all, just not data that is
     * currently loaded.
     *
     * @param player player to check for
     * @return true if data has loaded, false otherwise
     */
    public static boolean hasPlayerData(OfflinePlayer player) {
        return singleton != null && player != null && singleton.players.containsKey(player.getUniqueId()
                .toString()
                .toLowerCase());
    }

    /**
     * Unloads player data from memory, saving it to the config
     * first and then removing it from the map.
     *
     * @param player player to unload data for
     */
    public static void unloadPlayerData(final OfflinePlayer player) {
        unloadPlayerData(player, false);
    }

    /**
     * Unloads player data from memory, saving it to the config
     * first and then removing it from the map.
     *
     * @param player     player to unload data for
     * @param skipSaving whether to skip saving the data
     *                   before unloading
     */
    public static void unloadPlayerData(final OfflinePlayer player, final boolean skipSaving) {
        if (singleton == null || player == null || singleton.disabling
                || !singleton.players.containsKey(player.getUniqueId().toString().toLowerCase())) {
            return;
        }

        singleton.getServer().getScheduler().runTaskAsynchronously(singleton, () -> {
            PlayerAccounts accounts = getPlayerAccounts(player);
            if (!skipSaving) {
                singleton.io.saveData(accounts);
            }
            singleton.players.remove(player.getUniqueId().toString().toLowerCase());
        });
    }

    /**
     * Retrieves all class data for the player. This includes the active and
     * all inactive accounts the player has. If no data is found, a new set
     * of data will be created and returned.
     *
     * @param player player to get the data for
     * @return the class data of the player
     */
    public static PlayerAccounts getPlayerAccounts(OfflinePlayer player) {
        if (player == null) {
            return null;
        }

        String id = player.getUniqueId().toString().toLowerCase();
        if (!inst().players.containsKey(id)) {
            PlayerAccounts data = loadPlayerAccounts(player);
            singleton.players.put(id, data);
            return data;
        } else {
            return singleton.players.get(id);
        }
    }

    /**
     * Retrieves all the player data of Fabled. It is recommended not to
     * modify this map. Instead, use helper methods within individual player data.
     *
     * @return all Fabled player data
     */
    public static Map<String, PlayerAccounts> getPlayerAccounts() {
        return inst().players;
    }

    /**
     * Retrieves the list of active class groups used by
     * registered classes
     *
     * @return list of active class groups
     */
    public static List<String> getGroups() {
        return inst().groups;
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static BukkitTask schedule(BukkitRunnable runnable, int delay) {
        return runnable.runTaskLater(inst(), delay);
    }

    /**
     * Schedules a delayed task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks
     */
    public static BukkitTask schedule(Runnable runnable, int delay) {
        return Bukkit.getScheduler().runTaskLater(singleton, runnable, delay);
    }

    /**
     * Schedules a repeating task
     *
     * @param runnable the task to schedule
     * @param delay    the delay in ticks before the first tick
     * @param period   how often to run in ticks
     */
    public static BukkitTask schedule(BukkitRunnable runnable, int delay, int period) {
        return runnable.runTaskTimer(inst(), delay, period);
    }

    /**
     * Sets a value to an entity's metadata
     *
     * @param target entity to set to
     * @param key    key to store under
     * @param value  value to store
     */
    public static void setMeta(Metadatable target, String key, Object value) {
        target.setMetadata(key, new FixedMetadataValue(inst(), value));
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     * @return the stored value
     */
    @SuppressWarnings("ConstantValue")
    public static Object getMeta(Metadatable target, String key) {
        List<MetadataValue> meta = target.getMetadata(key);
        return meta == null || meta.isEmpty() ? null : meta.get(0).value();
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     * @return the stored value
     */
    public static int getMetaInt(Metadatable target, String key) {
        return target.getMetadata(key).get(0).asInt();
    }

    /**
     * Retrieves metadata from an entity
     *
     * @param target entity to retrieve from
     * @param key    key the value was stored under
     * @return the stored value
     */
    public static double getMetaDouble(Metadatable target, String key) {
        return target.getMetadata(key).get(0).asDouble();
    }

    /**
     * Removes metadata from an entity
     *
     * @param target entity to remove from
     * @param key    key metadata was stored under
     */
    public static void removeMeta(Metadatable target, String key) {
        target.removeMetadata(key, inst());
    }

    /**
     * Grabs a config for Fabled
     *
     * @param name config file name
     * @return config data
     */
    public static CommentedConfig getConfig(String name) {
        return new CommentedConfig(singleton, name);
    }

    /**
     * Reloads the plugin
     */
    public static void reload() {
        Fabled inst = inst();
        inst.onDisable();
        inst.onEnable();
        YAMLMenu.reloadMenus(inst);
    }

    @Override
    public void onLoad() {
        try {
            MigrationUtil.renameDirectory("plugins/ProSkillAPI", "plugins/Fabled");
            MigrationUtil.replace("plugins/Fabled/config.yml", "%sapi_", "%fabled_");
        } catch (IOException e) {
            getLogger().warning("Failed to migrate ProSkillAPI data to Fabled. " + e.getMessage());
        }

        MimicHook.init(this);
        fabledProvider = new FabledAttributeProvider(this);
        AttributeRegistry.registerProvider(fabledProvider);
    }

    /**
     * <p>Disables Fabled, saving data before unloading everything and disconnecting
     * listeners. This should not be called by other plugins.</p>
     */
    @Override
    public void onDisable() {
        // Validate instance
        if (singleton != this) {
            return;
        }

        disabling = true;

        AttributeRegistry.unregisterProvider(fabledProvider);

        GUITool.cleanUp();
        EffectManager.cleanUp();
        ArmorStandManager.cleanUp();

        mainThread.disable();
        mainThread = null;

        if (manaTask != null) {
            manaTask.cancel();
            manaTask = null;
        }

        for (FabledListener listener : listeners) {
            listener.cleanup();
        }
        listeners.clear();

        // Clear scoreboards
        ClassBoardManager.clearAll();

        // Clear skill bars and stop passives before disabling
        for (Player player : Bukkit.getOnlinePlayers()) {
            MainListener.unload(player);
        }

        io.saveAll();

        skills.clear();
        classes.clear();
        players.clear();

        HandlerList.unregisterAll(this);
        cmd.clear();

        loaded = false;
        disabling = false;
        singleton = null;
    }

    /**
     * <p>Enables Fabled, setting up listeners, managers, and loading data. This
     * should not be called by other plugins.</p>
     */
    @Override
    public void onEnable() {
        // Set up the singleton
        if (singleton != null) {
            throw new IllegalStateException("Cannot enable Fabled twice!");
        }

        String coreVersion = CodexEngine.getEngine().getDescription().getVersion();
        if (!DependencyRequirement.meetsVersion(DependencyRequirement.MIN_CORE_VERSION, coreVersion)) {
            getLogger().warning("Missing required Codex version. " + coreVersion + " installed. "
                    + DependencyRequirement.MIN_CORE_VERSION + " required. Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        singleton = this;

        mainThread = new MainThread();
        EffectManager.init();
        ArmorStandManager.init();

        // Load settings
        settings = new Settings(this);
        settings.reload();
        language = new CommentedLanguageConfig(this, "language");
        language.checkDefaults();
        language.trim();
        language.save();

        // Hook plugins
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            getLogger().info("Fabled hook into PlaceholderAPI: " + ChatColor.GREEN + "success.");
        }
        boolean protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;

        // Set up managers
        comboManager = new ComboManager();
        registrationManager = new RegistrationManager(this);
        cmd = new CmdManager(this);
        io = settings.isUseSql() ? new SQLIO(this) : new ConfigIO(this);
        PlayerStats.init();
        ClassBoardManager.registerText();
        if (settings.isAttributesEnabled()) {
            attributeManager = new AttributeManager();
            ((AttributeManager) attributeManager).load(this);
        }

        // Load classes and skills
        registrationManager.initialize();

        // Load group settings after groups are determined
        settings.loadGroupSettings();

        // Set up listeners
        listen(new BindListener(), true);
        listen(new BuffListener(), true);
        listen(new MainListener(), true);
        listen(new MechanicListener(), true);
        if (protocolLib) listen(new PacketListener(), true);
        listen(new ProjectileListener(), true);
        listen(new ShieldBlockListener(), true);
        listen(new StatusListener(), true);
        listen(new ToolListener(), true);
        listen(new KillListener(), true);
        listen(new AddonListener(), true);
        listen(new ClickListener(), true);
        listen(new BarListener(), settings.isSkillBarEnabled());
        listen(new ComboListener(), settings.isCombosEnabled());
        listen(new AttributeListener(), settings.isAttributesEnabled());
        listen(new ItemListener(), settings.isCheckLore() || settings.isCheckAttributes());
        if (settings.isCastEnabled()) {
            switch (settings.getCastMode()) {
                case ITEM -> {
                    listen(new CastItemListener(), true);
                    listen(new CastOffhandListener(), true);
                }
                case BARS -> {
                    listen(new CastBarsListener(), true);
                    listen(new CastOffhandListener(), true);
                }
                case COMBAT -> {
                    listen(new CastCombatListener(), true);
                    listen(new CastOffhandListener(), true);
                }
                case ACTION_BAR, TITLE, SUBTITLE, CHAT -> listen(new CastTextListener(settings.getCastMode()), true);
            }
        }
        listen(new LingeringPotionListener(), true);
        listen(new ExperienceListener(), settings.isYieldsEnabled());
        listen(new PluginChecker(), true);

        // Set up tasks
        if (settings.isManaEnabled()) {
            manaTask = Bukkit.getScheduler().runTaskTimer(
                    this,
                    new ManaTask(),
                    Fabled.getSettings().getGainFreq(),
                    Fabled.getSettings().getGainFreq()
            );
        }
        if (settings.isSkillBarCooldowns()) {
            MainThread.register(new CooldownTask());
        }
        if (settings.isAutoSave()) {
            MainThread.register(new SaveTask(this));
        }
        MainThread.register(new GUITask(this));

        GUITool.init();

        // Load player data
        players.putAll(io.loadAll());
        for (PlayerAccounts accounts : players.values()) {
            accounts.getActiveData().init(accounts.getPlayer());
        }

        // Must initialize listeners AFTER player data is loaded since the
        // player objects would otherwise change and mess a lot of things up.
        for (FabledListener listener : listeners) {
            listener.init();
        }

        // Copy the quests module if the plugin is loaded.
        if (Bukkit.getServer().getPluginManager().getPlugin("Quests") != null) {
            ResourceManager.copyQuestsModule();
        }

        loaded = true;
    }

    public void listen(FabledListener listener, boolean enabled) {
        if (enabled) {
            // Prevent double listener registering
            for (Iterator<FabledListener> iterator = this.listeners.iterator(); iterator.hasNext(); ) {
                FabledListener listener1 = iterator.next();
                if (listener.getClass().equals(listener1.getClass())) {
                    HandlerList.unregisterAll(listener1);
                    iterator.remove();
                }
            }
            Bukkit.getPluginManager().registerEvents(listener, this);
            this.listeners.add(listener);
        }
    }

    /**
     * This adds a dynamic skill to the skill list. This should
     * not be called by other plugins.
     *
     * @param skill the dynamic skill to register
     */
    public void addDynamicSkill(DynamicSkill skill) {
        if (registrationManager.isAddingDynamicSkills()) {
            skills.put(skill.getName().toLowerCase(), skill);
        } else {
            throw new IllegalStateException("Cannot add dynamic skills from outside Fabled");
        }
    }

    /**
     * Registers a new skill with Fabled. If this is called outside the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerSkills" method.
     *
     * @param skill skill to register
     */
    public void addSkill(Skill skill) {
        skill = registrationManager.validate(skill);
        if (skill != null) {
            skills.put(skill.getName().toLowerCase(), skill);
        }
    }

    /**
     * Registers multiple new skills with Fabled. If this is called outside the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerSkills" method.
     *
     * @param skills skills to register
     */
    public void addSkills(Skill... skills) {
        for (Skill skill : skills) {
            addSkill(skill);
        }
    }

    /**
     * Registers a new class with Fabled. If this is called outside the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerClasses" method.
     *
     * @param fabledClass class to register
     */
    public void addClass(FabledClass fabledClass) {
        fabledClass = registrationManager.validate(fabledClass);
        if (fabledClass != null) {
            classes.put(fabledClass.getName().toLowerCase(), fabledClass);
            ClassBoardManager.registerClass(fabledClass);
            if (!groups.contains(fabledClass.getGroup())) {
                groups.add(fabledClass.getGroup());
            }
        }
    }

    /**
     * Adds a dynamic class which ignores validation. This should only
     * be used by the API as other plugins should use the regular addClass.
     *
     * @param rpgClass dynamic class to add
     */
    public void addDynamicClass(DynamicClass rpgClass) {
        String key;
        if (rpgClass != null && !classes.containsKey(key = rpgClass.getName().toLowerCase())) {
            classes.put(key, rpgClass);
            ClassBoardManager.registerClass(rpgClass);
            if (!groups.contains(rpgClass.getGroup())) {
                groups.add(rpgClass.getGroup());
            }
        }
    }

    /**
     * Registers a new class with Fabled. If this is called outside the method
     * provided in SkillPlugin, this will throw an error. You should implement SkillPlugin
     * in your main class and call this from the provided "registerClasses" method.
     *
     * @param classes classes to register
     */
    public void addClasses(FabledClass... classes) {
        for (FabledClass fabledClass : classes) {
            addClass(fabledClass);
        }
    }
}
