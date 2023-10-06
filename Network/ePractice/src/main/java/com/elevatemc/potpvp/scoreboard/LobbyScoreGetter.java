package com.elevatemc.potpvp.scoreboard;

import com.elevatemc.elib.util.Pair;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.queue.MatchQueue;
import com.elevatemc.potpvp.queue.MatchQueueEntry;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.potpvp.hctranked.game.RankedGame;
import com.elevatemc.potpvp.hctranked.game.RankedGameHandler;
import com.elevatemc.potpvp.hctranked.game.RankedGameTeam;
import com.elevatemc.potpvp.tournament.Tournament;
import com.elevatemc.potpvp.tournament.Tournament.TournamentStage;
import com.elevatemc.elib.util.TimeUtils;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.BiConsumer;

final class LobbyScoreGetter implements BiConsumer<Player, LinkedList<String>> {
    private long lastUpdated = System.currentTimeMillis();

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        RankedGameHandler rankedGameHandler = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler();
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);


        scores.add(style.getValue() + "Online: " + style.getKey() + PotPvPSI.getInstance().getOnlineCount());
        scores.add("");
        scores.add(style.getValue() + "In Fights: " + style.getKey() + PotPvPSI.getInstance().getFightsCount());
        scores.add(style.getValue() + "In Queues: " + style.getKey() + PotPvPSI.getInstance().getQueueHandler().getQueuedCount());



        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            int size = playerParty.getMembers().size();

            String privacy = "";
            switch (playerParty.getAccessRestriction()) {
                case PUBLIC:
                    privacy = ChatColor.DARK_AQUA + "Open";
                    break;
                case INVITE_ONLY:
                    privacy = ChatColor.DARK_AQUA + "Invite Only";
                    break;
                case PASSWORD:
                    privacy = ChatColor.DARK_AQUA + "Password Only";
                    break;
                default:
                    break;
            }

            scores.add("");
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Party");
            scores.add(style.getKey() + "❘ " + style.getValue() + "Leader&7: " + style.getKey() + Bukkit.getPlayer(playerParty.getLeader()).getName());
            scores.add(style.getKey() + "❘ " + style.getValue() + "Members&7: " + style.getKey() + playerParty.getMembers().size() + "/" + Party.MAX_SIZE);
            scores.add(style.getKey() + "❘ " + style.getValue() + "Privacy&7: " + style.getKey() + privacy);
        }

        RankedGame rankedGame = rankedGameHandler.getJoinedGame(player);
        if (rankedGame != null) {
            RankedGameTeam team1 = rankedGame.getTeam1();
            RankedGameTeam team2 = rankedGame.getTeam2();
            scores.add("");
            scores.add(style.getKey() + "Ranked Game");
            scores.add(style.getKey() + "❘ " + style.getValue() + "Entered&7: " + style.getKey() + rankedGame.getJoinedPlayers().size() + "/" + rankedGame.getAllPlayers().size());
            scores.add(style.getKey() + "❘ " + style.getValue() + "Team 1&7: " + style.getKey() + (team1.isReady() ? "&aReady" : "&cNot Ready"));
            scores.add(style.getKey() + "❘ " + style.getValue() + "Team 2&7: " + style.getKey() + (team2.isReady() ? "&aReady" : "&cNot Ready"));
        }


        MatchQueueEntry entry = getQueueEntry(player);

        if (entry != null) {
            MatchQueue queue = entry.getQueue();

            String waitTimeFormatted = TimeUtils.formatIntoMMSS(entry.getWaitSeconds());

            scores.add("");
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Queue");
            scores.add(style.getKey() + "❘ " + style.getValue() + "Ladder: " + style.getKey() + queue.getGameMode().getName());
            scores.add(style.getKey() + "❘ " + style.getValue() + "Time: " + style.getKey() + waitTimeFormatted);


        }

        if(PotPvPSI.getInstance().getTournamentHandler() != null && PotPvPSI.getInstance().getTournamentHandler().getTournament() != null) {
            Tournament tournament = PotPvPSI.getInstance().getTournamentHandler().getTournament();
            if (tournament != null) {
                scores.add(" ");
                scores.add(style.getKey().toString() + "Tournament");
                scores.add(style.getKey() + "❘ " + style.getValue() + "&rLadder: " + style.getKey() + tournament.getType().getName());
                if (tournament.getStage() == TournamentStage.WAITING_FOR_TEAMS) {
                    int teamSize = tournament.getRequiredPartySize();

                    scores.add(style.getKey() + "❘ " + style.getValue() + "Team Size: " + style.getKey() + teamSize + "v" + teamSize);
                } else if (tournament.getStage() == TournamentStage.COUNTDOWN) {
                    if (tournament.getCurrentRound() == 0) {
                        scores.add(style.getKey() + "❘ " + style.getValue() + "Start: " + style.getKey() + TimeUtils.formatIntoMMSS(tournament.getBeginNextRoundIn()));
                    } else {
                        scores.add(style.getKey() + "❘ " + style.getValue() + "Next round: " + style.getKey() + TimeUtils.formatIntoMMSS(tournament.getBeginNextRoundIn()));
                    }
                } else if (tournament.getStage() == TournamentStage.IN_PROGRESS) {
                    scores.add(style.getKey() + "❘ " + style.getValue() + "Duration: " + style.getKey() + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
                    scores.add(style.getKey() + "❘ " + style.getValue() + "Round: " + style.getKey() + tournament.getCurrentRound());
                }
            }
        }
    }

    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        } else {
            return queueHandler.getQueueEntry(player.getUniqueId());
        }
    }
}