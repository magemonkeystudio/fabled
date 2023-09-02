/**
 * SkillAPI
 * com.sucy.skill.gui.handlers.AttributeHandler
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
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
package com.sucy.skill.gui.handlers;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.gui.tool.GUIHolder;
import com.sucy.skill.hook.VaultHook;
import com.sucy.skill.manager.AttributeManager;

public class AttributeHandler extends GUIHolder<AttributeManager.Attribute> {
    private static final String NO_MONEY = "attribute-no-money";

    @Override
    public void onClick(AttributeManager.Attribute type, int slot, boolean left, boolean shift) {
        if (left) {
            if (player.upAttribute(type.getKey())) setPage(page);
        } else if (SkillAPI.getSettings().isAttributesDowngrade() && player.getAttribute(type.getKey()) > 0) {

            if (SkillAPI.getSettings().getAttributesDowngradePrice() > 0 && !VaultHook.isEconomyValid()) {
                SkillAPI.inst()
                        .getLogger()
                        .info("Attributes should cost " + SkillAPI.getSettings().getAttributesDowngradePrice()
                                + " to refund " +
                                "but the Vault Hook couldn't find an economy plugin. Refunding anyway...");
            }

            if (SkillAPI.getSettings().getAttributesDowngradePrice() > 0 && VaultHook.isEconomyValid()
                    && VaultHook.hasBalance(player.getPlayer(), SkillAPI.getSettings().getAttributesDowngradePrice())) {
                if (player.refundAttribute(type.getKey())) {
                    VaultHook.withdraw(player.getPlayer(),
                            Double.parseDouble(String.valueOf(SkillAPI.getSettings().getAttributesDowngradePrice())));
                    setPage(page);
                }
                return;
            } else if (VaultHook.isEconomyValid() && !VaultHook.hasBalance(player.getPlayer(),
                    SkillAPI.getSettings().getAttributesDowngradePrice())) {
                SkillAPI.getLanguage().sendMessage(NO_MONEY, player.getPlayer());
                return;
            }

            if (player.refundAttribute(type.getKey())) {
                setPage(page);
            }
        }
    }
}
