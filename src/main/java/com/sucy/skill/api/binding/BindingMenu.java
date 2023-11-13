package com.sucy.skill.api.binding;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.listener.BindListener;
import mc.promcteam.engine.manager.api.menu.Menu;
import mc.promcteam.engine.manager.api.menu.Slot;
import mc.promcteam.engine.manager.api.menu.YAMLMenu;
import mc.promcteam.engine.mccore.util.TextFormatter;
import mc.promcteam.engine.utils.StringUT;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BindingMenu extends Menu {
    public static final NamespacedKey        SKILL_KEY = new NamespacedKey(SkillAPI.inst(), "skill");
    public static final  YAMLMenu<ItemStack> CONFIG    = new YAMLMenu<>(SkillAPI.inst(), "menus/binding.yml") {

        @Override
        protected String getTitle(String yamlTitle, @NotNull ItemStack itemStack) {
            ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
            return yamlTitle.replace("%item%", meta.hasDisplayName()
                    ? meta.getDisplayName() : TextFormatter.format(itemStack.getType().name()));
        }

        @Override
        public Slot getSlot(String key, ItemStack item, Player player) {
            ItemStack configItem = this.getItem(key);
            return switch (key) {
                case "clear" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        BindListener.setBoundSkills(item, null);
                        this.menu.open();
                    }
                };
                case "bound-prev-page" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        ((BindingMenu) this.menu).boundPage -= 1;
                        this.menu.open();
                    }
                };
                case "bound-next-page" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        ((BindingMenu) this.menu).boundPage += 1;
                        this.menu.open();
                    }
                };
                case "bound" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        List<String> inItem = BindListener.getBoundSkills(item);

                        String currentSkill = Objects.requireNonNull(getItemStack().getItemMeta()).getPersistentDataContainer().get(SKILL_KEY, PersistentDataType.STRING);
                        int current = inItem.indexOf(currentSkill);
                        if (current <= 0) {
                            this.menu.open();
                            return;
                        }
                        int prev = current - 1;
                        String prevSkill = inItem.get(prev);

                        inItem.set(current, prevSkill);
                        inItem.set(prev, currentSkill);
                        BindListener.setBoundSkills(item, inItem);
                        this.menu.open();
                    }

                    @Override
                    public void onRightClick() {
                        List<String> inItem = BindListener.getBoundSkills(item);

                        String currentSkill = Objects.requireNonNull(getItemStack().getItemMeta()).getPersistentDataContainer().get(SKILL_KEY, PersistentDataType.STRING);
                        int current = inItem.indexOf(currentSkill);
                        if (current < 0 || current == inItem.size() - 1) {
                            this.menu.open();
                            return;
                        }
                        int next = current + 1;
                        String nextSkill = inItem.get(next);

                        inItem.set(current, nextSkill);
                        inItem.set(next, currentSkill);
                        BindListener.setBoundSkills(item, inItem);
                        this.menu.open();
                    }

                    @Override
                    public void onDrop() {
                        List<String> inItem = BindListener.getBoundSkills(item);
                        inItem.remove(Objects.requireNonNull(getItemStack().getItemMeta()).getPersistentDataContainer().get(SKILL_KEY, PersistentDataType.STRING));
                        BindListener.setBoundSkills(item, inItem);
                        this.menu.open();
                    }
                };
                case "not-bound-prev-page" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        ((BindingMenu) this.menu).notBoundPage -= 1;
                        this.menu.open();
                    }
                };
                case "not-bound-next-page" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        ((BindingMenu) this.menu).notBoundPage += 1;
                        this.menu.open();
                    }
                };
                case "not-bound" -> new Slot(configItem) {
                    @Override
                    public void onLeftClick() {
                        String skill = Objects.requireNonNull(getItemStack().getItemMeta()).getPersistentDataContainer().get(SKILL_KEY, PersistentDataType.STRING);
                        List<String> inItem = BindListener.getBoundSkills(item);
                        if (!inItem.contains(skill)) {
                            inItem.add(skill);
                            BindListener.setBoundSkills(item, inItem);
                        }
                        this.menu.open();
                    }
                };
                default -> new Slot(configItem);
            };
            //if (slot.getType() == Material.JACK_O_LANTERN && (key.equalsIgnoreCase("bound") || key.equalsIgnoreCase("not-bound"))) {
        }
    };

    private final ItemStack itemStack;
    private       int       boundPage = 0;
    private       int       notBoundPage = 0;

    public BindingMenu(Player player, ItemStack itemStack) {
        super(player, CONFIG.getRows(), CONFIG.getTitle(itemStack));
        this.itemStack = itemStack;
    }

    @Override
    public void setContents() {
        registerListener(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getView().getTopInventory().getHolder() != BindingMenu.this) return;
                if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    event.setCancelled(true);
                } else switch (event.getClick()) {
                    case NUMBER_KEY, DOUBLE_CLICK, SWAP_OFFHAND -> event.setCancelled(true);
                }
            }
        });

        PlayerData playerData = SkillAPI.getPlayerData(this.player);
        List<PlayerSkill> boundSkills = BindListener.getBoundSkills(itemStack, playerData);
        BindListener.setBoundSkills(itemStack, boundSkills.stream().map(skill -> skill.getData().getKey()).collect(Collectors.toList()));
        List<PlayerSkill> notBoundSkills = playerData.getSkills().stream().filter(skill -> skill.getLevel() > 0 && !boundSkills.contains(skill)).collect(Collectors.toList());

        int boundSlots = 0;
        int notBoundSlots = 0;
        for (Map.Entry<Integer, String> entry : CONFIG.getSlots().entrySet()) {
            if (entry.getKey() >= this.inventory.getSize()) return;
            if (entry.getValue().equalsIgnoreCase("bound")) {
                boundSlots += 1;
            } else if (entry.getValue().equalsIgnoreCase("not-bound")) {
                notBoundSlots += 1;
            }
        }
        int boundPages = boundSkills.size()/(boundSlots+1)+1;
        int notBoundPages = notBoundSkills.size()/(notBoundSlots+1)+1;
        this.boundPage = ((this.boundPage%boundPages)+boundPages)%boundPages;
        this.notBoundPage = ((this.notBoundPage%notBoundPages)+notBoundPages)%notBoundPages;

        int bound = this.boundPage*boundSlots;
        int notBound = this.notBoundPage*notBoundSlots;
        for (Map.Entry<Integer, String> entry : CONFIG.getSlots().entrySet()) {
            if (entry.getKey() >= this.inventory.getSize()) return;
            Slot slot = CONFIG.getSlot(entry.getValue(), itemStack, this.player);
            if (slot == null) continue;
            switch (entry.getValue()) {
                case "bound-prev-page", "bound-next-page" -> {
                    if (boundPages <= 1) continue;
                }
                case "bound" -> {
                    if (bound >= boundSkills.size()) continue;
                    PlayerSkill skill = boundSkills.get(bound++);
                    ItemStack slotItemStack = slot.getItemStack();
                    ItemMeta meta = slotItemStack.getItemMeta();
                    if (slotItemStack.getType().equals(Material.JACK_O_LANTERN)) {
                        ItemStack indicator = skill.getData().getIndicator(skill, false);
                        slotItemStack.setType(indicator.getType());
                        slotItemStack.setAmount(indicator.getAmount());
                        ItemMeta indicatorMeta = indicator.getItemMeta();
                        if (meta != null && indicatorMeta != null) {
                            meta.setDisplayName(meta.getDisplayName().replace("%display-name%", indicatorMeta.getDisplayName()));
                            meta.setLore(StringUT.replace(
                                    meta.hasLore() ? Objects.requireNonNull(meta.getLore()) : new ArrayList<>(),
                                    "%lore%",
                                    indicatorMeta.hasLore() ? Objects.requireNonNull(indicatorMeta.getLore()) : new ArrayList<>()));
                            if (indicatorMeta.hasCustomModelData()) meta.setCustomModelData(indicatorMeta.getCustomModelData());
                        }
                        if (meta != null) {
                            meta.getPersistentDataContainer().set(SKILL_KEY, PersistentDataType.STRING, skill.getData().getKey());
                            slotItemStack.setItemMeta(meta);
                        }
                    }
                    slot.setItemStack(slotItemStack);
                }
                case "not-bound-prev-page", "not-bound-next-page" -> {
                    if (notBoundPages <= 1) continue;
                }
                case "not-bound" -> {
                    if (notBound >= notBoundSkills.size()) continue;
                    PlayerSkill skill = notBoundSkills.get(notBound++);
                    ItemStack slotItemStack = slot.getItemStack();
                    ItemMeta meta = slotItemStack.getItemMeta();
                    if (slotItemStack.getType().equals(Material.JACK_O_LANTERN)) {
                        ItemStack indicator = skill.getData().getIndicator(skill, false);
                        slotItemStack.setType(indicator.getType());
                        slotItemStack.setAmount(indicator.getAmount());
                        ItemMeta indicatorMeta = indicator.getItemMeta();
                        if (meta != null && indicatorMeta != null) {
                            meta.setDisplayName(meta.getDisplayName().replace("%display-name%", indicatorMeta.getDisplayName()));
                            meta.setLore(StringUT.replace(
                                    meta.hasLore() ? Objects.requireNonNull(meta.getLore()) : new ArrayList<>(),
                                    "%lore%",
                                    indicatorMeta.hasLore() ? Objects.requireNonNull(indicatorMeta.getLore()) : new ArrayList<>()));
                            if (indicatorMeta.hasCustomModelData()) meta.setCustomModelData(indicatorMeta.getCustomModelData());
                        }
                    }
                    if (meta != null) {
                        meta.getPersistentDataContainer().set(SKILL_KEY, PersistentDataType.STRING, skill.getData().getKey());
                        slotItemStack.setItemMeta(meta);
                    }
                    slot.setItemStack(slotItemStack);
                }
            }
            setSlot(entry.getKey(), slot);
        }
    }

    @Override
    public void onClose() {
        super.onClose();

        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) return;
        PlayerData playerData = SkillAPI.getPlayerData(player);
        playerData.setOnPreviewStop(null);
        List<PlayerSkill> boundSkills = BindListener.getBoundSkills(itemStack, playerData);
        if (boundSkills.isEmpty()) return;
        boundSkills.get(BindListener.getIndex(player, boundSkills.size())).startPreview();
    }
}
