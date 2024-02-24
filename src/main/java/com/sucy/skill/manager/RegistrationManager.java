/**
 * SkillAPI
 * com.sucy.skill.manager.RegistrationManager
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
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.dynamic.ComponentRegistry;
import com.sucy.skill.dynamic.DynamicClass;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.util.ConfigurationException;
import mc.promcteam.engine.mccore.config.CommentedConfig;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <p>Skill API Registration Manager.</p>
 * <p>This handles loading skill and class data from cofiguration files and fetching
 * them from other plugins while validating everything to make sure it should be
 * added.</p>
 */
public class RegistrationManager {
    private static final String          SKILL_FOLDER = "dynamic" + File.separator + "skill";
    private static final String          CLASS_FOLDER = "dynamic" + File.separator + "class";
    private static final String          SKILL_DIR    = SKILL_FOLDER + File.separator;
    private static final String          CLASS_DIR    = CLASS_FOLDER + File.separator;
    private final        SkillAPI        api;
    private final        CommentedConfig skillConfig;
    private final        CommentedConfig classConfig;
    private              Mode            mode         = Mode.STARTUP;

    /**
     * <p>Creates a new Registration Manager for handling registering new
     * classes or skills.</p>
     *
     * @param api SkillAPI reference
     */
    public RegistrationManager(SkillAPI api) {
        this.api = api;
        skillConfig = new CommentedConfig(api, "dynamic" + File.separator + "skills");
        classConfig = new CommentedConfig(api, "dynamic" + File.separator + "classes");
        new File(api.getDataFolder()
                .getAbsolutePath() + File.separator + "dynamic" + File.separator + "skill").mkdirs();
        new File(api.getDataFolder()
                .getAbsolutePath() + File.separator + "dynamic" + File.separator + "class").mkdirs();
    }

    /**
     * Initializes the registration manager, fetching skills and classes from
     * configuration files and other plugins.
     */
    public void initialize() {

        // Make sure dynamic files are created
        if (!skillConfig.getConfigFile().exists()) {
            skillConfig.save();
        }
        if (!classConfig.getConfigFile().exists()) {
            classConfig.save();
        }

        Logger.log(LogType.REGISTRATION, 1, "Loading components...");

        for (Plugin plugin : api.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                try {
                    Logger.log(LogType.REGISTRATION, 2, " - " + plugin.getName());
                    ((SkillPlugin) plugin).getTriggers().forEach(ComponentRegistry::register);
                    ((SkillPlugin) plugin).getComponents().forEach(ComponentRegistry::register);
                } catch (Throwable t) {
                    Logger.invalid("Plugin \"" + plugin.getName() + "\" failed to register skills. Error details:");
                    t.printStackTrace();
                }
            }
        }
        ComponentRegistry.save();

        Logger.log(LogType.REGISTRATION, 1, "Loading skills...");

