/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerSkill
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.api.player;

import com.sucy.skill.api.enums.Operation;

import java.util.UUID;

/**
 * Represents player-specific data for attributes, a replacement of bonus attribute
 */
public class PlayerAttributeModifier {

    private final UUID      uuid;
    private final String    name;
    private final double    amount;
    private final Operation operation;
    private final boolean   persistent;

    /**
     * Initializes a new PlayerAttributeModifier.
     * Used for modifying player attribute for a period of time
     *
     * @param name       The group name of this modifier
     * @param amount     The amount of value used for operation later
     * @param operation  The Operation type
     * @param persistent If this is set to true, this modifier will not be removed when player change class
     *                   or change world.
     */
    public PlayerAttributeModifier(String name, double amount, Operation operation, boolean persistent) {

        this.uuid = UUID.randomUUID();
        this.name = name;
        this.amount = amount;
        this.operation = operation;
        this.persistent = persistent;

    }

    public double applyOn(double value) {

        switch (this.operation) {
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

    public double getAmount() {
        return amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

}