package studio.magemonkey.fabled.testutil;

import org.apache.commons.io.FileUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.compat.NMS;
import studio.magemonkey.codex.compat.VersionManager;
import studio.magemonkey.codex.hooks.HookManager;
import studio.magemonkey.codex.mccore.scoreboard.Board;
import studio.magemonkey.codex.util.InventoryUtil;
import studio.magemonkey.codex.util.ItemUT;
import studio.magemonkey.codex.util.Reflex;
import studio.magemonkey.codex.util.actions.ActionsManager;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.util.DamageLoreRemover;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockbukkit.mockbukkit.matcher.plugin.PluginManagerFiredEventClassMatcher.hasFiredEventInstance;
import static org.mockbukkit.mockbukkit.matcher.plugin.PluginManagerFiredEventFilterMatcher.hasFiredFilteredEvent;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MockedTest {
    private final Logger                log              = LoggerFactory.getLogger(MockedTest.class);
    protected     List<PlayerMock>      players          = new ArrayList<>();
    protected     Map<UUID, PlayerData> activePlayerData = new HashMap<>();
    private final Set<String>           classesToLoad    = new HashSet<>();
    private final Set<String>           skillsToLoad     = new HashSet<>();
    protected     boolean               loadClasses      = false;
    protected     boolean               loadSkills       = false;

    protected ServerMock  server;
    protected WorldMock   world;
    protected CodexEngine engine;
    protected Fabled      plugin;

    protected HookManager    hookManager;
    protected ActionsManager actionsManager;

    protected MockedStatic<Reflex>            reflex;
    protected MockedStatic<Board>             board;
    protected MockedStatic<DamageLoreRemover> damageLoreRemover;
    protected MockedStatic<InventoryUtil>     inventoryUtil;

    public void preInit() {}

    public void useClasses(String masterFileLoc) {
        loadClasses = true;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass()
                .getClassLoader()
                .getResourceAsStream(masterFileLoc))))) {
            // Copy to Fabled/dynamic/classes.yml
            File classDir = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + System
                            .getProperty("FABLED_VERSION")
                            + File.separator + "dynamic" + File.separator + "class");
            if (!classDir.exists()) classDir.mkdirs();

            File classFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + System
                            .getProperty("FABLED_VERSION")
                            + File.separator + "dynamic", "classes.yml");
            if (!classFile.exists()) {
                classFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(classFile);) {
                String str;
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadClasses(String... classes) {
        loadClasses = true;
        Collections.addAll(classesToLoad, classes);
    }

    public void unloadClasses(String... classes) {
        loadClasses = false;
        Arrays.asList(classes).forEach(classesToLoad::remove);
        for (String clazz : classes) {
            // Remove from class folder
            File classFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + System
                            .getProperty("FABLED_VERSION")
                            + File.separator + "dynamic" + File.separator + "class", clazz + ".yml");
            if (classFile.exists()) {
                classFile.delete();
            }
        }
    }

    public void useSkills(String masterFileLoc) {
        loadSkills = true;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass()
                .getClassLoader()
                .getResourceAsStream(masterFileLoc))))) {
            // Copy to Fabled/dynamic/skills.yml
            File skillDir = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + System
                            .getProperty("FABLED_VERSION")
                            + File.separator + "dynamic" + File.separator + "skill");
            if (!skillDir.exists()) skillDir.mkdirs();

            File skillFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + System
                            .getProperty("FABLED_VERSION")
                            + File.separator + "dynamic", "skills.yml");
            if (!skillFile.exists()) {
                skillFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(skillFile)) {
                String str;
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSkills(String... skills) {
        loadSkills = true;
        Collections.addAll(skillsToLoad, skills);
    }

    public void unloadSkills(String... skills) {
        for (String skill : skills) {
            // Remove from skill folder
            File skillFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + System
                            .getProperty("FABLED_VERSION")
                            + File.separator + "dynamic" + File.separator + "skill", skill + ".yml");
            if (skillFile.exists()) {
                skillFile.delete();
            }
        }
        Arrays.asList(skills).forEach(skillsToLoad::remove);
    }

    @BeforeAll
    public void setupServer() {
        preInit();

        server = spy(MockBukkit.mock());
        world = server.addSimpleWorld("test");
        String coreVersion = System.getProperty("CODEX_VERSION");

        try {
            File core = DependencyResolver.resolve("studio.magemonkey:codex:" + coreVersion);
            File dest = new File(server.getPluginsFolder().getAbsolutePath(), "CodexCore-" + coreVersion + ".jar");
            FileUtils.copyFile(core, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        inventoryUtil = mockStatic(InventoryUtil.class);
        inventoryUtil.when(() -> InventoryUtil.getTopInventory(any(Player.class)))
                .thenAnswer(ans -> {
                    Player    player = ((Player) ans.getArgument(0));
                    Inventory inv    = player.getOpenInventory().getTopInventory();
                    //noinspection ConstantValue
                    if (inv != null) return inv;

                    // It shouldn't be possible to have a null topInventory, but is for MockBukkit
                    return player.getInventory();
                });

        NMS nms = mock(NMS.class);
        when(nms.getVersion()).thenReturn("test");
        when(nms.getAttribute(anyString())).thenAnswer(ans -> {
            try {
                return Attribute.valueOf(ans.getArgument(0).toString());
            } catch (IllegalArgumentException e) {
                return Attribute.valueOf("GENERIC_" + ans.getArgument(0).toString());
            }
        });
        when(nms.fixColors(anyString())).thenAnswer(ans -> ans.getArgument(0));
        when(nms.createEntityDamageEvent(any(Entity.class), any(Entity.class), any(EntityDamageEvent.DamageCause.class), anyDouble()))
                .thenAnswer(ans -> new EntityDamageByEntityEvent(ans.getArgument(0), ans.getArgument(1),
                        ans.getArgument(2), ans.getArgument(3)));

        VersionManager.setNms(nms);

        damageLoreRemover = mockStatic(DamageLoreRemover.class);
        damageLoreRemover.when(() -> DamageLoreRemover.removeAttackDmg(any(ItemStack.class)))
                .thenAnswer(a -> a.getArgument(0));
        board = mockStatic(Board.class);
        reflex = mockStatic(Reflex.class);

        hookManager = mock(HookManager.class);
        actionsManager = mock(ActionsManager.class);

        engine = spy(MockBukkit.load(CodexEngine.class));
        doReturn(hookManager).when(engine).getHooksManager();
        doReturn(actionsManager).when(engine).getActionsManager();
        ItemUT.setEngine(engine);

        plugin = MockBukkit.load(Fabled.class);
        log.info("Plugin loaded");
    }

    @AfterAll
    public void destroy() {
        if (reflex != null) reflex.close();
        if (board != null) board.close();
        if (damageLoreRemover != null) damageLoreRemover.close();
//        itemUT.close();
//        itemSerializer.close();
        MockBukkit.unmock();
        if (inventoryUtil != null) inventoryUtil.close();
    }

    @BeforeEach
    public void initClasses() {
        String fabledVersion = System.getProperty("FABLED_VERSION");
        File classDir = new File(
                server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + fabledVersion
                        + File.separator + "dynamic" + File.separator + "class");
        if (!classDir.exists()) classDir.mkdirs();
        try {
            FileUtils.cleanDirectory(classDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        classesToLoad.forEach(c -> {
            File classFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + fabledVersion
                            + File.separator + "dynamic" + File.separator + "class", c + ".yml");
            try {
                if (!classFile.exists()) {
                    classFile.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("classes" + File.separator + c + ".yml"))));
                 FileWriter writer = new FileWriter(classFile)) {
                String str;
                while ((str = in.readLine()) != null) {
                    writer.write(str + "\n");
                }
                log.info("Saved class file {}", c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        skillsToLoad.forEach(s -> {
            File skillFile = new File(
                    server.getPluginsFolder().getAbsolutePath() + File.separator + "Fabled-" + fabledVersion
                            + File.separator + "dynamic" + File.separator + "skill", s + ".yml");
            try {
                if (!skillFile.exists()) {
                    skillFile.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("skills" + File.separator + s + ".yml"))));
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

        reload();
    }

    public void reload() {
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
        if (!hasFiredEventInstance(clazz).matches(server.getPluginManager())) {
            Assertions.fail("Event " + clazz.getSimpleName() + " was not fired");
        }
    }

    public <T extends Event> void assertEventFired(Class<T> clazz, Predicate<T> predicate) {
        if (!hasFiredFilteredEvent(clazz, predicate).matches(server.getPluginManager())) {
            Assertions.fail("Event " + clazz.getSimpleName() + " was not fired");
        }
    }

    public <T extends Event> void assertEventNotFired(Class<T> clazz) {
        if (hasFiredEventInstance(clazz).matches(server.getPluginManager())) {
            Assertions.fail("Event " + clazz.getSimpleName() + " was fired");
        }
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
