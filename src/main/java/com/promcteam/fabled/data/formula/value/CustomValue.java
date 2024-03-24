/**
 * Fabled
 * com.promcteam.fabled.data.formula.value.CustomValue
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
package com.promcteam.fabled.data.formula.value;

import com.promcteam.fabled.data.formula.IValue;
import lombok.Getter;
import lombok.Setter;

/**
 * A custom defined value for a formula
 */
public class CustomValue implements IValue {
    /**
     * @return defining token
     */
    @Getter
    private final String token;
    /**
     * Sets the argument index for the value.
     * This is handled by formulas so you shouldn't
     * need to use this.
     *
     * @param index argument index
     */
    @Setter
    private       int    index;

    /**
     * A defined value used in formulas
     *
     * @param token equation token
     */
    public CustomValue(String token) {
        this.token = token;
    }

    /**
     * Gets the value using the inputs
     *
     * @param input the input data
     * @return result value
     */
    @Override
    public double compute(double... input) {
        return input[index];
    }
}
