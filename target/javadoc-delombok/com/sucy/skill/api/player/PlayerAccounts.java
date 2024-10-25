package com.sucy.skill.api.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerAccounts {
    private final studio.magemonkey.fabled.api.player.PlayerAccounts _accounts;

    public int getActiveId() {
        return _accounts.getActiveId();
    }

    public PlayerData getActiveData() {
        return new PlayerData(_accounts.getActiveData());
    }

    public Player getPlayer() {
        return _accounts.getPlayer();
    }

    public OfflinePlayer getOfflinePlayer() {
        return _accounts.getOfflinePlayer();
    }

    public String getPlayerName() {
        return _accounts.getPlayerName();
    }

    public int getAccountLimit() {
        return _accounts.getAccountLimit();
    }

    public boolean hasData(int id) {
        return _accounts.hasData(id);
    }

    public PlayerData getData(int id) {
        return new PlayerData(_accounts.getData(id));
    }

    public PlayerData getData(int id, OfflinePlayer player, boolean initialize) {
        return new PlayerData(_accounts.getData(id, player, initialize));
    }

    public HashMap<Integer, PlayerData> getAllData() {
        return _accounts.getAllData().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> new PlayerData(e.getValue()),
                        (a, b) -> a,
                        HashMap::new));
    }

    public void setAccount(int id) {
        _accounts.setAccount(id);
    }

    public void setAccount(int id, boolean apply) {
        _accounts.setAccount(id, apply);
    }

    public boolean isLoaded() {
        return _accounts.isLoaded();
    }

    public void isLoaded(boolean loaded) {
        _accounts.isLoaded(loaded);
    }
}
