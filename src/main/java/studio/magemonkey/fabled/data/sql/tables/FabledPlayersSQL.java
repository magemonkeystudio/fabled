package studio.magemonkey.fabled.data.sql.tables;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.config.parse.YAMLParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.PlayerLoader;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.data.io.IOManager;
import studio.magemonkey.fabled.data.sql.SQLManager;
import studio.magemonkey.fabled.data.sql.SQLUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FabledPlayersSQL extends IOManager {

    private final String table = "fabled_players";

    public FabledPlayersSQL() {
        super(Fabled.inst());
        try (PreparedStatement create = SQLManager.connection()
                .prepareStatement("CREATE TABLE IF NOT EXISTS " + Table + "("
                        + "UUID varchar(36), "
                        + "Data MEDIUMTEXT)")) {
            create.execute();
        } catch (SQLException e) {
            Fabled.inst().getLogger().warning("[SQL:FusionPlayersSQL:FusionPlayersSQL] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }
    }

    public PlayerAccounts loadPlayerAccounts(OfflinePlayer player) {
        if (player == null || player.getName() == null) return null;
        try(Connection connection = SQLManager.connection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Table + " WHERE UUID = ?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return SQLUtils.load(player, result.getString("Data"));
            }

        } catch (SQLException e) {
            Fabled.inst().getLogger().warning("[SQL:FusionPlayersSQL:loadPlayersAccounts] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }

        return null;
    }

    public Map<UUID, PlayerAccounts> loadAllOnlinePlayerAccounts() {
        Map<UUID, PlayerAccounts> result = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            result.put(player.getUniqueId(), loadPlayerAccounts(player));
        }
        return result;
    }

    public void savePlayerAccounts(OfflinePlayer player, PlayerAccounts accounts) {
        if (player == null || player.getName() == null) return;
        try(Connection connection = SQLManager.connection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + Table + " SET Data = ? WHERE UUID = ?");
            DataSection data = IOManager.save(accounts);
            if(data == null) {
                Fabled.inst().getLogger().warning("[SQL:FusionPlayersSQL:savePlayersAccounts] Data is null for player: " + player.getName());
                return;
            }
            statement.setString(1, data.toString());
            statement.setString(2, player.getUniqueId().toString());
            statement.execute();
        } catch (SQLException e) {
            Fabled.inst().getLogger().warning("[SQL:FusionPlayersSQL:savePlayersAccounts] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }
    }

    public void saveAllPlayerAccounts() {
        Map<UUID, PlayerAccounts> data = PlayerLoader.getAllPlayerAccounts();
        for(Map.Entry<UUID, PlayerAccounts> entry : data.entrySet())
            savePlayerAccounts(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue());
    }


    @Override
    public Map<UUID, PlayerAccounts> loadAll() {
        return loadAllOnlinePlayerAccounts();
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player) {
        return loadPlayerAccounts(player);
    }

    @Override
    public void saveData(PlayerAccounts data) {
        savePlayerAccounts(data.getOfflinePlayer(), data);
    }

    @Override
    public void saveAll() {
        saveAllPlayerAccounts();
    }

    public int backUpData() {
        int count = 0;
        try(Connection connection = SQLManager.connection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Table);
            ResultSet result = statement.executeQuery();
            final File file = new File(api.getDataFolder(), "players");
            file.mkdir();
            while(result.next()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("UUID")));
                String data = result.getString("Data");
                String yaml = new YAMLParser().parseText(data).toString();
                FileOutputStream out = new FileOutputStream(new File(file, player.getUniqueId() + ".yml"));
                BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out));
                write.write(yaml);
                write.close();
                count++;
            }
            return count;
        } catch (SQLException | IOException e) {
            Fabled.inst().getLogger().warning("&4SQL database backup failed - backed up {amount} entries" + Filter.AMOUNT.setReplacement(count + ""));
            Fabled.inst().getLogger().warning("[SQL:FusionPlayersSQL:backUpData] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }
        return count;
    }

    public void migrateTable() {
        try {
            // Check and delete Id column if it exists
            if (columnExists("Id")) {
                try (PreparedStatement delete = SQLManager.connection()
                        .prepareStatement("ALTER TABLE " + Table + " DROP COLUMN Id")) {
                    delete.execute();
                }
            }

            // Check and rename Name to UUID if needed
            if (columnExists("Name")) {
                try (PreparedStatement rename = SQLManager.connection()
                        .prepareStatement("ALTER TABLE " + Table + " CHANGE Name UUID varchar(36)")) {
                    rename.execute();
                }
            }

            // Check and rename data to Data if needed
            if (columnExists("data")) {
                try (PreparedStatement rename = SQLManager.connection()
                        .prepareStatement("ALTER TABLE " + Table + " CHANGE data Data MEDIUMTEXT")) {
                    rename.execute();
                }
            }
        } catch (SQLException e) {
            Fabled.inst().getLogger().warning("[SQL:FusionPlayersSQL:migrateTable] Something went wrong with the SQL connection: "
                    + e.getMessage());
        }
    }

    // Helper method to check if a column exists in the table
    private boolean columnExists(String columnName) throws SQLException {
        try (PreparedStatement stmt = SQLManager.connection()
                .prepareStatement("SHOW COLUMNS FROM " + Table + " LIKE ?")) {
            stmt.setString(1, columnName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
