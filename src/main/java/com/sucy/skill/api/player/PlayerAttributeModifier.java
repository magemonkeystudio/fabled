/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerSkill
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.api.player;

import java.util.UUID;

/**
 * Represents player-specific data for attributes, a replacement of bonus attribute
 */
public class PlayerAttributeModifier {

    private UUID uuid;
    private String name;
    private int amount;
    private Operation operation;

    public PlayerAttributeModifier(String name, int amount, Operation operation) {

        this.uuid = UUID.randomUUID();
        this.name = name;
        this.amount = amount;
        this.operation = operation;

    }

    public double applyOn(double value) {

        switch(this.operation) {
        case ADD_NUMBER:
            return value + this.amount;
        case MULTIPLY_PERCENTAGE:
            return value * this.amount;
        }

        return -1;

    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public Operation getOperation() {
        return operation;
    }

    /**
     * Enumerable operation to be applied.
     */
    public enum Operation {

        /**
         * Adds (or subtracts) the specified amount to the base value.
         */
        ADD_NUMBER,
        /**
         * Multiply final amount by this value.
         */
        MULTIPLY_PERCENTAGE;
    }

}