package com.sucy.skill.api.player;

import com.sucy.skill.api.classes.RPGClass;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.enums.ManaCost;
import studio.magemonkey.fabled.api.enums.ManaSource;
import studio.magemonkey.fabled.api.enums.PointSource;
import studio.magemonkey.fabled.api.player.PlayerAttributeModifier;
import studio.magemonkey.fabled.api.player.PlayerSkillBar;
import studio.magemonkey.fabled.api.player.PlayerStatModifier;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.cast.PlayerCastBars;
import studio.magemonkey.fabled.cast.PlayerTextCastingData;
import studio.magemonkey.fabled.dynamic.EffectComponent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Player data wrapper for Fabled
 * @deprecated Use {@link studio.magemonkey.fabled.api.player.PlayerData} instead
 */
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class PlayerData {
    private final studio.magemonkey.fabled.api.player.PlayerData _data;

    public studio.magemonkey.fabled.api.player.PlayerData getWrapped() {
        return _data;
    }

    public Player getPlayer() {
        return _data.getPlayer();
    }

    public String getPlayerName() {
        return _data.getPlayerName();
    }

    public HashMap<String, Integer> getAttributes() {
        return _data.getAttributes();
    }

    public Map<String, Integer> getAttrUpStages() {
        return _data.getAttrUpStages();
    }

    public Map<String, Integer> getAttributeStageData() {
        return _data.getAttributeStageData();
    }

    public Set<studio.magemonkey.fabled.api.player.PlayerData.ExternallyAddedSkill> getExtSkills() {
        return _data.getExtSkills();
    }

    public Map<String, List<PlayerAttributeModifier>> getAttributesModifiers() {
        return _data.getAttributesModifiers();
    }

    public Map<String, List<PlayerStatModifier>> getStatModifiers() {
        return _data.getStatModifiers();
    }

    public Map<String, String> getPersistentData() {
        return _data.getPersistentData();
    }

    public Map<String, Long> getCooldownCache() {
        return _data.getCooldownCache();
    }

    public DataSection getExtraData() {
        return _data.getExtraData();
    }

    public UUID getUUID() {
        return _data.getUUID();
    }

    public PlayerSkillBar getSkillBar() {
        return _data.getSkillBar();
    }

    public PlayerCastBars getCastBars() {
        return _data.getCastBars();
    }

    public PlayerTextCastingData getTextCastingData() {
        return _data.getTextCastingData();
    }

    public int subtractHungerValue(final double amount) {
        return _data.subtractHungerValue(amount);
    }

    public void endInit() {
        _data.endInit();
    }

    public HashMap<String, Integer> getInvestedAttributes() {
        return _data.getInvestedAttributes();
    }

    public HashMap<String, Integer> getInvestedAttributesStages() {
        return _data.getInvestedAttributesStages();
    }

    public int getAttribute(String key) {
        return _data.getAttribute(key);
    }

    public int getInvestedAttribute(String key) {
        return _data.getInvestedAttribute(key);
    }

    public int getInvestedAttributeStage(String key) {
        return _data.getInvestedAttributeStage(key);
    }

    public boolean hasAttribute(String key) {
        return _data.hasAttribute(key);
    }

    public boolean upAttribute(String key) {
        return _data.upAttribute(key);
    }

    public int getAttributeUpCost(String key) {
        return _data.getAttributeUpCost(key);
    }

    public int getAttributeUpCost(String key, Integer modifier) {
        return _data.getAttributeUpCost(key, modifier);
    }

    public int getAttributeUpCost(String key, Integer from, Integer to) {
        return _data.getAttributeUpCost(key, from, to);
    }

    public boolean giveAttribute(String key, int amount) {
        return _data.giveAttribute(key, amount);
    }

    public void addStatModifier(String key, PlayerStatModifier modifier, boolean update) {
        _data.addStatModifier(key, modifier, update);
    }

    public List<PlayerStatModifier> getStatModifiers(String key) {
        return _data.getStatModifiers(key);
    }

    public void addAttributeModifier(String key, PlayerAttributeModifier modifier, boolean update) {
        _data.addAttributeModifier(key, modifier, update);
    }

    public List<PlayerAttributeModifier> getAttributeModifiers(String key) {
        return _data.getAttributeModifiers(key);
    }

    public boolean refundAttribute(String key) {
        return _data.refundAttribute(key, 1);
    }

    public boolean refundAttributes(String key) {
        return _data.resetAttribute(key, true);
    }

    public List<String> refundAttributes() {
        return _data.refundAttributes();
    }

    public int getAttributePoints() {
        return _data.getAttributePoints();
    }

    public void giveAttribPoints(int amount) {
        _data.giveAttribPoints(amount);
    }

    public double scaleStat(String stat, double baseValue) {
        return _data.scaleStat(stat, baseValue);
    }

    public double scaleStat(String stat, double defaultValue, double min, double max) {
        return _data.scaleStat(stat, defaultValue, min, max);
    }

    public double scaleDynamic(EffectComponent component, String key, double value) {
        return _data.scaleDynamic(component, key, value);
    }

    public boolean openAttributeMenu() {
        return _data.openAttributeMenu();
    }

    public Map<String, Integer> getAttributeData() {
        return _data.getAttributeData();
    }

    public boolean hasSkill(String name) {
        return _data.hasSkill(name);
    }

    public PlayerSkill getSkill(String name) {
        return new PlayerSkill(_data.getSkill(name));
    }

    public int getInvestedSkillPoints() {
        return _data.getInvestedSkillPoints();
    }

    public Collection<PlayerSkill> getSkills() {
        return _data.getSkills().stream().map(
                PlayerSkill::new
        ).collect(Collectors.toList());
    }

    public Set<studio.magemonkey.fabled.api.player.PlayerData.ExternallyAddedSkill> getExternallyAddedSkills() {
        return _data.getExternallyAddedSkills();
    }

    public int getSkillLevel(String name) {
        return _data.getSkillLevel(name);
    }

    public void giveSkill(Skill skill) {
        _data.giveSkill(skill);
    }

    public void giveSkill(Skill skill, PlayerClass parent) {
        _data.giveSkill(skill, parent.getRealClass());
    }

    public void addSkill(Skill skill, PlayerClass parent) {
        _data.addSkill(skill, parent.getRealClass());
    }

    public void addSkillExternally(Skill skill,
                                   PlayerClass parent,
                                   NamespacedKey namespacedKey,
                                   int level) {
        _data.addSkillExternally(skill, parent.getRealClass(), namespacedKey, level);
    }

    public void removeSkillExternally(Skill skill, NamespacedKey namespacedKey) {
        _data.removeSkillExternally(skill, namespacedKey);
    }

    public void autoLevel() {
        _data.autoLevel();
    }

    public boolean upgradeSkill(Skill skill) {
        return _data.upgradeSkill(skill);
    }

    public void forceUpSkill(PlayerSkill skill) {
        _data.forceUpSkill(skill.getWrapped());
    }

    public void forceUpSkill(PlayerSkill skill, int amount) {
        _data.forceUpSkill(skill.getWrapped(), amount);
    }

    public boolean downgradeSkill(Skill skill) {
        return _data.downgradeSkill(skill);
    }

    public void forceDownSkill(PlayerSkill skill) {
        _data.forceDownSkill(skill.getWrapped());
    }

    public void forceDownSkill(PlayerSkill skill, int amount) {
        _data.forceDownSkill(skill.getWrapped(), amount);
    }

    public void refundSkill(PlayerSkill skill) {
        _data.refundSkill(skill.getWrapped());
    }

    public void refundSkills() {
        _data.refundSkills();
    }

    public void showSkills() {
        _data.showSkills();
    }

    public boolean showDetails(Player player) {
        return _data.showDetails(player);
    }

    public boolean showProfession(Player player) {
        return _data.showProfession(player);
    }

    public boolean showSkills(Player player) {
        return _data.showSkills(player);
    }

    public boolean showSkills(Player player, PlayerClass playerClass) {
        return _data.showSkills(player, playerClass.getRealClass());
    }

    public String getShownClassName() {
        return _data.getShownClassName();
    }

    public boolean hasClass() {
        return _data.hasClass();
    }

    public boolean hasClass(String group) {
        return _data.hasClass(group);
    }

    public Collection<PlayerClass> getClasses() {
        return _data.getClasses().stream().map(PlayerClass::new).collect(Collectors.toList());
    }

    public PlayerClass getClass(String group) {
        return new PlayerClass(_data.getClass(group));
    }

    public @Nullable
    PlayerClass getMainClass() {
        return new PlayerClass(_data.getMainClass());
    }

    public PlayerClass setClass(@Nullable FabledClass previous,
                                FabledClass fabledClass,
                                boolean reset) {
        return new PlayerClass(_data.setClass(previous, fabledClass, reset));
    }

    public boolean isExactClass(RPGClass playerClass) {
        return _data.isExactClass(playerClass.getWrapped());
    }

    public boolean isClass(PlayerClass playerClass) {
        return _data.isClass(playerClass.getData().getWrapped());
    }

    public boolean canProfess(PlayerClass playerClass) {
        return _data.canProfess(playerClass.getData().getWrapped());
    }

    public int reset(String group, boolean toSubclass) {
        return _data.reset(group, toSubclass);
    }

    public void resetAll() {
        _data.resetAll();
    }

    public void resetAttribs() {
        _data.resetAttribs(true);
    }

    public boolean profess(FabledClass fabledClass) {
        return _data.profess(fabledClass);
    }

    /**
     * @deprecated Use {@link #giveExp(double, ExpSource)} instead, utilizing the {@link ExpSource} enum
     * instead of the legacy com.sucy.skill.api.enums.ExpSource enum
     */
    @Deprecated
    public void giveExp(double amount, com.sucy.skill.api.enums.ExpSource expSource) {
        giveExp(amount, ExpSource.valueOf(expSource.name()));
    }

    /**
     * Gives the player experience from the specified source
     *
     * @param amount amount of experience to give
     * @param source source of the experience
     */
    public void giveExp(double amount, ExpSource source) {
        _data.giveExp(amount, source);
    }

    public void giveExp(double amount, ExpSource source, boolean message) {
        _data.giveExp(amount, source, message);
    }

    public void loseExp(double amount, boolean percent, boolean changeLevel) {
        _data.loseExp(amount, percent, changeLevel);
    }

    public void loseExp() {
        _data.loseExp();
    }

    public boolean giveLevels(int amount, ExpSource source) {
        return _data.giveLevels(amount, source);
    }

    public void loseLevels(int amount) {
        _data.loseLevels(amount);
    }

    @Deprecated
    public void givePoints(int amount, ExpSource source) {
        _data.givePoints(amount, source);
    }

    public void givePoints(int amount, PointSource source) {
        _data.givePoints(amount, source);
    }

    public void setPoints(int amount) {
        _data.setPoints(amount);
    }

    public void updatePlayerStat(Player player) {
        _data.updatePlayerStat(player);
    }

    public void updateWalkSpeed(Player player) {
        _data.updateWalkSpeed(player);
    }

    public void updateHealth(Player player) {
        _data.updateHealth(player);
    }

    public void regenMana() {
        _data.regenMana();
    }

    public void giveMana(double amount) {
        _data.giveMana(amount);
    }

    public void giveMana(double amount, ManaSource source) {
        _data.giveMana(amount, source);
    }

    public void useMana(double amount) {
        _data.useMana(amount);
    }

    public void useMana(double amount, ManaCost cost) {
        _data.useMana(amount, cost);
    }

    public double getMana() {
        return _data.getMana();
    }

    public double getMaxMana() {
        return _data.getMaxMana();
    }

    public void removeStatModifier(UUID uuid, boolean update) {
        _data.removeStatModifier(uuid, update);
    }

    public void clearStatModifier() {
        _data.clearStatModifier();
    }

    public void removeAttributeModifier(UUID uuid, boolean update) {
        _data.removeAttributeModifier(uuid, update);
    }

    public void clearAttributeModifiers() {
        _data.clearAttributeModifiers();
    }

    public void clearAllModifiers() {
        _data.clearAllModifiers();
    }

    @Deprecated
    public PlayerSkill getBoundSkill(Material mat) {
        return new PlayerSkill(_data.getBoundSkill(mat));
    }

    @Deprecated
    public HashMap<Material, PlayerSkill> getBinds() {
        Map<Material, studio.magemonkey.fabled.api.player.PlayerSkill> rawSkills = _data.getBinds();
        // Wrap into a new map
        HashMap<Material, PlayerSkill> skills = new HashMap<>();
        for (Map.Entry<Material, studio.magemonkey.fabled.api.player.PlayerSkill> entry : rawSkills.entrySet()) {
            skills.put(entry.getKey(), new PlayerSkill(entry.getValue()));
        }

        return skills;
    }

    @Deprecated
    public boolean isBound(Material mat) {
        return _data.isBound(mat);
    }

    @Deprecated
    public boolean bind(Material mat, PlayerSkill skill) {
        return _data.bind(mat, skill.getWrapped());
    }

    @Deprecated
    public boolean clearBind(Material mat) {
        return _data.clearBind(mat);
    }

    public Object getPersistentData(String key) {
        return _data.getPersistentData(key);
    }

    public void setPersistentData(String key, Object data) {
        _data.setPersistentData(key, data);
    }

    public void removePersistentData(String key) {
        _data.removePersistentData(key);
    }

    public Map<String, String> getAllPersistentData() {
        return _data.getAllPersistentData();
    }

    @Deprecated
    public void clearBinds(Skill skill) {
        _data.clearBinds(skill);
    }

    @Deprecated
    public void clearAllBinds() {
        _data.clearAllBinds();
    }

    public void record(Player player) {
        _data.record(player);
    }

    public void updateScoreboard() {
        _data.updateScoreboard();
    }

    public void startPassives(Player player) {
        _data.startPassives(player);
    }

    public void stopPassives(Player player) {
        _data.stopSkills(player);
    }

    public boolean cast(String skillName) {
        return _data.cast(skillName);
    }

    public boolean cast(PlayerSkill skill) {
        return _data.cast(skill.getWrapped());
    }

    public boolean check(PlayerSkill skill, boolean cooldown, boolean mana) {
        return _data.check(skill.getWrapped(), cooldown, mana);
    }

    public void setOnPreviewStop(@Nullable Runnable onPreviewStop) {
        _data.setOnPreviewStop(onPreviewStop);
    }

    public void init(Player player) {
        _data.init(player);
    }

    public boolean hasMana(double amount) {
        return _data.hasMana(amount);
    }
}
