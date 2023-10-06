package com.elevatemc.potpvp.elo.listener;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.base.Joiner;
import com.elevatemc.potpvp.elo.EloCalculator;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchEndEvent;
import com.elevatemc.potpvp.match.event.MatchTerminateEvent;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public final class EloUpdateListener implements Listener {

    private static final String ELO_CHANGE_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&eElo Changes: &a%s +%d (%d) &c%s -%d (%d)");
    private static final int COINS_ON_WIN = 10;

    private final EloHandler eloHandler;
    private final EloCalculator eloCalculator;

    public EloUpdateListener(EloHandler eloHandler, EloCalculator eloCalculator) {
        this.eloHandler = eloHandler;
        this.eloCalculator = eloCalculator;
    }

    // we actually save elo when the match first ends but only
    // send messages when it terminates (when players go back to
    // the lobby)
    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        GameMode gameMode = match.getGameMode();
        List<MatchTeam> teams = match.getTeams();

        if (!match.isRanked() || teams.size() != 2 || match.getWinner() == null) {
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        final int winningElo = eloHandler.getElo(winnerTeam.getAllMembers(), gameMode);
        final int losingElo = eloHandler.getElo(loserTeam.getAllMembers(), gameMode);

        EloCalculator.Result result = eloCalculator.calculate(
            winningElo,
            losingElo
        );

        // Clan handler access elo change
        final int deltaWin = Math.abs(result.getWinnerNew() - winningElo);
        final int deltaLose = Math.abs(losingElo - result.getLoserNew());

        final int winningPointsEarned = deltaWin / 10;
        final int losingPointsLost = deltaLose / 10;
        eloHandler.setElo(winnerTeam.getAllMembers(), gameMode, result.getWinnerNew());
        eloHandler.setElo(loserTeam.getAllMembers(), gameMode, result.getLoserNew());

        match.setEloChange(result);
    }

    // see comment on onMatchEnd method
    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();
        EloCalculator.Result result = match.getEloChange();

        if (result == null) {
            return;
        }

        List<MatchTeam> teams = match.getTeams();
        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        String winnerStr;
        String loserStr;

        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {
            winnerStr = UUIDUtils.name(winnerTeam.getFirstMember());
            loserStr = UUIDUtils.name(loserTeam.getFirstMember());
        } else {
            winnerStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(winnerTeam.getAllMembers()));
            loserStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(loserTeam.getAllMembers()));
        }

        // we negate loser gain to convert negative gain to positive (which we prefix with - in the string)
        match.messageAll(String.format(ELO_CHANGE_MESSAGE, winnerStr, result.getWinnerGain(), result.getWinnerNew(), loserStr, -result.getLoserGain(), result.getLoserNew()));
    }

}