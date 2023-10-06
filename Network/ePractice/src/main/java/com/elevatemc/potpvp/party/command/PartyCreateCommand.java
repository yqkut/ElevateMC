package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.hctranked.game.RankedGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyCreateCommand {

    @Command(names = {"party create", "p create", "t create", "team create", "f create"}, permission = "")
    public static void partyCreate(Player sender) {
        RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(sender);
        if (game != null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't create a party while in a ranked game.");
            return;
        }

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You are already in a party.");
            return;
        }

        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "✔ " + ChatColor.GREEN + "Your party has been created!");
    }
}