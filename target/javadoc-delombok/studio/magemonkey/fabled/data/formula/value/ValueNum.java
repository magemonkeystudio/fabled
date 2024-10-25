/**
 * Fabled
 * studio.magemonkey.fabled.data.formula.value.ValueNum
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
package studio.magemonkey.fabled.data.formula.value;

import studio.magemonkey.fabled.data.formula.IValue;
import studio.magemonkey.codex.mccore.config.parse.NumberParser;

/**
 * The attribute value
 */
public class ValueNum implements IValue {
    private double value;

    /**
     * @param value value to use
     */
    public ValueNum(String value) {
        this.value = NumberParser.parseDouble(value);
    }

    /**
     * @param value value to use
     */
    public ValueNum(double value) {
        this.value = value;
    }

    /**
     * Gets the value using the two inputs
     *
     * @param input the input data
     * @return result value
     */
    @Override
    public double compute(double... input) {
        return value;
    }
}
