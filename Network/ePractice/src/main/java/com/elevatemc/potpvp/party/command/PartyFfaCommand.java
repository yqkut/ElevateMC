package com.elevatemc.potpvp.party.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.menu.select.SelectArenaMenu;
import com.elevatemc.potpvp.arena.menu.select.teamfight.SelectArenaTeamfightCategoryMenu;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.menu.select.SelectGameModeMenu;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PartyFfaCommand {

    @Command(names = {"party ffa", "p ffa", "t ffa", "team ffa", "f ffa"}, permission = "")
    public static void partyFfa(Player sender) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectGameModeMenu(gameMode -> {
                if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                    new SelectArenaTeamfightCategoryMenu(category -> {
                        new SelectArenaMenu(gameMode, category, arenaName -> {
                            sender.closeInventory();

                            if (!PotPvPValidation.canStartFfa(party, sender)) {
                                return;
                            }

                            List<MatchTeam> teams = new ArrayList<>();

                            for (UUID member : party.getMembers()) {
                                teams.add(new MatchTeam(member));
                            }

                            matchHandler.startMatch(teams, gameMode, arenaName,false, false);
                        }).openMenu(sender);
                    }).openMenu(sender);
                } else {
                    new SelectArenaMenu(gameMode, arenaName -> {
                        sender.closeInventory();

                        if (!PotPvPValidation.canStartFfa(party, sender)) {
                            return;
                        }

                        List<MatchTeam> teams = new ArrayList<>();

                        for (UUID member : party.getMembers()) {
                            teams.add(new MatchTeam(member));
                        }

                        matchHandler.startMatch(teams, gameMode, arenaName,false, false);
                    }).openMenu(sender);
                }
            }, "Select gamemode for FFA").openMenu(sender);
        }
    }

}