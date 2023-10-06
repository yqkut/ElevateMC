package com.elevatemc.potpvp.match.listener;

import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.github.paperspigot.Title;

import java.util.Set;
import java.util.UUID;

public class MatchTitleListener implements Listener {

    private final Title.Builder VICTORY = Title.builder().title(ChatColor.GREEN.toString() + ChatColor.BOLD + "VICTORY");
    private final Title SOLO_VICTORY = VICTORY.subtitle("You won the match").stay(100).build();
    private final Title TEAM_VICTORY = VICTORY.subtitle("Your team won this match").stay(100).build();

    // We do not send a solo loss because we send a dead message titel already
    private final Title.Builder LOSS = Title.builder().title(ChatColor.RED.toString() + ChatColor.BOLD + "LOSS");
    private final Title TEAM_LOSS = VICTORY.subtitle("Your team lost").stay(100).build();

    private final Title DIED = Title.builder().title(ChatColor.RED.toString() + ChatColor.BOLD + "YOU DIED").subtitle("You are now a spectator").stay(100).build();

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();

        MatchTeam winner = match.getWinner();
        Set<UUID> losingPlayers = match.getLosingPlayers();

        boolean winnerIsTeam = winner.getAllMembers().size() > 1;
        winner.forEachAlive(player -> {
            PlayerUtils.sendTitle(player, winnerIsTeam ? TEAM_VICTORY : SOLO_VICTORY);
        });

        for (UUID spectator : match.getSpectators()) {
            if (winner.getAllMembers().contains(spectator)) {
                Player player = Bukkit.getPlayer(spectator);
                PlayerUtils.sendTitle(player, winnerIsTeam ? TEAM_VICTORY : SOLO_VICTORY);
            }

            if (losingPlayers != null && losingPlayers.contains(spectator)) {
                if (match.getTeam(spectator) != null && match.getTeam(spectator).getAllMembers().size() > 1) {
                    Player player = Bukkit.getPlayer(spectator);
                    PlayerUtils.sendTitle(player, TEAM_LOSS);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMatchEnd(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getEntity();

        Match match = matchHandler.getMatchPlayingOrSpectating(player);
        if (match == null) {
            return;
        }

        MatchTeam team = match.getPreviousTeam(player.getUniqueId());
        if (team == null) {
            return;
        }

        if (team.getAliveMembers().size() > 0 || team.getAllMembers().size() == 1) {
            PlayerUtils.sendTitle(player, DIED);
        }
    }

}
