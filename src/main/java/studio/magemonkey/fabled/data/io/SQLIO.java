/**
 * Fabled
 * studio.magemonkey.fabled.data.io.SQLIO
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
package studio.magemonkey.fabled.data.io;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.config.parse.YAMLParser;
import studio.magemonkey.codex.mccore.sql.ColumnType;
import studio.magemonkey.codex.mccore.sql.direct.SQLDatabase;
import studio.magemonkey.codex.mccore.sql.direct.SQLTable;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.data.Settings;
import studio.magemonkey.fabled.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads player data from the SQL Database
 */
public class SQLIO extends IOManager {
    public static final String ID   = "id";
    public static final String DATA = "data";

    /**
     * Initializes the SQL IO Manager
     *
     * @param api API reference
     */
    public SQLIO(Fabled api) {
        super(api);
    }

    private SQLConnection openConnection() {
        SQLConnection connection = new SQLConnection();

        Settings settings = Fabled.getSettings();
        connection.database = new SQLDatabase(api, settings.getSqlHost(), settings.getSqlPort(),
                settings.getSqlDatabase(), settings.getSqlUser(), settings.getSqlPass());
        connection.database.openConnection();
        connection.table = connection.database.createTable(api, "players");

        connection.table.createColumn(ID, ColumnType.INCREMENT);
        connection.table.createColumn(DATA, ColumnType.MEDIUM_TEXT);
        return connection;
    }

    @Override
    public Map<String, PlayerAccounts> loadAll() {
        SQLConnection connection = openConnection();

        Map<String, PlayerAccounts> result = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            result.put(player.getUniqueId().toString().toLowerCase(), load(connection, player));
        }

        connection.database.closeConnection();

        return result;
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player) {
        if (player == null) return null;

        SQLConnection connection = openConnection();

        PlayerAccounts result = load(connection, player);

        connection.database.closeConnection();

        return result;
    }

    @Override
    public void saveData(PlayerAccounts data) {
        if (!data.isLoaded()) return;

        SQLConnection connection = openConnection();
        saveSingle(connection, data);
        connection.database.closeConnection();
    }

    @Override
    public void saveAll() {
        SQLConnection               connection = openConnection();
        Map<String, PlayerAccounts> data       = Fabled.getPlayerAccounts();
        ArrayList<String>           keys       = new ArrayList<String>(data.keySet());
        for (String key : keys) {
            saveSingle(connection, data.get(key));
        }
        connection.database.closeConnection();
    }

    private PlayerAccounts load(SQLConnection connection, OfflinePlayer player) {
        try {
            String playerKey = player.getUniqueId().toString().toLowerCase();
            DataSection file =
                    new YAMLParser().parseText(connection.table.createEntry(playerKey).getString(DATA));
            return load(player, file);
        } catch (Exception ex) {
            Logger.bug("Failed to load data from the SQL Database - " + ex.getMessage());
            return null;
        }
    }

    private void saveSingle(SQLConnection connection, PlayerAccounts data) {
        DataSection file = save(data);

        try {
            String playerKey = data.getOfflinePlayer().getUniqueId().toString().toLowerCase();
            connection.table.createEntry(playerKey).set(DATA, file.toString());
        } catch (Exception ex) {
            Logger.bug("Failed to save data for invalid player");
        }
    }

    private class SQLConnection {
        private SQLDatabase database;
        private SQLTable    table;
    }
}
