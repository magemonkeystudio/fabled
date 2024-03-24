/**
 * Fabled
 * com.promcteam.fabled.gui.map.SkillDetailMenu
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 ProMCTeam
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.promcteam.fabled.gui.map;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.player.PlayerSkill;
import com.promcteam.codex.mccore.gui.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SkillDetailMenu extends MapMenu {
    private static final String BACKGROUND = "background";
    private static final String DETAIL     = "detail";
    private static final String BACK_ON    = "back1";
    private static final String UP_ON      = "up1";
    private static final String DOWN_ON    = "down1";
    private static final String MORE_ON    = "more1";
    private static final String BACK_OFF   = "back0";
    private static final String UP_OFF     = "up0";
    private static final String DOWN_OFF   = "down0";
    private static final String MORE_OFF   = "more0";
    private              Fabled api;

    public SkillDetailMenu(Fabled api) {
        this.api = api;
    }

    private int getButton(Player player) {
        return getSelection(player) & 0xff;
    }

    private int getPage(Player player) {
        return (getSelection(player) >> 8) & 0xff;
    }

    private int getPages(Player player) {
        return getSelection(player) >> 16;
    }

    private void setButton(Player player, int button) {
        setSelection(player, button, getPage(player), getPages(player));
    }

    private void setPage(Player player, int page) {
        setSelection(player, getButton(player), page, getPages(player));
    }

    private void setPages(Player player, int pages) {
        setSelection(player, getButton(player), getPage(player), pages);
    }

    private void setSelection(Player player, int button, int page, int pages) {
        setSelection(player, button | (page << 8) | (pages << 16));
    }

    @Override
    public void onLeft(Player player) {
        int button = getButton(player);

        button = Math.max(0, button - 1);
        boolean down = Fabled.getSettings().isAllowDowngrade();
        if (button == 2 && !down) button--;

        setButton(player, button);
    }

    @Override
    public void onRight(Player player) {
        int button = getButton(player);
        int page   = getPage(player);

        button = Math.min(3, button + 1);
        boolean down = Fabled.getSettings().isAllowDowngrade();
        if (button == 2 && !down) button++;

        setButton(player, button);
    }

    @Override
    public void onUp(Player player) {
        Fabled.getPlayerData(player).upgradeSkill(SkillListMenu.getSkill(player).getData());
    }

    @Override
    public void onDown(Player player) {
        if (!Fabled.getSettings().isAllowDowngrade()) return;
        Fabled.getPlayerData(player).downgradeSkill(SkillListMenu.getSkill(player).getData());
    }

    @Override
    public void onSelect(Player player) {
        int button = getButton(player);
        int page   = getPage(player);

        // Back button
        if (button == 0) {
            MapMenuManager.sendBack(player);
        }

        // Upgrade button
        else if (button == 1) {
            Fabled.getPlayerData(player).upgradeSkill(SkillListMenu.getSkill(player).getData());
        }

        // Downgrade button
        else if (button == 2) {
            if (Fabled.getSettings().isAllowDowngrade())
                Fabled.getPlayerData(player).downgradeSkill(SkillListMenu.getSkill(player).getData());
        }

        // More info button
        else {
            page = (page + 1) % getPages(player);
            setPage(player, page);
            setup(player);
        }
    }

    @Override
    public void setup(Player player) {
        MapScheme scheme = MapScheme.get(api, Fabled.getPlayerData(player).getScheme());
        MapScene  scene  = getScene(player);
        int       page   = getPage(player);

        scene.add(BACKGROUND, new MapObject(scheme.getImage(Menu.BACKGROUND), 0, 0));

        // Get text to draw
        PlayerSkill  skill = SkillListMenu.getSkill(player);
        ItemStack    icon  = skill.getData().getIndicator(skill, true);
        ItemMeta     meta  = icon.getItemMeta();
        List<String> lore  = meta == null ? null : meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        if (meta != null) {
            lore.add(0, meta.getDisplayName());
        }

        // Find page details
        MapFont font         = scheme.getFont(Menu.DETAIL);
        int     linesPerPage = 90 / (font.getFont().getSize() + 3);
        int     pages        = (lore.size() + linesPerPage - 1) / linesPerPage;

        // Add the text
        int y     = font.getFont().getSize() + 5;
        int start = linesPerPage * page;
        for (int i = start; i < start + linesPerPage && i < lore.size(); i++) {
            String line = lore.get(i);
            scene.add(DETAIL + i, new MapObject(new MapString(font, scheme.getColor(Menu.FONT), line), 7, y));
            y += font.getFont().getSize() + 3;
        }

        // Get position for upgrade arrow
        boolean down = Fabled.getSettings().isAllowDowngrade();
        int     x;
        if (down) x = 42;
        else x = 56;

        // Add buttons

        scene.add(BACK_ON, new MapObject(scheme.getImage(Menu.BACK_1), 6, 95));
        scene.add(BACK_OFF, new MapObject(scheme.getImage(Menu.BACK_0), 6, 95));

        scene.add(UP_ON, new MapObject(scheme.getImage(Menu.UP_1), x, 95));
        scene.add(UP_OFF, new MapObject(scheme.getImage(Menu.UP_0), x, 95));

        scene.add(DOWN_ON, new MapObject(scheme.getImage(Menu.DOWN_1), 68, 95));
        scene.add(DOWN_OFF, new MapObject(scheme.getImage(Menu.DOWN_0), 68, 95));

        scene.add(MORE_ON, new MapObject(scheme.getImage(Menu.MORE_1), 90, 95));
        scene.add(MORE_OFF, new MapObject(scheme.getImage(Menu.MORE_0), 90, 95));

        setPages(player, pages);
    }

    @Override
    public void render(MapBuffer mapBuffer, Player player) {
        int      button = getButton(player);
        MapScene scene  = getScene(player);

        // Draw buttons
        scene.get(BACK_ON).setVisible(button == 0);
        scene.get(BACK_OFF).setVisible(button != 0);

        scene.get(UP_ON).setVisible(button == 1);
        scene.get(UP_OFF).setVisible(button != 1);

        scene.get(DOWN_ON).setVisible(button == 2);
        scene.get(DOWN_OFF).setVisible(button != 2);

        scene.get(MORE_ON).setVisible(button == 3);
        scene.get(MORE_OFF).setVisible(button != 3);

        scene.apply(mapBuffer);
    }
}
