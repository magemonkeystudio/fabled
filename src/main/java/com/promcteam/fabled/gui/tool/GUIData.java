/**
 * Fabled
 * com.promcteam.fabled.gui.tool.GUIData
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
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
package com.promcteam.fabled.gui.tool;

import com.google.common.base.Preconditions;
import com.promcteam.codex.mccore.config.parse.DataSection;
import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerData;
import com.promcteam.fabled.api.skills.Skill;
import com.promcteam.fabled.tree.basic.CustomTree;
import com.promcteam.fabled.tree.basic.InventoryTree;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GUIData {
    public static final String
            ROWS  = "rows",
            PAGES = "pages",
            SLOTS = "slots";

    private final ArrayList<GUIPage> pageMap  = new ArrayList<GUIPage>();
    private       boolean            editable = true;

    private int rows  = 3;
    private int pages = 1;
    private int nav   = 0;

    GUIData() {
        pageMap.add(new GUIPage(this));
    }

    GUIData(InventoryTree skillTree) {
        if (!(skillTree instanceof CustomTree)) {
            editable = false;
        }
        rows = skillTree.getHeight();
        try {
            Preconditions.checkArgument(rows > 0 && rows <= 6);
        } catch (IllegalArgumentException e) {
            Fabled.inst().getLogger().warning("Error loading GUI: Rows should be > 0 and <= 6. Rows: " + rows);
            throw e;
        }
        TreeMap<Integer, Skill> skillSlots   = skillTree.getSkillSlots();
        int                     slotsPerPage = rows * 9;
        int                     i            = slotsPerPage;
        Map<Integer, Skill>     map          = new HashMap<>();
        for (Map.Entry<Integer, Skill> entry : skillSlots.entrySet()) {
            int slot = entry.getKey();
            if (slot >= i) {
                this.pageMap.add(new GUIPage(this, map));
                map.clear();
                i += slotsPerPage;
            }
            map.put(slot - i + slotsPerPage, entry.getValue());
        }
        if (!map.isEmpty() || this.pageMap.isEmpty()) {
            this.pageMap.add(new GUIPage(this, map));
        }
        pages = pageMap.size();
    }

    GUIData(DataSection data) {
        if (data != null) {
            rows = data.getInt(ROWS, rows);
            try {
                Preconditions.checkArgument(rows > 0 && rows <= 6);
            } catch (IllegalArgumentException e) {
                Fabled.inst().getLogger().warning("Error loading GUI: Rows should be > 0 and <= 6. Rows: " + rows);
                throw e;
            }
            this.pages = data.getInt(PAGES, this.pages);
            DataSection pages = data.getSection(SLOTS);
            if (pages != null)
                for (String page : pages.keys())
                    if (pages.isSection(page))
                        this.pageMap.add(new GUIPage(this, pages.getSection(page)));
        }
        while (pageMap.size() < pages)
            pageMap.add(new GUIPage(this));
    }

    public void show(GUIHolder handler, PlayerData player, String title, Map<String, ? extends IconHolder> data) {
        Inventory   inv      = Bukkit.getServer().createInventory(handler, rows * 9, title);
        ItemStack[] contents = pageMap.get(0).instance(player, data);
        if (pages > 1) GUITool.addPageButtons(contents);

        inv.setContents(contents);
        handler.set(this, player, inv, data);
        player.getPlayer().openInventory(inv);
    }

    public boolean isEditable() {return editable;}

    public GUIPage getPage(int page) {
        return pageMap.get(page % pages);
    }

    public GUIPage getPage() {
        return pageMap.get(nav);
    }

    public int getSize() {
        return rows * 9;
    }

    public void init(ItemStack[] contents) {
        nav = 0;
        fill(contents);
    }

    public void load(ItemStack[] contents) {
        pageMap.get(nav).load(contents);
    }

    public void fill(ItemStack[] contents) {
        pageMap.get(nav).fill(contents);
    }

    public void next() {
        nav = (nav + 1) % pages;
    }

    public void prev() {
        nav = (nav + pages - 1) % pages;
    }

    public int getPages() {
        return pages;
    }

    public void addPage() {
        pageMap.add(new GUIPage(this));
        pages += 1;
        nav++;
        if (pages == 2)
            for (GUIPage page : pageMap)
                page.clearRight();
    }

    public void removePage() {
        pageMap.remove(nav);
        pages--;
        nav = Math.min(nav, pages - 1);

        if (pages == 0) {
            addPage();
        }
    }

    public void resize(int rows) {
        this.rows = Math.max(Math.min(rows, 6), 1);
    }

    public void shrink() {
        if (rows > 1)
            rows--;

        for (GUIPage page : pageMap)
            page.remove(getSize(), getSize() + 9);
    }

    public void grow() {
        if (rows < 6)
            rows++;
    }

    public boolean isValid() {
        for (GUIPage page : pageMap)
            if (page.isValid())
                return true;

        return false;
    }

    public boolean has(String item) {
        for (GUIPage page : pageMap)
            if (page.getIndex(item) != -1)
                return true;

        return false;
    }

    public void save(DataSection data) {
        data.set(ROWS, rows);
        data.set(PAGES, pages);
        DataSection slots = data.createSection(SLOTS);
        int         i     = 0;
        for (GUIPage page : pageMap) {
            page.save(slots.createSection((++i) + ""));
        }
    }
}
