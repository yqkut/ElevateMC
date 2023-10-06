package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyAccessRestriction;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyOpenCommand {

    @Command(names = {"party open", "p open", "t open", "team open", "f open", "party unlock", "p unlock", "t unlock", "team unlock", "f unlock"}, permission = "")
    public static void partyOpen(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.PUBLIC) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your party is already open.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
            sender.sendMessage(ChatColor.DARK_AQUA + "۩ " + ChatColor.AQUA + "Your party is now " + ChatColor.GREEN + "open" + ChatColor.AQUA + ".");
        }
    }

}
