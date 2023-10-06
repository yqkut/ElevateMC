package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyKickCommand {

    @Command(names = {"party kick", "p kick", "t kick", "team kick", "f kick"}, permission = "")
    public static void partyKick(Player sender, @Parameter(name = "player") Player target) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (sender == target) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot kick yourself.");
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " isn't in your party.");
        } else {
            party.kick(target);
        }
    }

}