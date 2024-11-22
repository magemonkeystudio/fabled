/**
 * Fabled
 * studio.magemonkey.fabled.gui.handlers.AttributeHandler
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
package studio.magemonkey.fabled.gui.handlers;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.gui.customization.tools.GUIHolder;
import studio.magemonkey.fabled.hook.VaultHook;
import studio.magemonkey.fabled.language.ErrorNodes;
import studio.magemonkey.fabled.manager.FabledAttribute;

public class AttributeHandler extends GUIHolder<FabledAttribute> {
    @Override
    public void onClick(FabledAttribute type, int slot, boolean left, boolean shift) {
        if (left) {
            if (player.upAttribute(type.getKey())) setPage(page);
        } else if (Fabled.getSettings().isAttributesDowngrade() && player.getAttribute(type.getKey()) > 0) {

            if (Fabled.getSettings().getAttributesDowngradePrice() > 0 && !VaultHook.isEconomyValid()) {
                Fabled.inst()
                        .getLogger()
                        .info("Attributes should cost " + Fabled.getSettings().getAttributesDowngradePrice()
                                + " to refund " +
                                "but the Vault Hook couldn't find an economy plugin. Refunding anyway...");
            }

            if (Fabled.getSettings().getAttributesDowngradePrice() > 0 && VaultHook.isEconomyValid()
                    && VaultHook.hasBalance(player.getPlayer(), Fabled.getSettings().getAttributesDowngradePrice())) {
                if (player.refundAttribute(type.getKey(), 1)) {
                    VaultHook.withdraw(player.getPlayer(),
                            Double.parseDouble(String.valueOf(Fabled.getSettings().getAttributesDowngradePrice())));
                    setPage(page);
                }
                return;
            } else if (VaultHook.isEconomyValid() && !VaultHook.hasBalance(player.getPlayer(),
                    Fabled.getSettings().getAttributesDowngradePrice())) {
                Fabled.getLanguage().sendMessage(ErrorNodes.NO_MONEY, player.getPlayer());
                return;
            }

            if (player.refundAttribute(type.getKey(), 1)) {
                setPage(page);
            }
        }
    }
}
