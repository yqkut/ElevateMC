package com.elevatemc.potpvp.match.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class SpectateCommand {

    private static final int SPECTATE_COOLDOWN_SECONDS = 2;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    @Command(names = {"spectate", "spec"}, permission = "")
    public static void spectate(Player sender, @Parameter(name = "target") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot spectate yourself.");
            return;
        } else if (cooldowns.containsKey(sender.getUniqueId()) && cooldowns.get(sender.getUniqueId()) > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Please wait before using this command again.");
            return;
        }

        cooldowns.put(sender.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SPECTATE_COOLDOWN_SECONDS));

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " is not in a match.");
            return;
        }

        //boolean bypassesSpectating = PotPvPSI.getInstance().getTournamentHandler().isInTournament(targetMatch);
        boolean bypassesSpectating = false;

        // only check the seting if the target is actually playing in the match
        if (!bypassesSpectating && (targetMatch.getTeam(target.getUniqueId()) != null && !settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS))) {
            if (sender.isOp() || sender.hasPermission("core.staffteam")) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " has their spectators setting turned off right now.");
                return;
            }
        }

        if ((!sender.isOp() && !sender.hasPermission("core.staffteam")) && targetMatch.getTeams().size() == 2 && !bypassesSpectating) {
            MatchTeam teamA = targetMatch.getTeams().get(0);
            MatchTeam teamB = targetMatch.getTeams().get(1);

            if (teamA.getAllMembers().size() == 1 && teamB.getAllMembers().size() == 1) {
                UUID teamAPlayer = teamA.getFirstMember();
                UUID teamBPlayer = teamB.getFirstMember();

                if (
                    !settingHandler.getSetting(Bukkit.getPlayer(teamAPlayer), Setting.ALLOW_SPECTATORS) ||
                    !settingHandler.getSetting(Bukkit.getPlayer(teamBPlayer), Setting.ALLOW_SPECTATORS)
                ) {
                    sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Not all players have their spectators setting turned off right now.");
                    return;
                }
            }
        }

        Player teleportTo = null;

        // /spectate looks up matches being played OR watched by the target,
        // so we can only target them if they're not spectating
        if (!targetMatch.isSpectator(target.getUniqueId())) {
            teleportTo = target;
        }

        if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(sender)) {
            Match currentlySpectating = matchHandler.getMatchSpectating(sender);

            if (currentlySpectating != null) {
                if (currentlySpectating.equals(targetMatch)) {
                    sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You're already spectating this match.");
                    return;
                }

                currentlySpectating.removeSpectator(sender);
            }

            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}