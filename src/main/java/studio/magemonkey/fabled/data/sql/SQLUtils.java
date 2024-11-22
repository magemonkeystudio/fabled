package studio.magemonkey.fabled.data.sql;

import org.bukkit.OfflinePlayer;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.config.parse.YAMLParser;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.data.io.IOManager;
import studio.magemonkey.fabled.log.Logger;

public class SQLUtils {

    public static PlayerAccounts load(OfflinePlayer player, String data) {
        try {
            DataSection file = new YAMLParser().parseText(data);
            return IOManager.load(player, file);
        } catch (Exception ex) {
            Logger.bug("Failed to load data from the SQL Database - " + ex.getMessage());
            return null;
        }
    }
}
