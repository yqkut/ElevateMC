package com.elevatemc.potpvp.match.command;

import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FocusCommand {

    @Command(names = {"focus"}, permission = "")
    public static void focus(Player sender, @Parameter(name="player") Player target) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(sender);
        if (match == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must be in a match to do this.");
            return;
        }

        MatchTeam team = match.getPreviousTeam(sender.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You don't have a team to do this.");
            return;
        }

        MatchTeam targetTeam = match.getTeam(target.getUniqueId());
        if (targetTeam == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The target must be alive and in the same match as you.");
            return;
        }

        if (team.equals(targetTeam)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The target can't be in your team.");
            return;
        }

        if (team.getAllMembers().size() < 2) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have no teammates to do this for.");
            return;
        }

        if (team.getFocus() != null && team.getFocus().equals(target.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "That person is already focused.");
            return;
        }


        team.setFocus(target);

        team.forEachAlive(p -> {
            p.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");
        });
    }

    @Command(names = {"unfocus"}, permission = "")
    public static void unfocus(Player sender) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(sender);
        if (match == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must be in a match to do this.");
            return;
        }

        MatchTeam team = match.getPreviousTeam(sender.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You don't have a team to do this.");
            return;
        }

        if (team.getFocus() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No one is focused.");
            return;
        }

        team.setFocus(null);

        team.forEachAlive(p -> {
            p.sendMessage(ChatColor.YELLOW + sender.getName() + " has reset the focus.");
        });
    }
}