package com.elevatemc.potpvp.party.command;

import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public final class PartyHelpCommand {

    private static final List<String> HELP_MESSAGE = ImmutableList.of(
            ChatColor.GRAY + PotPvPLang.LONG_LINE,
            "§bParty Commands:",
            "§3/party create §7- §fCreates a party",
            "§3/party invite §7- §fInvite a player to join your party",
            "§3/party leave §7- §fLeave your current party",
            "§3/party accept (player) §7- §fAccept party invitation",
            "§3/party info (player) §7- §fView the info of a players party",
            "",
            "§bLeader Commands:",
            "§3/party open §7 - §fOpen your party for other players",
            "§3/party lock §7 - §fLock party from others joining",
            "§3/party password <password> §7 - §fSets a password",
            "§3/party kick (player) §7- §fKick a player from your party",
            "§3/party disband §7 - §fDisband the party",
            "",
            "To use §3party chat§f, prefix your message with the §3@ §fsign.",
            ChatColor.GRAY + PotPvPLang.LONG_LINE
    );

    @Command(names = {"party", "p", "t", "team", "f", "party help", "p help", "t help", "team help", "f help"}, permission = "")
    public static void party(Player sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

}