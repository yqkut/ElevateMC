package com.elevatemc.potpvp.match.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MapCommand {

    @Command(names = { "map" }, permission = "op")
    public static void map(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(sender);

        if (match == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You are not in a match right now.");
            return;
        }

        Arena arena = match.getArena();
        sender.sendMessage(ChatColor.YELLOW + "Playing on copy " + ChatColor.GOLD + arena.getCopy() + ChatColor.YELLOW + " of " + ChatColor.GOLD + arena.getSchematic() + ChatColor.YELLOW + ".");
    }
}