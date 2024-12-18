/**
 * Fabled
 * studio.magemonkey.fabled.api.enums.PointSource
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
package studio.magemonkey.fabled.api.enums;

/**
 * <p>A collection of possible reasons skill points were gained by a player.</p>
 * <p>This is used when giving skill points to a player to allow effects
 * to react differently depending on why it is being added.</p>
 */
public enum PointSource {
    /**
     * The player leveled up to get more skill points
     */
    LEVEL,

    /**
     * The player downgraded a skill and was refunded the skill points
     */
    REFUND,

    /**
     * A command gave the player additional skill points
     */
    COMMAND,

    /**
     * The player was given skill points for an unspecified reason
     */
    SPECIAL,

    /**
     * The skill points are from the player's data being initialized on startup
     */
    INITIALIZATION,
}
