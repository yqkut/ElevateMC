package com.elevatemc.potpvp.follow.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UnfollowCommand {

    @Command(names={"unfollow"}, permission="")
    public static void unfollow(Player sender) {
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }
}