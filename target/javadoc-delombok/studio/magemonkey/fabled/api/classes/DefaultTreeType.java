/**
 * Fabled
 * studio.magemonkey.fabled.api.classes.DefaultTreeType
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
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
package studio.magemonkey.fabled.api.classes;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.tree.basic.*;

import java.util.Locale;

/**
 * Types of SkillTrees that are available for classes/skills to use
 */
public enum DefaultTreeType implements TreeType {
    BASIC_HORIZONTAL,
    BASIC_VERTICAL,
    LEVEL_HORIZONTAL,
    LEVEL_VERTICAL,
    FLOOD,
    REQUIREMENT,
    CUSTOM,
    ;

    /**
     * Retrieves the skill tree depending on the enum value
     *
     * @return skill tree instance
     */
    public InventoryTree getTree(Fabled api, FabledClass parent) {
        switch (this) {
            case BASIC_HORIZONTAL:
                return new BasicHorizontalTree(api, parent);
            case BASIC_VERTICAL:
                return new BasicVerticalTree(api, parent);
            case LEVEL_HORIZONTAL:
                return new LevelHorizontalTree(api, parent);
            case LEVEL_VERTICAL:
                return new LevelVerticalTree(api, parent);
            case FLOOD:
                return new FloodTree(api, parent);
            case REQUIREMENT:
                return new RequirementTree(api, parent);
            case CUSTOM:
                return new CustomTree(api, parent);
            default:
                return null;
        }
    }

    /**
     * Retrieves a tree type by enum value name
     *
     * @param name enum value name
     * @return corresponding tree type or the default REQUIREMENT if invalid
     */
    public static DefaultTreeType getByName(String name) {
        try {
            return Enum.valueOf(DefaultTreeType.class, name.toUpperCase(Locale.US).replace(' ', '_'));
        } catch (Exception ex) {
            return REQUIREMENT;
        }
    }
}
