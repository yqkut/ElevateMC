package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PStatusCommand {

    @Command(names = {"pstatus"}, permission = "op")
    public static void pStatus(Player sender, @Parameter(name="target", defaultValue = "self") Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();

        sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + ":");
        sender.sendMessage("In match: " + matchHandler.isPlayingMatch(target));
        sender.sendMessage("In match (NC): " + noCacheIsPlayingMatch(target));
        sender.sendMessage("In Event: " + matchHandler.isPlayingEvent(target));
        sender.sendMessage("Spectating match: " + matchHandler.isSpectatingMatch(target));
        sender.sendMessage("Spectating match (NC): " + noCacheIsSpectatingMatch(target));
        sender.sendMessage("In or spectating match: " + matchHandler.isPlayingOrSpectatingMatch(target));
        sender.sendMessage("In or spectating match (NC): " + noCacheIsPlayingOrSpectatingMatch(target));
        sender.sendMessage("In queue: " + queueHandler.isQueued(target.getUniqueId()));
        sender.sendMessage("In party: " + partyHandler.hasParty(target));
        sender.sendMessage("Following: " + followHandler.getFollowing(target).isPresent());
    }

    private static boolean noCacheIsPlayingMatch(Player target) {
        for (Match match : PotPvPSI.getInstance().getMatchHandler().getHostedMatches()) {
            for (MatchTeam team : match.getTeams()) {
                if (team.isAlive(target.getUniqueId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean noCacheIsSpectatingMatch(Player target) {
        for (Match match : PotPvPSI.getInstance().getMatchHandler().getHostedMatches()) {
            if (match.isSpectator(target.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    private static boolean noCacheIsPlayingOrSpectatingMatch(Player target) {
        return noCacheIsPlayingMatch(target) || noCacheIsSpectatingMatch(target);
    }

}