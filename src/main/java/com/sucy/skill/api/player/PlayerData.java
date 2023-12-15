/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerData
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
package com.sucy.skill.api.player;

import com.google.common.base.Preconditions;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.*;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;
import com.sucy.skill.api.target.TargetHelper;
import com.sucy.skill.cast.PlayerCastBars;
import com.sucy.skill.cast.PlayerTextCastingData;
import com.sucy.skill.data.GroupSettings;
import com.sucy.skill.data.PlayerEquips;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.gui.handlers.AttributeHandler;
import com.sucy.skill.gui.handlers.DetailsHandler;
import com.sucy.skill.gui.handlers.ProfessHandler;
import com.sucy.skill.gui.handlers.SkillHandler;
import com.sucy.skill.gui.tool.GUITool;
import com.sucy.skill.language.ErrorNodes;
import com.sucy.skill.language.GUINodes;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.AttributeManager;
import com.sucy.skill.task.ScoreboardTask;
import mc.promcteam.engine.NexEngine;
import mc.promcteam.engine.api.meta.NBTAttribute;
import mc.promcteam.engine.mccore.config.Filter;
import mc.promcteam.engine.mccore.config.FilterType;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import mc.promcteam.engine.mccore.util.VersionManager;
import mc.promcteam.engine.utils.EntityUT;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents one account for a player which can contain one class from each group
 * and the skills in each of those classes. You should not instantiate this class
 * yourself and instead get it from the SkillAPI static methods.
 * <p>
 * In order to get a player's data, use "SkillAPI.getPlayerData(...)". Do NOT
 * try to instantiate your own PlayerData object.
 */
public class PlayerData {
    public final  HashMap<String, Integer>                       attributes          = new HashMap<>(); // iomatix: It's an attr total like described
    public final  HashMap<String, Integer>                       attrUpStages         = new HashMap<>(); // iomatix: distinguish attr total from attrUpStage (it's an attr upgrade level/stage)
    private final HashMap<String, PlayerClass>                   classes             = new HashMap<>();
    private final HashMap<String, PlayerSkill>                   skills              = new HashMap<>();
    private final HashSet<ExternallyAddedSkill>                  extSkills           = new HashSet<>();
    private final HashMap<String, List<PlayerAttributeModifier>> attributesModifiers = new HashMap<>();
    private final HashMap<String, List<PlayerStatModifier>>      statModifiers       = new HashMap<>();
    private final HashMap<String, String>                        persistentData      = new HashMap<>();

    private final DataSection           extraData  = new DataSection();
    private final UUID                  playerUUID;
    private       PlayerSkillBar        skillBar;
    private       PlayerCastBars        castBars;
    private       PlayerTextCastingData textCastingData;
    private final PlayerCombos          combos;
    private final PlayerEquips          equips;
    private final List<UUID>            onCooldown = new ArrayList<>();

    public        int                   attribPoints;
    private       String                scheme;
    private       String                menuClass;
    private       double                mana;
    private       double                maxMana;
    private       double                lastHealth;
    private       double                health;
    private       double                maxHealth;
    private       double                hunger;
    private       boolean               init;
    private       boolean               passive;
    private       long                  skillTimer;
    private       BukkitTask            removeTimer;
    private       Runnable              onPreviewStop;

    /**
     * Initializes a new account data representation for a player.
     *
     * @param player player to store the data for
     */
    PlayerData(OfflinePlayer player, boolean init) {
        this.playerUUID = player.getUniqueId();
        this.castBars = new PlayerCastBars(this);
        this.combos = new PlayerCombos(this);
        this.equips = new PlayerEquips(this);
        this.init = SkillAPI.isLoaded() && init;
        this.scheme = "default";
        this.hunger = 1;
        for (String group : SkillAPI.getGroups()) {
            GroupSettings settings = SkillAPI.getSettings().getGroupSettings(group);
            RPGClass      rpgClass = settings.getDefault();

            if (rpgClass != null && settings.getPermission() == null) {
                setClass(null, rpgClass, true);
            }
        }
    }

    /**
     * Retrieves the Bukkit player object of the owner
     *
     * @return Bukkit player object of the owner or null if offline
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    /**
     * Retrieves the name of the owner
     *
     * @return name of the owner
     */
    public String getPlayerName() {
        return getPlayer().getName();
    }

    public UUID getUUID() {
        return playerUUID;
    }

    /**
     * Retrieves the skill bar data for the owner
     *
     * @return skill bar data of the owner
     */
    public PlayerSkillBar getSkillBar() {
        if (skillBar == null) this.skillBar = new PlayerSkillBar(this);
        return skillBar;
    }

    /**
     * @return cast bars data for the player
     */
    public PlayerCastBars getCastBars() {
        if (castBars == null) castBars = new PlayerCastBars(this);
        return castBars;
    }

    /**
     * @return cast bars data for the player
     */
    public PlayerTextCastingData getTextCastingData() {
        if (textCastingData == null) textCastingData = new PlayerTextCastingData(this);
        return textCastingData;
    }

    /**
     * Returns the data for the player's combos
     *
     * @return combo data for the player
     */
    public PlayerCombos getComboData() {
        return combos;
    }

    /**
     * @return extra data attached to the player's account
     */
    public DataSection getExtraData() {
        return extraData;
    }

    /**
     * @return equipped item data
     */
    public PlayerEquips getEquips() {
        return equips;
    }

    /**
     * @return health during last logout
     */
    public double getLastHealth() {
        return lastHealth;
    }

    /**
     * Used by the API for restoring health - do not use this.
     *
     * @param health health logged off with
     */
    public void setLastHealth(double health) {
        lastHealth = health;
    }

    /**
     * The hunger value here is not representative of the player's total hunger,
     * rather the amount left of the next hunger point. This is manipulated by
     * attributes were if an attribute says a player has twice as much "hunger"
     * as normal, this will go down by decimals to slow the decay of hunger.
     *
     * @return amount of the next hunger point the player has
     */
    public double getHungerValue() {
        return hunger;
    }

    /**
     * @param hungerValue new hunger value
     * @see PlayerData#getHungerValue
     */
    public void setHungerValue(final double hungerValue) {
        this.hunger = hungerValue;
    }

    public int subtractHungerValue(final double amount) {
        final double scaled = amount / scaleStat(AttributeManager.HUNGER, amount, 0D, Double.MAX_VALUE);
        final int    lost   = scaled >= hunger ? (int) (scaled - hunger) + 1 : 0;
        this.hunger += lost - amount;
        return lost;
    }

