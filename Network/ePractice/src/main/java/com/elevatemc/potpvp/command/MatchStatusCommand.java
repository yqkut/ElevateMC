package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MatchStatusCommand {

    @Command(names = { "match status" }, permission = "op")
    public static void matchStatus(CommandSender sender, @Parameter(name = "target") Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(target);

        if (match == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " is in the lobby right now.");
            return;
        }

        for (String line : PotPvPSI.getGson().toJson(match).split("\n")) {
            sender.sendMessage("  " + ChatColor.GRAY + line);
        }
    }

}