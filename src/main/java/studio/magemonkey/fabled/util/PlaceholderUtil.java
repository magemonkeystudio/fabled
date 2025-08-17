package studio.magemonkey.fabled.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.compat.VersionManager;
import studio.magemonkey.codex.util.StringUT;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerAccounts;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.util.FlagData;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.cast.PlayerCastWheel;
import studio.magemonkey.fabled.cast.PlayerTextCastingData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.PlaceholderAPIHook;
import studio.magemonkey.fabled.hook.PluginChecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface PlaceholderFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}

public class PlaceholderUtil {
    private static final Map<String, PlaceholderFunction<OfflinePlayer, List<String>, Integer, String>> actions =
            new HashMap<>();

    private static long legacyMessageTime;

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
        actions.put("group", PlaceholderUtil::groupPlaceholder);
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
        actions.put("requiredexpat", PlaceholderUtil::requiredExpAtPlaceholder);
        actions.put("skillpoints", PlaceholderUtil::skillPointsPlaceholder);
        // // Accounts Placeholders
        actions.put("accounts", PlaceholderUtil::accountsPlaceholder);
        actions.put("accountlimit", PlaceholderUtil::accountLimitPlaceholder);
        actions.put("currentaccount", PlaceholderUtil::currentAccountPlaceholder);
        actions.put("accountinfo", PlaceholderUtil::accountInfoPlaceholder);
        // Variable Placeholders
        actions.put("flag", PlaceholderUtil::flagPlaceholder);
        actions.put("flagleft", PlaceholderUtil::flagLeftPlaceholder);
        actions.put("value", PlaceholderUtil::valuePlaceholder);
        actions.put("fvalue", PlaceholderUtil::formatValuePlaceholder);
        // // SKill Placeholders
        actions.put("skilllevel", PlaceholderUtil::skillLevelPlaceholder);
        actions.put("skillmaxlevel", PlaceholderUtil::skillMaxLevelPlaceholder);
        actions.put("skillrequiredlevel", PlaceholderUtil::skillMaxLevelPlaceholder);
        actions.put("skilltype", PlaceholderUtil::skillTypePlaceholder);
        actions.put("skillcost", PlaceholderUtil::skillCostPlaceholder);
        actions.put("skillmanacost", PlaceholderUtil::skillManaCostPlaceholder);
        actions.put("fskillmanacost", PlaceholderUtil::formatSkillManaCostPlaceholder);
        actions.put("skillcooldown", PlaceholderUtil::skillCooldownPlaceholder);
        actions.put("fskillcooldown", PlaceholderUtil::formatSkillCooldownPlaceholder);
        actions.put("skillcooldownleft", PlaceholderUtil::skillCooldownLeftPlaceholder);
        actions.put("skillmessage", PlaceholderUtil::skillMessagePlaceholder);
        actions.put("fskillmessage", PlaceholderUtil::formatSkillMessagePlaceholder);
        actions.put("skillmodeldata", PlaceholderUtil::skillModelDataPlaceholder);
        actions.put("skills", PlaceholderUtil::skillsPlaceholder);
        actions.put("fskills", PlaceholderUtil::formatSkillsPlaceholder);
        actions.put("skillsname", PlaceholderUtil::skillsNamePlaceholder);
        actions.put("skillsinfo", PlaceholderUtil::skillsInfoPlaceholder);
        actions.put("casting", PlaceholderUtil::castingPlaceholder);
        actions.put("castingname", PlaceholderUtil::castingNamePlaceholder);
        actions.put("castinginfo", PlaceholderUtil::castingInfoPlaceholder);
    }

    private static PlayerData getPlayerData(OfflinePlayer player, @Nullable Integer accountId) {
        return (accountId != null) ? Fabled.getPlayerAccounts(player).getData(accountId) : Fabled.getData(player);
    }

    private static PlayerClass getPlayerClass(OfflinePlayer player,
                                              List<String> arguments,
                                              @Nullable Integer accountId) {
        PlayerData playerData = getPlayerData(player, accountId);
        String     group      = arguments.isEmpty() ? null : String.join("_", arguments);
        return group != null ? playerData.getClass(group) : playerData.getMainClass();
    }

    @NotNull
    public static String colorizeAndReplace(@NotNull String str, OfflinePlayer player) {
        // If we have PlaceholderAPI, use it, otherwise we can default
        // to our internal supplied placeholders
        if (PluginChecker.isPlaceholderAPIActive()) {
            return StringUT.color(PlaceholderAPIHook.format(str, player.getPlayer()));
        }

        Pattern regex         = Pattern.compile("%fabled_(.*?)%");
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
        return list.stream().map(line -> colorizeAndReplace(line, player)).collect(Collectors.toList());
    }

    public static String replace(OfflinePlayer player, String identifier) {
        List<String> arguments   = new ArrayList<>(Arrays.asList(identifier.strip().split("_")));
        String       placeholder = arguments.remove(0);
        return actions.getOrDefault(placeholder, (a, b, c) -> {

            String legacyPlaceholder = getLegacyPlaceholder(player, identifier);
            if (legacyPlaceholder != null) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - legacyMessageTime > 300000) {
                    Fabled.inst()
                            .getLogger()
                            .warning("%fabled_" + identifier
                                    + "% is a deprecated Placeholder and will be removed at some point in the future. Please see the Wiki for updated Placeholders.\n"
                                    + "https://github.com/magemonkeystudio/fabled/wiki/placeholder-rework#legacy-placeholders");
                    legacyMessageTime = currentTime;
                }
            }
            return legacyPlaceholder;
        }).apply(player, arguments, null);
    }

    // ** Class ** //
    // Returns the level of the current class in the specified group. 0 if not found.
    public static String levelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return String.valueOf(playerClass.getLevel());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the max level of the current class in the specified group. 0 if not found.
    public static String maxLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return String.valueOf(playerClass.getData().getMaxLevel());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the total level of all classes. 0 if no classes are professed.
    public static String totalLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerData              playerData    = getPlayerData(player, accountId);
        Collection<PlayerClass> playerClasses = playerData.getClasses();
        int                     totalLevel    = 0;
        for (PlayerClass playerClass : playerClasses) {
            totalLevel += playerClass.getLevel();
        }
        return String.valueOf(totalLevel);
    }

    // Returns the total max level of all classes.
    public static String totalMaxLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerData              playerData    = getPlayerData(player, accountId);
        Collection<PlayerClass> playerClasses = playerData.getClasses();
        int                     totalMaxLevel = 0;
        for (PlayerClass playerClass : playerClasses) {
            totalMaxLevel += playerClass.getData().getMaxLevel();
        }
        return String.valueOf(totalMaxLevel);
    }

    // Returns the Prefix of the current class in the specified group. Blank if not found.
    private static String prefixPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return playerClass.getData().getPrefix();
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the Prefix of the current class in the specified group without its color. Blank if not found.
    private static String formatPrefixPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        return ChatColor.stripColor(prefixPlaceholder(player, arguments, accountId));
    }

    // Returns the Name of the current class in the specified group. Blank if not found.
    private static String classPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return playerClass.getData().getName();
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the Name of the current Class in the specified group without its color. Blank if not found.
    private static String formatClassPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        return ChatColor.stripColor(classPlaceholder(player, arguments, accountId));
    }

    // Returns the name of the main class Group. Blank if not found.
    private static String groupPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            return playerData.getMainClass().getData().getGroup();
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the Name of the Parent Class in the specified group. Blank if not found.
    private static String parentPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return playerClass.getData().getParent().getName();
        } catch (Exception e) {
            return "";
        }
    }

    // Returns all the Children of the current Class in the specified group. Emptry list if not found.
    private static String childrenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            FabledClass  playerClass = getPlayerClass(player, arguments, accountId).getData();
            List<String> children    = new ArrayList<>();
            for (FabledClass classes : Fabled.getClasses().values()) {
                String classname = classes.getName();
                if (classes.hasParent()) {
                    if (classes.getParent().getName().equalsIgnoreCase(playerClass.getName())) {
                        children.add(classname);
                    }
                }
            }
            return children.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    // Returns all the Children of the current Class in the specified group formatted as a nice list. Blank if not found.
    private static String formatChildrenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        return childrenPlaceholder(player, arguments, accountId).replaceAll("(^\\[|\\]$)", "");
    }

    // ** Health ** //
    // Returns the current health of the player, as a decimal.
    private static String healthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(player.getPlayer().getHealth());
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the current health of the player, as an integer.
    private static String formatHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) player.getPlayer().getHealth());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the max health of the player, as a decimal.
    private static String maxHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(player.getPlayer()
                    .getAttribute(VersionManager.getNms().getAttribute("MAX_HEALTH"))
                    .getBaseValue());
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the max health of the player, as an integer.
    private static String formatMaxHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) player.getPlayer()
                    .getAttribute(VersionManager.getNms().getAttribute("MAX_HEALTH"))
                    .getBaseValue());
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getBaseHealth(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getBaseHealth();
    }

    // Returns the base health of the current class or class in the specified group. 0.0 if not found.
    private static String baseHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getBaseHealth(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the base health of the current class or class in the specified group, as an integer. 0 if not found.
    private static String formatBaseHealthPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getBaseHealth(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getHealthAt(OfflinePlayer player, List<String> arguments, Integer accountId) {
        int         level       = Integer.parseInt(arguments.remove(0));
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getHealth(level);
    }

    // Returns the health value of the current class or class in the specified group at a specific level. 0.0 if not found.
    private static String healthAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getHealthAt(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the health value of the current class or class in the specified group at a specific level, as an integer. 0 if not found.
    private static String formatHealthAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getHealthAt(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the value in which health scales per level of the current class or class in the specified group. 0.0 if not found.
    private static String healthScalePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return String.valueOf((int) playerClass.getData().getHealthScale());
        } catch (Exception e) {
            return "0.0";
        }
    }

    // ** Mana ** //
    // Returns the amount of mana the player currently has. 0.0 if not found.
    private static String manaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = Fabled.getData(player);
            return String.valueOf(playerData.getMana());
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the amount of mana the player currently has, as an integer. 0 if not found.
    private static String formatManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = Fabled.getData(player);
            return String.valueOf((int) playerData.getMana());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the maximum amount of mana the player currently has. 0.0 if not found.
    private static String maxManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = Fabled.getData(player);
            return String.valueOf(playerData.getMaxMana());
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the maximum amount of mana the player currently has, as an integer. 0 if not found.
    private static String formatMaxManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = Fabled.getData(player);
            return String.valueOf((int) playerData.getMaxMana());
        } catch (Exception e) {
            return "0";
        }
    }

    private static String getManaName(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getManaName();
    }

    // Returns the mana name of the current class or class in the specified group. Blank if not found.
    private static String manaNamePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return getManaName(player, arguments, accountId);
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the mana name of the current class or class in the specified group, without its color. Blank if not found.
    private static String formatManaNamePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return ChatColor.stripColor(getManaName(player, arguments, accountId));
        } catch (Exception e) {
            return "";
        }
    }

    private static double getBaseMana(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getBaseMana();
    }

    // Returns the base mana of the current class or class in the specified group. 0.0 if not found.
    private static String baseManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getBaseMana(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the base mana of the current class or class in the specified group, as an integer. 0 if not found.
    private static String formatBaseManaPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getBaseMana(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getManaRegen(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getManaRegen();
    }

    // Returns the mana regen rate of the current class or class in the specified group. 0.0 if not found.
    private static String manaRegenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getManaRegen(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the mana regen rate of the current class or class in the specified group, as an integer. 0 if not found.
    private static String formatManaRegenPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getManaRegen(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getManaAt(OfflinePlayer player, List<String> arguments, Integer accountId) {
        int         level       = Integer.parseInt(arguments.remove(0));
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getMana(level);
    }

    // Returns the mana value of the current class or class in the specified group at a specific level. 0.0 if not found.
    private static String manaAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getManaAt(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the health value of the current class or class in the specified group at a specific level, as an integer. 0 if not found.
    private static String formatManaAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getManaAt(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the value in which mana scales per level of the current class or class in the specified group. 0.0 if not found.
    private static String manaScalePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return String.valueOf(playerClass.getData().getManaScale());
        } catch (Exception e) {
            return "0.0";
        }
    }

    // ** Leveling ** //
    // Return how many points the player has in the specified. If no attribute is specified, all invested points are shown. 0 if not found.
    private static String attributePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            String     attribute  = arguments.isEmpty() ? null : String.join("_", arguments);
            return String.valueOf(attribute != null ? playerData.getAttribute(attribute)
                    : playerData.getInvestedAttributes().values().stream().mapToInt(Integer::intValue).sum());
        } catch (Exception e) {
            return "0";
        }
    }

    // Return the number of unspent attribute points. 0 if not found.
    private static String attributePointsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            return String.valueOf(playerData.getAttributePoints());
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getCurrentExp(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getExp();
    }

    // Returns the current experience point for the specified group. 0.0 if not found.
    private static String currentExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getCurrentExp(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the current experience points for the specified group as an integer. 0 if not found.
    private static String formatCurrentExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getCurrentExp(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    private static int getRequiredExp(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getRequiredExp();
    }

    // Returns the required experience for the next level for the specified group. 0 if not found.
    private static String requiredExpPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getRequiredExp(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    private static int getRequiredExpAt(OfflinePlayer player, List<String> arguments, Integer accountId) {
        int         level       = Integer.parseInt(arguments.remove(0));
        PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
        return playerClass.getData().getRequiredExp(level);
    }

    // Returns the required experience for the specified level for a given group. 0 if not found.
    private static String requiredExpAtPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getRequiredExpAt(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the skill points for the specified group. 0 if not found.
    private static String skillPointsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerClass playerClass = getPlayerClass(player, arguments, accountId);
            return String.valueOf(playerClass.getPoints());
        } catch (Exception e) {
            return "0";
        }
    }

    //Accounts
    // Returns the number of accounts the player currently has. 0 if not available.
    private static String accountsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerAccounts playerData = Fabled.getPlayerAccounts(player);
            return String.valueOf(playerData.getAllData().size());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the maximum number of accounts the player can have. 0 if not available.
    private static String accountLimitPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerAccounts playerData = Fabled.getPlayerAccounts(player);
            return String.valueOf(playerData.getAccountLimit());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the value of the current account the player is using. 0 if not available.
    private static String currentAccountPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerAccounts playerData = Fabled.getPlayerAccounts(player);
            return String.valueOf(playerData.getActiveId());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the value of the specified placeholder for a specific account.
    // Proper usage %fabled_accountInfo_[id]_[fabled placeholder]%.
    // Example %fabled_accountInfo_1_class_race% 
    // This will return the class name of the race group for account number 1.
    private static String accountInfoPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        Integer id = Integer.valueOf(arguments.remove(0));
        return actions.getOrDefault(arguments.remove(0), (a, b, c) -> null).apply(player, arguments, id);
    }

    //Variables
    // Returns true if this flag is set, false otherwise.
    private static String flagPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            String flag = String.join("_", arguments);
            return String.valueOf(FlagManager.hasFlag((LivingEntity) player, flag));
        } catch (Exception e) {
            return "false";
        }
    }

    // Returns the remaining time the flag has. 0 if not set.
    private static String flagLeftPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            String flag = String.join("_", arguments);
            return String.valueOf(FlagManager.getTimeLeft((LivingEntity) player, flag));
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getValue(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            CastData data  = DynamicSkill.getCastData((LivingEntity) player);
            String   value = String.join("_", arguments);
            return data.getDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Returns the value stored. 0.0 if not found.
    private static String valuePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getValue(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the formatted value stored. 0 if not found.
    private static String formatValuePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf((int) getValue(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    // Skills
    // Returns the level of the specified skill. 0 if not found.
    private static String skillLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            String     skill      = String.join("_", arguments);
            return String.valueOf(playerData.getSkill(skill).getLevel());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the maximum level of the specified skill. 0 if not found.
    private static String skillMaxLevelPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            String     skill      = String.join("_", arguments);
            return String.valueOf(playerData.getSkill(skill).getData().getMaxLevel());
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the type of the specified skill. Empty string if not found.
    private static String skillTypePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            String     skill      = String.join("_", arguments);
            return String.valueOf(playerData.getSkill(skill).getData().getType());
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the cost in skill points to level up the specified skill. 0 if not found.
    private static String skillCostPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData  playerData = getPlayerData(player, accountId);
            String      skillName  = String.join("_", arguments);
            PlayerSkill skill      = playerData.getSkill(skillName);
            return String.valueOf(skill.getData().getCost(skill.getLevel()));
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getManaCost(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerData playerData = getPlayerData(player, accountId);
        String     skill      = String.join("_", arguments);
        return playerData.getSkill(skill).getManaCost();
    }

    // Returns the mana cost of a specified skill. 0.0 if not found.
    private static String skillManaCostPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getManaCost(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the mana cost of a specified skill, as an integer. 0 if not found.
    private static String formatSkillManaCostPlaceholder(OfflinePlayer player,
                                                         List<String> arguments,
                                                         Integer accountId) {
        try {
            return String.valueOf((int) getManaCost(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    private static double getSkillCooldown(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerData playerData = getPlayerData(player, accountId);
        String     skillName  = String.join("_", arguments);
        Skill      skill      = playerData.getSkill(skillName).getData();
        return skill.getCooldown(playerData.getSkillLevel(skillName));
    }

    // Returns the cooldown of a skill. 0.0 if not found.
    private static String skillCooldownPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return String.valueOf(getSkillCooldown(player, arguments, accountId));
        } catch (Exception e) {
            return "0.0";
        }
    }

    // Returns the cooldown of a skill, as an integer. 0 if not found.
    private static String formatSkillCooldownPlaceholder(OfflinePlayer player,
                                                         List<String> arguments,
                                                         Integer accountId) {
        try {
            return String.valueOf((int) getSkillCooldown(player, arguments, accountId));
        } catch (Exception e) {
            return "0";
        }
    }

    // Returns the cooldown remaining of a skill. 0 if not found.
    private static String skillCooldownLeftPlaceholder(OfflinePlayer player,
                                                       List<String> arguments,
                                                       Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            String     skill      = String.join("_", arguments);
            return String.valueOf(playerData.getSkill(skill).getCooldownLeft());
        } catch (Exception e) {
            return "0";
        }
    }

    private static String getSkillMessage(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerData playerData = getPlayerData(player, accountId);
        String     skill      = String.join("_", arguments);
        return playerData.getSkill(skill).getData().getMessage();
    }

    // Returns the cast message of a skill, empty string if not found.
    private static String skillMessagePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return getSkillMessage(player, arguments, accountId);
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the cast message of a skill without color codes, empty string if not found.
    private static String formatSkillMessagePlaceholder(OfflinePlayer player,
                                                        List<String> arguments,
                                                        Integer accountId) {
        try {
            return ChatColor.stripColor(getSkillMessage(player, arguments, accountId));
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the custom model data of the skill item, 0 if not found.
    private static String skillModelDataPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData playerData = getPlayerData(player, accountId);
            String     skill      = String.join("_", arguments);
            return String.valueOf(playerData.getSkill(skill)
                    .getData()
                    .getIcon(playerData)
                    .getItemMeta()
                    .getCustomModelData());
        } catch (Exception e) {
            return "0";
        }
    }

    private static List<String> getSkills(OfflinePlayer player, List<String> arguments, Integer accountId) {
        PlayerData   playerData = getPlayerData(player, accountId);
        List<String> skills     = new ArrayList<>();
        for (PlayerSkill skill : playerData.getSkills()) {
            skills.add(skill.getData().getName());
        }
        Collections.sort(skills);
        return skills;
    }

    // Returns a list of all the skills a player current has. [] if not found.
    private static String skillsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return getSkills(player, arguments, accountId).toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    // Returns a list of all the skills a player current has. "" if not found.
    private static String formatSkillsPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return getSkills(player, arguments, accountId).toString().replaceAll("(^\\[|\\]$)", "");
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the name of a skill at the specified location. "" if not found.
    private static String skillsNamePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            return getSkills(player, arguments, accountId).get(Integer.parseInt(arguments.remove(0)) - 1);
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the information of a skill at the specified location and given placeholder. "" if not found.
    private static String skillsInfoPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            arguments.add(getSkills(player, arguments, accountId).get(Integer.parseInt(arguments.remove(0)) - 1));
            return actions.getOrDefault(arguments.remove(0), (a, b, c) -> null).apply(player, arguments, accountId);
        } catch (Exception e) {
            return "";
        }
    }

    // Returns true if the player is skill casting. False otherwise.
    private static String castingPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData            playerData = getPlayerData(player, accountId);
            PlayerTextCastingData textData   = playerData.getTextCastingData();
            PlayerCastWheel       wheelData  = playerData.getCastWheel();
            return String.valueOf(textData.isCasting() || wheelData.isCasting());
        } catch (Exception e) {
            return "false";
        }
    }

    // Returns the name of the skill found in a specified slot if the ACTION_BAR is being used. Must be 1-8. Blank if not found.
    private static String castingNamePlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData            playerData = getPlayerData(player, accountId);
            PlayerTextCastingData skillData  = playerData.getTextCastingData();
            String                skillName  = skillData.getSkill(Integer.parseInt(arguments.remove(0)));
            return skillName != null ? skillName : "";
        } catch (Exception e) {
            return "";
        }
    }

    // Returns the requested info of the skill found in a specified slot if the ACTION_BAR is being used. Must be 1-8. Blank if not found.
    private static String castingInfoPlaceholder(OfflinePlayer player, List<String> arguments, Integer accountId) {
        try {
            PlayerData            playerData = getPlayerData(player, accountId);
            PlayerTextCastingData skillData  = playerData.getTextCastingData();
            String                skillName  = skillData.getSkill(Integer.parseInt(arguments.remove(0)));
            arguments.add(skillName);
            return actions.getOrDefault(arguments.remove(0), (a, b, c) -> null).apply(player, arguments, accountId);
        } catch (Exception e) {
            return "";
        }
    }

    // If any of the above Placeholders fail to compute, will attempt to return the value of a Legacy Placeholder.
    // If successful, will message the console to let players know they are reading a depreciated placeholder and to check the Wiki.
    public static String getLegacyPlaceholder(OfflinePlayer player, String identifier) {
        PlayerData playerData = Fabled.getData(player);

        if (identifier.startsWith("group_")) {
            if (!Fabled.getClasses().isEmpty()) {
                Set<String> groups = Fabled.getClasses()
                        .values()
                        .stream()
                        .map(clazz -> clazz.getGroup().toLowerCase())
                        .collect(Collectors.toSet());
                for (String groupName : groups) {
                    PlayerClass playerClass = playerData.getClass(groupName);
                    if (playerClass == null || playerClass.getData() == null) continue;

                    if (identifier.equals("group_" + groupName + "_basehealth")) {
                        return String.valueOf(playerClass.getData().getBaseHealth());
                    }
                    if (identifier.equals("group_" + groupName + "_sbasehealth")) {
                        return String.valueOf((int) playerClass.getData().getBaseHealth());
                    }
                    if (identifier.equals("group_" + groupName + "_basemana")) {
                        return String.valueOf(playerClass.getData().getBaseMana());
                    }
                    if (identifier.equals("group_" + groupName + "_sbasemana")) {
                        return String.valueOf((int) playerClass.getData().getBaseMana());
                    }
                    if (identifier.startsWith("group_" + groupName + "_healthat:")) {
                        String[] idSplit = identifier.split(":");
                        try {
                            int lvl = Integer.parseInt(idSplit[1]);
                            return String.valueOf(playerClass.getData().getHealth(lvl));
                        } catch (NumberFormatException e) {
                            return "0";
                        }
                    }
                    if (identifier.startsWith("group_" + groupName + "_shealthat:")) {
                        String[] idSplit = identifier.split(":");
                        try {
                            int lvl = Integer.parseInt(idSplit[1]);
                            return String.valueOf((int) playerClass.getData().getHealth(lvl));
                        } catch (NumberFormatException e) {
                            return "0";
                        }
                    }
                    if (identifier.equals("group_" + groupName + "_healthscale")) {
                        return String.valueOf(playerClass.getData().getHealthScale());
                    }
                    if (identifier.startsWith("group_" + groupName + "_manaat:")) {
                        String[] idSplit = identifier.split(":");
                        try {
                            int lvl = Integer.parseInt(idSplit[1]);
                            return String.valueOf(playerClass.getData().getMana(lvl));
                        } catch (NumberFormatException e) {
                            return "0";
                        }
                    }
                    if (identifier.startsWith("group_" + groupName + "_smanaat:")) {
                        String[] idSplit = identifier.split(":");
                        try {
                            int lvl = Integer.parseInt(idSplit[1]);
                            return String.valueOf((int) playerClass.getData().getMana(lvl));
                        } catch (NumberFormatException e) {
                            return "0";
                        }
                    }
                    if (identifier.equals("group_" + groupName + "_mananame")) {
                        return String.valueOf(playerClass.getData().getManaName());
                    }
                    if (identifier.equals("group_" + groupName + "_smananame")) {
                        return String.valueOf(ChatColor.stripColor(playerClass.getData().getManaName()));
                    }
                    if (identifier.equals("group_" + groupName + "_manaregen")) {
                        return String.valueOf(playerClass.getData().getManaRegen());
                    }
                    if (identifier.equals("group_" + groupName + "_manascale")) {
                        return String.valueOf(playerClass.getData().getManaScale());
                    }
                    if (identifier.equals("group_" + groupName + "_maxlevel")) {
                        return String.valueOf(playerClass.getData().getMaxLevel());
                    }
                    if (identifier.equals("group_" + groupName + "_parent")) {
                        if (playerClass.getData().getParent() != null) {
                            return String.valueOf(playerClass.getData().getParent().getName());
                        } else {
                            return "0";
                        }
                    }
                    if (identifier.equals("group_" + groupName + "_prefix")) {
                        return String.valueOf(playerClass.getData().getPrefix());
                    }
                    if (identifier.equals("group_" + groupName + "_sprefix")) {
                        return String.valueOf(ChatColor.stripColor(playerClass.getData().getPrefix()));
                    }
                    if (identifier.startsWith("group_" + groupName + "_reqexpat:")) {
                        String[] idSplit = identifier.split(":");
                        try {
                            int lvl = Integer.parseInt(idSplit[1]);
                            return String.valueOf(playerClass.getData().getRequiredExp(lvl));
                        } catch (NumberFormatException e) {
                            return "0";
                        }
                    }

                    if (identifier.startsWith("group_" + groupName + "_children")) {
                        if (Fabled.getClass(groupName) == null) {
                            return "0";
                        }

                        List<String> childList = new ArrayList<>();

                        for (FabledClass classes : Fabled.getClasses().values()) {
                            String classname = classes.getName().toLowerCase();

                            if (classes.hasParent()) {
                                if (classes.getParent().getName().equalsIgnoreCase(groupName)) {
                                    childList.add(classname);
                                }
                            }
                        }
                        return childList.toString();
                    }

                    if (identifier.startsWith("group_" + groupName + "_schildren")) {
                        if (Fabled.getClass(groupName) == null) {
                            return "0";
                        }

                        List<String> childList = new ArrayList<>();

                        for (FabledClass classes : Fabled.getClasses().values()) {
                            String classname = classes.getName().toLowerCase();

                            if (classes.hasParent()) {
                                if (classes.getParent().getName().equalsIgnoreCase(groupName)) {
                                    childList.add(classname);
                                }
                            }
                        }
                        String finalList = childList.toString().replaceAll("(^\\[|\\]$)", "");
                        if (finalList.equals("") || finalList == null) {
                            return "0";
                        } else {
                            return finalList;
                        }

                    }

                }
            }
        }

        String[] args = identifier.split("_");
        if ((args.length == 3 && args[0].equals("default")) || (args.length == 4 && args[0].equals("player")
                && !args[1].equals("account"))) {
            // Another player
            String playerName = args[args.length - 1];
            UUID   uuid       = null;
            try {
                uuid = UUID.fromString(playerName);
            } catch (IllegalArgumentException ignored) {
            }
            player = uuid == null ? Bukkit.getOfflinePlayer(playerName) : Bukkit.getOfflinePlayer(uuid);
            identifier = identifier.substring(0, identifier.length() - playerName.length() - 1);
        }

        if (player == null) {
            return "0";
        }

        if (!Fabled.hasPlayerData(player)) {
            return "0";
        }

        PlayerData data = Fabled.getData(player);

        if (data == null) {
            return "0";
        }

        if (!data.hasClass()) {
            return "0";
        }

        if (identifier.startsWith("default_")) {
            if (identifier.equals("default_currentlevel")) {
                return String.valueOf(data.getMainClass().getLevel());
            }
            if (identifier.equals("default_currentmaxlevel")) {
                return String.valueOf(data.getMainClass().getData().getMaxLevel());
            }
            if (identifier.equals("default_currentmaxmana")) {
                return String.valueOf(data.getMainClass().getPlayerData().getMaxMana());
            }
            if (identifier.equals("default_scurrentmaxmana")) {
                return String.valueOf((int) data.getMainClass().getPlayerData().getMaxMana());
            }
            if (identifier.equals("default_currentmana")) {
                return String.valueOf(data.getMainClass().getPlayerData().getMana());
            }
            if (identifier.equals("default_scurrentmana")) {
                return String.valueOf((int) data.getMainClass().getPlayerData().getMana());
            }
            if (identifier.equals("default_currentmaxhealth")) {
                double maxHP = player.getPlayer()
                        .getAttribute(VersionManager.getNms().getAttribute("MAX_HEALTH"))
                        .getBaseValue();
                return String.valueOf(maxHP);
            }
            if (identifier.equals("default_scurrentmaxhealth")) {
                double maxHP = player.getPlayer()
                        .getAttribute(VersionManager.getNms().getAttribute("MAX_HEALTH"))
                        .getBaseValue();
                return String.valueOf((int) maxHP);
            }
            if (identifier.equals("default_currenthealth")) {
                double currentHP = player.getPlayer().getHealth();
                return String.valueOf(currentHP);
            }
            if (identifier.equals("default_scurrenthealth")) {
                double currentHP = player.getPlayer().getHealth();
                return String.valueOf((int) currentHP);
            }
            if (identifier.equals("default_currentmananame")) {
                return String.valueOf(data.getMainClass().getData().getManaName());
            }
            if (identifier.equals("default_scurrentmananame")) {
                return String.valueOf(ChatColor.stripColor(data.getMainClass().getData().getManaName()));
            }
            if (identifier.equals("default_currentmanaregen")) {
                return String.valueOf(data.getMainClass().getData().getManaRegen());
            }
            if (identifier.equals("default_scurrentmanaregen")) {
                return String.valueOf((int) data.getMainClass().getData().getManaRegen());
            }
            if (identifier.equals("default_currentgroupname")) {
                return String.valueOf(data.getMainClass().getData().getGroup());
            }
            if (identifier.equals("default_currentclassname")) {
                return String.valueOf(data.getMainClass().getData().getName());
            }
            if (identifier.equals("default_currentavailableattributepoints") || identifier.equals(
                    "default_attributepoints")) {
                return String.valueOf(data.getMainClass().getPlayerData().getAttributePoints());
            }
            if (identifier.equals("default_currentavailableskillpoints") || identifier.equals("default_skillpoints")
                    || identifier.equals("default_currentlyavailablesuperawesomeultramegagigaamazingskillpoints")) {
                return String.valueOf(data.getMainClass().getPoints());
            }
            if (identifier.equals("default_currentprefix")) {
                return String.valueOf(data.getMainClass().getData().getPrefix());
            }
            if (identifier.equals("default_scurrentprefix")) {
                return String.valueOf(ChatColor.stripColor(data.getMainClass().getData().getPrefix()));
            }
            if (identifier.equals("default_currentexp")) {
                return String.valueOf(data.getMainClass().getExp());
            }
            if (identifier.equals("default_scurrentexp")) {
                return String.valueOf((int) data.getMainClass().getExp());
            }
            if (identifier.equals("default_currentrequiredexp")) {
                return String.valueOf(data.getMainClass().getRequiredExp());
            }
            if (identifier.equals("default_scurrentrequiredexp")) {
                return String.valueOf(data.getMainClass().getRequiredExp());
            }
            if (identifier.startsWith("default_value_")) {
                return data.getPersistentData(identifier.substring(14)).toString();
            }
        }

        if (identifier.startsWith("player_")) {
            if (identifier.startsWith("player_account_")) {
                PlayerAccounts accounts = Fabled.getPlayerAccounts(player);
                Pattern        pattern  = Pattern.compile("player_account_(\\d+)");
                Matcher        matcher  = pattern.matcher(identifier);
                if (matcher.find()) {
                    int accNum = Integer.parseInt(matcher.group(1));
                    data = accounts.getData(accNum);

                    if (data == null) return ChatColor.GRAY + "Not Professed";

                    identifier = identifier.replace("player_account_" + accNum + "_", "");
                    return getPlaceholder(player, identifier, data.getMainClass());
                }
            } else if (!data.getClasses().isEmpty()) {
                for (PlayerClass group : data.getClasses()) {
                    String      groupName   = group.getData().getGroup();
                    PlayerClass playerClass = playerData.getClass(groupName);
                    if (!identifier.startsWith("player_" + groupName)) continue;

                    identifier = identifier.replace("player_" + groupName + "_", "");
                    return getPlaceholder(player, identifier, playerClass);
                }
            }
        }

        if (identifier.startsWith("dynamic_")) {
            if (!player.isOnline()) return "0";
            LivingEntity caster = player.getPlayer();
            String[]     ident  = identifier.split("_", 3);
            switch (ident[1]) {
                case "value" -> {
                    CastData castData = DynamicSkill.getCastData(caster);
                    if (ident.length < 3) return "0";
                    if (castData == null) return "0";
                    return castData.getOrDefault(ident[2], "0");
                }
                case "flags" -> {
                    FlagData flagData = FlagManager.getFlagData(caster);
                    if (flagData == null) return "0";
                    Stream<String> stream = flagData.flagList().stream();
                    if (ident.length > 2)
                        stream = stream.filter(f -> f.startsWith(ident[2])).map(f -> f.replaceFirst(ident[2], ""));
                    return stream.collect(Collectors.joining(" "));
                }
                case "flagremain" -> {
                    if (ident.length < 3) return "0";
                    return String.valueOf(FlagManager.getTimeLeft(caster, ident[2]));
                }
                case "cooldown" -> {
                    if (ident.length < 3) return "0";
                    PlayerSkill skill = data.getSkill(ident[2]);
                    if (skill == null) return "0";
                    return String.valueOf(skill.getCooldownLeft());
                }
            }
        }

        return null;
    }

    private static String getPlaceholder(OfflinePlayer player, String identifier, PlayerClass playerClass) {
        if (identifier.startsWith("attribute:")) {
            String[] idSplit = identifier.split(":");
            try {
                return String.valueOf(playerClass.getPlayerData().getAttribute(idSplit[1]));
            } catch (Exception e) {
                return "0";
            }
        }
        if (identifier.equals("availableattributepoints") || identifier.equals("attributepoints")) {
            return String.valueOf(playerClass.getPlayerData().getAttributePoints());
        }
        if (identifier.equals("availableskillpoints") || identifier.equals("skillpoints") || identifier.equals(
                "availablesuperawesomeultramegagigaamazingskillpoints")) {
            return String.valueOf(playerClass.getPoints());
        }
        if (identifier.startsWith("investedattributepoints:")) {
            String[] idSplit = identifier.split(":");
            try {
                return String.valueOf(playerClass.getPlayerData().getInvestedAttribute(idSplit[1]));
            } catch (Exception e) {
                return "0";
            }
        }
        if (identifier.equals("mainclass")) {
            return String.valueOf(playerClass.getPlayerData().getMainClass().getData().getName());
        }
        if (identifier.equals("class")) {
            return String.valueOf(playerClass.getData().getName());
        }

        if (identifier.equals("currentexp")) {
            return String.valueOf(playerClass.getExp());
        }
        if (identifier.equals("requiredexp")) {
            return String.valueOf(playerClass.getRequiredExp());
        }
        if (identifier.equals("scurrentexp")) {
            return String.valueOf((int) playerClass.getExp());
        }
        if (identifier.equals("srequiredexp")) {
            return String.valueOf((int) playerClass.getRequiredExp());
        }
        if (identifier.equals("level")) {
            return String.valueOf(playerClass.getLevel());
        }
        if (identifier.equals("currentmana")) {
            return String.valueOf(playerClass.getPlayerData().getMana());
        }
        if (identifier.equals("maxmana")) {
            return String.valueOf(playerClass.getPlayerData().getMaxMana());
        }
        if (identifier.equals("scurrentmana")) {
            return String.valueOf((int) playerClass.getPlayerData().getMana());
        }
        if (identifier.equals("smaxmana")) {
            return String.valueOf((int) playerClass.getPlayerData().getMaxMana());
        }
        if (identifier.equals("scurrenthealth")) {
            double currentHP = player.getPlayer().getHealth();
            return String.valueOf((int) currentHP);
        }
        if (identifier.equals("smaxhealth")) {
            double maxHP =
                    player.getPlayer().getAttribute(VersionManager.getNms().getAttribute("MAX_HEALTH")).getBaseValue();
            return String.valueOf((int) maxHP);
        }
        if (identifier.startsWith("skillevel:")) {
            String[] idSplit = identifier.split(":");
            try {
                return String.valueOf(playerClass.getPlayerData().getSkillLevel(idSplit[1]));
            } catch (Exception e) {
                return "0";
            }
        }
        return null;
    }
}
