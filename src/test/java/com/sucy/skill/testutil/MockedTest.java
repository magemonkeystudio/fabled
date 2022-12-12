package com.sucy.skill.testutil;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.DamageLoreRemover;
import mc.promcteam.engine.NexEngine;
import mc.promcteam.engine.core.config.CoreLang;
import mc.promcteam.engine.hooks.HookManager;
import mc.promcteam.engine.mccore.commands.CommandManager;
import mc.promcteam.engine.mccore.scoreboard.Board;
import mc.promcteam.engine.nms.NMS;
import mc.promcteam.engine.utils.ItemUT;
import mc.promcteam.engine.utils.Reflex;
import mc.promcteam.engine.utils.actions.ActionsManager;
import mc.promcteam.engine.utils.reflection.ReflectionUtil;
import mc.promcteam.engine.utils.reflection.Reflection_1_17;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MockedTest {
    protected ServerMock                      server;
    protected NexEngine                       engine;
    protected SkillAPI                        plugin;
    protected List<PlayerMock>                players          = new ArrayList<>();
    protected Map<UUID, PlayerData>           activePlayerData = new HashMap<>();
    protected MockedStatic<Reflex>            reflex;
    protected MockedStatic<ReflectionUtil>    mockedReflection;
    protected MockedStatic<Reflection_1_17>   mockedReflection17;
    protected MockedStatic<NexEngine>         nexEngine;
    protected MockedStatic<Board>             board;
    protected MockedStatic<DamageLoreRemover> damageLoreRemover;
    protected HookManager                     hookManager;
    protected NMS                             nms;
    protected ActionsManager                  actionsManager;
    protected CoreLang                        coreLang;

    @BeforeAll
    public void setupServer() {
        server = spy(MockBukkit.mock());
        String coreVersion  = System.getProperty("PROMCCORE_VERSION");

        try {
            File core = DependencyResolver.resolve("com.promcteam:promccore:" + coreVersion);
            File dest = new File(server.getPluginsFolder().getAbsolutePath(), "ProMCCore-" + coreVersion + ".jar");
            FileUtils.copyFile(core, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        damageLoreRemover = mockStatic(DamageLoreRemover.class);
        board = mockStatic(Board.class);
        reflex = mockStatic(Reflex.class);
        mockedReflection = mockStatic(ReflectionUtil.class);
        mockedReflection17 = mockStatic(Reflection_1_17.class);

        coreLang = mock(CoreLang.class);
        when(coreLang.getEnum(any()))
                .thenAnswer(args -> {
                    Enum<?> e    = args.getArgument(0);
                    String  path = e.getClass().getSimpleName() + "." + e.name();
                    return path;
                });

        hookManager = mock(HookManager.class);
        actionsManager = mock(ActionsManager.class);
        nms = mock(NMS.class);
        when(nms.fixColors(anyString()))
                .thenAnswer(args -> args.getArgument(0));
        when(nms.toBase64(any())).thenReturn("");

        engine = mock(NexEngine.class);
        nexEngine = mockStatic(NexEngine.class);
        nexEngine.when(() -> NexEngine.get()).thenReturn(engine);
        when(engine.getHooksManager()).thenReturn(hookManager);
        when(engine.getActionsManager()).thenReturn(actionsManager);
        when(engine.getNMS()).thenReturn(nms);
        when(engine.lang()).thenReturn(coreLang);
        when(engine.getLogger()).thenReturn(Logger.getLogger("ProMCCore"));
        doReturn(server.getPluginManager())
                .when(engine).getPluginManager();

        ItemUT.setEngine(engine);

        plugin = MockBukkit.load(SkillAPI.class);
    }

    @AfterAll
    public void destroy() {
        CommandManager.unregisterAll();
        MockBukkit.unmock();
    }

    @AfterEach
    public void clearData() {
        activePlayerData.clear();
        clearEvents();
        players.clear();
    }

    public PlayerData generatePlayerData(Player player) {
        PlayerData pd = mock(PlayerData.class);
        activePlayerData.put(player.getUniqueId(), pd);

        when(pd.getPlayer()).thenReturn(player);
        return pd;
    }

    public PlayerMock genPlayer(String name) {
        return genPlayer(name, true);
    }

    public PlayerMock genPlayer(String name, boolean op) {
//        PlayerMock pm = server.addPlayer(name);
        PlayerMock pm = new PlayerMock(server, name, UUID.randomUUID());
        server.addPlayer(pm);
        players.add(pm);
        pm.setOp(op);

        return pm;
    }

    public <T extends Event> void assertEventFired(Class<T> clazz) {
        server.getPluginManager().assertEventFired(clazz);
    }

    public <T extends Event> void assertEventFired(Class<T> clazz, Predicate<T> predicate) {
        server.getPluginManager().assertEventFired(clazz, predicate);
    }

    public void clearEvents() {
        server.getPluginManager().clearEvents();
    }

    private final static int BUFFER = 2048;

    public boolean createZipArchive(File destFile, String srcFolder) {
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)))) {
            addFolder(srcFolder, "", out);
        } catch (Exception e) {
            System.out.println("createZipArchive threw exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void addFolder(String srcFolder, String baseFolder, ZipOutputStream out) throws IOException {
        File   subDir       = new File(srcFolder);
        String subdirList[] = subDir.list();
        for (String sd : subdirList) {
            // get a list of files from current directory
            File f = new File(srcFolder + "/" + sd);
            if (f.isDirectory())
                addFolder(f.getAbsolutePath(), baseFolder + "/" + sd, out);
            else {//it is just a file
                addFile(new FileInputStream(f), baseFolder + "/" + sd, out);
            }
        }
    }

    @NotNull
    private void addFile(FileInputStream f, String sd, ZipOutputStream out) throws IOException {
        byte            data[] = new byte[BUFFER];
        FileInputStream fi     = f;
        try (BufferedInputStream origin = new BufferedInputStream(fi, BUFFER)) {
            ZipEntry entry = new ZipEntry(sd);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
                out.flush();
            }
        }
    }
}
