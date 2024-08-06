package com.sucy.skill.api.enums;

/**
 * @deprecated Use {@link studio.magemonkey.fabled.api.enums.ExpSource} instead
 */
public enum ExpSource {
    /**
     * Experience resulted from defeating a monster
     */
    MOB(0x1),

    /**
     * Experience resulted from breaking a block
     */
    BLOCK_BREAK(0x2),

    /**
     * Experience resulted from placing a block
     */
    BLOCK_PLACE(0x4),

    /**
     * Experience resulted from crafting an item
     */
    CRAFT(0x8),

    /**
     * Experience resulted from an issued command
     */
    COMMAND(0x10),

    /**
     * Experience resulted from an unspecified reason
     */
    SPECIAL(0x20),

    /**
     * Experience from Bottles o' Enchanting
     */
    EXP_BOTTLE(0x40),

    /**
     * Experience from smelting ore
     */
    SMELT(0x80),

    /**
     * Experience from a quest
     */
    QUEST(0x100),

    /**
     * Experience given through a plugin. This exp is not shared
     */
    PLUGIN(0x200);

    /**
     * The ID of the experience source which should be a unique power of 2 (or bit value)
     */
    private final int id;

    /**
     * Enum constructor
     *
     * @param id ID of the experience source (should use a unique bit)
     */
    ExpSource(int id) {
        this.id = id;
    }

    /**
     * <p>Retrieves the ID of the experience source.</p>
     *
     * @return ID of the experience source
     */
    public int getId() {
        return id;
    }
}
