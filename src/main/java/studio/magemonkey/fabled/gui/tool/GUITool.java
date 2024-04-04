/**
 * Fabled
 * studio.magemonkey.fabled.gui.tool.GUITool
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 Mage Monkey Studios
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.gui.tool;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.DamageLoreRemover;
import studio.magemonkey.fabled.log.Logger;
import studio.magemonkey.fabled.manager.ProAttribute;
import studio.magemonkey.fabled.tree.basic.CustomTree;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.util.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GUITool implements ToolMenu {
    // Page buttons
    private static final String                     NEXT_PAGE     = "NEXT_PAGE";
    private static final String                     PREV_PAGE     = "PREV_PAGE";
    private static final HashMap<String, GUIData>   setups        = new HashMap<>();
    private static final HashMap<String, ItemStack> items         = new HashMap<>();
    private static       boolean                    inUse         = false;
    private static       CommentedConfig            config;
    private static final NamespacedKey              CAST_ITEM_KEY = new NamespacedKey(Fabled.inst(), "castItem");

    private static ItemStack
            NEXT,
            PREV,
            SHRINK,
            GROW,
            ADD_PAGE,
            DEL_PAGE,
            NEXT_CLASS,
            PREV_CLASS,
            NEXT_PROFESSION,
            PREV_PROFESSION;

    private static FabledClass[] availableClasses;
    private static FabledClass[] availableProfesses;
    private static String[]      availableGroups;
    private static GUIType       type;
    private static int           classId;
    private static int           professId;
    private final  Player        player;
    private        InventoryData data;
    private        Inventory     inventory;
    private        FabledClass   fabledClass;
    private        Skill         skill;
    private        GUIData       guiData;
    private        ItemStack[]   playerContents;
    private        ItemStack[]   inventoryContents;
    private        int           i;
    private        boolean       switching = false;

    public GUITool(Player player) {
        this.player = player;
    }

    public static boolean isInUse() {
        return inUse;
    }

    public static void init() {
        if (NEXT != null)
            return;

        NEXT = make(Material.BOOK, ChatColor.GOLD + "Next Menu");
        PREV = make(Material.BOOK, ChatColor.GOLD + "Previous Menu");
        SHRINK = make(Material.MELON_SEEDS, ChatColor.GOLD + "Shrink", "", "Removes a row from the GUI");
        GROW = make(Material.MELON, ChatColor.GOLD + "Grow", "", "Adds a row to the GUI");
        ADD_PAGE = make(Material.PAPER,
                ChatColor.GOLD + "Add Page",
                "",
                "Adds another page to the GUI",
                "right after the current one");
        DEL_PAGE = make(Material.PAPER, ChatColor.GOLD + "Delete Page", "", "Deletes the currently", "viewed page");
        NEXT_CLASS = make(Material.DIAMOND_SWORD, ChatColor.GOLD + "Next Class");
        PREV_CLASS = make(Material.IRON_SWORD, ChatColor.GOLD + "Previous Class");
        NEXT_PROFESSION = make(Material.DIAMOND_HOE, ChatColor.GOLD + "Next Profession");
        PREV_PROFESSION = make(Material.IRON_HOE, ChatColor.GOLD + "Previous Profession");

        availableClasses = Fabled.getClasses().values().toArray(new FabledClass[Fabled.getClasses().size()]);
        Set<FabledClass> professes = new HashSet<>();
        Set<String>      groups    = new HashSet<>();
        professes.add(null);

        config = Fabled.getConfig("gui");
        DataSection data = config.getConfig();
        for (String key : data.keys()) {
            try {
                GUIData loaded = new GUIData(data.getSection(key));
                if (loaded.isValid())
                    setups.put(key, loaded);
            } catch (IllegalArgumentException e) {
                Fabled.inst().getLogger().warning("Failed to load GUI data for " + key);
                e.printStackTrace();
            }
        }

        for (FabledClass c : availableClasses) {
            if (!(c.getSkillTree() instanceof CustomTree)) {
                try {
                    setups.put(GUIType.SKILL_TREE.getPrefix() + c.getName(), new GUIData(c.getSkillTree()));
                } catch (IllegalArgumentException e) {
                    Fabled.inst().getLogger().warning("Failed to load skill tree for " + c.getName());
                    e.printStackTrace();
                }
            }
            if (c.hasParent())
                professes.add(c.getParent());
            groups.add(c.getGroup());
        }
        availableGroups = groups.toArray(new String[groups.size()]);
        availableProfesses = professes.toArray(new FabledClass[professes.size()]);

        CommentedConfig itemFile = Fabled.getConfig("tool");
        itemFile.checkDefaults();
        itemFile.save();
        DataSection custom = itemFile.getConfig();
        for (String key : custom.keys()) {
            try {
                ItemStack item = parseItem(custom.getSection(key));
                items.put(key.toUpperCase(Locale.US), item);
            } catch (Exception ex) {
                Logger.invalid("Bad custom tool item: " + key);
            }
        }
    }

    public static ItemStack parseItem(DataSection data) {
        Material  material = Material.valueOf(data.getString("type").toUpperCase(Locale.US).replace(" ", "_"));
        ItemStack item     = new ItemStack(material);
        ItemMeta  meta     = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(data.getInt("data"));
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(data.getInt("durability"));
            }

            meta.setDisplayName(TextFormatter.colorString(data.getString("name")));
            meta.setLore(TextFormatter.colorStringList(data.getList("lore")));

            item.setItemMeta(meta);
        }

        return DamageLoreRemover.removeAttackDmg(item);
    }

    public static ItemStack markCastItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(CAST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public static boolean isCastItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            Byte data = meta.getPersistentDataContainer().get(CAST_ITEM_KEY, PersistentDataType.BYTE);
            return data != null && data >= 1;
        }
        return false;
    }

    public static void removeCastItems(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (GUITool.isCastItem(contents[i])) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    public static void cleanUp() {
        if (config != null) {
            config.clear();
            DataSection data = config.getConfig();
            for (Map.Entry<String, GUIData> entry : setups.entrySet())
                if (entry.getValue().isValid())
                    entry.getValue().save(data.createSection(entry.getKey()));
            config.save();
        }

        setups.clear();
        items.clear();
        config = null;
        NEXT = null;
        PREV = null;
        SHRINK = null;
        GROW = null;
        ADD_PAGE = null;
        DEL_PAGE = null;
        NEXT_CLASS = null;
        PREV_CLASS = null;
        NEXT_PROFESSION = null;
        PREV_PROFESSION = null;

        availableClasses = null;
        availableProfesses = null;
        availableGroups = null;
    }

    public static ItemStack getIcon(final String key) {
        return items.get(key.toUpperCase(Locale.US));
    }

    public static boolean hasData(String key) {
        return setups.containsKey(key) && setups.get(key).isValid();
    }

    public static GUIData getSkillTree(FabledClass fabledClass) {
        return get(GUIType.SKILL_TREE.getPrefix() + fabledClass.getName());
    }

    public static GUIData getProfessMenu(FabledClass current) {
        return get(current == null
                ? GUIType.CLASS_SELECTION.name()
                : GUIType.CLASS_SELECTION.getPrefix() + current.getName());
    }

    public static GUIData getDetailsMenu() {
        return get(GUIType.CLASS_DETAILS.name());
    }

    public static GUIData getAttributesMenu() {
        return get(GUIType.ATTRIBUTES.name());
    }

    private static GUIData get(String key) {
        if (!setups.containsKey(key)) {
            setups.put(key, new GUIData());
        }
        return setups.get(key);
    }

    public static GUIData getActiveData() {
        String key = type.getPrefix();
        switch (type) {
            case CLASS_SELECTION:
                if (professId == 0)
                    key = type.name();
                else if (availableProfesses.length > professId)
                    key += availableProfesses[professId].getName();
                break;
            case CLASS_DETAILS:
                key = type.name();
                break;
            case SKILL_TREE:
                if (availableClasses.length > classId)
                    key += availableClasses[classId].getName();
                break;
            case ATTRIBUTES:
                key = type.name();
        }

        return get(key);
    }

    private static ItemStack make(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void addPageButtons(ItemStack[] contents) {
        if (contents.length > 9) {
            contents[8] = items.get(PREV_PAGE);
            contents[17] = items.get(NEXT_PAGE);
        } else {
            contents[7] = items.get(PREV_PAGE);
            contents[8] = items.get(NEXT_PAGE);
        }
    }

    public void open() {
        if (!inUse) {
            PlayerData data = Fabled.getPlayerData(player);
            if (data.hasClass() && Fabled.getSettings().isSkillBarEnabled())
                data.getSkillBar().clear(player);

            this.data = new InventoryData(player);
            setType(GUIType.CLASS_SELECTION);
            inUse = true;
        }
    }

    public void setType(GUIType type) {
        GUITool.type = type;
        guiData = getActiveData();
        inventoryContents = new ItemStack[guiData.getSize()];
        String title = populate();
        inventory = player.getServer().createInventory(this, guiData.getSize(), title);
        inventory.setContents(inventoryContents);
        player.getInventory().setContents(playerContents);

        switching = true;
        player.openInventory(inventory);
        switching = false;
    }

    private void update() {
        inventoryContents = inventory.getContents();
        guiData.load(inventoryContents);
        if (type == GUIType.SKILL_TREE) {
            FabledClass fabledClass = availableClasses[classId];
            String      name        = GUIType.SKILL_TREE.getPrefix() + fabledClass.getName();
            guiData.save(config.getConfig().createSection(name));
            config.save();
            if (!(fabledClass.getSkillTree() instanceof CustomTree)) {
                fabledClass.reloadSkillTree();
                guiData = new GUIData(fabledClass.getSkillTree());
            }
            setups.put(name, guiData);
        }
    }

    private String populate() {
        playerContents = new ItemStack[36];

        playerContents[0] = PREV;
        playerContents[1] = NEXT;
        playerContents[2] = SHRINK;
        playerContents[3] = GROW;
        playerContents[4] = ADD_PAGE;
        playerContents[5] = DEL_PAGE;

        String name = null;

        switch (type) {
            case CLASS_DETAILS:
                name = populateClassDetails();
                break;
            case SKILL_TREE:
                name = populateSkillTree();
                break;
            case CLASS_SELECTION:
                name = populateClassSelection();
                break;
            case ATTRIBUTES:
                name = populateAttributes();
                break;
        }

        GUIPage page = guiData.getPage();
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            if (entry.getKey().equals(NEXT_PAGE) || entry.getKey().equals(PREV_PAGE))
                continue;

            if (i < playerContents.length)
                playerContents[i++] = toPlaceholder(entry.getKey(), entry.getValue());
            int index = page.getIndex(entry.getKey());
            if (index >= 0)
                inventoryContents[index] = toPlaceholder(entry.getKey(), entry.getValue());
        }

        // Page buttons
        if (guiData.getPages() > 1)
            addPageButtons(inventoryContents);

        return name;
    }

    private ItemStack toPlaceholder(String key, ItemStack custom) {
        ItemStack copy = custom.clone();
        ItemMeta  meta = copy.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(key);
            meta.setLore(new ArrayList<>());
            copy.setItemMeta(meta);
        }
        return copy;
    }

    private String populateClassSelection() {
        playerContents[7] = PREV_PROFESSION;
        playerContents[8] = NEXT_PROFESSION;

        GUIPage     page       = guiData.getPage();
        FabledClass profession = availableProfesses[professId];
        i = 9;
        for (FabledClass c : availableClasses) {
            if (c.getParent() != profession)
                continue;

            int index = page.getIndex(c.getName());
            if (index != -1)
                inventoryContents[index] = c.getToolIcon();
            else if (!guiData.has(c.getName()) && i < playerContents.length)
                playerContents[i++] = c.getToolIcon();
        }

        if (profession == null)
            return "Class Selection";
        else
            return limit(profession.getName() + " / Sub-profession");
    }

    private String limit(String text) {
        return text.substring(0, Math.min(text.length(), 32));
    }

    private String populateClassDetails() {
        i = 9;
        GUIPage page = guiData.getPage();
        for (String group : availableGroups) {
            ItemStack item = make(Material.DRAGON_EGG,
                    group,
                    "",
                    "Spot for the player's current",
                    "class in the group should",
                    "be placed in the GUI");
            int index = page.getIndex(group);
            if (index != -1)
                inventoryContents[index] = item;
            else if (!guiData.has(group) && i < playerContents.length)
                playerContents[i++] = item;
        }

        return "GUI Editor - Class Details";
    }

    private String populateSkillTree() {
        playerContents[7] = PREV_CLASS;
        playerContents[8] = NEXT_CLASS;

        FabledClass current = availableClasses[classId];
        GUIPage     page    = guiData.getPage();
        i = 9;
        Set<Skill> skills = new HashSet<>();
        while (current != null) {
            skills.addAll(current.getSkills());
            current = current.getParent();
        }

        for (Skill skill : skills) {
            int index = page.getIndex(skill.getName());
            if (index > -1 && index < inventoryContents.length)
                inventoryContents[index] = skill.getToolIndicator();
            else if (!guiData.has(skill.getName()) && i < playerContents.length && i > -1) {
                playerContents[i++] = skill.getToolIndicator();
            }
        }

        return limit(availableClasses[classId].getName() + " / Skill Tree");
    }

    private String populateAttributes() {
        i = 9;
        GUIPage page = guiData.getPage();
        for (String key : Fabled.getAttributeManager().getKeys()) {
            ProAttribute attr  = Fabled.getAttributeManager().getAttribute(key);
            int          index = page.getIndex(attr.getKey());
            if (index != -1)
                inventoryContents[index] = attr.getToolIcon();
            else if (!guiData.has(attr.getKey()) && i < playerContents.length) {
                playerContents[i++] = attr.getToolIcon();
            }
        }

        return "GUI Editor - Attributes";
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
            event.setCancelled(true);
        else if (event.getRawSlot() < event.getView().getTopInventory().getSize()) { // Clicked upper inventory
            if (guiData.getPages() > 1) { // Check if clicked next or prev buttons
                if (guiData.getSize() > 9) { // Next and prev buttons are placed vertically
                    switch (event.getSlot()) {
                        case 8 -> {
                            update();
                            guiData.prev();
                            setType(type);
                            event.setCancelled(true);
                        }
                        case 17 -> {
                            update();
                            guiData.next();
                            setType(type);
                            event.setCancelled(true);
                        }
                    }
                } else { // Next and prev buttons are placed horizontally
                    switch (event.getSlot()) {
                        case 7 -> {
                            update();
                            guiData.prev();
                            setType(type);
                            event.setCancelled(true);
                        }
                        case 8 -> {
                            update();
                            guiData.next();
                            setType(type);
                            event.setCancelled(true);
                        }
                    }
                }
            }
            if (!this.guiData.isEditable()) {
                event.setCancelled(true);
            }
        } else { // Clicked lower inventory
            if (event.getSlot() < 9) {
                update();
                event.setCancelled(true);
            }
            switch (event.getSlot()) {
                case 0:
                    setType(type.prev());
                    break;
                case 1:
                    setType(type.next());
                    break;
                case 2:
                    guiData.shrink();
                    update();
                    setType(type);
                    break;
                case 3:
                    guiData.grow();
                    update();
                    setType(type);
                    break;
                case 4:
                    guiData.addPage();
                    setType(type);
                    break;
                case 5:
                    guiData.removePage();
                    setType(type);
                    break;
                case 7:
                    switch (type) {
                        case CLASS_SELECTION:
                            professId = (professId + 1) % availableProfesses.length;
                            setType(type);
                            break;
                        case SKILL_TREE:
                            classId = (classId + 1) % availableClasses.length;
                            setType(type);
                            break;
                    }
                    break;
                case 8:
                    switch (type) {
                        case CLASS_SELECTION:
                            professId = (professId + availableProfesses.length - 1) % availableProfesses.length;
                            setType(type);
                            break;
                        case SKILL_TREE:
                            classId = (classId + availableClasses.length - 1) % availableClasses.length;
                            setType(type);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void restore() {
        if (data != null && !switching) {
            update();
            data.restore(player);
            data = null;
            inUse = false;
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