    /**
     * Ends the initialization flag for the data. Used by the
     * API to avoid async issues. Do not use this in other
     * plugins.
     */
    public void endInit() {
        init = false;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                    Attributes                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Retrieves the name of the active map menu scheme for the player
     *
     * @return map menu scheme name
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the active scheme name for the player
     *
     * @param name name of the scheme
     */
    public void setScheme(String name) {
        scheme = name;
    }

    /**
     * Retrieves a map of all player attribute totals. Modifying
     * the map will not change actual player attributes.
     *
     * @return attribute totals
     */
    public HashMap<String, Integer> getAttributes() {
        HashMap<String, Integer> map = new HashMap<>();
        for (String key : SkillAPI.getAttributeManager().getKeys()) {
            map.put(key, getAttribute(key));
        }
        return map;
    }

    /**
     * Retrieves a map of all attributes the player invested.
     * This doesn't count base attributes from classes or
     * bonus attributes from effects. Modifying the map will
     * not change actual player attributes.
     *
     * @return attribute totals
     */
    public HashMap<String, Integer> getInvestedAttributes() {
        return new HashMap<>(attributes);
    }

    /**
     * Retrieves a map of all attributes upgrade stages.
     * This doesn't count base attributes from classes or
     * bonus attributes from effects. Modifying the map will
     * not change actual player attributes.
     *
     * @return attribute upgrade stages
     */
    public HashMap<String, Integer> getInvestedAttributesStages() {
        return new HashMap<>(attrUpStages);
    }

    /**
     * Gets the number of attribute points the player has
     * from invested and bonus sources.
     *
     * @param key attribute key
     * @return number of total points
     */
    public int getAttribute(String key) {
        key = key.toLowerCase();
        double total = 0;

        // Attribute points comes with class level
        for (PlayerClass playerClass : this.classes.values()) {
            total += playerClass.getData().getAttribute(key, playerClass.getLevel());

        }

        // Attribute points come with invested attributes
        if (this.attrUpStages.containsKey(key)) {
            total += this.attrUpStages.get(key); // iomatix: please verify safety of this change, done because attributes cost != attribute stage
        }

        // Attribute points come with modifier api
        if (this.attributesModifiers.containsKey(key)) {

            double multiplier = 1;

            for (PlayerAttributeModifier modifier : this.getAttributeModifiers(key)) {

                switch (modifier.getOperation()) {
                    case ADD_NUMBER:
                        total = modifier.applyOn(total);
                        break;
                    case MULTIPLY_PERCENTAGE:
                        multiplier = modifier.applyOn(multiplier);
                        break;
                }

            }

            total = total * multiplier;
        }

        return Math.max(0, (int) Math.round(total));
    }


    /**
     * Gets the number of attribute points invested in the
     * given attribute
     *
     * @param key attribute key
     * @return number of invested points
     */
    public int getInvestedAttribute(String key) {
        return attributes.getOrDefault(key.toLowerCase(), 0);
    }

    /**
     * Gets the upgrade stage of the
     * given attribute
     *
     * @param key attribute key
     * @return the stage of the attribute
     */
    public int getInvestedAttributeStage(String key) {
        return attrUpStages.getOrDefault(key.toLowerCase(), 0); // iomatix: attributes -> attrUpStages
    }

    /**
     * Checks whether the player has any
     * points invested in a given attribute
     *
     * @param key attribute key
     * @return true if any points are invested, false otherwise
     */
    public boolean hasAttribute(String key) {
        return getAttribute(key) > 0;
    }


    /**
     * Invests a point in the attribute if the player
     * has enough remaining attribute points. If the player
     * has no remaining points, this will do nothing.
     *
     * @param key attribute key
     * @return whether it was successfully upgraded
     */
    public boolean upAttribute(String key) {
        key = key.toLowerCase();
        int currentStage = getInvestedAttributeStage(key); // iomatix: the current upgrade stage, not the invested attributes
        int currentInvested = getInvestedAttribute(key); // iomatix: the invested attributes
        int max     = SkillAPI.getAttributeManager().getAttribute(key).getMax();
        int cost = getAttributeUpCost(key);

        // iomatix apply the new logic below:
        if (attribPoints >= cost && currentStage < max) {
            attributes.put(key, currentInvested + cost); // iomatix: total spent goes by the cost
            attrUpStages.put(key, currentStage + 1); // iomatix: upgrade stage goes by 1
            attribPoints -= cost; // iomatix new cost has been applied

            PlayerUpAttributeEvent event = new PlayerUpAttributeEvent(this, key);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                // iomatix: roll back the cost and previous stage
                attributes.put(key, currentInvested);
                attrUpStages.put(key, currentStage);
                attribPoints += cost;
            } else {
                return true;
            }
        }
        return false;
    }
    /**
     * Calculating cost using the formula:
     * costBase + (int) Math.floor(attrStage*costModifier).
     *
     * @param key attribute key
     * @return calculated cost of single attribute upgrade
     */
    public int getAttributeUpCost(String key){
        return Math.max(0,SkillAPI.getAttributeManager().getAttribute(key).getCostBase() + (int) Math.floor((getInvestedAttributeStage(key)+1)*SkillAPI.getAttributeManager().getAttribute(key).getCostMod()));
    }
    /**
     * Calculating cost using the formula:
     * costBase + (int) Math.floor(attrStage*costModifier).
     *
     * OVERLOAD to check cost of certain stage:
     * mod = -1 is e.g. of previous stage,
     * mod = 0 is e.g. of current stage,
     * mod = 1 is e.g. default cost of next stage.
     *
     * @param key attribute key
     * @param modifier stage number modifier
     * @return calculated cost of single attribute upgrade
     */
    public int getAttributeUpCost(String key, Integer modifier){
        int selectedStage = getInvestedAttributeStage(key)+modifier;
        return Math.max(0,SkillAPI.getAttributeManager().getAttribute(key).getCostBase() + (int) Math.floor(selectedStage*SkillAPI.getAttributeManager().getAttribute(key).getCostMod()));
    }
    /**
     * Calculating cost using the formula:
     * costBase + (int) Math.floor(attrStage*costModifier).
     *
     * OVERLOAD to check total cost of upgrading
     * [from] stage --> [to] stage
     * where [from] is starting stage
     * and where [to] is target stage
     *
     * @param key attribute key
     * @param from starting stage
     * @param to target stage
     * @return calculated cost of single attribute upgrade
     */
    public int getAttributeUpCost(String key, Integer from, Integer to){
        int totalCost = 0;
        for (int i = from+1; i <= to; i++){ // iomatix: so if from = 2 then first upgrade is from 2 to 3 (from+1)
            totalCost += Math.max(0,SkillAPI.getAttributeManager().getAttribute(key).getCostBase()+ (int) Math.floor(i*SkillAPI.getAttributeManager().getAttribute(key).getCostMod()));
        }
        return totalCost;
    }

    /**
     * Upgrades the player attribute stage without costing
     * attribute points.
     *
     * @param key    attribute to give points for
     * @param amount amount to give
     */
    public void giveAttribute(String key, int amount) {
        key = key.toLowerCase(); // iomatix: is it necessary?
        int currentStage = getInvestedAttributeStage(key);
        int invested = getInvestedAttribute(key);
        int max     = SkillAPI.getAttributeManager().getAttribute(key).getMax();

        amount = Math.min(amount + currentStage, max);
        if (amount > currentStage) {
            attrUpStages.put(key, amount); // iomatix: attr stage goes up by the given value
            attributes.put(key,invested+getAttributeUpCost(key, currentStage, currentStage+amount)); // let's increase totals value for now <- it's just one line!

            this.updatePlayerStat(getPlayer());
        }
    }

    /**
     * Adds stat modifier to the player.
     * These bypass min/max invest amount and cannot be refunded.
     *
     * @param key      stat key
     * @param modifier The player stat modifier
     * @param update   calculate player stat immediately and apply to him
     */
    public void addStatModifier(String key, PlayerStatModifier modifier, boolean update) {
        List<PlayerStatModifier> modifiers = this.getStatModifiers(key);
        modifiers.add(modifier);
        this.statModifiers.put(key, modifiers);

        if (update) {
            this.updatePlayerStat(getPlayer());
        }
    }

