package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.party.event.PartyMemberJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * When a player joins a party, attempt to have them spectate their
 * party's active match, if there is one.
 * https://github.com/ElevateOrb/PotPvP-SI/issues/32
 */
public final class MatchPartySpectateListener implements Listener {

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match leaderMatch = matchHandler.getMatchPlayingOrSpectating(Bukkit.getPlayer(event.getParty().getLeader()));

        if (leaderMatch != null) {
            leaderMatch.addSpectator(event.getMember(), null);
        }
    }

}