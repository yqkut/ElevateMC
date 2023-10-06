package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyAccessRestriction;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyPasswordCommand {

    @Command(names = {"party password", "p password", "t password", "team password", "party pass", "p pass", "t pass", "team pass", "f password", "f pass"}, permission = "")
    public static void partyPassword(Player sender, @Parameter(name = "password") String password) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PASSWORD);
            party.setPassword(password);

            sender.sendMessage(ChatColor.DARK_AQUA + "۩ " + ChatColor.AQUA + "Your party's password is now " + ChatColor.RED + password + ChatColor.AQUA + ".");
        }
    }

}
