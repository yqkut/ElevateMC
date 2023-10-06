package com.elevatemc.potpvp.tab;

import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.hctranked.game.RankedGame;
import com.elevatemc.potpvp.hctranked.game.RankedGameTeam;
import com.elevatemc.potpvp.tournament.Tournament;
import com.google.common.collect.Sets;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

final class LobbyLayoutProvider implements BiConsumer<Player, PotPvPLayoutProvider.TabLayout> {

    @Override
    public void accept(Player player, PotPvPLayoutProvider.TabLayout tabLayout) {
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
        Tournament tournament = PotPvPSI.getInstance().getTournamentHandler().getTournament();
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(player);

        int y = 3;

        rankings: {
            tabLayout.put(1, y++, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Your Rankings");

            int x = 0;

            for (GameMode gameMode : GameMode.getAll()) {
                if (!gameMode.getSupportsCompetitive()) {
                    continue;
                }

                tabLayout.put(x++, y, ChatColor.DARK_AQUA + gameMode.getName() + ChatColor.GRAY +  " - " + ChatColor.WHITE + eloHandler.getElo(player, gameMode));

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }

        party: {
            if (party == null) {
                break party;
            }

            y += 2;

            tabLayout.put(1, y++, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Your Party");

            int x = 0;

            for (UUID member : getOrderedMembers(player, party)) {
                int ping = PotPvPLayoutProvider.getPingOrDefault(member);
                String suffix = member == party.getLeader() ? ChatColor.GRAY + "*" : "";
                String displayName = ChatColor.BLUE + UUIDUtils.name(member) + suffix;

                tabLayout.put(x++, y, displayName/*, ping*/);

                if (x == 3 && y == PotPvPLayoutProvider.MAX_TAB_Y) {
                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }

        tournament: {
            if (tournament == null) {
                break tournament;
            }

            y += 2;

            tabLayout.put(1, y++, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Tournament");
            if (tournament.getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
                tabLayout.put(1, y, ChatColor.GRAY + "Waiting...");
            } else if (tournament.getStage() == Tournament.TournamentStage.COUNTDOWN) {
                if (tournament.getCurrentRound() == 0) {
                    tabLayout.put(1, y, ChatColor.GRAY + "Starts: &f" + TimeUtils.formatIntoMMSS(tournament.getBeginNextRoundIn()));
                } else {
                    tabLayout.put(1, y, ChatColor.GRAY + "Next Round: " + TimeUtils.formatIntoMMSS(tournament.getBeginNextRoundIn()));
                }
            } else if (tournament.getStage() == Tournament.TournamentStage.IN_PROGRESS) {
                int teamSize = tournament.getRequiredPartySize();
                int multiplier = teamSize < 3 ? teamSize : 1;

                tabLayout.put(0, y, ChatColor.GRAY + "Round" + ": " + tournament.getCurrentRound());
                tabLayout.put(1, y, ChatColor.GRAY + (teamSize < 3 ? "Players" : "Teams") + ": " + tournament.getActiveParties().size() * multiplier);
                tabLayout.put(2, y, ChatColor.GRAY + "Duration: " + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
            }

        }

        rankedgame: {
            if (game == null) {
                break rankedgame;
            }

            y += 2;

            tabLayout.put(1, y++, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Ranked Team");

            int x = 0;

            for (UUID member : getOrderedPlayers(player, game.getTeam(player))) {
                int ping = PotPvPLayoutProvider.getPingOrDefault(member);
                String suffix = member == game.getTeam(player).getCaptain() ? ChatColor.GRAY + "*" : "";
                String displayName = (game.getJoinedPlayers().contains(member) ? ChatColor.GREEN : ChatColor.GRAY) + UUIDUtils.name(member) + suffix;

                tabLayout.put(x++, y, displayName/*, ping*/);

                if (x == 3 && y == PotPvPLayoutProvider.MAX_TAB_Y) {
                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    // player first, leader next, then all other members
    private Set<UUID> getOrderedMembers(Player viewer, Party party) {
        Set<UUID> orderedMembers = Sets.newSetFromMap(new LinkedHashMap<>());
        UUID leader = party.getLeader();

        orderedMembers.add(viewer.getUniqueId());

        // if they're the leader we don't display them twice
        if (viewer.getUniqueId() != leader) {
            orderedMembers.add(leader);
        }

        for (UUID member : party.getMembers()) {
            // don't display the leader or the viewer again
            if (member == leader || member == viewer.getUniqueId()) {
                continue;
            }

            orderedMembers.add(member);
        }

        return orderedMembers;
    }

    private Set<UUID> getOrderedPlayers(Player viewer, RankedGameTeam team) {
        Set<UUID> orderedMembers = Sets.newSetFromMap(new LinkedHashMap<>());
        UUID captain = team.getCaptain();

        orderedMembers.add(viewer.getUniqueId());

        // if they're the leader we don't display them twice
        if (viewer.getUniqueId() != captain) {
            orderedMembers.add(captain);
        }

        for (UUID member : team.getPlayers()) {
            // don't display the leader or the viewer again
            if (member == captain || member == viewer.getUniqueId()) {
                continue;
            }

            orderedMembers.add(member);
        }

        return orderedMembers;
    }

}