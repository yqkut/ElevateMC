package com.elevatemc.potpvp.queue;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MatchQueue {

    @Getter private final GameMode gameMode;
    @Getter private final boolean competitive;
    private final List<MatchQueueEntry> entries = new CopyOnWriteArrayList<>();

    MatchQueue(GameMode gameMode, boolean competitive) {
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
        this.competitive = competitive;
    }

    void tick() {
        // we clone so we can remove entries from our working set
        // (sometimes matches fail to create [ex no maps open] and
        // we should retry)
        List<MatchQueueEntry> entriesCopy = new ArrayList<>(entries);
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

        // ranked match algorithm requires entries are in
        // order by elo. There's no reason we only do this for ranked
        // matches aside from performance
        if (competitive) {
            entriesCopy.sort(Comparator.comparing(e -> eloHandler.getElo(e.getMembers(), gameMode)));
        }

        while (entriesCopy.size() >= 2) {
            // remove from 0 both times because index shifts down
            MatchQueueEntry a = entriesCopy.remove(0);
            MatchQueueEntry b = entriesCopy.remove(0);

            // the algorithm for ranked and unranked queues is actually very similar,
            // except for the fact ranked matches can't be made if the elo window for
            // both players don't overlap
            if (competitive) {
                int aElo = eloHandler.getElo(a.getMembers(), gameMode);
                int bElo = eloHandler.getElo(b.getMembers(), gameMode);

                int aEloWindow = a.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                int bEloWindow = b.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;

                if (Math.abs(aElo - bElo) > Math.max(aEloWindow, bEloWindow)) {
                    continue;
                }
            }

            createMatchAndRemoveEntries(a, b);
        }
    }

    public int countPlayersQueued() {
        int count = 0;

        for (MatchQueueEntry entry : entries) {
            count += entry.getMembers().size();
        }

        return count;
    }

    void addToQueue(MatchQueueEntry entry) {
        entries.add(entry);
    }

    void removeFromQueue(MatchQueueEntry entry) {
        entries.remove(entry);
    }

    private void createMatchAndRemoveEntries(MatchQueueEntry entryA, MatchQueueEntry entryB) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        MatchTeam teamA = new MatchTeam(entryA.getMembers());
        MatchTeam teamB = new MatchTeam(entryB.getMembers());

        Match match = matchHandler.startMatch(
            ImmutableList.of(teamA, teamB),
                gameMode,
            null,
                competitive,
            !competitive // allowRematches is the inverse of ranked
        );

        // only remove entries if match creation was successfull
        if (match != null) {
            queueHandler.removeFromQueueCache(entryA);
            queueHandler.removeFromQueueCache(entryB);

            String teamAElo = "";
            String teamBElo = "";

            if (competitive) {
                EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

                teamAElo = " (" + eloHandler.getElo(teamA.getAliveMembers(), gameMode) + " Elo)";
                teamBElo = " (" + eloHandler.getElo(teamB.getAliveMembers(), gameMode) + " Elo)";
            }


            String foundStart = ChatColor.DARK_AQUA.toString() + ChatColor.BOLD +  (competitive ? "Competitive" : "Casual") + " Match";
            String ladder = "➥ Ladder: " + ChatColor.DARK_AQUA + gameMode.getName();
            String opponentStart = "➥ Opponent: " + ChatColor.DARK_AQUA;
            String map = "➥ Map: " + ChatColor.DARK_AQUA + PotPvPSI.getInstance().getArenaHandler().getSchematic(match.getArena().getSchematic()).getDisplayName();
            teamA.messageAlive("");
            teamB.messageAlive("");
            teamA.messageAlive(foundStart);
            teamB.messageAlive(foundStart);
            teamA.messageAlive(ladder);
            teamB.messageAlive(ladder);
            teamA.messageAlive(opponentStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamB.getAllMembers())) + teamBElo);
            teamB.messageAlive(opponentStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamA.getAllMembers())) + teamAElo);
            teamA.messageAlive(map);
            teamB.messageAlive(map);
            teamA.messageAlive("");
            teamB.messageAlive("");
            entries.remove(entryA);
            entries.remove(entryB);
        }
    }

}