/**
 * Fabled
 * com.promcteam.fabled.manager.TitleManager
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
package com.promcteam.fabled.manager;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.api.util.Title;
import com.promcteam.fabled.data.Settings;
import com.promcteam.fabled.data.TitleType;
import com.promcteam.codex.mccore.config.CustomFilter;
import com.promcteam.codex.mccore.config.FilterType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Handles accessing the Title display resource
 */
public class TitleManager {
    private static int fadeIn;
    private static int duration;
    private static int fadeOut;

    /**
     * Initializes the title object if not done so already
     */
    private static void init() {
        Settings settings = Fabled.getSettings();
        fadeIn = settings.getTitleFadeIn();
        duration = settings.getTitleDuration();
        fadeOut = settings.getTitleFadeOut();
    }

    /**
     * Shows a message using the Title display
     *
     * @param player  player to send to
     * @param type    type of message
     * @param msgKey  language config key for the message
     * @param filters filters to apply to the message
     */
    public static void show(Player player, TitleType type, String msgKey, CustomFilter... filters) {
        if (Fabled.getSettings().useTitle(type) && msgKey != null) {
            List<String> message = Fabled.getLanguage().getMessage(msgKey, true, FilterType.COLOR, filters);
            if (message != null && !message.isEmpty()) {
                init();
                String title    = message.get(0);
                String subtitle = message.size() > 1 ? message.get(1) : null;
                Title.send(player, title, subtitle, fadeIn, duration, fadeOut);
            }
        } else if (msgKey != null)
            Fabled.getLanguage().sendMessage(msgKey, player, FilterType.COLOR, filters);
    }
}
