/**
 * Fabled
 * studio.magemonkey.fabled.api.util.BuffType
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
package studio.magemonkey.fabled.api.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BuffType {
    DAMAGE("FABLED_damage"),
    DEFENSE("FABLED_defense"),
    SKILL_DAMAGE("FABLED_skill_damage"),
    SKILL_DEFENSE("FABLED_skill_defense"),
    HEALING("FABLED_healing"),
    INVISIBILITY("FABLED_invisibility"),
    NO_SCREEN_SHAKE("FABLED_no_screen_shake");

    private final String localizedName;

    public static BuffType getByNameOrLocal(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name) || type.getLocalizedName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
