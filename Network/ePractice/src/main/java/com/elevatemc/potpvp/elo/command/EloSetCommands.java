package com.elevatemc.potpvp.elo.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class EloSetCommands {

    @Command(names = {"elo setSolo"}, permission = "op")
    public static void eloSetSolo(Player sender, @Parameter(name="target") Player target, @Parameter(name="gamemode") GameMode gameMode, @Parameter(name="new elo") int newElo) {
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();
        eloHandler.setElo(target, gameMode, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + target.getName() + "'s " + gameMode.getName() + " elo to " + newElo + ".");
    }

    @Command(names = {"elo setTeam"}, permission = "op")
    public static void eloSetTeam(Player sender, @Parameter(name="target") Player target, @Parameter(name="gamemode") GameMode gameMode, @Parameter(name="new elo") int newElo) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

        Party targetParty = partyHandler.getParty(target);

        if (targetParty == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }

        eloHandler.setElo(targetParty.getMembers(), gameMode, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + gameMode.getName() + " elo of " + UUIDUtils.name(targetParty.getLeader()) + "'s party to " + newElo + ".");
    }

}