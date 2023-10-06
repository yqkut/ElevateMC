package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.command.Command;
import org.bukkit.entity.Player;

public final class PartyLeaveCommand {

    @Command(names = {"party leave", "p leave", "t leave", "team leave", "leave", "f leave"}, permission = "")
    public static void partyLeave(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else {
            party.leave(sender);
        }
    }

}