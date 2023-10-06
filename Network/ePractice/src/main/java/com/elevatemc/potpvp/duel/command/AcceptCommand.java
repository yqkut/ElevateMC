package com.elevatemc.potpvp.duel.command;

import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.duel.DuelHandler;
import com.elevatemc.potpvp.duel.DuelInvite;
import com.elevatemc.potpvp.duel.PartyDuelInvite;
import com.elevatemc.potpvp.duel.PlayerDuelInvite;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class AcceptCommand {

    @Command(names = {"accept"}, permission = "")
    public static void accept(Player sender, @Parameter(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot accept duel requests from yourself.");
            return;
        }

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            // party accepting from party (legal)
            PartyDuelInvite invite = duelHandler.findInvite(targetParty, senderParty);

            if (invite != null) {
                acceptParty(sender, senderParty, targetParty, invite);
            } else {
                // we grab the leader's name as the member targeted might not be the leader
                String leaderName = UUIDUtils.name(targetParty.getLeader());
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your party doesn't have a duel invite from " + leaderName + "'s party.");
            }
        } else if (senderParty == null && targetParty == null) {
            // player accepting from player (legal)
            PlayerDuelInvite invite = duelHandler.findInvite(target, sender);

            if (invite != null) {
                acceptPlayer(sender, target, invite);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You don't have a duel invite from " + target.getName() + ".");
            }
        } else if (senderParty == null) {
            // player accepting from party (illegal)
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You don't have a duel invite from " + target.getName() + ".");
        } else {
            // party accepting from player (illegal)
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your party doesn't have a duel invite from " + target.getName() + "'s party.");
        }
    }

    private static void acceptParty(Player sender, Party senderParty, Party targetParty, DuelInvite invite) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();

        if (!senderParty.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
            return;
        }

        if (!PotPvPValidation.canAcceptDuel(senderParty, targetParty, sender)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(senderParty.getMembers()), new MatchTeam(targetParty.getMembers())),
                invite.getGameMode(),
                invite.getArenaName(),
                false,
                true // see Match#allowRematches
        );

        if (match != null) {
            // only remove invite if successful
            duelHandler.removeInvite(invite);
        } else {
            senderParty.message(PotPvPLang.ERROR_WHILE_STARTING_MATCH);
            targetParty.message(PotPvPLang.ERROR_WHILE_STARTING_MATCH);
        }
    }

    private static void acceptPlayer(Player sender, Player target, DuelInvite invite) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();

        if (!PotPvPValidation.canAcceptDuel(sender, target)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(sender.getUniqueId()), new MatchTeam(target.getUniqueId())),
                invite.getGameMode(),
                invite.getArenaName(),
                false,
                true // see Match#allowRematches
        );

        if (match != null) {
            // only remove invite if successful
            duelHandler.removeInvite(invite);
        } else {
            sender.sendMessage(PotPvPLang.ERROR_WHILE_STARTING_MATCH);
            target.sendMessage(PotPvPLang.ERROR_WHILE_STARTING_MATCH);
        }
    }

}