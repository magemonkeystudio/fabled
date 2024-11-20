/**
 * Fabled
 * studio.magemonkey.fabled.data.PlayerEquips
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
package studio.magemonkey.fabled.data;

import com.google.common.base.Objects;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.codex.mccore.config.parse.NumberParser;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.enums.Operation;
import studio.magemonkey.fabled.api.player.PlayerAttributeModifier;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;

import java.util.*;
import java.util.Map.Entry;

/**
 * Handles keeping track of and applying attribute
 * bonuses and requirements for items.
 */
public class PlayerEquips {

    private final PlayerData playerData;

    private final EquipData                   emptyEquip   = new EquipData();
    private final HashMap<Integer, EquipData> equips       = new HashMap<Integer, EquipData>();
    private       EquipData                   handHeldItem = new EquipData();

    /**
     * @param player player data reference
     */
    public PlayerEquips(PlayerData player) {
        this.playerData = player;

        for (int slot : Fabled.getSettings().getSlots()) {
            equips.put(slot, emptyEquip);
        }
    }

    /**
     * @return true if the player can hit something, false otherwise
     */
    public boolean canHit() {
        return this.handHeldItem.hasMetConditions();
    }

    public boolean canBlock() {
        return this.equips.get(40).hasMetConditions();
    }

    /**
     * Updates all available items for the player
     *
     * @return true if the equip data is being changed
     * call {@link PlayerData#updatePlayerStat(Player)} to update player stats
     */
    public boolean update(Player player) {
        boolean isChanged = false;

        this.updateHandHeldItem(player, player.getInventory().getItemInMainHand());

        for (Entry<Integer, EquipData> entry : this.equips.entrySet()) {
            int slot = entry.getKey();
            if (this.updateEquip(player, slot, player.getInventory().getItem(slot))) {
                isChanged = true;
            }
        }

        return isChanged;
    }

    /**
     * Update item for hand held item, handling any requirements and attribute
     *
     * @param player the player reference
     * @param item   the <strong>new</strong> item
     */
    public boolean updateHandHeldItem(Player player, ItemStack item) {

        EquipData from = this.handHeldItem;

        if (Objects.equal(from.item, item)) {
            return false;
        }

        from.revert();

        EquipData to;

        if (item == null) {
            to = this.emptyEquip;
        } else {
            to = new EquipData(item, EquipType.HAND_HELD_ITEM);
        }

        if (!to.hasMetConditions()) {
            if (Fabled.getSettings().isDropWeapon()) {
                player.getInventory().removeItem(to.item);
                player.updateInventory();
                player.getWorld().dropItemNaturally(player.getLocation(), to.item);

                return false;
            } else {
                this.handHeldItem = to;
            }
        } else {
            this.handHeldItem = to;

            to.apply();
        }

        return true;
    }

    /**
     * Update equips for certain slot, handling any requirements and attribute
     *
     * @param player the player reference
     * @param slot   related index
     * @param item   the <strong>new</strong> item
     * @return true if the equip data is being changed
     * call {@link PlayerData#updatePlayerStat(Player)} to update player stats
     */
    public boolean updateEquip(Player player, int slot, ItemStack item) {
        EquipData from = this.equips.get(slot);

        if (Objects.equal(from.item, item)) {
            return false;
        }

        from.revert();

        EquipData to;

        // Skip the hand held slot as we will do it separately
        if (item == null || slot == player.getInventory().getHeldItemSlot()) {
            to = this.emptyEquip;
        } else {
            to = new EquipData(item, EquipType.fromSlot(slot));
        }

        if (!to.hasMetConditions()) {
            if (Fabled.getSettings().isDropWeapon()) {
                player.getInventory().setItem(slot, null);
                player.updateInventory();
                player.getWorld().dropItemNaturally(player.getLocation(), to.item);

                return false;
            }
        } else {
            this.equips.put(slot, to);

            to.apply();
        }

        return true;
    }

    public EquipData getEquipData(ItemStack item, EquipType type) {
        return new EquipData(item, type);
    }

    public enum EquipType {

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        HOT_BAR_ITEM,
        HAND_HELD_ITEM,
        OFF_HAND_ITEM,
        INVENTORY_ITEM,
        EXTERNAL_ITEM;

        public static EquipType fromSlot(int slot) {
            switch (slot) {
                case 0 - 8:
                    return HOT_BAR_ITEM;
                case 9 - 35:
                    return INVENTORY_ITEM;
                case 36:
                    return BOOTS;
                case 37:
                    return LEGGINGS;
                case 38:
                    return CHESTPLATE;
                case 39:
                    return HELMET;
                case 40:
                    return OFF_HAND_ITEM;
                default:
                    return EXTERNAL_ITEM;
            }
        }

        public boolean isArmor() {
            switch (this) {
                case HELMET:
                case CHESTPLATE:
                case LEGGINGS:
                case BOOTS:
                    return true;
                default:
                    return false;
            }
        }

    }

    /**
     * Represents one available item's data
     */
    public class EquipData {
        private final List<UUID>               attrModifierUUIDs = new ArrayList<>();
        private       HashMap<String, Integer> skillReq;
        private       HashMap<String, Integer> attrReq;
        private       HashMap<String, Integer> attribs;
        private       HashSet<String>          classReq;
        private       HashSet<String>          classExc;
        private       ItemStack                item;
        private       int                      levelReq;
        private       EquipType                type;

        /**
         * Sets up for an empty item slot
         */
        public EquipData() {
        }

