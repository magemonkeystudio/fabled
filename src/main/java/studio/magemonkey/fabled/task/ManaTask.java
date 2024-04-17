/**
 * Fabled
 * studio.magemonkey.fabled.task.ManaTask
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
package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.log.LogType;
import studio.magemonkey.fabled.log.Logger;
import studio.magemonkey.fabled.thread.RepeatThreadTask;
import studio.magemonkey.codex.mccore.util.VersionManager;
import org.bukkit.entity.Player;

/**
 * <p>Restores mana to all players over time.</p>
 * <p>This task is run by the API and you should not
 * use this task yourself.</p>
 */
public class ManaTask extends RepeatThreadTask {
    /**
     * Starts a new task for regenerating mana over time. The task is
     * started automatically so don't initialize this class unless wanting to
     * start a new task.
     */
    public ManaTask() {
        super(
                Fabled.getSettings().getGainFreq(),
                Fabled.getSettings().getGainFreq()
        );
    }

    /**
     * <p>Checks all players for mana regeneration each interval</p>
     */
    public void run() {
        Player[] players = VersionManager.getOnlinePlayers();
        Logger.log(LogType.MANA, 1, "Applying mana regen for " + players.length + " players");
        for (Player player : players) {
            PlayerData data = Fabled.getPlayerData(player);
            data.regenMana();
        }
    }
}