        // Request plugin skills
        mode = Mode.SKILL;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                try {
                    Logger.log(LogType.REGISTRATION, 2, " - " + plugin.getName());
                    ((SkillPlugin) plugin).registerSkills(api);
                } catch (Throwable t) {
                    Logger.invalid("Plugin \"" + plugin.getName() + "\" failed to register skills. Error details:");
                    t.printStackTrace();
                }
            }
        }

        // Load individual dynamic skills first
        mode = Mode.DYNAMIC;
        Logger.log(LogType.REGISTRATION, 1, "Loading individual dynamic skill files...");
        File skillRoot = new File(api.getDataFolder().getPath() + File.separator + SKILL_FOLDER);
        if (skillRoot.exists()) {
            Path skillRootPath = skillRoot.toPath();
            try {
                Files.walk(skillRootPath).forEach(path -> {
                    String longName = getQualifiedFileName(skillRootPath, path);
                    if (longName == null) return;
                    String name = path.getFileName().toString().replace(".yml", "");
                    try {
                        CommentedConfig sConfig = new CommentedConfig(api, SKILL_DIR + longName);
                        DynamicSkill    skill   = new DynamicSkill(name);
                        DataSection     section = getBaseSection(sConfig, name);
                        skill.load(section);
                        if (!SkillAPI.isSkillRegistered(skill.getName())) {
                            api.addDynamicSkill(skill);
                            skill.registerEvents(api);
                            sConfig.clear();
                            skill.save(sConfig.getConfig().createSection(name));
                            skill.save(skillConfig.getConfig().createSection(name));
                            sConfig.save();
                            Logger.log(LogType.REGISTRATION, 2, "Loaded the dynamic skill: " + name);
                        } else if (SkillAPI.getSkill(name) instanceof DynamicSkill) {
                            Logger.log(LogType.REGISTRATION, 3, name + " is already loaded, skipping it");
                        } else {
                            Logger.invalid("Duplicate skill detected: " + name);
                        }
                    } catch (Exception ex) {
                        Logger.invalid("Failed to load skill: " + name + " - " + ex.getMessage());
                        if (ex instanceof NullPointerException)
                            Logger.log(
                                    "Please check that " + (SKILL_DIR + name) + ".yml exists and has proper contents");
                        ex.printStackTrace();
                    }
                });
            } catch (IOException ex) {
                Logger.invalid("Failed to read dynamic skills: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Load dynamic skills from skills.yml
        if (!skillConfig.getConfig().getBoolean("loaded", false)) {
            Logger.log(LogType.REGISTRATION, 1, "Loading dynamic skills from skills.yml...");
            skillConfig.getConfig().set("loaded", true);
            for (String key : skillConfig.getConfig().keys()) {
                if (!skillConfig.getConfig().isSection(key)) {
                    Logger.log(
                            LogType.REGISTRATION,
                            3,
                            "Skipping \"" + key + "\" because it isn't a configuration section");
                    continue;
                }
                try {
                    DynamicSkill skill = new DynamicSkill(key);
                    skill.load(skillConfig.getConfig().getSection(key));
                    if (!SkillAPI.isSkillRegistered(skill.getName())) {
                        api.addDynamicSkill(skill);
                        skill.registerEvents(api);
                        CommentedConfig sConfig = new CommentedConfig(api, SKILL_DIR + key);
                        sConfig.clear();
                        skill.save(sConfig.getConfig().createSection(key));
                        skill.save(skillConfig.getConfig().createSection(key));
                        sConfig.save();
                        Logger.log(LogType.REGISTRATION, 2, "Loaded the dynamic skill: " + key);
                    } else {
                        Logger.invalid("Duplicate skill detected: " + key);
                    }
                } catch (Exception ex) {
                    Logger.invalid("Failed to load skill: " + key + " - " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            Logger.log(LogType.REGISTRATION, 1, "skills.yml doesn't have any changes, skipping it");
        }

        Logger.log(LogType.REGISTRATION, 1, "Loading classes...");

        // Request plugin classes
        mode = Mode.CLASS;
        for (Plugin plugin : api.getServer().getPluginManager().getPlugins()) {
            if (plugin instanceof SkillPlugin) {
                Logger.log(LogType.REGISTRATION, 2, " - " + plugin.getName());
                try {
                    ((SkillPlugin) plugin).registerClasses(api);
                } catch (Throwable t) {
                    Logger.invalid("Plugin \"" + plugin.getName() + "\" failed to register classes. Error details:");
                    t.printStackTrace();
                }
            }
        }

        // Load individual dynamic classes first
        Logger.log(LogType.REGISTRATION, 1, "Loading individual dynamic class files...");
        File classRoot = new File(api.getDataFolder().getPath() + File.separator + CLASS_FOLDER);
        if (classRoot.exists()) {
            Path classRootPath = classRoot.toPath();
            try {
                Files.walk(classRootPath).forEach(path -> {
                    String longName = getQualifiedFileName(classRootPath, path);
                    if (longName == null) return;
                    String name = path.getFileName().toString().replace(".yml", "");
                    try {
                        CommentedConfig cConfig = new CommentedConfig(api, CLASS_DIR + longName);
                        DynamicClass    tree    = new DynamicClass(api, name);
                        DataSection     section = getBaseSection(cConfig, name);
                        tree.load(section);
                        if (!SkillAPI.isClassRegistered(tree.getName())) {
                            api.addDynamicClass(tree);
                            cConfig.clear();
                            tree.save(cConfig.getConfig().createSection(name));
                            tree.save(classConfig.getConfig().createSection(name));
                            cConfig.save();
                            Logger.log(LogType.REGISTRATION, 2, "Loaded the dynamic class: " + name);
                        } else if (SkillAPI.getClass(name) instanceof DynamicClass) {
                            Logger.log(LogType.REGISTRATION, 3, name + " is already loaded, skipping it");
                        } else {
                            Logger.invalid("Duplicate class detected: " + name);
                        }
                    } catch (Exception ex) {
                        Logger.invalid("Failed to load class file: " + name + " - " + ex.getMessage());
                    }
                });
            } catch (IOException ex) {
                Logger.invalid("Failed to read dynamic classes: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Load dynamic classes from classes.yml
        if (!classConfig.getConfig().getBoolean("loaded", false)) {
            Logger.log(LogType.REGISTRATION, 1, "Loading dynamic classes from classes.yml...");
            classConfig.getConfig().set("loaded", true);
            for (String key : classConfig.getConfig().keys()) {
                if (key.equals("loaded")) {
                    continue;
                }
                try {
                    DynamicClass tree = new DynamicClass(api, key);
                    tree.load(classConfig.getConfig().getSection(key));
                    if (!SkillAPI.isClassRegistered(tree.getName())) {
                        api.addDynamicClass(tree);
                        CommentedConfig cConfig = new CommentedConfig(api, CLASS_DIR + key);
                        cConfig.clear();
                        tree.save(cConfig.getConfig().createSection(key));
                        tree.save(classConfig.getConfig().createSection(key));
                        cConfig.save();
                        Logger.log(LogType.REGISTRATION, 2, "Loaded the dynamic class: " + key);
                    } else {
                        Logger.invalid("Duplicate class detected: " + key);
                    }
                } catch (Exception ex) {
                    Logger.invalid("Failed to load class \"" + key + "\"");
                    ex.printStackTrace();
                }
            }
        } else {
            Logger.log(LogType.REGISTRATION, 1, "classes.yml doesn't have any changes, skipping it");
        }

        skillConfig.save();
        classConfig.save();

        mode = Mode.DONE;

        // Arrange skill trees
        for (RPGClass c : SkillAPI.getClasses().values()) {
            c.arrange();
        }

        Logger.log(LogType.REGISTRATION, 0, "Registration complete");
        Logger.log(LogType.REGISTRATION, 0, " - " + SkillAPI.getSkills().size() + " skills");
        Logger.log(LogType.REGISTRATION, 0, " - " + SkillAPI.getClasses().size() + " classes");
    }

    private String getQualifiedFileName(Path root, Path path) {
        String longName = root.relativize(path).toString();
        if (!longName.endsWith(".yml")) {
            return null;
        }
        longName = longName.replace(".yml", "");

        return longName;
    }

    /**
     * Gets the {@link DataSection} from the config identified by the name, or fetches
     * the first base-level config section and attempts to use that.
     * If there is no base-level section available, an exception is thrown.
     *
     * @param config The source config to use
     * @param name   The desired data section to fetch
     * @return {@link DataSection} from the target config
     * @throws ConfigurationException
     */
    private DataSection getBaseSection(CommentedConfig config, String name) throws ConfigurationException {
        DataSection configSection = config.getConfig();
        String      sectionName   = name;
        if (name == null || name.isBlank() || !configSection.has(name)) {
            sectionName = configSection.keys()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ConfigurationException(
                            name + ".yml does not appear to contain any class data"));
        }
        return configSection.getSection(sectionName);
    }

    /**
     * <p>Validates a skill, making sure it is being registered during the
     * appropriate time, it isn't null, and it doesn't conflict with other
     * registered skills.</p>
     *
     * @param skill skill to validate
     * @return the class if valid, null otherwise
     */
    public Skill validate(Skill skill) {

        // Cannot register outside the allotted time
        if (mode != Mode.SKILL) {
            throw new IllegalStateException("Skills cannot be added outside the provided SkillPlugin method");
        }

        // Cannot be null
        else if (skill == null) {
            throw new IllegalArgumentException("Cannot register a null skill");
        }

        // Cannot have multiple skills with the same name
        else if (SkillAPI.isSkillRegistered(skill.getName())) {
            Logger.invalid("Duplicate skill name: \"" + skill.getName() + "\" - skipping the duplicate");
        }

        // Save new data to config
        else {

            CommentedConfig singleFile = new CommentedConfig(api, "skill" + File.separator + skill.getName());
            DataSection     config     = singleFile.getConfig();

            try {
                // Soft save to ensure optional data starts off in the config
                skill.softSave(config);

                // Load the config data to apply any previous data
                skill.load(config);

                // Finally, do a full save to make sure the config is up to date
                skill.save(config);
                singleFile.save();

                if (skill instanceof Listener) {
                    Bukkit.getServer().getPluginManager().registerEvents((Listener) skill, api);
                }

                // Skill is ready to be registered
                return skill;
            } catch (Exception ex) {
                Logger.bug(
                        "Failed to save skill data to config for \"" + skill.getName() + "\" - skipping registration");
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     * <p>Validates a class, making sure it is being registered during the
     * appropriate time, it isn't null, and it doesn't conflict with other
     * registered classes.</p>
     *
     * @param rpgClass class to validate
     * @return the class if valid, null otherwise
     */
    public RPGClass validate(RPGClass rpgClass) {

        // Cannot register outside the allotted time
        if (mode != Mode.CLASS) {
            throw new IllegalStateException("Classes cannot be added outside the provided SkillPlugin method");
        }

        // Cannot be null
        else if (rpgClass == null) {
            throw new IllegalArgumentException("Cannot register a null class");
        }

        // Cannot have multiple skills with the same name
        else if (SkillAPI.isClassRegistered(rpgClass.getName())) {
            Logger.invalid("Duplicate class name: \"" + rpgClass.getName() + "\" - skipping the duplicate");
        }

        // Save new data to config
        else {

            CommentedConfig singleFile = new CommentedConfig(api, "class" + File.separator + rpgClass.getName());
            DataSection     config     = singleFile.getConfig();

            try {

                // Soft save to ensure optional data starts off in the config
                rpgClass.softSave(config);

                // Load the config data to apply any previous data
                rpgClass.load(config);

                // Finally, do a full save to make sure the config is up to date
                rpgClass.save(config);
                singleFile.save();

                // Skill is ready to be registered
                return rpgClass;
            } catch (Exception ex) {
                Logger.bug("Failed to save class data to config for \"" + rpgClass.getName()
                        + "\" - skipping registration");
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @return true if registering dynamic skills, false otherwise
     */
    public boolean isAddingDynamicSkills() {
        return mode == Mode.DYNAMIC;
    }

    /**
     * The registration modes used by the manager. These values are used to check
     * what can be registered at any given time.
     */
    public enum Mode {
        STARTUP,
        SKILL,
        CLASS,
        DYNAMIC,
        DONE
    }
}
