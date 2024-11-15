package studio.magemonkey.fabled.data.io;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerAccounts;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PlayerLoader {

    private static final Map<UUID, PlayerAccounts> cachedPlayers = new TreeMap<>();

    public static PlayerAccounts getPlayerAccounts(OfflinePlayer player) {
        if (!cachedPlayers.containsKey(player.getUniqueId())) {
            loadPlayer(player);
        }
        return cachedPlayers.get(player.getUniqueId());
    }

    public static void loadPlayer(OfflinePlayer player) {
        PlayerAccounts playerAccounts = Fabled.getIO().loadData(player);
        cachedPlayers.put(player.getUniqueId(), playerAccounts);
    }

    public static void unloadPlayer(OfflinePlayer player) {
        if (cachedPlayers.containsKey(player.getUniqueId())) {
            PlayerAccounts playerAccounts = cachedPlayers.get(player.getUniqueId());
            Fabled.getIO().saveData(playerAccounts);
            cachedPlayers.remove(player.getUniqueId());
        }
    }

    public static boolean hasPlayerAccounts(OfflinePlayer player) {
        return cachedPlayers.containsKey(player.getUniqueId());
    }

    public static void saveAllPlayerAccounts() {
        for (PlayerAccounts playerAccounts : cachedPlayers.values())
            Fabled.getIO().saveData(playerAccounts);
        cachedPlayers.clear();
    }

    public static Map<UUID, PlayerAccounts> getAllPlayerAccounts() {
        return cachedPlayers;
    }

    public static void loadAllPlayerAccounts() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player);
        }
    }
}
