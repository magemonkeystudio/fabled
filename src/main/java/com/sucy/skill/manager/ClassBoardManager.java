/**
 * SkillAPI
 * com.sucy.skill.manager.ClassBoardManager
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
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.CustomScoreboardHolder;
import com.sucy.skill.util.PlaceholderUtil;
import mc.promcteam.engine.mccore.chat.Chat;
import mc.promcteam.engine.mccore.chat.Prefix;
import mc.promcteam.engine.mccore.scoreboard.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Manages prefixes for classes
 * - Only works if ChatAPI is installed -
 */
public class ClassBoardManager {
    /**
     * Clears the prefix for a player
     *
     * @param player player reference
     */
    public static void clear(Player player) {
        Chat.getPlayerData(player.getName()).clearPluginPrefix("SkillAPI");
        BoardManager.getPlayerBoards(player.getName()).removeBoards("SkillAPI");
        BoardManager.clearTeam(player.getName());
        BoardManager.clearScore(player.getName());
    }

    /**
     * Clears all scoreboards for the plugin
     */
    public static void clearAll() {
        BoardManager.clearPluginBoards("SkillAPI");
    }

    /**
     * Updates scoreboard information for the player data
     *
     * @param player     player name
     * @param prefix     prefix text
     * @param braceColor color of braces
     */
    public static void update(PlayerData player, String prefix, ChatColor braceColor) {
        try {
            // Give a chat prefix
            Chat.getPlayerData(player.getPlayerName()).setPluginPrefix(
                    new Prefix("SkillAPI", prefix, braceColor)
            );

            PlayerBoards boards = BoardManager.getPlayerBoards(player.getPlayerName());

            // Clear previous data
            boards.removeBoards("SkillAPI");
            BoardManager.clearTeam(player.getPlayerName());

            // Apply new data
            if (SkillAPI.getSettings().isShowScoreboard()) {
                StatBoard board = new StatBoard(
                        PlaceholderUtil.colorizeAndReplace(
                                SkillAPI.getSettings().getScoreboardTitle(),
                                player.getPlayer()
                        ), "SkillAPI");
                StatHolder holder = new CustomScoreboardHolder(player.getPlayer());
                board.addStats(holder);
                boards.addBoard(board);
            }
            if (SkillAPI.getSettings().isShowClassName()) {
                BoardManager.setTeam(player.getPlayerName(), player.getMainClass().getData().getName());
            }
            if (SkillAPI.getSettings().isShowClassLevel()) {
                BoardManager.setBelowNameScore(player.getPlayerName(), player.getMainClass().getLevel());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Registers a class with the MCCore scoreboards
     *
     * @param c class to register
     */
    public static void registerClass(RPGClass c) {
        if (SkillAPI.getSettings().isShowClassName()) {
            String name = c.getName();
            if (name.length() > 16) {
                name = name.substring(0, 16);
            }
            BoardManager.registerTeam(new Team(name, c.getPrefix() + ChatColor.RESET + " ", null));
        }
    }

    /**
     * Updates the player's level in the scoreboards
     *
     * @param data player's data to use for the update
     */
    public static void updateLevel(PlayerData data) {
        if (SkillAPI.getSettings().isShowClassLevel() && data.hasClass()) {
            BoardManager.setBelowNameScore(data.getPlayerName(), data.getMainClass().getLevel());
        }
    }

    /**
     * Registers the text below player names
     */
    public static void registerText() {
        if (SkillAPI.getSettings().isShowClassLevel()) {
            BoardManager.init();
            BoardManager.setTextBelowNames(SkillAPI.getSettings().getLevelText());
        }
    }
}
