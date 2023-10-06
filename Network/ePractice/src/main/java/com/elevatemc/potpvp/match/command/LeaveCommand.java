package com.elevatemc.potpvp.match.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class LeaveCommand {

    @Command(names = { "spawn", "leave" }, permission = "")
    public static void leave(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot do this while playing in a match.");
            return;
        }

        if(matchHandler.isPlayingEvent(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot do this while playing an event.");
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating == null) {
            PotPvPSI.getInstance().getLobbyHandler().returnToLobby(sender);
        } else {
            spectating.removeSpectator(sender);
        }

        sender.sendMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "✔ " + ChatColor.GREEN + "Teleported to spawn");
    }

}