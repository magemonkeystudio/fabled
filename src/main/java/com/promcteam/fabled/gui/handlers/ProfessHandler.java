/**
 * Fabled
 * com.promcteam.fabled.gui.handlers.ProfessHandler
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
package com.promcteam.fabled.gui.handlers;

import com.promcteam.fabled.api.classes.FabledClass;
import com.promcteam.fabled.gui.tool.GUIHolder;
import com.promcteam.fabled.manager.CmdManager;

public class ProfessHandler extends GUIHolder<FabledClass> {
    /**
     * Professes as the clicked class
     *
     * @param type player class clicked on
     * @param slot slot number
     */
    @Override
    protected void onClick(FabledClass type, int slot, boolean left, boolean shift) {
        player.getPlayer().closeInventory();
        CmdManager.PROFESS_COMMAND.execute(player.getPlayer(), new String[]{type.getName()});
    }
}
