package studio.magemonkey.fabled.data.sql.tables;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.config.parse.YAMLParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.data.io.IOManager;
import studio.magemonkey.fabled.data.io.PlayerLoader;
import studio.magemonkey.fabled.data.sql.SQLManager;
import studio.magemonkey.fabled.data.sql.SQLUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FabledPlayersSQL extends IOManager {

    private final String table = "Fabled_players";

    public FabledPlayersSQL() {
        super(Fabled.inst());
        try (PreparedStatement create = SQLManager.connection()
                .prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "("
                        + "uuid varchar(36), "
                        + "data MEDIUMTEXT)")) {
            create.execute();
        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("[SQL:FusionPlayersSQL:FusionPlayersSQL] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }
    }

    public PlayerAccounts loadPlayerAccounts(OfflinePlayer player) {
        if (player == null || player.getName() == null) return null;
        try (Connection connection = SQLManager.connection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return SQLUtils.load(player, result.getString("data"));
            }

        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("[SQL:FusionPlayersSQL:loadPlayersAccounts] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }

        PlayerAccounts accounts = new PlayerAccounts(player);
        saveData(accounts);
        return accounts;
    }

    public Map<UUID, PlayerAccounts> loadAllOnlinePlayerAccounts() {
        Map<UUID, PlayerAccounts> result     = new HashMap<>();
        StringBuilder             bulkSelect = new StringBuilder("SELECT * FROM " + table + " WHERE uuid IN (");
        List<Player>              players    = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < players.size(); i++) {
            bulkSelect.append(players.get(i).getUniqueId());
            if (i < Bukkit.getOnlinePlayers().size() - 1) {
                bulkSelect.append(",");
            }
        }
        bulkSelect.append(")");

        try (Connection connection = SQLManager.connection()) {
            PreparedStatement statement = connection.prepareStatement(bulkSelect.toString());
            ResultSet         resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID           uuid     = UUID.fromString(resultSet.getString("uuid"));
                PlayerAccounts accounts = SQLUtils.load(Bukkit.getOfflinePlayer(uuid), resultSet.getString("data"));
                result.put(uuid, accounts);
            }
        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning(
                            "[SQL:FusionPlayersSQL:loadAllOnlinePlayerAccounts] Something went wrong with the sql-connection: "
                                    + e.getMessage());
        }
        return result;
    }

    public boolean accountExists(OfflinePlayer player) {
        try (Connection connection = SQLManager.connection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("[SQL:FusionPlayersSQL:accountExists] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }

        return false;
    }

    public void savePlayerAccounts(OfflinePlayer player, PlayerAccounts accounts) {
        if (player == null || player.getName() == null) return;
        boolean accountExists = accountExists(player);
        String sql = "UPDATE " + table + " SET data = ? WHERE uuid = ?";
        if (!accountExists) {
            sql = "INSERT INTO " + table + " (data, uuid) VALUES (?, ?)";
        }
        try (Connection connection = SQLManager.connection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            DataSection data = IOManager.save(accounts);
            if (data == null) {
                Fabled.inst()
                        .getLogger()
                        .warning("[SQL:FusionPlayersSQL:savePlayersAccounts] data is null for player: "
                                + player.getName());
                return;
            }

            statement.setString(1, data.toString());
            statement.setString(2, player.getUniqueId().toString());
            statement.execute();
        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("[SQL:FusionPlayersSQL:savePlayersAccounts] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }
    }

    public void saveAllPlayerAccounts() {
        Map<UUID, PlayerAccounts> data = PlayerLoader.getAllPlayerAccounts();

        try (Connection connection = SQLManager.connection()) {
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE " + table + " SET data = ? WHERE uuid = ?");
            for (Map.Entry<UUID, PlayerAccounts> entry : data.entrySet()) {
                UUID           uuid     = entry.getKey();
                PlayerAccounts accounts = entry.getValue();

                DataSection accountData = IOManager.save(accounts);
                if (accountData == null) {
                    Fabled.inst()
                            .getLogger()
                            .warning("[SQL:FusionPlayersSQL:saveAllPlayerAccounts] data is null for UUID: " + uuid);
                    continue;
                }
                statement.setString(1, accountData.toString());
                statement.setString(2, uuid.toString());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning(
                            "[SQL:FusionPlayersSQL:saveAllPlayerAccounts] Something went wrong with the sql-connection: "
                                    + e.getMessage());
        }
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
        try (Connection connection = SQLManager.connection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet         result    = statement.executeQuery();
            final File        file      = new File(api.getDataFolder(), "players");
            file.mkdir();
            while (result.next()) {
                OfflinePlayer    player = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("uuid")));
                String           data   = result.getString("data");
                String           yaml   = new YAMLParser().parseText(data).toString();
                FileOutputStream out    = new FileOutputStream(new File(file, player.getUniqueId() + ".yml"));
                BufferedWriter   write  = new BufferedWriter(new OutputStreamWriter(out));
                write.write(yaml);
                write.close();
                count++;
            }
            return count;
        } catch (SQLException | IOException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("&4SQL database backup failed - backed up {amount} entries" + Filter.AMOUNT.setReplacement(
                            count + ""));
            Fabled.inst()
                    .getLogger()
                    .warning("[SQL:FusionPlayersSQL:backUpdata] Something went wrong with the sql-connection: "
                            + e.getMessage());
        }
        return count;
    }

    public void migrateTable() {
        try {
            // Check and delete id column if it exists
            if (columnExists("id")) {
                try (PreparedStatement delete = SQLManager.connection()
                        .prepareStatement("ALTER TABLE " + table + " DROP COLUMN id")) {
                    delete.execute();
                }
            }

            // Check and rename Name to uuid if needed
            if (columnExists("Name")) {
                try (PreparedStatement rename = SQLManager.connection()
                        .prepareStatement("ALTER TABLE " + table + " CHANGE Name uuid varchar(36) PRIMARY KEY")) {
                    rename.execute();
                }
            }
        } catch (SQLException e) {
            Fabled.inst()
                    .getLogger()
                    .warning("[SQL:FusionPlayersSQL:migrateTable] Something went wrong with the SQL connection: "
                            + e.getMessage());
        }
    }

    // Helper method to check if a column exists in the table
    private boolean columnExists(String columnName) throws SQLException {
        try (PreparedStatement stmt = SQLManager.connection()
                .prepareStatement("SHOW COLUMNS FROM " + table + " LIKE ?")) {
            stmt.setString(1, columnName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
