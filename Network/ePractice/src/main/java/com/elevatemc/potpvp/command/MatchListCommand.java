package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MatchListCommand {

    @Command(names = { "match list" }, permission = "op")
    public static void matchList(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            sender.sendMessage(ChatColor.GOLD + match.getSimpleDescription(true));
        }
    }

}