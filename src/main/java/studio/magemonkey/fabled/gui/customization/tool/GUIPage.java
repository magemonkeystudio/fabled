/**
 * Fabled
 * studio.magemonkey.fabled.gui.customization.tool.GUIPage
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.gui.customization.tool;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.hook.PlaceholderAPIHook;
import studio.magemonkey.fabled.hook.PluginChecker;

import java.util.HashMap;
import java.util.Map;

public class GUIPage {
    private final Map<String, Integer> slots  = new HashMap<>();
    private final Map<Integer, String> lookup = new HashMap<>();

    private final GUIData parent;

    GUIPage(GUIData parent) {
        this.parent = parent;
    }

    GUIPage(GUIData parent, Map<Integer, Skill> skillSlots) {
        this.parent = parent;
        for (Map.Entry<Integer, Skill> entry : skillSlots.entrySet()) {
            slots.put(entry.getValue().getName().toLowerCase(), entry.getKey());
            lookup.put(entry.getKey(), entry.getValue().getName().toLowerCase());
        }
    }

    GUIPage(GUIData parent, DataSection data) {
        this(parent);

        for (String key : data.keys()) {
            slots.put(key, data.getInt(key));
            lookup.put(data.getInt(key), key);
        }
    }

    public String get(int index) {
        return lookup.get(index);
    }

    public void set(int index, String value) {
        slots.put(value, index);
        lookup.put(index, value);
    }

    public int getIndex(String item) {
        item = item.toLowerCase();
        return slots.getOrDefault(item, -1);
    }

    public void fill(ItemStack[] data) {
        for (Map.Entry<String, Integer> entry : slots.entrySet())
            data[entry.getValue()] = make(entry.getKey());
    }

    public void clearRight() {
        for (int i = 8; i < 54; i += 9)
            if (lookup.containsKey(i))
                slots.remove(lookup.remove(i));
    }

    public void remove(int min, int max) {
        for (int i = min; i < max; i++)
            if (lookup.containsKey(i))
                slots.remove(lookup.remove(i));
    }

    private ItemStack make(String key) {
        ItemStack item;
        if (Fabled.isSkillRegistered(key))
            item = Fabled.getSkill(key).getToolIndicator();
        else if (Fabled.isClassRegistered(key))
            item = Fabled.getClass(key).getToolIcon();
        else
            item = GUITool.getIcon(key);

        return item;
    }

    public void load(ItemStack[] data) {
        slots.clear();
        lookup.clear();
        for (int i = 0; i < data.length; i++) {
            ItemStack item = data[i];
            if (item == null)
                continue;

            ItemMeta meta = data[i].getItemMeta();
            if (meta == null) {
                continue;
            }
            String key = ChatColor.stripColor(meta.getDisplayName()).toLowerCase();
            if (key.equals("next page") || key.equals("prev page"))
                continue;

            slots.put(key.toLowerCase(), i);
            lookup.put(i, key.toLowerCase());
        }
    }

    public ItemStack[] instance(PlayerData player, Map<String, ? extends IconHolder> data) {
        ItemStack[] contents = new ItemStack[parent.getSize()];

        Player bukkitPlayer = player.getPlayer();
        for (Map.Entry<Integer, String> entry : lookup.entrySet()) {
            IconHolder holder = data.get(entry.getValue());
            ItemStack item = holder != null && holder.isAllowed(bukkitPlayer)
                    ? holder.getIcon(player)
                    : GUITool.getIcon(entry.getValue());

            if ((holder == null || holder.isAllowed(bukkitPlayer)) && item == null) {
                Fabled.inst()
                        .getLogger()
                        .warning("Could not find GUI item for " + entry.getValue()
                                + ". Please either remove it from the GUI or add it to the config.");
                continue;
            }

            if (item != null && PluginChecker.isPlaceholderAPIActive()) {
                PlaceholderAPIHook.processPlaceholders(item, bukkitPlayer);
            }
            try {
                contents[entry.getKey()] = item;
            } catch (ArrayIndexOutOfBoundsException e) {
                Fabled.inst()
                        .getLogger()
                        .warning("Invalid slot index for " + entry.getValue() + " (" + entry.getKey() + ")");
            }
        }

        return contents;
    }

    public void save(DataSection data) {
        for (Map.Entry<String, Integer> entry : slots.entrySet()) {
            data.set(entry.getKey(), entry.getValue());
        }
    }

    public boolean isValid() {
        return !slots.isEmpty();
    }
}
