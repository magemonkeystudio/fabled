/**
 * SkillAPI
 * com.sucy.skill.data.GroupSettings
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
package com.sucy.skill.data;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.log.Logger;
import lombok.Getter;
import lombok.experimental.Accessors;
import mc.promcteam.engine.mccore.config.parse.DataSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings for class groups
 */
public class GroupSettings {
    private static final int[] POINTS = new int[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

    private static final String  PROFESS_RESET             = "profess-reset";
    private static final String  PROFESS_REFUND_SKILLS     = "profess-refund-skills";
    private static final String  PROFESS_REFUND_ATTRIBUTES = "profess-refund-attributes";
    private static final String  CAN_RESET                 = "can-reset";
    private static final String  EXP_LOST                  = "exp-lost-on-death";
    private static final String  STARTING_POINTS           = "starting-points";
    private static final String  POINTS_PER_LEVEL          = "points-per-level";
    private static final String  PERMISSION                = "permission";
    private static final String  FRIENDLY                  = "friendly";
    private static final String  DEFAULT                   = "default";
    private static final String  STARTING_ATTRIBS          = "starting-attribs";
    private static final String  ATTRIB_PER_LEVEL          = "attribs-per-level";
    private static final String  SCOREBOARD                = "show-scoreboard";
    private static final String  CUSTOM_POINTS             = "use-custom-points";
    private static final String  DEFINED_POINTS            = "custom-points";
    private static final String  CUSTOM_ATTRIBS            = "use-custom-attribute-points";
    private static final String  DEFINED_ATTRIBS           = "custom-attribute-points";
    private              String  defaultClass              = "none";
    private              String  permission                = "none";
    /**
     * Checks whether classes reset upon profession in the group
     *
     * @return true if resets upon profession, false otherwise
     */
    @Getter
    private              boolean professReset              = false;
    @Getter
    private              boolean professRefundSkills       = false;
    @Getter
    private              boolean professRefundAttributes   = false;
    /**
     * @return Whether to show the scoreboard or not
     */
    @Getter
    private              boolean showScoreboard            = true;
    /**
     * @return true if the group is allowed to reset, false otherwise
     */
    @Getter
    @Accessors(fluent = true)
    private              boolean canReset                  = true;
    /**
     * @return true if players with the same class under this group are allies
     */
    @Getter
    private              boolean friendly                  = false;
    private              boolean useCustomPoints           = false;
    private              boolean useCustomAttribs          = false;
    private              int[]   customPoints              = POINTS;
    private              int[]   customAttribs             = POINTS;
    /**
     * Retrieves the death penalty for classes in this group
     *
     * @return death penalty
     */
    @Getter
    private              double  deathPenalty              = 0;
    /**
     * Retrieves the number of skill points classes in this group start with
     *
     * @return starting skill points
     */
    @Getter
    private              int     startingPoints            = 1;
    /**
     * Retrieves the number of skill points gained in the group per level
     *
     * @return skill points per level
     */
    @Getter
    private              double  pointsPerLevel            = 1;
    /**
     * Retrieves the number of attribute points gained each level
     *
     * @return attribute points gained each level
     */
    @Getter
    private              double  attribsPerLevel           = 1;
    /**
     * @return attribute points classes in the group start with
     */
    @Getter
    private              int     startingAttribs           = 0;

    /**
     * Initializes group settings with default settings
     */
    public GroupSettings() {}

    /**
     * Initializes a new set of settings for a class group by
     * loading settings from the config
     *
     * @param config config to load from
     */
    public GroupSettings(DataSection config) {
        defaultClass = config.getString(DEFAULT, defaultClass);
        permission = config.getString(PERMISSION, permission);
        professReset = config.getBoolean(PROFESS_RESET, professReset);
        professRefundAttributes = config.getBoolean(PROFESS_REFUND_ATTRIBUTES, professRefundAttributes);
        professRefundSkills = config.getBoolean(PROFESS_REFUND_SKILLS, professRefundSkills);
        showScoreboard = config.getBoolean(SCOREBOARD, showScoreboard);
        canReset = config.getBoolean(CAN_RESET, canReset);
        friendly = config.getBoolean(FRIENDLY, friendly);
        deathPenalty = config.getDouble(EXP_LOST, deathPenalty);
        startingPoints = config.getInt(STARTING_POINTS, startingPoints);
        pointsPerLevel = config.getDouble(POINTS_PER_LEVEL, pointsPerLevel);
        attribsPerLevel = config.getDouble(ATTRIB_PER_LEVEL, attribsPerLevel);
        startingAttribs = config.getInt(STARTING_ATTRIBS, startingAttribs);
        useCustomPoints = config.getBoolean(CUSTOM_POINTS, false);
        useCustomAttribs = config.getBoolean(CUSTOM_ATTRIBS, false);
        customPoints = loadCustomPoints(config.getSection(DEFINED_POINTS));
        customAttribs = loadCustomPoints(config.getSection(DEFINED_ATTRIBS));
        save(config);
    }

    private int[] loadCustomPoints(final DataSection data) {
        if (data != null) {
            final List<Integer> points = new ArrayList<>();
            for (final String key : data.keys()) {
                try {
                    final int level = Integer.parseInt(key);
                    if (level < points.size()) {
                        points.set(level, data.getInt(key, 0));
                    } else {
                        while (level > points.size()) {
                            points.add(0);
                        }
                        points.add(data.getInt(key, 0));
                    }
                } catch (final NumberFormatException ex) {
                    Logger.invalid(key + " is not a valid level for custom skill points");
                }
            }
            return points.stream().mapToInt(Integer::intValue).toArray();
        }
        return POINTS;
    }

    /**
     * Retrieves the default class of the group
     *
     * @return default class of the group or null/"none" if none
     */
    public RPGClass getDefault() {
        return SkillAPI.getClass(defaultClass);
    }

    /**
     * Retrieves whether or not the group requires a permission to use at all
     *
     * @return true if requires a permission, false otherwise
     */
    public boolean requiresPermission() {
        return !permission.equals("none");
    }

    /**
     * Gets the permission required by the group
     *
     * @return required permission or null if none
     */
    public String getPermission() {
        return requiresPermission() ? permission : null;
    }

    /**
     * Gets the number of skill points to gain for a specified level
     *
     * @param newLevel level to check for
     * @param oldLevel level coming from
     * @return gained points
     */
    public int getPointsForLevels(int newLevel, int oldLevel) {
        return computePoints(newLevel, oldLevel, useCustomPoints, customPoints, pointsPerLevel);
    }

    /**
     * Gets the number of attribute points to gain for a specified level
     *
     * @param newLevel level to check for
     * @param oldLevel level coming from
     * @return gained points
     */
    public int getAttribsForLevels(int newLevel, int oldLevel) {
        return computePoints(newLevel, oldLevel, useCustomAttribs, customAttribs, attribsPerLevel);
    }

    private int computePoints(int newLevel, int oldLevel, boolean custom, int[] data, double perLevel) {
        if (custom) {
            int newPoints = 0;
            for (int i = 1; i < data.length && i <= newLevel; i++) {
                newPoints += data[i];
            }

            int oldPoints = 0;
            for (int i = 1; i < data.length && i <= oldLevel; i++) {
                oldPoints += data[i];
            }

            return newPoints - oldPoints;
        } else {
            return (int) (newLevel * perLevel) - (int) (oldLevel * perLevel);
        }
    }

    /**
     * Saves the group settings to a config
     *
     * @param config config to save to
     */
    public void save(DataSection config) {
        config.setComments(DEFAULT, ImmutableList.of(
                "",
                " The starting class for all players for this group.",
                " \"none\" will result in no starting class"));
        config.set(DEFAULT, defaultClass);

        config.setComments(PERMISSION, ImmutableList.of(
                "",
                " The permission required to profess as any class in this group.",
                " \"none\" will result in no permissions being required"));
        config.set(PERMISSION, permission);

        config.setComments(PROFESS_RESET, ImmutableList.of(
                "",
                " Whether to reset a players level and skill points to starting values",
                " when professing into a subclass"));
        config.set(PROFESS_RESET, professReset);

        config.setComments(PROFESS_REFUND_SKILLS, ImmutableList.of(
                "",
                " If " + PROFESS_RESET + " is true, this determines if skill points are refunded",
                " or just deleted"));
        config.set(PROFESS_REFUND_SKILLS, professRefundSkills);

        config.setComments(PROFESS_REFUND_ATTRIBUTES, ImmutableList.of(
                "",
                " If " + PROFESS_RESET + " is true, this determines if attribute points are refunded",
                " or just deleted"));
        config.set(PROFESS_REFUND_ATTRIBUTES, professRefundAttributes);

        config.setComments(CAN_RESET, ImmutableList.of(
                "",
                " Whether or not this class is reset when players use the reset command"));
        config.set(CAN_RESET, canReset);

        config.setComments(FRIENDLY, ImmutableList.of(
                "",
                " Whether or not players professed as the same base class in this group",
                " are considered allies and cannot attack each other"));
        config.set(FRIENDLY, friendly);

        config.setComments(SCOREBOARD, ImmutableList.of(
                "",
                " Whether or not to show a scoreboard for classes within this group.",
                " Scoreboards must be enabled for this to work."));
        config.set(SCOREBOARD, showScoreboard);

        config.setComments(EXP_LOST, ImmutableList.of(
                "",
                " Percentage of experience lost upon dying.",
                " This will not cause players to lose levels."));
        config.set(EXP_LOST, deathPenalty);

        config.setComments(STARTING_POINTS, ImmutableList.of(
                "",
                " Number of skill points players start with"));
        config.set(STARTING_POINTS, startingPoints);

        config.setComments(POINTS_PER_LEVEL, ImmutableList.of(
                "",
                " How many skill points a player gains every level.",
                " You can use decimal values for one point every few levels.",
                " 0.2, for instance, would give one point every 5 levels.",
                " If use-custom-points is enabled, this is ignored"));
        config.set(POINTS_PER_LEVEL, pointsPerLevel);

        config.setComments(STARTING_ATTRIBS, ImmutableList.of("",
                " Number of attribute points players start with"));
        config.set(STARTING_ATTRIBS, startingAttribs);

        config.setComments(ATTRIB_PER_LEVEL, ImmutableList.of("",
                " How many attribute points a player gains every level.",
                " You can use decimal values for one point every few levels.",
                " 0.2, for instance, would give one point every 5 levels.",
                " If use-custom-attribute-points is enabled, this is ignored."));
        config.set(ATTRIB_PER_LEVEL, attribsPerLevel);

        config.setComments(CUSTOM_POINTS, ImmutableList.of("",
                " Whether or not to use custom skill point distribution.",
                " When enabled, skill points are given based on custom-points",
                " instead of points-per-level"));
        config.set(CUSTOM_POINTS, useCustomPoints);

        config.setComments(CUSTOM_ATTRIBS, ImmutableList.of("",
                " Whether or not to use custom attribute point distribution.",
                " When enabled, attribute points are given based on custom-attribute-points",
                " instead of attribs-per-level"));
        config.set(CUSTOM_ATTRIBS, useCustomAttribs);

        config.setComments(DEFINED_POINTS, ImmutableList.of("",
                " Defines how many skill points players get at specific levels.",
                " This only applies when use-custom-points is set to \"true\".",
                " Numbers on the left are the level the skill points are given.",
                " Numbers on the right are how many skill points are given."));
        savePoints(config.createSection(DEFINED_POINTS), customPoints);

        config.setComments(DEFINED_ATTRIBS, ImmutableList.of("",
                " Defines how many attribute points players get at specific levels.",
                " This only applies when use-custom-attribute-points is set to \"true\".",
                " Numbers on the left are the level the attribute points are given.",
                " Numbers on the right are how many attribute points are given."));
        savePoints(config.createSection(DEFINED_ATTRIBS), customAttribs);
    }

    private void savePoints(final DataSection destination, final int[] points) {
        for (int i = 0; i < points.length; i++) {
            if (points[i] > 0) {
                destination.set(Integer.toString(i), points[i]);
            }
        }
    }
}