        /**
         * Scans an items for bonuses or requirements
         *
         * @param item item to grab data from
         * @param type equipment type
         */
        public EquipData(ItemStack item, EquipType type) {
            this.item = item;
            this.type = type;

            if (!item.hasItemMeta())
                return;

            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) {
                return;
            }

            if (!itemMeta.hasLore()) {
                return;
            }

            List<String> lore = itemMeta.getLore();

            Settings settings    = Fabled.getSettings();
            String   classText   = settings.getLoreClassText();
            String   excludeText = settings.getLoreExcludeText();
            String   levelText   = settings.getLoreLevelText();
            boolean  skills      = settings.isCheckSkills();
            boolean  attributes  = settings.isAttributesEnabled();

            for (String line : lore) {
                String lower = ChatColor.stripColor(line).toLowerCase();

                // Level requirements
                if (lower.startsWith(levelText)) {
                    levelReq = NumberParser.parseInt(lower.substring(levelText.length()));
                }

                // Class requirements
                else if (lower.startsWith(classText)) {
                    List<String> required = Arrays.asList(lower.substring(classText.length()).split(", "));
                    if (classReq == null)
                        classReq = new HashSet<>();
                    classReq.addAll(required);
                }

                // Excluded classes
                else if (lower.startsWith(excludeText)) {
                    List<String> excluded = Arrays.asList(lower.substring(excludeText.length()).split(", "));
                    if (classExc == null)
                        classExc = new HashSet<>();
                    classExc.addAll(excluded);
                } else {
                    boolean done = false;

                    // Skill requirements
                    if (skills) {
                        for (Skill skill : Fabled.getSkills().values()) {
                            String text = settings.getSkillText(skill.getName());
                            if (lower.startsWith(text)) {
                                done = true;
                                if (skillReq == null)
                                    skillReq = new HashMap<>();

                                skillReq.put(skill.getName(), NumberParser.parseInt(lower.substring(text.length())));
                                break;
                            }
                        }
                    }

                    // Attribute requirements
                    if (attributes && !done) {
                        for (String attr : Fabled.getAttributesManager().getLookupKeys()) {
                            String text = settings.getAttrReqText(attr);
                            if (lower.startsWith(text)) {
                                if (attrReq == null)
                                    attrReq = new HashMap<>();

                                String normalized = Fabled.getAttributesManager().normalize(attr);
                                attrReq.put(normalized, NumberParser.parseInt(lower.substring(text.length())));
                                break;
                            }

                            text = settings.getAttrGiveText(attr);
                            if (lower.startsWith(text)) {
                                if (attribs == null)
                                    attribs = new HashMap<>();

                                String normalized = Fabled.getAttributesManager().normalize(attr);
                                int    current    = attribs.containsKey(attr) ? attribs.get(attr) : 0;
                                int extra =
                                        NumberParser.parseInt(lower.substring(text.length()).replace("%", ""));
                                attribs.put(normalized, current + extra);
                                break;
                            }
                        }
                    }
                }
            }
        }

        /**
         * Applies bonus attributes for the item
         */
        private void apply() {
            if (attribs != null) {
                for (Map.Entry<String, Integer> entry : attribs.entrySet()) {
                    PlayerAttributeModifier attrModifier = new PlayerAttributeModifier("fabled.player_equips",
                            entry.getValue(),
                            Operation.ADD_NUMBER,
                            false);
                    this.attrModifierUUIDs.add(attrModifier.getUUID());
                    playerData.addAttributeModifier(entry.getKey(), attrModifier, false);
                }
            }
        }

        /**
         * Reverts bonus attributes for the item
         */
        private void revert() {
            if (attribs != null) {
                for (UUID uuid : this.attrModifierUUIDs) {
                    playerData.removeAttributeModifier(uuid, false);
                }
            }
        }

        /**
         * Checks for conditions of an item
         *
         * @return true if conditions are met
         */
        public boolean hasMetConditions() {
            if (item == null) {
                return true;
            }

            PlayerClass main      = playerData.getMainClass();
            String      className = main == null ? "null" : main.getData().getName().toLowerCase();
            if ((levelReq > 0 && (main == null || main.getLevel() < levelReq))
                    || (classExc != null && main != null && classExc.contains(className))
                    || (classReq != null && (main == null || !classReq.contains(className))))
                return false;

            if (classExc != null)
                for (PlayerClass playerClass : playerData.getClasses())
                    if (matches(classExc, playerClass))
                        return false;

            if (classReq != null) {
                boolean metClassReq = false;
                for (PlayerClass playerClass : playerData.getClasses())
                    if (matches(classReq, playerClass))
                        metClassReq = true;

                if (!metClassReq)
                    return false;
            }


            for (PlayerClass playerClass : playerData.getClasses())
                if (!playerClass.getData().canUse(item.getType()))
                    return false;

            if (skillReq != null)
                for (Map.Entry<String, Integer> entry : skillReq.entrySet())
                    if (playerData.getSkillLevel(entry.getKey()) < entry.getValue())
                        return false;

            if (attrReq != null)
                for (Map.Entry<String, Integer> entry : attrReq.entrySet())
                    if (playerData.getAttribute(entry.getKey()) < entry.getValue())
                        return false;

            return true;
        }

        private boolean matches(final Set<String> names, final PlayerClass playerClass) {
            if (playerClass == null) return false;

            FabledClass current = playerClass.getData();
            while (current != null) {
                if (names.contains(current.getName().toLowerCase())) {
                    return true;
                }
                current = current.getParent();
            }

            return false;
        }
    }

}
