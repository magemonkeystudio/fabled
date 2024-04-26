package studio.magemonkey.fabled.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.config.Filter;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkillBar;
import studio.magemonkey.fabled.api.util.BuffManager;
import studio.magemonkey.fabled.api.util.Combat;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.CitizensHook;
import studio.magemonkey.fabled.language.RPGFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.cmd.CmdChangeClass
 */
public class CmdChangeClass implements IFunction, TabCompleter {
    private static final String INVALID_GROUP  = "invalid-group";
    private static final String INVALID_PLAYER = "invalid-player";
    private static final String INVALID_TARGET = "invalid-class";
    private static final String SUCCESS        = "success";
    private static final String NOTIFICATION   = "notification";

    public static void unload(Player player) {
        if (CitizensHook.isNPC(player)) return;

        PlayerData data = Fabled.getPlayerData(player);
        if (Fabled.getSettings().isWorldEnabled(player.getWorld())) {
            data.record(player);
            data.stopPassives(player);
        }

        FlagManager.clearFlags(player);
        BuffManager.clearData(player);
        Combat.clearData(player);
        DynamicSkill.clearCastData(player);

        player.setDisplayName(player.getName());
        player.setWalkSpeed(0.2f);
    }

    /**
     * Executes the command
     *
     * @param cmd    owning command
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   arguments
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args) {
        if (args.length >= 3) {
            final String playerName = args[0];
            final String groupName  = args[1];
            String       className  = args[2];
            for (int i = 3; i < args.length; i++) className += ' ' + args[i];

            OfflinePlayer player = Bukkit.getPlayer(playerName);
            if (player == null) {
                player = Bukkit.getOfflinePlayer(playerName);
                if (!player.hasPlayedBefore()) {
                    cmd.sendMessage(sender,
                            INVALID_PLAYER,
                            ChatColor.DARK_RED + "{player} is not online, nor have they played before.",
                            Filter.PLAYER.setReplacement(playerName));
                    return;
                }
            }

            final PlayerData  data  = Fabled.getPlayerData(player);
            final PlayerClass clazz = data.getClass(groupName);
            if (clazz == null) {
                cmd.sendMessage(sender, INVALID_GROUP, "{player} does not have a {class}",
                        Filter.PLAYER.setReplacement(player.getName()),
                        RPGFilter.GROUP.setReplacement(groupName),
                        RPGFilter.CLASS.setReplacement(className));
                return;
            }

            final String      original = clazz.getData().getName();
            final FabledClass target   = Fabled.getClass(className);
            if (target == null) {
                cmd.sendMessage(sender, INVALID_TARGET, "{class} is not a valid class to change to",
                        RPGFilter.CLASS.setReplacement(className));
                return;
            }

            boolean bar = data.getSkillBar().isEnabled() && Fabled.getSettings().isSkillBarEnabled();
            if (bar) {
                PlayerSkillBar skillBar = data.getSkillBar();
                skillBar.toggleEnabled();
                skillBar.reset();
                data.getSkillBar().toggleEnabled();
            }

            clazz.setClassData(target);
            if (player.isOnline()) {
                unload((Player) player);
                Fabled.getPlayerAccounts(player).getActiveData().init((Player) player);
            }
            if (bar)
                Fabled.getPlayerData(player).getSkillBar().toggleEnabled();


            cmd.sendMessage(sender, SUCCESS, "You have changed {player} from a {name} to a {class}",
                    Filter.PLAYER.setReplacement(player.getName()),
                    RPGFilter.GROUP.setReplacement(groupName),
                    RPGFilter.CLASS.setReplacement(className),
                    RPGFilter.NAME.setReplacement(original));

            if (sender != player && player.isOnline()) {
                cmd.sendMessage((Player) player, NOTIFICATION, "You have changed from a {name} to a {class}",
                        RPGFilter.GROUP.setReplacement(groupName),
                        RPGFilter.CLASS.setReplacement(className),
                        RPGFilter.NAME.setReplacement(original));
            }
        } else {
            cmd.displayHelp(sender);
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String s,
                                      @NotNull String[] args) {
        if (args.length == 1) {
            return ConfigurableCommand.getPlayerTabCompletions(commandSender, args[0]);
        } else if (args.length > 1) {
            String[] group = Arrays.copyOfRange(args, 1, 2);
            int      i     = 3;
            while (i <= args.length) {
                String[] concat = Arrays.copyOfRange(args, 1, i);
                String   g      = String.join(" ", concat);
                if (Fabled.getGroups().stream().noneMatch(g1 -> StringUtil.startsWithIgnoreCase(g1, g))) {
                    // Assume latest concatenation was right, let's do the class
                    String finalGroup = String.join(" ", group);
                    return ConfigurableCommand.getTabCompletions(Fabled.getClasses().values().stream()
                            .filter(rpgClass -> rpgClass.getGroup().equalsIgnoreCase(finalGroup))
                            .map(FabledClass::getName)
                            .collect(Collectors.toList()), Arrays.copyOfRange(args, i - 1, args.length));
                }
                group = concat;
                i++;
            }

            // There's still valid groups available, let's suggest those
            return ConfigurableCommand.getTabCompletions(Fabled.getGroups(), group);
        }
        return null;
    }
}