    /**
     * Get all stat modifier from the player.
     *
     * @param key stat key
     * @return stat modifier list of the attribute given
     */
    public List<PlayerStatModifier> getStatModifiers(String key) {
        if (this.statModifiers.containsKey(key)) {
            return this.statModifiers.get(key);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Adds attribute modifier to the player.
     * These bypass min/max invest amount and cannot be refunded.
     *
     * @param key      attribute key
     * @param modifier The player attribute modifier
     * @param update   calculate player stat immediately and apply to him
     */
    public void addAttributeModifier(String key, PlayerAttributeModifier modifier, boolean update) {
        key = SkillAPI.getAttributeManager().normalize(key);
        List<PlayerAttributeModifier> modifiers = this.getAttributeModifiers(key);
        modifiers.add(modifier);
        this.attributesModifiers.put(key, modifiers);

        if (update) {
            this.updatePlayerStat(getPlayer());
        }
    }

    /**
     * Get all attribute modifier from the player.
     *
     * @param key attribute key
     * @return attribute modifier list of the attribute given
     */
    public List<PlayerAttributeModifier> getAttributeModifiers(String key) {
        if (this.attributesModifiers.containsKey(key)) {
            return this.attributesModifiers.get(key);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Refunds an attribute point from the given attribute
     * if there are any points invested in it. If there are
     * none, this will do nothing.
     *
     * @param key attribute key
     */
    public boolean refundAttribute(String key) {
        key = key.toLowerCase(); // iomatix: is this line necessary ? if yes there should be method getKey which returns key.toLowerCase(); and applied everywhere
        int current = getInvestedAttributeStage(key); // iomatix: get current stage
        int invested = getInvestedAttribute(key); // iomatix: the total invested
        int currentCost = getAttributeUpCost(key, 0); // iomatix: the cost [from] previous --> [to] current stage

        if (current > 0) {
            PlayerRefundAttributeEvent event = new PlayerRefundAttributeEvent(this, key);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            attribPoints += currentCost; // iomatix: get the current stage cost back
            attributes.put(key, invested - currentCost); // iomatix: the fix for total spent attributes
            attrUpStages.put(key, current - 1); // iomatix: single step back to previous stage
            if (current - 1 <= 0) {
                attributes.remove(key);
                attrUpStages.remove(key);
            }
            this.updatePlayerStat(getPlayer());

            return true;
        }
        return false;
    }

    /**
     * Refunds all spent attribute points for a specific attribute
     *
     * @param key attribute key
     */
    public void refundAttributes(String key) {
        key = key.toLowerCase(); // iomatix: is it necessary ? it's not applied everywhere, there should be method getKey() implemented if necessary
        attribPoints += getInvestedAttribute(key); // alternative totalCost==>getAttributeUpCost(key, 0, getInvestedAttributeStage(key)); // iomatix: alternative calculate total cost in points
        attributes.remove(key);
        attrUpStages.remove(key); // iomatix: reset to stage 0 by removing the mapping
        this.updatePlayerStat(getPlayer());
    }

    /**
     * Refunds all spent attribute points
     */
    public void refundAttributes() {
        ArrayList<String> keys = new ArrayList<>(attributes.keySet());
        for (String key : keys) {
            refundAttributes(key);
        }
    }

    /**
     * Retrieves the current number of attribute points the player has
     *
     * @return attribute point total
     */
    public int getAttributePoints() {
        return attribPoints;
    }

    /**
     * Gives the player attribute points
     *
     * @param amount amount of attribute points
     */
    public void giveAttribPoints(int amount) {
        attribPoints += amount;
    }

    /**
     * Sets the current amount of attribute points
     *
     * @param amount amount of points to have
     */
    public void setAttribPoints(int amount) {
        attribPoints = amount;
    }

    /**
     * Scales a stat value using the player's attributes
     *
     * @param stat      stat key
     * @param baseValue the default value come with vanilla Minecraft, <strong>Only needed for custom stats and Speed</strong>
     * @return modified value
     */
    public double scaleStat(String stat, double baseValue) {
        return this.scaleStat(stat, baseValue, 0D, Double.MAX_VALUE);
    }

    /**
     * Scales a stat value using the player's attributes
     *
     * @param stat         stat key
     * @param defaultValue the default value come with vanilla Minecraft, <strong>Only needed for custom stats and Speed</strong>
     * @param min          min value
     * @param max          max value
     * @return modified value
     */
    public double scaleStat(String stat, double defaultValue, double min, double max) {

        Player player = this.getPlayer();
        if (player != null) {
            if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) {
                return defaultValue;
            }
        }

        final AttributeManager manager = SkillAPI.getAttributeManager();
        if (manager == null) {
            return defaultValue;
        }

        double modified = defaultValue;

        final List<AttributeManager.Attribute> matches = manager.forStat(stat);
        if (matches != null) {

            for (final AttributeManager.Attribute attribute : matches) {
                int amount = this.getAttribute(attribute.getKey());
                if (amount > 0) {
                    modified = attribute.modifyStat(stat, modified, amount);
                }
            }

        }

        // Stats come with modifier api
        if (this.statModifiers.containsKey(stat)) {

            double multiplier = 1;

            for (PlayerStatModifier modifier : this.getStatModifiers(stat)) {

                switch (modifier.getOperation()) {
                    case ADD_NUMBER:
                        modified = modifier.applyOn(modified);
                        break;
                    case MULTIPLY_PERCENTAGE:
                        multiplier = modifier.applyOn(multiplier);
                        break;
                }

            }

            modified = modified * multiplier;
        }

        return Math.max(min, Math.min(max, modified));
    }

    /**
     * Scales a dynamic skill's value using global modifiers
     *
     * @param component component holding the value
     * @param key       key of the value
     * @param value     unmodified value
     * @return the modified value
     */
    public double scaleDynamic(EffectComponent component, String key, double value) {
        final AttributeManager manager = SkillAPI.getAttributeManager();
        if (manager == null) {
            return value;
        }

        final List<AttributeManager.Attribute> matches = manager.forComponent(component, key);
        if (matches == null) {
            return value;
        }

        for (final AttributeManager.Attribute attribute : matches) {
            int amount = getAttribute(attribute.getKey());
            if (amount > 0) {
                value = attribute.modify(component, key, value, amount);
            }
        }
        return value;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                      Skills                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Opens the attribute menu for the player
     *
     * @return true if successfully opened, false if conditions weren't met
     */
    public boolean openAttributeMenu() {
        Player player = getPlayer();
        if (SkillAPI.getSettings().isAttributesEnabled() && player != null) {
            GUITool.getAttributesMenu()
                    .show(new AttributeHandler(),
                            this,
                            SkillAPI.getLanguage().getMessage(GUINodes.ATTRIB_TITLE,
                                    true,
                                    FilterType.COLOR, RPGFilter.POINTS.setReplacement(attribPoints + ""),
                                    Filter.PLAYER.setReplacement(player.getName())
                            ).get(0), SkillAPI.getAttributeManager().getAttributes()
                    );
            return true;
        }
        return false;
    }

    /**
     * Retrieves the player's attribute data regarding total amounts spent.
     * Modifying this will modify the player's
     * actual data.
     *
     * @return the player's attribute data
     */
    public HashMap<String, Integer> getAttributeData() {
        return attributes;
    }

    /**
     * Retrieves the player's attribute data regarding current stages.
     * Modifying this will modify the player's
     * actual data.
     *
     * @return the player's attribute data
     */
    public HashMap<String, Integer> getAttributeStageData() {
        return attrUpStages;
    }


    /**
     * Checks if the owner has a skill by name. This is not case-sensitive
     * and does not check to see if the skill is unlocked. It only checks if
     * the skill is available to upgrade/use.
     *
     * @param name name of the skill
     * @return true if has the skill, false otherwise
     */
    public boolean hasSkill(String name) {
        return name != null && skills.containsKey(name.toLowerCase());
    }

    /**
     * Retrieves a skill of the owner by name. This is not case-sensitive.
     *
     * @param name name of the skill
     * @return data for the skill or null if the player doesn't have the skill
     */
    public PlayerSkill getSkill(String name) {
        if (name == null) {
            return null;
        }
        PlayerSkill skill = skills.get(name.toLowerCase());
        if (skill != null) return skill;

        // We'll try it by manually searching and comparing the name
        for (PlayerSkill playerSkill : skills.values()) {
            if (playerSkill.getData().getName().equalsIgnoreCase(name)) {
                return playerSkill;
            }
        }

        return null;
    }

    public int getInvestedSkillPoints() {
        int total = 0;
        for (PlayerSkill playerSkill : skills.values()) {
            total += playerSkill.getInvestedCost();
        }
        return total;
    }

    /**
     * Retrieves all of the skill data the player has. Modifying this
     * collection will not modify the player's owned skills but modifying
     * one of the elements will change that element's data for the player.
     *
     * @return collection of skill data for the owner
     */
    public Collection<PlayerSkill> getSkills() {
        return skills.values();
    }

    public Set<ExternallyAddedSkill> getExternallyAddedSkills() {
        return Collections.unmodifiableSet(extSkills);
    }

    /**
     * Retrieves the level of a skill for the owner. This is not case-sensitive.
     *
     * @param name name of the skill
     * @return level of the skill or 0 if not found
     */
    public int getSkillLevel(String name) {
        PlayerSkill skill = getSkill(name);
        return skill == null ? 0 : skill.getLevel();
    }

    /**
     * Gives the player a skill outside of the normal class skills.
     * This skill will not show up in a skill tree.
     *
     * @param skill skill to give the player
     */
    public void giveSkill(Skill skill) {
        giveSkill(skill, null);
    }

    /**
     * Gives the player a skill using the class data as a parent. This
     * skill will not show up in a skill tree.
     *
     * @param skill  skill to give the player
     * @param parent parent class data
     */
    public void giveSkill(Skill skill, PlayerClass parent) {
        String key = skill.getKey();
        if (!skills.containsKey(key)) {
            addSkill(skill, parent);
            autoLevel(skill);
        }
    }

    public void addSkill(Skill skill, PlayerClass parent) {
        String      key      = skill.getKey();
        PlayerSkill existing = skills.get(key);
        if (existing == null || !existing.isExternal()) {
            PlayerSkill data = new PlayerSkill(this, skill, parent);
            skills.put(key, data);
            combos.addSkill(skill);
        }
    }

    public void addSkillExternally(Skill skill, PlayerClass parent, NamespacedKey namespacedKey, int level) {
        String key = skill.getKey();
        extSkills.removeIf(extSkill -> extSkill.getId().equals(key) && extSkill.getKey().equals(namespacedKey));
        extSkills.add(new ExternallyAddedSkill(key, namespacedKey, level));

        PlayerSkill existing = skills.get(key);
        if (existing == null || existing.getLevel() == 0) {
            PlayerSkill data = new PlayerSkill(this, skill, parent, true);
            skills.put(key, data);
            combos.addSkill(skill);
            forceUpSkill(data, level);
        } else if (existing.isExternal() && level > existing.getLevel()) {
            forceUpSkill(existing, level - existing.getLevel());
        }
    }

    public void removeSkillExternally(Skill skill, NamespacedKey namespacedKey) {
        String key = skill.getKey();
        extSkills.removeIf(extSkill -> extSkill.getId().equals(key) && extSkill.getKey().equals(namespacedKey));
        PlayerSkill existing = skills.get(key);
        if (existing != null && existing.isExternal()) {
            ExternallyAddedSkill max      = null;
            int                  maxLevel = Integer.MIN_VALUE;
            for (ExternallyAddedSkill extSkill : extSkills) {
                if (!extSkill.getId().equals(key)) {
                    continue;
                }
                int level = extSkill.getLevel();
                if (level > maxLevel) {
                    maxLevel = level;
                    max = extSkill;
                }
            }
            if (max == null) {
                skills.remove(key);
                combos.removeSkill(existing.getData());
                forceDownSkill(existing, existing.getLevel());
            } else {
                forceDownSkill(existing, existing.getLevel() - maxLevel);
            }
        }
    }

    /**
     * Attempts to auto-level any skills that are able to do so
     */
    public void autoLevel() {
        if (init) {
            return;
        }

        final Player player = getPlayer();
        if (player == null) {
            return;
        }

        for (PlayerSkill skill : skills.values()) {
            if (skill.getData().isAllowed(player)) {
                autoLevel(skill.getData());
            }
        }
    }

    private void autoLevel(Skill skill) {
        PlayerSkill data = skills.get(skill.getKey());
        if (data == null || getPlayer() == null || !skill.isAllowed(getPlayer())) {
            return;
        }

        int lastLevel = data.getLevel();
        while (data.getData().canAutoLevel(lastLevel) && !data.isMaxed() && data.getLevelReq() <= data.getPlayerClass()
                .getLevel()) {
            upgradeSkill(skill);
            if (lastLevel == data.getLevel()) {
                break;
            }
            lastLevel++;
        }
    }

    /**
     * Upgrades a skill owned by the player. The player must own the skill,
     * have enough skill points, meet the level and skill requirements, and
     * not have maxed out the skill already in order to upgrade the skill.
     * This will consume the skill point cost while upgrading the skill.
     *
     * @param skill skill to upgrade
     * @return true if successfully was upgraded, false otherwise
     */
    public boolean upgradeSkill(Skill skill) {
        // Cannot be null
        if (skill == null) {
            return false;
        }

        // Must be a valid available skill
        PlayerSkill data = getSkill(skill.getName());
        if (data == null) {
            return false;
        }

        // Must meet any skill requirements
        if (!skill.isCompatible(this) || !skill.hasInvestedEnough(this) || !skill.hasEnoughAttributes(this) || !skill.hasDependency(this)) {
            return false;
        }

        int level  = data.getPlayerClass().getLevel();
        int points = data.getPlayerClass().getPoints();
        int cost   = data.getCost();
        if (!data.isMaxed() && level >= data.getLevelReq() && points >= cost) {
            // Upgrade event
            PlayerSkillUpgradeEvent event = new PlayerSkillUpgradeEvent(this, data, cost);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            // Apply upgrade
            data.getPlayerClass().usePoints(cost);
            forceUpSkill(data);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Forcefully upgrades a skill, not letting other plugins
     * cancel it and ignoring any requirements to do so
     *
     * @param skill skill to forcefully upgrade
     */
    public void forceUpSkill(PlayerSkill skill) {
        forceUpSkill(skill, 1);
    }

    public void forceUpSkill(PlayerSkill skill, int amount) {
        Preconditions.checkArgument(amount >= 0);
        if (amount == 0) {
            return;
        }
        int oldLevel = skill.getLevel();
        skill.addLevels(amount);

        // Passive calls
        if (passive) {
            Player player = getPlayer();
            if (player != null && skill.getData() instanceof PassiveSkill) {
                if (oldLevel == 0) {
                    ((PassiveSkill) skill.getData()).initialize(player, skill.getLevel());
                } else {
                    ((PassiveSkill) skill.getData()).update(player, oldLevel, skill.getLevel());
                }
            }

            // Unlock event
            if (skill.getLevel() == 1) {
                Bukkit.getPluginManager().callEvent(new PlayerSkillUnlockEvent(this, skill));
                this.autoLevel();
            }
        }
    }

    /**
     * Downgrades a skill owned by the player. The player must own the skill and it must
     * not currently be level 0 for the player to downgrade the skill. This will refund
     * the skill point cost when downgrading the skill.
     *
     * @param skill skill to downgrade
     * @return true if successfully downgraded, false otherwise
     */
    public boolean downgradeSkill(Skill skill) {
        // Cannot be null
        if (skill == null) {
            return false;
        }

        // Must be a valid available skill
        PlayerSkill data = skills.get(skill.getName().toLowerCase());
        if (data == null) {
            return false;
        }

        // Must not be a free skill
        if (data.getCost() == 0) {
            return false;
        }

        // Must not be required by another skill
        for (PlayerSkill s : skills.values()) {
            if (s.getData().getSkillReq() != null && s.getData().getSkillReq().equalsIgnoreCase(skill.getName())
                    && data.getLevel() <= s.getData().getSkillReqLevel() && s.getLevel() > 0) {
                return false;
            }
        }

        int cost = skill.getCost(data.getLevel() - 1);
        if (data.getLevel() > 0) {
            // Upgrade event
            PlayerSkillDowngradeEvent event = new PlayerSkillDowngradeEvent(this, data, cost);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            // Apply upgrade
            data.getPlayerClass().givePoints(cost, PointSource.REFUND);
            forceDownSkill(data);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Forcefully downgrades a skill, not letting other plugins
     * stop it and ignoring any skill requirements to do so.
     *
     * @param skill skill to forcefully downgrade
     */
    public void forceDownSkill(PlayerSkill skill) {
        forceDownSkill(skill, 1);
    }

    public void forceDownSkill(PlayerSkill skill, int amount) {
        Preconditions.checkArgument(amount >= 0);
        if (amount == 0) {
            return;
        }
        skill.addLevels(-amount);

        // Passive calls
        Player player = getPlayer();
        if (player != null && skill.getData() instanceof PassiveSkill) {
            if (skill.getLevel() == 0) {
                ((PassiveSkill) skill.getData()).stopEffects(player, 1);
            } else {
                ((PassiveSkill) skill.getData()).update(player, skill.getLevel() + amount, skill.getLevel());
            }
        }
    }

    /**
     * Refunds a skill for the player, resetting it down
     * to level 0 and giving back any invested skill points.
     *
     * @param skill skill to refund
     */
    public void refundSkill(PlayerSkill skill) {
        Player player = getPlayer();

        if (skill.getCost() == 0 || skill.getLevel() == 0) {
            return;
        }

        skill.getPlayerClass().givePoints(skill.getInvestedCost(), PointSource.REFUND);
        skill.setLevel(0);

        if (player != null && (skill.getData() instanceof PassiveSkill)) {
            ((PassiveSkill) skill.getData()).stopEffects(player, 1);
        }
    }

    /**
     * Refunds all skills for the player
     */
    public void refundSkills() {
        for (PlayerSkill skill : skills.values()) {
            refundSkill(skill);
        }
    }

    /**
     * Shows the skill tree for the player. If the player has multiple trees,
     * this will show the list of skill trees they can view.
     */
    public void showSkills() {
        showSkills(getPlayer());
    }

    /**
     * Shows the class details for the player
     *
     * @param player player to show to
     * @return true if shown, false if nothing to show
     */
    public boolean showDetails(Player player) {
        if (classes.size() > 0 && player != null) {
            HashMap<String, RPGClass> iconMap = new HashMap<>();
            for (Map.Entry<String, PlayerClass> entry : classes.entrySet()) {
                iconMap.put(entry.getKey().toLowerCase(), entry.getValue().getData());
            }

            GUITool.getDetailsMenu()
                    .show(new DetailsHandler(),
                            this,
                            SkillAPI.getLanguage()
                                    .getMessage(GUINodes.CLASS_LIST,
                                            true,
                                            FilterType.COLOR,
                                            Filter.PLAYER.setReplacement(player.getName()))
                                    .get(0),
                            iconMap);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows profession options of the first class group available
     *
     * @param player player to show profession options for
     * @return true if shown profession options, false if none available
     */
    public boolean showProfession(Player player) {
        for (String group : SkillAPI.getGroups()) {
            PlayerClass c = getClass(group);
            if (c == null || (c.getLevel() == c.getData().getMaxLevel() && c.getData().getOptions().size() > 0)) {
                GUITool.getProfessMenu(c == null
                                ? null
                                : c.getData())
                        .show(new ProfessHandler(),
                                this,
                                SkillAPI.getLanguage()
                                        .getMessage(GUINodes.PROFESS_TITLE,
                                                true,
                                                FilterType.COLOR,
                                                Filter.PLAYER.setReplacement(player.getName()),
                                                RPGFilter.GROUP.setReplacement(group))
                                        .get(0),
                                SkillAPI.getClasses());
                return true;
            }
        }
        return false;
    }

    /**
     * Shows the skill tree for the player. If the player has multiple trees,
     * this will show the list of skill trees they can view.
     *
     * @param player player to show the skill tree for
     * @return true if able to show the player, false otherwise
     */
    public boolean showSkills(Player player) {
        // Cannot show an invalid player, and cannot show no skills
        if (player == null || classes.size() == 0 || skills.size() == 0) {
            return false;
        }

        // Show list of classes that have skill trees
        // Show only class's skill tree otherwise
        return classes.size() > 1 ? showDetails(player) : showSkills(player, getMainClass());
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Classes                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Shows the skill tree to the player for the given class
     *
     * @param player      player to show
     * @param playerClass class to look for
     * @return true if succeeded, false otherwise
     */
    public boolean showSkills(Player player, PlayerClass playerClass) {
        // Cannot show an invalid player, and cannot show no skills
        if (player == null || playerClass.getData().getSkills().size() == 0) {
            return false;
        }

        // Show skill tree of the class
        this.menuClass = playerClass.getData().getName();
        GUITool.getSkillTree(playerClass.getData())
                .show(new SkillHandler(), this,
                        SkillAPI.getLanguage().getMessage(GUINodes.SKILL_TREE,
                                true,
                                FilterType.COLOR,
                                RPGFilter.POINTS.setReplacement(playerClass.getPoints() + ""),
                                RPGFilter.LEVEL.setReplacement(playerClass.getLevel() + ""),
                                RPGFilter.CLASS.setReplacement(playerClass.getData().getName()),
                                Filter.PLAYER.setReplacement(getPlayerName())
                        ).get(0),
                        playerClass.getData().getSkillMap());
        return true;
    }

    /**
     * Retrieves the name of the class shown in the skill tree
     *
     * @return class name
     */
    public String getShownClassName() {
        return menuClass;
    }

    /**
     * Checks whether the player has as least one class they have professed as.
     *
     * @return true if professed, false otherwise
     */
    public boolean hasClass() {
        return classes.size() > 0;
    }

    /**
     * Checks whether a player has a class within the given group
     *
     * @param group class group to check
     * @return true if has a class in the group, false otherwise
     */
    public boolean hasClass(String group) {
        return classes.containsKey(group);
    }

    /**
     * Retrieves the collection of the data for classes the player has professed as.
     *
     * @return collection of the data for professed classes
     */
    public Collection<PlayerClass> getClasses() {
        return classes.values();
    }

    /**
     * Retrieves the data of a class the player professed as by group. This is
     * case-sensitive.
     *
     * @param group group to get the profession for
     * @return professed class data or null if not professed for the group
     */
    public PlayerClass getClass(String group) {
        return classes.get(group);
    }

    /**
     * Retrieves the data of the professed class under the main class group. The
     * "main" group is determined by the setting in the config.
     *
     * @return main professed class data or null if not professed for the main group
     */
    public @Nullable
    PlayerClass getMainClass() {
        String main = SkillAPI.getSettings().getMainGroup();
        if (classes.containsKey(main)) {
            return classes.get(main);
        } else if (classes.size() > 0) {
            return classes.values().toArray(new PlayerClass[classes.size()])[0];
        } else {
            return null;
        }
    }

    /**
     * Sets the professed class for the player for the corresponding group. This
     * will not save any skills, experience, or levels of the previous class if
     * there was any. The new class will start at level 1 with 0 experience.
     *
     * @param rpgClass class to assign to the player
     * @return the player-specific data for the new class
     */
    public PlayerClass setClass(RPGClass previous, RPGClass rpgClass, boolean reset) {
        PlayerClass c = classes.remove(rpgClass.getGroup());
        if (c != null) {
            for (Skill skill : c.getData().getSkills()) {
                String      nm = skill.getName().toLowerCase();
                PlayerSkill ps = skills.get(nm);
                if (previous != null && rpgClass.hasParent() && rpgClass.getParent()
                        .getName()
                        .equals(previous.getName())) {
                    GroupSettings group = SkillAPI.getSettings().getGroupSettings(rpgClass.getGroup());
                    if (group.isProfessReset()) {
                        if (group.isProfessRefundSkills() && ps.getInvestedCost() > 0)
                            c.givePoints(ps.getInvestedCost(), PointSource.REFUND);

                        if (group.isProfessRefundAttributes())
                            resetAttribs();

                        skills.remove(nm);
                        combos.removeSkill(skill);
                    }
                } else {
                    if (!reset && SkillAPI.getSettings().isRefundOnClassChange() && skills.containsKey(nm)) {
                        if (ps.getInvestedCost() > 0)
                            c.givePoints(ps.getInvestedCost(), PointSource.REFUND);
                        skills.remove(nm);
                        combos.removeSkill(skill);
                    }

                    if (reset) {
                        skills.remove(nm);
                        combos.removeSkill(skill);
                    }
                    resetAttribs();
                }
            }
        } else {
            attribPoints += rpgClass.getGroupSettings().getStartingAttribs();
        }

        PlayerClass classData = new PlayerClass(this, rpgClass);
        if (!reset && c != null) {
            classData.setLevel(c.getLevel());
            classData.setExp(c.getExp());
            classData.setPoints(c.getPoints());
        }
        classes.put(rpgClass.getGroup(), classData);

        // Add in missing skills
        for (Skill skill : rpgClass.getSkills()) {
            giveSkill(skill, classData);
        }

        this.updatePlayerStat(getPlayer());
        this.updateScoreboard();
        return classes.get(rpgClass.getGroup());
    }

    /**
     * Checks whether the player is professed as the class
     * without checking child classes.
     *
     * @param rpgClass class to check
     * @return true if professed as the specific class, false otherwise
     */
    public boolean isExactClass(RPGClass rpgClass) {
        if (rpgClass == null) {
            return false;
        }
        PlayerClass c = classes.get(rpgClass.getGroup());
        return (c != null) && (c.getData() == rpgClass);
    }

    /**
     * Checks whether the player is professed as the class
     * or any of its children.
     *
     * @param rpgClass class to check
     * @return true if professed as the class or one of its children, false otherwise
     */
    public boolean isClass(RPGClass rpgClass) {
        if (rpgClass == null) {
            return false;
        }

        PlayerClass pc = classes.get(rpgClass.getGroup());
        if (pc == null) {
            return false;
        }

        RPGClass temp = pc.getData();
        while (temp != null) {
            if (temp == rpgClass) {
                return true;
            }
            temp = temp.getParent();
        }

        return false;
    }

    /**
     * Checks whether the player can profess into the given class. This
     * checks to make sure the player is currently professed as the parent of the
     * given class and is high enough of a level to do so.
     *
     * @param rpgClass class to check
     * @return true if can profess, false otherwise
     */
    public boolean canProfess(RPGClass rpgClass) {
        Player p = getPlayer();
        if (p == null || !rpgClass.isAllowed(p)) {
            return false;
        }

        if (classes.containsKey(rpgClass.getGroup())) {
            PlayerClass current = classes.get(rpgClass.getGroup());
            return rpgClass.getParent() == current.getData() && current.getData().getMaxLevel() <= current.getLevel();
        } else {
            return !rpgClass.hasParent();
        }
    }

    /**
     * Resets the class data for the owner under the given group. This will remove
     * the profession entirely, leaving no remaining data until the player professes
     * again to a starting class.
     *
     * @param group      group to reset
     * @param toSubclass - whether we are professing to a subclass of the previous class
     * @return the number of skill points to be refunded
     */
    public int reset(String group, boolean toSubclass) {
        GroupSettings settings = SkillAPI.getSettings().getGroupSettings(group);
        if (!settings.canReset()) {
            return 0;
        }

        PlayerClass playerClass = classes.remove(group);
        int         points      = 0;
        if (playerClass != null) {
            // Remove skills
            RPGClass data = playerClass.getData();
            for (Skill skill : data.getSkills()) {
                PlayerSkill ps = skills.remove(skill.getName().toLowerCase());
                if (ps != null && ps.isUnlocked() && ps.getData() instanceof PassiveSkill) {
                    ((PassiveSkill) ps.getData()).stopEffects(getPlayer(), ps.getLevel());
                }

                if (settings.isProfessRefundSkills() && toSubclass) points += ps.getInvestedCost();
                combos.removeSkill(skill);
            }

            // Update GUI features
            updateScoreboard();

            // Call the event
            Bukkit.getPluginManager().callEvent(new PlayerClassChangeEvent(playerClass, data, null));
        }

        // Restore default class if applicable
        RPGClass rpgClass = settings.getDefault();
        if (rpgClass != null && settings.getPermission() == null) {
            setClass(null, rpgClass, true);
        }

        int aPoints = 0;
        if (settings.isProfessRefundAttributes() && toSubclass) {
            aPoints += attribPoints;
            for (Entry<String, Integer> entry : attributes.entrySet()) aPoints += entry.getValue();
        }
        resetAttribs(); //Should reset attribute points to 0.
        attribPoints += aPoints;

        return points;
    }

    /**
     * Resets all profession data for the player. This clears all professions the player
     * has, leaving no remaining data until the player professes again to a starting class.
     */
    public void resetAll() {
        ArrayList<String> keys = new ArrayList<>(classes.keySet());
        for (String key : keys) {
            reset(key, false);
        }
    }

    /**
     * Resets attributes for the player
     */
    public void resetAttribs() {
        attributes.clear();
        attrUpStages.clear();
        attribPoints = 0;
        for (PlayerClass c : classes.values()) {
            GroupSettings s = c.getData().getGroupSettings();
            attribPoints += s.getStartingAttribs() + s.getAttribsForLevels(c.getLevel(), 1);
        }
        this.updatePlayerStat(getPlayer());
    }

    /**
     * Professes the player into the class if they are able to. This will
     * reset the class data if the group options are set to reset upon
     * profession. Otherwise, all skills, experience, and levels of the
     * current class under the group will be retained and carried over into
     * the new profession.
     *
     * @param rpgClass class to profess into
     * @return true if successfully professed, false otherwise
     */
    public boolean profess(RPGClass rpgClass) {
        if (rpgClass != null && canProfess(rpgClass)) {
            final PlayerClass previousData = classes.get(rpgClass.getGroup());
            final RPGClass    previous     = previousData == null ? null : previousData.getData();

            // Pre-class change event in case someone wants to stop it
            final PlayerPreClassChangeEvent event =
                    new PlayerPreClassChangeEvent(this, previousData, previous, rpgClass);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            // Reset data if applicable
            final boolean isResetting = SkillAPI.getSettings().getGroupSettings(rpgClass.getGroup()).isProfessReset();
            boolean       isSubclass  = previous != null && rpgClass.getParent().getName().equals(previous.getName());
            int skillPoints = isResetting
                    ? reset(rpgClass.getGroup(), isSubclass)
                    : -1;

            // Inherit previous class data if any
            final PlayerClass current;
            if (previousData == null || isResetting) {
                current = new PlayerClass(this, rpgClass);
                classes.put(rpgClass.getGroup(), current);
                attribPoints += rpgClass.getGroupSettings().getStartingAttribs();
                if (skillPoints == -1) skillPoints = current.getPoints();
            } else {
                current = previousData;
                previousData.setClassData(rpgClass);
            }


            // Add skills
            for (Skill skill : rpgClass.getSkills(!isResetting)) {
                if (!skills.containsKey(skill.getKey())) {
                    skills.put(skill.getKey(), new PlayerSkill(this, skill, current));
                    combos.addSkill(skill);
                }
            }

            Bukkit.getPluginManager().callEvent(new PlayerClassChangeEvent(current, previous, current.getData()));
            if (skillPoints < 0 || (isResetting && skillPoints == 0))
                skillPoints = rpgClass.getGroupSettings().getStartingPoints();
            current.setPoints(skillPoints);
            updateScoreboard();
            updatePlayerStat(getPlayer());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gives experience to the player from the given source
     *
     * @param amount amount of experience to give
     * @param source source of the experience
     */
    public void giveExp(double amount, ExpSource source) {
        giveExp(amount, source, true);
    }

    /**
     * Gives experience to the player from the given source
     *
     * @param amount  amount of experience to give
     * @param source  source of the experience
     * @param message whether to show the configured message if enabled
     */
    public void giveExp(double amount, ExpSource source, boolean message) {
        for (PlayerClass playerClass : classes.values()) {
            playerClass.giveExp(amount, source, message); // TODO Xp duplicating
        }
    }

    /**
     * Causes the player to lose experience
     * This will launch a {@link PlayerExperienceLostEvent} event before taking the experience.
     *
     * @param amount  percent of experience to lose
     * @param percent whether to take the amount as a percentage
     */
    public void loseExp(double amount, boolean percent, boolean changeLevel) {
        for (PlayerClass playerClass : classes.values()) {
            playerClass.loseExp(amount, percent, changeLevel);
        }
    }

    /**
     * Causes the player to lose experience as a penalty (generally for dying)
     */
    public void loseExp() {
        for (PlayerClass playerClass : classes.values()) {
            double penalty = playerClass.getData().getGroupSettings().getDeathPenalty();
            if (penalty > 0) {
                playerClass.loseExp(penalty);
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Health and Mana                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Gives levels to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    public boolean giveLevels(int amount, ExpSource source) {
        boolean success = false;
        for (PlayerClass playerClass : classes.values()) {
            RPGClass data = playerClass.getData();
            if (data.receivesExp(source)) {
                success = true;
                playerClass.giveLevels(amount);
            }
        }
        this.updatePlayerStat(getPlayer());
        return success;
    }

    /**
     * Causes the player to lose levels
     */
    public void loseLevels(int amount) {
        classes.values().stream()
                .filter(playerClass -> amount > 0)
                .forEach(playerClass -> playerClass.loseLevels(amount));
    }

    /**
     * Gives skill points to the player for all classes matching the experience source
     *
     * @deprecated See {@link PlayerData#givePoints(int, PointSource)} instead
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    @Deprecated
    public void givePoints(int amount, ExpSource source) {
        for (PlayerClass playerClass : classes.values()) {
            if (playerClass.getData().receivesExp(source)) {
                playerClass.givePoints(amount);
            }
        }
    }

    /**
     * Gives skill points to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    public void givePoints(int amount, PointSource source) {
        for (PlayerClass playerClass : classes.values()) {
            playerClass.givePoints(amount, source);
        }
    }

    /**
     * Sets the skill point amount to the player for all classes
     *
     * @param amount amount of levels to set to
     */
    public void setPoints(int amount) {
        for (PlayerClass playerClass : classes.values()) {
            playerClass.setPoints(amount);
        }
    }

    /**
     * Updates all the stats of a player based on their current attributes
     * This method is very heavy, consume resources and notable by player
     * Checkout other method such as {@link #updateWalkSpeed(Player)} for a light refresh
     * <br>
     * This also does not update the player equipment
     * You will need to call {@link PlayerEquips#update(Player)} before this function
     * to update attribute/stats that comes with equipments
     */
    public void updatePlayerStat(Player player) {
        if (!this.hasClass()) {
            this.maxHealth = 0;
            if (player != null)
                this.updateHealth(player);
            return;
        }

        final double oldMaxHealth = this.maxHealth;
        this.maxHealth = 0;
        this.maxMana = 0;

        for (PlayerClass playerClass : classes.values()) {
            this.maxHealth += playerClass.getHealth();
            this.maxMana += playerClass.getMana();
        }

        this.maxHealth = this.scaleStat(AttributeManager.HEALTH, maxHealth);
        this.maxMana = this.scaleStat(AttributeManager.MANA, maxMana);

        this.mana = Math.min(mana, maxMana);

        // AsyncPlayerPreLoginEvent has to call this without player object to update Mana
        if (player == null) {
            return;
        }

        this.updateWalkSpeed(player);

        // Update health if it's been changed
        if (oldMaxHealth != this.maxHealth
                || player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() != this.maxHealth) {
            this.updateHealth(player);
        } else {
            // Health scaling is available starting with 1.6.2
            if (SkillAPI.getSettings().isOldHealth()) {
                player.setHealthScaled(true);
                player.setHealthScale(20);
            } else {
                player.setHealthScaled(false);
            }
        }

        // Others stats
        if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
            this.updateMCAttribute(player, Attribute.GENERIC_ATTACK_SPEED, AttributeManager.ATTACK_SPEED, 0, 1024);
            this.updateMCAttribute(player, Attribute.GENERIC_ARMOR, AttributeManager.ARMOR, 0, 30);
            this.updateMCAttribute(player, Attribute.GENERIC_LUCK, AttributeManager.LUCK, -1024, 1024);
            this.updateMCAttribute(player,
                    Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                    AttributeManager.KNOCKBACK_RESIST,
                    0,
                    1.0);
        }
        if (VersionManager.isVersionAtLeast(110200)) {
            this.updateMCAttribute(player, Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeManager.ARMOR_TOUGHNESS, 0, 20);
        }

    }

    /**
     * Updates walk speed of a player based on their current attributes and apply
     *
     * @param player the player
     */
    public void updateWalkSpeed(Player player) {

        float level = (float) (this.scaleStat(AttributeManager.MOVE_SPEED, 0.2f, 0D, Double.MAX_VALUE));
        try {
            player.setWalkSpeed(level);
        } catch (IllegalArgumentException e) {
            SkillAPI.inst()
                    .getLogger()
                    .warning("Attempted to set player speed to " + level + " but failed: " + e.getMessage());
        }

    }

    private double getModifiedMaxHealth(Player player) {
        final double baseMaxHealth = this.maxHealth;
        double       modifiedMax   = this.maxHealth;
        // Actually apply other modifiers (Like from RPGItems
        for (ItemStack equipment : EntityUT.getEquipment(player)) {
            if (equipment == null || equipment.getType().isAir() || equipment.getItemMeta() == null) continue;

            ItemMeta meta = equipment.getItemMeta();
            if (!meta.hasAttributeModifiers()
                    || meta.getAttributeModifiers(NBTAttribute.MAX_HEALTH.getAttribute()) == null)
                continue;

            for (AttributeModifier modifier : meta.getAttributeModifiers(NBTAttribute.MAX_HEALTH.getAttribute())) {
                switch (modifier.getOperation()) {
                    case MULTIPLY_SCALAR_1:
                        modifiedMax += baseMaxHealth * modifier.getAmount();
                        break;
                    default:
                        modifiedMax += modifier.getAmount();
                }
            }
        }

        return modifiedMax;
    }

    /**
     * Updates health of a player based on their current attributes and apply
     *
     * @param player the player
     */
    public void updateHealth(Player player) {
        if (!SkillAPI.getSettings().isModifyHealth()) return;

        if (this.maxHealth <= 0) {
            this.maxHealth = SkillAPI.getSettings().getDefaultHealth();
            this.health = this.maxHealth;
        }

        double modifiedMax = getModifiedMaxHealth(player);

        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(this.maxHealth);

        // Health scaling is available starting with 1.6.2
        if (SkillAPI.getSettings().isOldHealth()) {
            if (SkillAPI.getSettings().isDownScaling() && player.getMaxHealth() < 20)
                player.setHealthScaled(false);
            else {
                player.setHealthScaled(true);
                player.setHealthScale(20);
            }
        } else
            player.setHealthScaled(false);

        if (player.getHealth() > modifiedMax)
            player.setHealth(this.maxHealth);

    }

    private void updateMCAttribute(Player player, Attribute attribute, String attribKey, double min, double max) {

        AttributeInstance instance = player.getAttribute(attribute);
        double            def      = instance.getDefaultValue();
        double            modified = this.scaleStat(attribKey, def, min, max);
        instance.setBaseValue(/*def + */modified);
    }

    /**
     * Retrieves the amount of mana the player currently has.
     *
     * @return current player mana
     */
    public double getMana() {
        return mana;
    }

    /**
     * Sets the player's amount of mana without launching events
     *
     * @param amount current mana
     */
    public void setMana(double amount) {
        this.mana = amount;
    }

    /**
     * Checks whether the player has at least the specified amount of mana
     *
     * @param amount required mana amount
     * @return true if has the amount of mana, false otherwise
     */
    public boolean hasMana(double amount) {
        return mana >= amount;
    }

    /**
     * Retrieves the max amount of mana the player can have including bonus mana
     *
     * @return max amount of mana the player can have
     */
    public double getMaxMana() {
        return maxMana;
    }

    /**
     * Regenerates mana for the player based on the regen amounts of professed classes
     */
    public void regenMana() {
        double amount = 0;
        for (PlayerClass c : classes.values()) {
            if (c.getData().hasManaRegen()) {
                amount += c.getData().getManaRegen();
            }
        }
        if (amount > 0) {
            double finalAmount = amount;
            Bukkit.getScheduler().runTask(NexEngine.get(), () -> giveMana(finalAmount, ManaSource.REGEN));
        }
    }

    /**
     * Gives mana to the player from an unknown source. This will not
     * cause the player's mana to go above their max amount.
     *
     * @param amount amount of mana to give
     */
    public void giveMana(double amount) {
        giveMana(amount, ManaSource.SPECIAL);
    }

    /**
     * Gives mana to the player from the given mana source. This will not
     * cause the player's mana to go above the max amount.
     *
     * @param amount amount of mana to give
     * @param source source of the mana
     */
    public void giveMana(double amount, ManaSource source) {
        PlayerManaGainEvent event = new PlayerManaGainEvent(this, amount, source);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            Logger.log(LogType.MANA,
                    2,
                    getPlayerName() + " gained " + amount + " mana due to " + event.getSource().name());

            mana += event.getAmount();
            if (mana > maxMana) {
                mana = maxMana;
            }
            if (mana < 0) {
                mana = 0;
            }
        } else {
            Logger.log(LogType.MANA, 2, getPlayerName() + " had their mana gain cancelled");
        }
    }

    /**
     * Takes mana away from the player for an unknown reason. This will not
     * cause the player to fall below 0 mana.
     *
     * @param amount amount of mana to take away
     */
    public void useMana(double amount) {
        useMana(amount, ManaCost.SPECIAL);
    }

    /**
     * Takes mana away from the player for the specified reason. This will not
     * cause the player to fall below 0 mana.
     *
     * @param amount amount of mana to take away
     * @param cost   source of the mana cost
     */
    public void useMana(double amount, ManaCost cost) {
        PlayerManaLossEvent event = new PlayerManaLossEvent(this, amount, cost);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            Logger.log(LogType.MANA,
                    2,
                    getPlayerName() + " used " + amount + " mana due to " + event.getSource().name());

            mana -= event.getAmount();
            if (mana < 0) {
                mana = 0;
            }
        }
    }

    /**
     * Remove stat modifier with the exact uuid
     *
     * @param uuid   The uuid
     * @param update calculate player stat immediately and apply to him
     */
    public void removeStatModifier(UUID uuid, boolean update) {
        for (Entry<String, List<PlayerStatModifier>> entry : this.statModifiers.entrySet()) {
            List<PlayerStatModifier>     modifiers = entry.getValue();
            Iterator<PlayerStatModifier> i         = modifiers.iterator();

            while (i.hasNext()) {
                PlayerStatModifier modifier = i.next();
                if (modifier.getUUID().equals(uuid)) {
                    i.remove();
                }
            }

            this.statModifiers.put(entry.getKey(), modifiers);
        }

        if (update) {
            this.updatePlayerStat(getPlayer());
        }
    }

    /**
     * Clear all stat modifier which is not persistent
     */
    public void clearStatModifier() {
        for (Entry<String, List<PlayerStatModifier>> entry : this.statModifiers.entrySet()) {
            List<PlayerStatModifier>     modifiers = entry.getValue();
            Iterator<PlayerStatModifier> i         = modifiers.iterator();

            while (i.hasNext()) {
                PlayerStatModifier modifier = i.next();
                if (!modifier.isPersistent()) {
                    i.remove();
                }
            }

            this.statModifiers.put(entry.getKey(), modifiers);
        }

        this.updatePlayerStat(getPlayer());
    }

    /**
     * Remove attribute modifier with the exact uuid
     *
     * @param uuid   The uuid
     * @param update calculate player stat immediately and apply to him
     */
    public void removeAttributeModifier(UUID uuid, boolean update) {
        for (Entry<String, List<PlayerAttributeModifier>> entry : this.attributesModifiers.entrySet()) {
            List<PlayerAttributeModifier>     modifiers = entry.getValue();
            Iterator<PlayerAttributeModifier> i         = modifiers.iterator();

            while (i.hasNext()) {
                PlayerAttributeModifier modifier = i.next();
                if (modifier.getUUID().equals(uuid)) {
                    i.remove();
                }
            }

            this.attributesModifiers.put(entry.getKey(), modifiers);
        }

        if (update) {
            this.updatePlayerStat(getPlayer());
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Skill Binding                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Clear all attribute modifier which is not persistent
     */
    public void clearAttributeModifiers() {
        for (Entry<String, List<PlayerAttributeModifier>> entry : this.attributesModifiers.entrySet()) {
            List<PlayerAttributeModifier>     modifiers = entry.getValue();
            Iterator<PlayerAttributeModifier> i         = modifiers.iterator();

            while (i.hasNext()) {
                PlayerAttributeModifier modifier = i.next();
                if (!modifier.isPersistent()) {
                    i.remove();
                }
            }

            this.attributesModifiers.put(entry.getKey(), modifiers);
        }

        this.equips.update(getPlayer());
        this.updatePlayerStat(getPlayer());
    }

    /**
     * Clear all of the modifiers including stat modifier and attribute modifier
     */
    public void clearAllModifiers() {
        this.clearStatModifier();
        this.clearAttributeModifiers();
    }

    /**
     * Retrieves a skill the player has bound by material
     *
     * @param mat material to get the bind for
     * @return skill bound to the material or null if none are bound
     */
    @Deprecated
    public PlayerSkill getBoundSkill(Material mat) {
        return null;
    }

    /**
     * Retrieves the bound data for the player. Modifying this map will
     * modify the bindings the player has.
     *
     * @return the skill binds data for the player
     */
    @Deprecated
    public HashMap<Material, PlayerSkill> getBinds() {
        return new HashMap<>();
    }

    /**
     * Checks whether the material has a skill bound to it
     *
     * @param mat material to check
     * @return true if a skill is bound to it, false otherwise
     */
    @Deprecated
    public boolean isBound(Material mat) {
        return false;
    }

    /**
     * Binds a skill to a material for the player. The bind will not work if the skill
     * was already bound to the material.
     *
     * @param mat   material to bind the skill to
     * @param skill skill to bind to the material
     * @return true if was able to bind the skill, false otherwise
     */
    @Deprecated
    public boolean bind(Material mat, PlayerSkill skill) {
        return false;
    }

    /**
     * Clears a skill binding on the material. If there is no binding on the
     * material, this will do nothing.
     *
     * @param mat material to clear bindings from
     * @return true if a binding was cleared, false otherwise
     */
    @Deprecated
    public boolean clearBind(Material mat) {
        return false;
    }


    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Persistent data                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Decrypt and return the saved values on the account.
     * @param key The key is used to save the value.
     * @return Decrypted value
     */
    public Object getPersistentData(String key){
        String data = persistentData.get(key);
        if (data==null) return 0;
        if (data.startsWith("targets")){
            data = data.substring(8);
            List<LivingEntity> targets = new ArrayList<>();
            Arrays.stream(data.split(";")).forEach(target -> {
                if (target.startsWith("loc")) {
                    String[] loc = target.split(",");
                    Location location = new Location(
                            Bukkit.getWorld(loc[1]),
                            Double.parseDouble(loc[2]),
                            Double.parseDouble(loc[3]),
                            Double.parseDouble(loc[4])
                    );
                    targets.add(new TempEntity(location));
                } else if (target.startsWith("entity-")) {
                    Entity entity = Bukkit.getEntity(UUID.fromString(target.substring(7)));
                    if (entity!=null) targets.add((LivingEntity) entity);
                } else {
                    Player player = Bukkit.getPlayer(UUID.fromString(target));
                    if (player==null || !player.isOnline()) return;
                    targets.add(player);
                }
            });
            return targets;
        }
        if (data.startsWith("loc")) {
            String[] loc = data.split(",");
            return new Location(
                    Bukkit.getWorld(loc[1]),
                    Double.parseDouble(loc[2]),
                    Double.parseDouble(loc[3]),
                    Double.parseDouble(loc[4])
            );
        }
        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException ignored){}
        return data;
    }


    /**
     * Encrypt and save values to account for long-term storage
     * @param key The key is used to save the value.
     * @param data The value is stored. Currently supported types are:
     *             Number, String, Player, TempEntity, Entity
     */
    public void setPersistentData(String key, Object data){
        if (data==null || Objects.equals(data, 0)) {
            removePersistentData(key);
            return;
        }
        if (data instanceof List){
            List<String> sum = new ArrayList<>();
            ((List<?>) data).forEach(entry -> {
                if (entry instanceof Player){
                    sum.add(((Player) entry).getUniqueId().toString());
                } else if (entry instanceof TempEntity) {
                    Location loc = ((TempEntity) entry).getLocation();
                    if (loc.getWorld()==null) return;
                    sum.add(String.format("loc,%s,%f,%f,%f", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
                } else if (entry instanceof Entity){
                    sum.add("entity-"+((Entity) entry).getUniqueId());
                }
            });
            if (sum.isEmpty()) return;
            persistentData.put(key,"targets-"+String.join(";", sum));
            return;
        }
        if (data instanceof Location){
            Location loc = (Location) data;
            persistentData.put(key, String.format("loc,%s,%f,%f,%f", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
            return;
        }
        persistentData.put(key,data.toString());
    }

    /**
     * Remove a value with a specific key
     * @param key The key is used to save the value.
     */
    public void removePersistentData(String key){
        persistentData.remove(key);
    }

    /**
     * @return original HashMap used to store persistent data
     */
    public HashMap<String,String> getAllPersistentData(){
        return persistentData;
    }



    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Functions                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Clears the skill binding for the given skill. This will remove the bindings
     * on all materials involving the skill.
     *
     * @param skill skill to unbind
     */
    @Deprecated
    public void clearBinds(Skill skill) {}

    /**
     * Clears all binds the player currently has
     */
    @Deprecated
    public void clearAllBinds() {}

    /**
     * Records any data to save with class data
     *
     * @param player player to record for
     */
    public void record(Player player) {
        this.lastHealth = player.getHealth();
    }

    /**
     * Updates the scoreboard with the player's current class.
     * This is already done by the API and doesn't need to be
     * done by other plugins.
     */
    public void updateScoreboard() {
        if (SkillAPI.getSettings().isShowScoreboard()) {
            SkillAPI.schedule(new ScoreboardTask(this), 2);
        }
    }

    /**
     * Starts passive abilities for the player if they are online. This is
     * already called by the API and shouldn't be called by other plugins.
     *
     * @param player player to set the passive skills up for
     */
    public void startPassives(Player player) {
        if (player == null) {
            return;
        }
        passive = true;
        for (PlayerSkill skill : skills.values()) {
            if (skill.isUnlocked() && (skill.getData() instanceof PassiveSkill)) {
                ((PassiveSkill) skill.getData()).initialize(player, skill.getLevel());
            }
        }
    }

    /**
     * Stops passive abilities for the player if they are online. This is already
     * called by the API and shouldn't be called by other plugins.
     *
     * @param player player to stop the passive skills for
     */
    public void stopPassives(Player player) {
        if (player == null) {
            return;
        }
        passive = false;
        for (PlayerSkill skill : skills.values()) {
            if (skill.isUnlocked() && (skill.getData() instanceof PassiveSkill)) {
                try {
                    ((PassiveSkill) skill.getData()).stopEffects(player, skill.getLevel());
                } catch (Exception ex) {
                    Logger.bug("Failed to stop passive skill " + skill.getData().getName());
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Casts a skill by name for the player. In order to cast the skill,
     * the player must be online, have the skill unlocked, have enough mana,
     * have the skill off cooldown, and have a proper target if applicable.
     *
     * @param skillName name of the skill ot cast
     * @return true if successfully cast the skill, false otherwise
     */
    public boolean cast(String skillName) {
        return cast(skills.get(skillName.toLowerCase()));
    }

    /**
     * Casts a skill for the player. In order to cast the skill,
     * the player must be online, have the skill unlocked, have enough mana,
     * have the skill off cooldown, and have a proper target if applicable.
     *
     * @param skill skill to cast
     * @return true if successfully cast the skill, false otherwise
     */
    public boolean cast(PlayerSkill skill) {
        // Invalid skill
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null");
        }

        int level = skill.getLevel();

        // Not unlocked or on cooldown
        if (!check(skill, true, true)) return false;

        // Dead players can't cast skills
        Player p = getPlayer();
        if (p.isDead()) return PlayerSkillCastFailedEvent.invoke(skill, Cause.CASTER_DEAD);

        // Disable casting in spectator mode
        if (p.getGameMode().name().equals("SPECTATOR"))
            return PlayerSkillCastFailedEvent.invoke(skill, Cause.SPECTATOR);

        // Skill Shots
        if (skill.getData() instanceof SkillShot) {
            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill, p);
            Bukkit.getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled()) {
                try {
                    if (((SkillShot) skill.getData()).cast(p, level)) {
                        return applyUse(p, skill, event.getManaCost());
                    } else {
                        return PlayerSkillCastFailedEvent.invoke(skill, Cause.EFFECT_FAILED);
                    }
                } catch (Exception ex) {
                    Logger.bug("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                    return PlayerSkillCastFailedEvent.invoke(skill, Cause.EFFECT_FAILED);
                }
            } else {
                return PlayerSkillCastFailedEvent.invoke(skill, Cause.CANCELED);
            }
        }

        // Target Skills
        else if (skill.getData() instanceof TargetSkill) {
            LivingEntity target = TargetHelper.getLivingTarget(p, skill.getData().getRange(level));

            // Must have a target
            if (target == null) {
                return PlayerSkillCastFailedEvent.invoke(skill, Cause.NO_TARGET);
            }

            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill, p);
            Bukkit.getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled()) {
                try {
                    final boolean canAttack = !SkillAPI.getSettings().canAttack(p, target);
                    if (((TargetSkill) skill.getData()).cast(p, target, level, canAttack)) {
                        return applyUse(p, skill, event.getManaCost());
                    } else {
                        return PlayerSkillCastFailedEvent.invoke(skill, Cause.EFFECT_FAILED);
                    }
                } catch (Exception ex) {
                    Logger.bug("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                    return PlayerSkillCastFailedEvent.invoke(skill, Cause.EFFECT_FAILED);
                }
            } else {
                PlayerSkillCastFailedEvent.invoke(skill, Cause.CANCELED);
            }
        }

        return false;
    }

    private boolean applyUse(final Player player, final PlayerSkill skill, final double manaCost) {
        player.setMetadata("custom-cooldown", new FixedMetadataValue(SkillAPI.inst(), 1));
        skill.startCooldown();
        if (SkillAPI.getSettings().isShowSkillMessages()) {
            skill.getData().sendMessage(player, SkillAPI.getSettings().getMessageRadius());
        }
        if (SkillAPI.getSettings().isManaEnabled()) {
            useMana(manaCost, ManaCost.SKILL_CAST);
        }
        skillTimer = System.currentTimeMillis() + SkillAPI.getSettings().getCastCooldown();
        if (removeTimer != null) {
            if (!removeTimer.isCancelled()) removeTimer.cancel();
        }
        removeTimer = Bukkit.getScheduler()
                .runTaskLater(SkillAPI.inst(), () -> player.removeMetadata("custom-cooldown", SkillAPI.inst()), 20L);
        return true;
    }

    /**
     * Checks the cooldown and mana requirements for a skill
     *
     * @param skill    skill to check for
     * @param cooldown whether to check cooldowns
     * @param mana     whether to check mana requirements
     * @return true if can use
     */
    public boolean check(PlayerSkill skill, boolean cooldown, boolean mana) {
        if (skill == null || System.currentTimeMillis() < skillTimer) {
            return false;
        }

        SkillStatus status = skill.getStatus();
        int         level  = skill.getLevel();
        double      cost   = skill.getData().getManaCost(level);

        // Not unlocked
        if (level <= 0) {
            return PlayerSkillCastFailedEvent.invoke(skill, Cause.NOT_UNLOCKED);
        }

        // On Cooldown
        if (status == SkillStatus.ON_COOLDOWN && cooldown) {
            if (skill.getData().cooldownMessage() && !onCooldown.contains(getUUID())) {
                SkillAPI.getLanguage()
                        .sendMessage(ErrorNodes.COOLDOWN,
                                getPlayer(),
                                FilterType.COLOR,
                                RPGFilter.COOLDOWN.setReplacement(skill.getCooldown() + ""),
                                RPGFilter.SKILL.setReplacement(skill.getData().getName()));
                onCooldown.add(getUUID());
                Bukkit.getScheduler().runTaskLater(SkillAPI.inst(), () -> onCooldown.remove(getUUID()), 40L);
            }
            return PlayerSkillCastFailedEvent.invoke(skill, Cause.ON_COOLDOWN);
        }

        // Not enough mana
        else if (status == SkillStatus.MISSING_MANA && mana) {
            SkillAPI.getLanguage()
                    .sendMessage(ErrorNodes.MANA,
                            getPlayer(),
                            FilterType.COLOR,
                            RPGFilter.SKILL.setReplacement(skill.getData().getName()),
                            RPGFilter.MANA.setReplacement(getMana() + ""),
                            RPGFilter.COST.setReplacement((int) Math.ceil(cost) + ""),
                            RPGFilter.MISSING.setReplacement((int) Math.ceil(cost - getMana()) + ""));
            return PlayerSkillCastFailedEvent.invoke(skill, Cause.NO_MANA);
        } else {
            return true;
        }
    }

    /**
     * Stops the current preview, if any, and registers
     * the on-stop runnable for a new preview, if any
     *
     * @param onPreviewStop runnable to execute when the new preview stops
     */
    public void setOnPreviewStop(@Nullable Runnable onPreviewStop) {
        if (this.onPreviewStop != null) this.onPreviewStop.run();
        this.onPreviewStop = onPreviewStop;
    }

    /**
     * Initializes the application of the data for the player
     *
     * @param player player to set up for
     */
    public void init(Player player) {
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) {
            return;
        }

        this.getEquips().update(player);
        this.updatePlayerStat(player);
        this.startPassives(player);
        this.updateScoreboard();
        if (this.getLastHealth() > 0 && !player.isDead()) {
            player.setHealth(Math.min(this.getLastHealth(), player.getMaxHealth()));
        }

        this.autoLevel();
        this.updateScoreboard();
    }

    public static class ExternallyAddedSkill {
        private final String        id;
        private final NamespacedKey key;
        private final int           level;

        public ExternallyAddedSkill(String id, NamespacedKey key, int level) {
            this.id = id;
            this.key = key;
            this.level = level;
        }

        public String getId() {return id;}

        public NamespacedKey getKey() {return key;}

        public int getLevel() {return level;}
    }
}
