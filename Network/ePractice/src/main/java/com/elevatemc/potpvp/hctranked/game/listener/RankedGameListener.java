package com.elevatemc.potpvp.hctranked.game.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.event.MatchSpectatorLeaveEvent;
import com.elevatemc.potpvp.match.event.MatchTerminateEvent;
import com.elevatemc.potpvp.hctranked.game.RankedGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RankedGameListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(player);
        if (game == null) {
            return;
        }

        game.leave(player);
    }

    @EventHandler
    public void onSpectatorLeave(MatchSpectatorLeaveEvent event) {
        Match match = event.getMatch();
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getGameByMatchId(match.get_id());
        if (game != null) {
            game.leave(event.getSpectator());
        }
    }

    @EventHandler
    public void onMatchEnd(MatchTerminateEvent event) {
        Match match = event.getMatch();
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getGameByMatchId(match.get_id());
        if (game != null) {
            game.messageJoined(ChatColor.GREEN + "This match has been registered as a HCT Ranked game. If there were any issues please make a ticket in the discord.");
            game.end(game.getTeam1().getPlayers().contains(match.getWinner().getFirstMember()) ? 1 : 2);
        }
    }
}