/**
 * Fabled
 * studio.magemonkey.fabled.manager.ClassBoardManager
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.manager;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.data.CustomScoreboardHolder;
import studio.magemonkey.fabled.util.PlaceholderUtil;
import studio.magemonkey.codex.mccore.chat.Chat;
import studio.magemonkey.codex.mccore.chat.Prefix;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.mccore.scoreboard.*;

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
        Chat.getPlayerData(player.getName()).clearPluginPrefix("Fabled");
        BoardManager.getPlayerBoards(player.getName()).removeBoards("Fabled");
        BoardManager.clearTeam(player.getName());
        BoardManager.clearScore(player.getName());
    }

    /**
     * Clears all scoreboards for the plugin
     */
    public static void clearAll() {
        BoardManager.clearPluginBoards("Fabled");
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
                    new Prefix("Fabled", prefix, braceColor)
            );

            PlayerBoards boards = BoardManager.getPlayerBoards(player.getPlayerName());

            // Clear previous data
            boards.removeBoards("Fabled");
            BoardManager.clearTeam(player.getPlayerName());

            // Apply new data
            if (Fabled.getSettings().isShowScoreboard()) {
                StatBoard board = new StatBoard(
                        PlaceholderUtil.colorizeAndReplace(
                                Fabled.getSettings().getScoreboardTitle(),
                                player.getPlayer()
                        ), "Fabled");
                StatHolder holder = new CustomScoreboardHolder(player.getPlayer());
                board.addStats(holder);
                boards.addBoard(board);
            }
            if (Fabled.getSettings().isShowClassName()) {
                BoardManager.setTeam(player.getPlayerName(), player.getMainClass().getData().getName());
            }
            if (Fabled.getSettings().isShowClassLevel()) {
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
    public static void registerClass(FabledClass c) {
        if (Fabled.getSettings().isShowClassName()) {
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
        if (Fabled.getSettings().isShowClassLevel() && data.hasClass()) {
            BoardManager.setBelowNameScore(data.getPlayerName(), data.getMainClass().getLevel());
        }
    }

    /**
     * Registers the text below player names
     */
    public static void registerText() {
        if (Fabled.getSettings().isShowClassLevel()) {
            BoardManager.init();
            BoardManager.setTextBelowNames(Fabled.getSettings().getLevelText());
        }
    }
}
