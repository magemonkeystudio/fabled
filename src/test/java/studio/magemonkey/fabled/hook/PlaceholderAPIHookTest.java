// package studio.magemonkey.fabled.hook;

// import me.clip.placeholderapi.PlaceholderAPI;
// import org.bukkit.OfflinePlayer;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockbukkit.mockbukkit.entity.PlayerMock;
// import org.mockito.MockedStatic;
// import studio.magemonkey.fabled.api.player.PlayerData;
// import studio.magemonkey.fabled.testutil.MockedTest;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.mockStatic;

// public class PlaceholderAPIHookTest extends MockedTest {

//     private PlayerMock                   player;
//     private PlaceholderAPIHook           hook;
//     private MockedStatic<PlaceholderAPI> placeholderApiMock;

//     @Override
//     public void preInit() {
//         loadClasses("Honor Guard");
//         placeholderApiMock = mockStatic(PlaceholderAPI.class);
//         placeholderApiMock.when(() -> PlaceholderAPI.setBracketPlaceholders(any(OfflinePlayer.class), anyString()))
//                 .thenAnswer(ans -> {
//                     String identifier = ans.getArgument(1);
//                     return identifier.replaceFirst("fabled_", "");
//                 });
//         hook = new PlaceholderAPIHook();
//     }

//     @BeforeEach
//     public void setup() {
//         player = genPlayer("Travja");
//         PlayerData data = plugin.getData(player);
//         data.profess(plugin.getClass("Honor Guard"));
//     }

//     @AfterEach
//     public void teardown() {
//     }

//     @Test
//     public void account1MainClass() {
//         String placeholder = hook.onRequest(player, "fabled_player_account_1_mainclass");
//         assertEquals("Honor Guard", placeholder);
//     }

//     @Test
//     public void account1Level() {
//         String placeholder = hook.onRequest(player, "fabled_player_account_1_level");
//         assertEquals("1", placeholder);
//     }
// }