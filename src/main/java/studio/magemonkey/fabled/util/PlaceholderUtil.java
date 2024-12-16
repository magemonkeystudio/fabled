package studio.magemonkey.fabled.util;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import io.lumine.mythic.bukkit.utils.interfaces.TriFunction;
import studio.magemonkey.codex.util.AttributeUT;
import studio.magemonkey.codex.util.StringUT;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.cast.PlayerTextCastingData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.PlaceholderAPIHook;
import studio.magemonkey.fabled.hook.PluginChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlaceholderUtil {

    private static Map<String, TriFunction<OfflinePlayer, List<String>, Integer, String>> actions = new HashMap<>(); 
    
    static {
        // Class Placeholders
        actions.put("level", PlaceholderUtil::levelPlaceholder);
        actions.put("maxlevel", PlaceholderUtil::maxLevelPlaceholder);
        actions.put("totallevel", PlaceholderUtil::totalLevelPlaceholder);
        actions.put("totalmaxlevel", PlaceholderUtil::totalMaxLevelPlaceholder);
        actions.put("prefix", PlaceholderUtil::prefixPlaceholder);
        actions.put("fprefix", PlaceholderUtil::formatPrefixPlaceholder);
        actions.put("class", PlaceholderUtil::classPlaceholder);
        actions.put("fclass", PlaceholderUtil::formatClassPlaceholder);
        actions.put("parent", PlaceholderUtil::parentPlaceholder);
        actions.put("children", PlaceholderUtil::childrenPlaceholder);
        actions.put("fchildren", PlaceholderUtil::formatChildrenPlaceholder);
        // // Health Placeholders
        actions.put("health", PlaceholderUtil::healthPlaceholder);
        actions.put("fhealth", PlaceholderUtil::formatHealthPlaceholder);
        actions.put("maxhealth", PlaceholderUtil::maxHealthPlaceholder);
        actions.put("fmaxhealth", PlaceholderUtil::formatMaxHealthPlaceholder);
        actions.put("basehealth", PlaceholderUtil::baseHealthPlaceholder);
        actions.put("fbasehealth", PlaceholderUtil::formatBaseHealthPlaceholder);
        actions.put("healthat", PlaceholderUtil::healthAtPlaceholder);
        actions.put("fhealthat", PlaceholderUtil::formatHealthAtPlaceholder);
        actions.put("healthscale", PlaceholderUtil::healthScalePlaceholder);
        // // Mana Placeholders
        actions.put("mana", PlaceholderUtil::manaPlaceholder);
        actions.put("fmana", PlaceholderUtil::formatManaPlaceholder);
        actions.put("maxmana", PlaceholderUtil::maxManaPlaceholder);
        actions.put("fmaxmana", PlaceholderUtil::formatMaxManaPlaceholder);
        actions.put("mananame", PlaceholderUtil::manaNamePlaceholder);
        actions.put("fmananame", PlaceholderUtil::formatManaNamePlaceholder);
        actions.put("basemana", PlaceholderUtil::baseManaPlaceholder);
        actions.put("fbasemana", PlaceholderUtil::formatBaseManaPlaceholder);
        actions.put("manaregen", PlaceholderUtil::manaRegenPlaceholder);
        actions.put("fmanaregen", PlaceholderUtil::formatManaRegenPlaceholder);
        actions.put("manaat", PlaceholderUtil::manaAtPlaceholder);
        actions.put("fmanaat", PlaceholderUtil::formatManaAtPlaceholder);
        actions.put("manascale", PlaceholderUtil::manaScalePlaceholder);
        // // Leveling Placeholders
        actions.put("attribute", PlaceholderUtil::attributePlaceholder);
        actions.put("attributepoints", PlaceholderUtil::attributePointsPlaceholder);
        actions.put("currentexp", PlaceholderUtil::currentExpPlaceholder);
        actions.put("fcurrentexp", PlaceholderUtil::formatCurrentExpPlaceholder);
        actions.put("requiredexp", PlaceholderUtil::requiredExpPlaceholder);
        actions.put("frequiredexp", PlaceholderUtil::formatRequiredExpPlaceholder);
        actions.put("requiredexpat", PlaceholderUtil::requiredExperienceAtPlaceholder);
        actions.put("frequiredexpat", PlaceholderUtil::formatRequiredExperienceAtPlaceholder);
        actions.put("skillpoints", PlaceholderUtil::skillPointsPlaceholder);
        // // Accounts Placeholders
        actions.put("accounts", PlaceholderUtil::accountsPlaceholder);
        actions.put("accountlimit", PlaceholderUtil::accountLimitPlaceholder);
        actions.put("currentaccount", PlaceholderUtil::currentAccountPlaceholder);
        actions.put("accountinfo", PlaceholderUtil::accountInfoPlaceholder);
        // Variable Placeholders
        actions.put("flag", PlaceholderUtil::flagPlaceholder);
        actions.put("flagleft", PlaceholderUtil::flagleftPlaceholder);
        actions.put("value", PlaceholderUtil::valuePlaceholder);
        // // SKill Placeholders
        actions.put("skilllevel", PlaceholderUtil::skillLevelPlaceholder);
        actions.put("casting", PlaceholderUtil::castingPlaceholder);
        actions.put("manacost", PlaceholderUtil::manaCostPlaceholder);
        actions.put("cooldownleft", PlaceholderUtil::cooldownLeftPlaceholder);
        actions.put("actionbar", PlaceholderUtil::actionBarPlaceholder);
        actions.put("actionbarmanacost", PlaceholderUtil::actionBarManaCostPlaceholder);
        actions.put("actionbarcooldownleft", PlaceholderUtil::actionBarCooldownLeftPlaceholder);
    }

    @NotNull
    public static String colorizeAndReplace(@NotNull String str, OfflinePlayer player) {
        // If we have PlaceholderAPI, use it, otherwise we can default
        // to our internal supplied placeholders
        if (PluginChecker.isPlaceholderAPIActive()) {
            return StringUT.color(PlaceholderAPIHook.format(str, player.getPlayer()));
        }
    
        Pattern regex = Pattern.compile("%fabled_(.*?)%");
        String  formattedLine = str;
        Matcher matcher       = regex.matcher(str);
        while (matcher.find()) {
            String match = matcher.group(1);
            String value = replace(player, match);
            if (value != null) {
                formattedLine = formattedLine.replace("%fabled_" + match + "%", value);
            }
        }
        return StringUT.color(formattedLine);
    }

    @NotNull
    public static List<String> colorizeAndReplace(List<String> list, OfflinePlayer player) {
        return list.stream()
                .map(line -> colorizeAndReplace(line, player))
                .collect(Collectors.toList());
    }

    public static String replace(OfflinePlayer player, String identifier) {
        List<String> arguments = new ArrayList<String>(Arrays.asList(identifier.toLowerCase().strip().split("_")));
        String placeholder = arguments.remove(0);
        try {
            return actions.getOrDefault(placeholder, (a, b, c) -> {return null;}).apply(player, arguments, null);
        } catch (Exception e) {
            return null;
        }
    }

    // Class
    public static String levelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            // This accounts for names that may have underscores present.
            String group = String.join("_",arguments);
            return String.valueOf(playerData.getClass(group).getLevel());
        }
        return String.valueOf(playerData.getMainClass().getLevel());
    }

    public static String maxLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return String.valueOf(playerData.getClass(group).getData().getMaxLevel());
        }
        return String.valueOf(playerData.getMainClass().getData().getMaxLevel());
    }

    public static String totalLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        Collection<PlayerClass> playerClasses = playerData.getClasses();
        int totalLevel = 0;
        for (PlayerClass playerClass : playerClasses) {
            totalLevel += playerClass.getLevel();
        }
        return String.valueOf(totalLevel);
    }

    public static String totalMaxLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        Collection<PlayerClass> playerClasses = playerData.getClasses();
        int totalMaxLevel = 0;
        for (PlayerClass playerClass : playerClasses) {
            totalMaxLevel += playerClass.getData().getMaxLevel();
        }
        return String.valueOf(totalMaxLevel);
    }

    private static String prefixPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return playerData.getClass(group).getData().getPrefix();
        }
        return playerData.getMainClass().getData().getPrefix();
    }

    private static String formatPrefixPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return ChatColor.stripColor(playerData.getClass(group).getData().getPrefix());
        }
        return ChatColor.stripColor(playerData.getMainClass().getData().getPrefix());
    }

    
    private static String classPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return playerData.getClass(group).getData().getName();
        }
        return playerData.getMainClass().getData().getName();
    }

    private static String formatClassPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return ChatColor.stripColor(playerData.getClass(group).getData().getName());
        }
        return ChatColor.stripColor(playerData.getMainClass().getData().getName());
    }

    private static String parentPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return playerData.getClass(group).getData().getParent().getName();
        }
        return playerData.getMainClass().getData().getParent().getName();
    }

    private static String childrenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        FabledClass playerClass = playerData.getMainClass().getData();
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            playerClass = playerData.getClass(group).getData();
        }
        ArrayList<String> children = new ArrayList<String>();
        for (FabledClass classes: Fabled.getClasses().values()) {
            String classname = classes.getName();

            if (classes.hasParent()) {
                if (classes.getParent().getName().equalsIgnoreCase(playerClass.getName())) {
                    children.add(classname);
                }
            }
        }
        return children.toString();
    }

    private static String formatChildrenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        FabledClass playerClass = playerData.getMainClass().getData();
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            playerClass = playerData.getClass(group).getData();
        }
        ArrayList<String> children = new ArrayList<String>();
        for (FabledClass classes: Fabled.getClasses().values()) {
            String classname = classes.getName();

            if (classes.hasParent()) {
                if (classes.getParent().getName().equalsIgnoreCase(playerClass.getName())) {
                    children.add(classname);
                }
            }
        }
        return children.toString().replaceAll("(^\\[|\\]$)", "");
    }

    //Health
    private static String healthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        return String.valueOf(player.getPlayer().getHealth());
    }

    private static String formatHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        return String.valueOf((int) player.getPlayer().getHealth());
    }

    private static String maxHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        return String.valueOf(player.getPlayer().getAttribute(AttributeUT.resolve("MAX_HEALTH")).getBaseValue());
    }

    private static String formatMaxHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        return String.valueOf((int) player.getPlayer().getAttribute(AttributeUT.resolve("MAX_HEALTH")).getBaseValue());
    }

    private static String baseHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return String.valueOf(playerData.getClass(group).getData().getBaseHealth());
        }
        return String.valueOf(playerData.getMainClass().getData().getBaseHealth());
    }

    private static String formatBaseHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_",arguments);
            return String.valueOf((int) playerData.getClass(group).getData().getBaseHealth());
        }
        return String.valueOf((int) playerData.getMainClass().getData().getBaseHealth());
    }

    private static String healthAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        int level = Integer.parseInt(arguments.remove(0));
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getHealth(level));
        }
        return String.valueOf(playerData.getMainClass().getData().getHealth(level));
    }

    private static String formatHealthAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        int level = Integer.parseInt(arguments.remove(0));
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int) playerData.getClass(group).getData().getHealth(level));
        }
        return String.valueOf((int) playerData.getMainClass().getData().getHealth(level));
    }

    private static String healthScalePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getHealthScale());
        }
        return String.valueOf(playerData.getMainClass().getData().getHealthScale());
    }

    //Mana
    private static String manaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        return String.valueOf(playerData.getMana());
    } 

    private static String formatManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        return String.valueOf((int) playerData.getMana());
    } 

    private static String maxManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        return String.valueOf(playerData.getMaxMana());
    }

    private static String formatMaxManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        return String.valueOf((int) playerData.getMaxMana());
    } 

    private static String manaNamePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return playerData.getClass(group).getData().getManaName();
        }
        return playerData.getMainClass().getData().getManaName();
    }

    private static String formatManaNamePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return ChatColor.stripColor(playerData.getClass(group).getData().getManaName());
        }
        return ChatColor.stripColor(playerData.getMainClass().getData().getManaName());
    }

    private static String baseManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        Boolean formatted = arguments.remove(0).equalsIgnoreCase("y");
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getBaseMana());
        }
        return String.valueOf(playerData.getMainClass().getData().getBaseMana());
    }

    
    private static String formatBaseManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int) playerData.getClass(group).getData().getBaseMana());
        }
        return String.valueOf((int) playerData.getMainClass().getData().getBaseMana());
    }

    private static String manaRegenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getManaRegen());
        }
        return String.valueOf(playerData.getMainClass().getData().getManaRegen());
    }

    private static String formatManaRegenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int) playerData.getClass(group).getData().getManaRegen());
        }
        return String.valueOf((int) playerData.getMainClass().getData().getManaRegen());
    }

    private static String manaAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        int level = Integer.parseInt(arguments.remove(0));
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getMana(level));
        }
        return String.valueOf(playerData.getMainClass().getData().getMana(level));
    }

    private static String formatManaAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        int level = Integer.parseInt(arguments.remove(0));
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int) playerData.getClass(group).getData().getMana(level));
        }
        return String.valueOf((int) playerData.getMainClass().getData().getMana(level));
    }

    private static String manaScalePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getManaScale());
        }
        return String.valueOf(playerData.getMainClass().getData().getManaScale());
    }

    //Leveling
    private static String attributePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String attribute = String.join("_", arguments);
            return String.valueOf(playerData.getAttribute(attribute));
        }
        return String.valueOf(playerData.getInvestedAttributes().values().stream().mapToInt(Integer::intValue).sum());
    } 

    private static String attributePointsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        return String.valueOf(playerData.getAttributePoints());
    } 

    private static String currentExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getExp());
        }
        return String.valueOf(playerData.getMainClass().getExp());
    } 

    private static String formatCurrentExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int) playerData.getClass(group).getExp());
        }
        return String.valueOf((int) playerData.getMainClass().getExp());
    } 
    
    private static String requiredExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getRequiredExp());
        }
        return String.valueOf(playerData.getMainClass().getRequiredExp());
    }

    private static String formatRequiredExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int)playerData.getClass(group).getRequiredExp());
        }
        return String.valueOf((int)playerData.getMainClass().getRequiredExp());
    } 

    private static String requiredExperienceAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        Boolean formatted = arguments.remove(0).equalsIgnoreCase("y");
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        int level = Integer.parseInt(arguments.remove(0));
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getData().getRequiredExp(level));
        }
        return String.valueOf(playerData.getMainClass().getData().getRequiredExp(level));
    }

    private static String formatRequiredExperienceAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        int level = Integer.parseInt(arguments.remove(0));
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf((int) playerData.getClass(group).getData().getRequiredExp(level));
        }
        return String.valueOf((int) playerData.getMainClass().getData().getRequiredExp(level));
    }
    
    private static String skillPointsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        if (!arguments.isEmpty()){
            String group = String.join("_", arguments);
            return String.valueOf(playerData.getClass(group).getPoints());
        }
        return String.valueOf(playerData.getMainClass().getPoints());
    }

    //Accounts

    private static String accountsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerAccounts playerData = Fabled.getPlayerAccounts(player);
        return String.valueOf(playerData.getAllData().size());
    }

    private static String accountLimitPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerAccounts playerData = Fabled.getPlayerAccounts(player);
        return String.valueOf(playerData.getAccountLimit());
    }

    private static String currentAccountPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerAccounts playerData = Fabled.getPlayerAccounts(player);
        return String.valueOf(playerData.getActiveId());
    }

    private static String accountInfoPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        Integer id = Integer.valueOf(arguments.remove(0));
        return actions.getOrDefault(arguments.remove(0), (a, b, c) -> {return null;}).apply(player, arguments, id);
    }


    //Variables

    private static String flagPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        String flag = String.join("_", arguments);
        return String.valueOf(FlagManager.hasFlag((LivingEntity) player, flag));
    } 

    private static String flagleftPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        String flag = String.join("_", arguments);
        return String.valueOf(FlagManager.getTimeLeft((LivingEntity) player, flag));
    }

    private static String valuePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        CastData data = DynamicSkill.getCastData((LivingEntity) player);
        String value = String.join("_", arguments);
        return String.valueOf(data.getDouble(value));
    }

    // Skills

    private static String skillLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        String skill = String.join("_",arguments);
        return String.valueOf(playerData.getSkill(skill).getLevel());
    }

    private static String castingPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerTextCastingData skillData = Fabled.getData(player).getTextCastingData();
        return String.valueOf(skillData.isCasting());
    }

    private static String manaCostPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        String skill = String.join("_",arguments);
        return String.valueOf(playerData.getSkill(skill).getManaCost());
    }

    private static String cooldownLeftPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        if (accountID != null){
            playerData = Fabled.getPlayerAccounts(player).getData(accountID);
        }
        String skill = String.join("_",arguments);
        return String.valueOf(playerData.getSkill(skill).getCooldownLeft());
    }

    private static String actionBarPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerTextCastingData skillData = Fabled.getData(player).getTextCastingData();
        return skillData.getSkill(Integer.parseInt(arguments.remove(0)));
    }

    private static String actionBarManaCostPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        PlayerTextCastingData skillData = Fabled.getData(player).getTextCastingData();
        String slot = String.valueOf(arguments.remove(0));
        String skill = skillData.getSkill(Integer.parseInt(slot));
        return String.valueOf(playerData.getSkill(skill).getManaCost());
    }

    private static String actionBarCooldownLeftPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountID){
        PlayerData playerData = Fabled.getData(player);
        PlayerTextCastingData skillData = Fabled.getData(player).getTextCastingData();
        String slot = String.valueOf(arguments.remove(0));
        String skill = skillData.getSkill(Integer.parseInt(slot));
        return String.valueOf(playerData.getSkill(skill).getCooldownLeft());
    }

}
