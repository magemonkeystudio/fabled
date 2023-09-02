package com.sucy.skill.testutil;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.DamageLoreRemover;
import lombok.extern.slf4j.Slf4j;
import mc.promcteam.engine.NexEngine;
import mc.promcteam.engine.core.config.CoreLang;
import mc.promcteam.engine.hooks.HookManager;
import mc.promcteam.engine.mccore.commands.CommandManager;
import mc.promcteam.engine.mccore.scoreboard.Board;
import mc.promcteam.engine.nms.NMS;
import mc.promcteam.engine.utils.ItemUT;
import mc.promcteam.engine.utils.Reflex;
import mc.promcteam.engine.utils.actions.ActionsManager;
import mc.promcteam.engine.utils.reflection.ReflectionManager;
import mc.promcteam.engine.utils.reflection.ReflectionUtil;
import mc.promcteam.engine.utils.reflection.Reflection_1_17;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MockedTest {
    protected List<PlayerMock>      players          = new ArrayList<>();
    protected Map<UUID, PlayerData> activePlayerData = new HashMap<>();
    private   Set<String>           classesToLoad    = new HashSet<>();
    private   Set<String>           skillsToLoad     = new HashSet<>();
    protected boolean               loadClasses      = false;
    protected boolean loadSkills = false;

    protected ServerMock server;
    protected WorldMock  world;
    protected NexEngine  engine;
    protected SkillAPI   plugin;

    protected HookManager    hookManager;
    protected NMS            nms;
    protected ActionsManager actionsManager;
    protected CoreLang       coreLang;

    protected ReflectionUtil                  reflectionMock;
    protected Reflection_1_17                 reflection17Mock;
    protected MockedStatic<ReflectionManager> reflectionManager;
    protected MockedStatic<Reflex>            reflex;
    protected MockedStatic<NexEngine>         nexEngine;
    protected MockedStatic<Board>             board;
    protected MockedStatic<DamageLoreRemover> damageLoreRemover;

    public void preInit() {}

    public void loadClasses(String... classes) {
        loadClasses = true;
        Collections.addAll(classesToLoad, classes);
    }

    public void loadSkills(String... skills) {
        loadSkills = true;
        Collections.addAll(skillsToLoad, skills);
    }

    @BeforeAll
    public void setupServer() {
        preInit();

        server = spy(MockBukkit.mock());
        world       = server.addSimpleWorld("test");
        String coreVersion = System.getProperty("PROMCCORE_VERSION");

        try {
            File core = DependencyResolver.resolve("com.promcteam:promccore:" + coreVersion);
            File dest = new File(server.getPluginsFolder().getAbsolutePath(), "ProMCCore-" + coreVersion + ".jar");
            FileUtils.copyFile(core, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        damageLoreRemover = mockStatic(DamageLoreRemover.class);
        damageLoreRemover.when(() -> DamageLoreRemover.removeAttackDmg(any(ItemStack.class)))
                .thenAnswer(a -> a.getArgument(0));
        board = mockStatic(Board.class);
        reflex = mockStatic(Reflex.class);
        reflectionMock = mock(ReflectionUtil.class);
        reflection17Mock = mock(Reflection_1_17.class);
        reflectionManager = mockStatic(ReflectionManager.class);
        reflectionManager.when(() -> ReflectionManager.getReflectionUtil())
                .thenReturn(reflectionMock);

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
        when(nms.fromBase64(any())).thenReturn(new ItemStack(Material.AIR));

        engine = mock(NexEngine.class);
        nexEngine = mockStatic(NexEngine.class);
        nexEngine.when(() -> NexEngine.get()).thenReturn(engine);
        when(engine.getDescription()).thenReturn(new PluginDescriptionFile("ProMCCore",
                coreVersion,
                NexEngine.class.getName()));
        when(engine.getHooksManager()).thenReturn(hookManager);
        when(engine.getActionsManager()).thenReturn(actionsManager);
        when(engine.getNMS()).thenReturn(nms);
        when(engine.lang()).thenReturn(coreLang);
        when(engine.getLogger()).thenReturn(Logger.getLogger("ProMCCore"));
        doReturn(server.getPluginManager())
                .when(engine).getPluginManager();
        ItemUT.setEngine(engine);
//        itemUT.when(() -> ItemUT.getEngine())
//                        .thenReturn(engine);
//        itemUT.when(() -> ItemUT.fromBase64(anyList()))
//                .thenReturn(new ItemStack[0]);
//        itemUT.when(() -> ItemUT.toBase64(any(ItemStack.class)))
//                        .thenReturn(null);
//        itemUT.when(() -> ItemUT.toBase64(anyList()))
//                        .thenReturn(List.of());
//        itemUT.when(() -> ItemUT.toBase64(any(ItemStack[].class)))
//                        .thenReturn(List.of());
//        itemSerializer.when(() -> ItemSerializer.fromBase64(anyString()))
//                .thenReturn(new ItemStack[0]);

        plugin = MockBukkit.load(SkillAPI.class);
        log.info("Plugin loaded");
    }

    @AfterAll
    public void destroy() {
        CommandManager.unregisterAll();

        reflex.close();
        reflectionManager.close();
        nexEngine.close();
        board.close();
        damageLoreRemover.close();
//        itemUT.close();
//        itemSerializer.close();
        MockBukkit.unmock();
    }

    @BeforeEach
    public void initClasses() {
        String sapiVersion = System.getProperty("PROSKILLAPI_VERSION");
        File classDir = new File(
                server.getPluginsFolder().getAbsolutePath() + File.separator + "ProSkillAPI-" + sapiVersion
                        + File.separator + "dynamic" + File.separator + "class");
        if (!classDir.exists()) classDir.mkdirs();
        try {
            FileUtils.cleanDirectory(classDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        classesToLoad.forEach(c -> {
            File classFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "ProSkillAPI-" + sapiVersion
                            + File.separator + "dynamic" + File.separator + "class", c + ".yml");
            try {
                if (!classFile.exists()) {
                    classFile.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("classes" + File.separator + c + ".yml")));
                 FileWriter writer = new FileWriter(classFile);) {
                String str;
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                }
                log.info("Saved class file " + c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        skillsToLoad.forEach(s -> {
            File skillFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "ProSkillAPI-" + sapiVersion
                            + File.separator + "dynamic" + File.separator + "skill", s + ".yml");
            try {
                if (!skillFile.exists()) {
                    skillFile.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("skills" + File.separator + s + ".yml")));
                 FileWriter writer = new FileWriter(skillFile);) {
                String str;
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                }
                log.info("Saved skill file " + s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        plugin.reload();
    }

    @AfterEach
    public void clearData() {
        activePlayerData.clear();
        clearEvents();
        players.clear();
        loadClasses = false;
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
