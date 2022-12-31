package com.sucy.skill.hook;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.testutil.MockedTest;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PlaceholderAPIHookTest extends MockedTest {

    private PlayerMock                   player;
    private PlaceholderAPIHook           hook;
    private MockedStatic<PlaceholderAPI> placeholderApiMock;

    @Override
    public void preInit() {
        loadClasses("Honor Guard");
        placeholderApiMock = mockStatic(PlaceholderAPI.class);
        placeholderApiMock.when(() -> PlaceholderAPI.setBracketPlaceholders(any(OfflinePlayer.class), anyString()))
                .thenAnswer(ans -> {
                    String identifier = ans.getArgument(1);
                    return identifier.replaceFirst("sapi_", "");
                });
        hook = new PlaceholderAPIHook(plugin);
    }

    @BeforeEach
    public void setup() {
        player = genPlayer("Travja");
        PlayerData data = plugin.getPlayerData(player);
        data.profess(plugin.getClass("Honor Guard"));
    }

    @AfterEach
    public void teardown() {
    }

    @Test
    public void account1MainClass() {
        String placeholder = hook.onRequest(player, "sapi_player_account_1_mainclass");
        assertEquals("Honor Guard", placeholder);
    }

    @Test
    public void account1Level() {
        String placeholder = hook.onRequest(player, "sapi_player_account_1_level");
        assertEquals("1", placeholder);
    }
}