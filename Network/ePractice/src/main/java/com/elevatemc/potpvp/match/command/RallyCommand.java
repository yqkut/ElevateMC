package com.elevatemc.potpvp.match.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class RallyCommand {

    @Command(names = {"t rally", "team rally", "rally", "f rally", "p rally"}, permission = "")
    public static void partyRally(Player sender) {
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

        if (team.getAllMembers().size() < 2) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have no teammates to do this for.");
            return;
        }

        team.sendRally(sender);
    }

}