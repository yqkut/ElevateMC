package com.elevatemc.potpvp.duel.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.duel.DuelHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchCountdownStartEvent;
import com.elevatemc.potpvp.match.event.MatchSpectatorJoinEvent;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.event.PartyDisbandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class DuelListener implements Listener {

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        Player player = event.getSpectator();

        duelHandler.removeInvitesTo(player);
        duelHandler.removeInvitesFrom(player);
    }

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        Party party = event.getParty();

        duelHandler.removeInvitesTo(party);
        duelHandler.removeInvitesFrom(party);
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                duelHandler.removeInvitesTo(memberPlayer);
                duelHandler.removeInvitesFrom(memberPlayer);
            }
        }
    }

}