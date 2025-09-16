package studio.magemonkey.fabled.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.config.CustomFilter;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.data.Settings;
import studio.magemonkey.fabled.manager.CmdManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CmdExpTest {
    private ConfigurableCommand classCmd;
    private JavaPlugin          plugin;
    private CmdExp              command;
    private Player              player;
    private Player              targetPlayer;
    private PlayerData          playerData;
    private PlayerClass         playerClass;

    private MockedStatic<Bukkit>         bukkitMock;
    private MockedStatic<Fabled>         fabledMock;
    private MockedStatic<CommandManager> commandManager;
    private MockedStatic<CmdManager>     cmdManager;

    @BeforeEach
    void setUp() {
        classCmd = mock(ConfigurableCommand.class);
        plugin = mock(JavaPlugin.class);
        command = new CmdExp();
        player = mock(Player.class);
        targetPlayer = mock(Player.class);

        when(player.isOnline()).thenReturn(true);
        when(player.getPlayer()).thenReturn(player);
        when(targetPlayer.isOnline()).thenReturn(true);
        when(targetPlayer.getPlayer()).thenReturn(targetPlayer);

        bukkitMock = mockStatic(Bukkit.class);
        when(Bukkit.getOfflinePlayer("not-a-player")).thenReturn(null);
        when(Bukkit.getOfflinePlayer("player")).thenReturn(targetPlayer);
        fabledMock = mockStatic(Fabled.class);

        Fabled fabled = mock(Fabled.class);
        when(Fabled.inst()).thenReturn(fabled);

        commandManager = mockStatic(CommandManager.class);
        cmdManager = mockStatic(CmdManager.class);

        cmdManager.when(() -> CmdManager.join(any(String[].class), anyInt(), anyInt())).thenReturn("player");

        Settings settings = mock(Settings.class);
        when(Fabled.getSettings()).thenReturn(settings);
        when(settings.isWorldEnabled(nullable(World.class))).thenReturn(true);

        playerData = mock(PlayerData.class);
        when(Fabled.getData(any(Player.class))).thenReturn(playerData);

        playerClass = mock(PlayerClass.class);
        when(playerData.getClass(anyString())).thenReturn(playerClass);

        FabledClass fabledClass = mock(FabledClass.class);
        when(playerClass.getData()).thenReturn(fabledClass);
        when(fabledClass.getGroup()).thenReturn("class");
    }

    @AfterEach
    void tearDown() {
        bukkitMock.close();
        fabledMock.close();
        commandManager.close();
        cmdManager.close();
    }

    @Nested
    class CmdLevelExecuteTests {
        @Test
        void execute_noArgs() {
            command.execute(classCmd, plugin, player, new String[0], false);

            commandManager.verify(() -> CommandManager.displayUsage(classCmd, player));
        }

        @Test
        void execute_invalidTarget() {
            command.execute(classCmd, plugin, player, new String[]{"not-a-player", "add", "5"}, false);

            verify(classCmd).sendMessage(eq(player),
                    eq("not-player"),
                    eq(ChatColor.RED + "That is not a valid player name"),
                    anyBoolean());
        }

        @Test
        void execute_plainExp() {
            command.execute(classCmd, plugin, player, new String[]{"add", "5"}, false);

            verify(playerData).giveExp(5, ExpSource.COMMAND, true);
        }

        @Test
        void execute_negativeExp() {
            command.execute(classCmd, plugin, player, new String[]{"add", "-5"}, false);

            verify(playerData).loseExp(5, false, true, true);
        }

        @Test
        void execute_targetPlayer() {
            command.execute(classCmd, plugin, player, new String[]{"player", "add", "5"}, false);

            verify(playerData).giveExp(5, ExpSource.COMMAND, true);
            verify(classCmd).sendMessage(eq(player),
                    eq("gave-exp"),
                    eq(ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {exp}{class} experience"),
                    anyBoolean(),
                    any(CustomFilter.class),
                    any(CustomFilter.class),
                    any(CustomFilter.class));
        }

        @Test
        void execute_targetPlayerNegativeExp() {
            command.execute(classCmd, plugin, player, new String[]{"player", "remove", "5"}, false);

            verify(playerData).loseExp(5, false, true, true);
            verify(classCmd).sendMessage(eq(player),
                    eq("took-exp"),
                    eq(ChatColor.DARK_GREEN + "You have taken " + ChatColor.GOLD + "{exp}{class} experience "
                            + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}"),
                    anyBoolean(),
                    any(CustomFilter.class),
                    any(CustomFilter.class),
                    any(CustomFilter.class));
        }

        // Class-based modifications
        @Test
        void execute_classExp() {
            command.execute(classCmd, plugin, player, new String[]{"add", "5", "class"}, false);

            verify(playerClass).giveExp(5, ExpSource.COMMAND, true);
        }

        @Test
        void execute_classNegativeExp() {
            command.execute(classCmd, plugin, player, new String[]{"remove", "5", "class"}, false);

            verify(playerClass).loseExp(5, false, true, true);
        }

        @Test
        void execute_playerClassExp() {
            command.execute(classCmd, plugin, player, new String[]{"player", "add", "5", "class"}, false);

            verify(playerClass).giveExp(5, ExpSource.COMMAND, true);
            verify(classCmd).sendMessage(eq(player),
                    eq("gave-exp"),
                    eq(ChatColor.DARK_GREEN + "You have given " + ChatColor.GOLD + "{player} {exp}{class} experience"),
                    anyBoolean(),
                    any(CustomFilter.class),
                    any(CustomFilter.class),
                    any(CustomFilter.class));
        }

        @Test
        void execute_playerClassNegativeExp() {
            command.execute(classCmd, plugin, player, new String[]{"player", "remove", "5", "class"}, false);

            verify(playerClass).loseExp(5, false, true, true);
            verify(classCmd).sendMessage(eq(player),
                    eq("took-exp"),
                    eq(ChatColor.DARK_GREEN + "You have taken " + ChatColor.GOLD + "{exp}{class} experience "
                            + ChatColor.DARK_GREEN + "from " + ChatColor.GOLD + "{player}"),
                    anyBoolean(),
                    any(CustomFilter.class),
                    any(CustomFilter.class),
                    any(CustomFilter.class));
        }

        @Test
        void execute_setExp() {
            command.execute(classCmd, plugin, player, new String[]{"set", "5"}, false);

            verify(playerData).setExp(5, ExpSource.COMMAND, true);
        }

        @Test
        void execute_playerClassSet() {
            when(playerClass.getTotalExp()).thenReturn(15.0);
            command.execute(classCmd, plugin, player, new String[]{"player", "set", "5", "class"}, false);

            verify(playerClass).loseExp(10, false, true, true);
        }

        @Test
        void execute_invalidOperation() {
            command.execute(classCmd, plugin, player, new String[]{"invalid", "5"}, false);

            // Display usage
            commandManager.verify(() -> CommandManager.displayUsage(classCmd, player));
        }

        // Silent mode
        @Test
        void execute_silent() {
            command.execute(classCmd, plugin, player, new String[]{"add", "5"}, true);

            verify(playerData).giveExp(5, ExpSource.COMMAND, false);
            verify(classCmd, never()).sendMessage(any(), any(), any(), anyBoolean(), any(), any());
        }

        @Test
        void execute_silentTargetPlayer() {
            command.execute(classCmd, plugin, player, new String[]{"player", "add", "5"}, true);

            verify(playerData).giveExp(5, ExpSource.COMMAND, false);
            verify(classCmd, never()).sendMessage(any(), any(), any(), anyBoolean(), any(), any());
        }

        @Test
        void execute_silentClassExp() {
            command.execute(classCmd, plugin, player, new String[]{"remove", "5", "class"}, true);

            verify(playerClass).loseExp(5, false, true, false);
            verify(classCmd, never()).sendMessage(any(), any(), any(), anyBoolean(), any(), any());
        }

        @Test
        void execute_silentPlayerClassExp() {
            command.execute(classCmd, plugin, player, new String[]{"player", "add", "5", "class"}, true);

            verify(playerClass).giveExp(5, ExpSource.COMMAND, false);
            verify(classCmd, never()).sendMessage(any(), any(), any(), anyBoolean(), any(), any());
        }
    }

    @Nested
    class CmdLevelTabCompleteTests {
        private MockedStatic<ConfigurableCommand> configurableCommand;

        @BeforeEach
        void setUp() {
            configurableCommand = mockStatic(ConfigurableCommand.class);
            configurableCommand.when(() -> ConfigurableCommand.getPlayerTabCompletions(any(), anyString()))
                    .thenReturn(List.of("player", "player2"));
            configurableCommand.when(() -> ConfigurableCommand.getTabCompletions(anyList(), any(String[].class)))
                    .thenCallRealMethod();

            fabledMock.when(Fabled::getGroups).thenReturn(List.of("class"));
        }

        @AfterEach
        void tearDown() {
            configurableCommand.close();
        }

        @Test
        void tabComplete_noArgs() {
            List<String> completions = command.onTabComplete(player, classCmd, "level", new String[]{""});

            assertEquals(List.of("player", "player2", "add", "remove", "set"), completions);
        }

        @Test
        void tabComplete_playerInFirst() {
            List<String> completions = command.onTabComplete(player, classCmd, "level", new String[]{"player", ""});

            assertEquals(List.of("add", "remove", "set"), completions);
        }

        @Test
        void tabComplete_addFirst() {
            List<String> completions = command.onTabComplete(player, classCmd, "level", new String[]{"add", ""});

            assertEquals(List.of("<exp>"), completions);
        }

        @Test
        void tabComplete_playerAdd() {
            List<String> completions =
                    command.onTabComplete(player, classCmd, "level", new String[]{"player", "add", ""});

            assertEquals(List.of("<exp>"), completions);
        }

        @Test
        void tabComplete_numberInSecond() {
            List<String> completions = command.onTabComplete(player, classCmd, "level", new String[]{"add", "5", ""});

            assertEquals(List.of("class"), completions);
        }

        @Test
        void tabComplete_playerWithNumber() {
            List<String> completions =
                    command.onTabComplete(player, classCmd, "level", new String[]{"player", "add", "5", ""});

            assertEquals(List.of("class"), completions);
        }
    }
}