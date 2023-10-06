package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.command.Command;
import org.bukkit.entity.Player;

public final class PartyDisbandCommand {

    @Command(names = {"party disband", "p disband", "t disband", "team disband", "f disband"}, permission = "")
    public static void partyDisband(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            return;
        }

        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
            return;
        }

        party.disband();
    }

}