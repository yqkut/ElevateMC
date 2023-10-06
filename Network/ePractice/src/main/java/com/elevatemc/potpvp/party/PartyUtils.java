package com.elevatemc.potpvp.party;

import com.elevatemc.potpvp.arena.menu.select.teamfight.SelectArenaTeamfightCategoryMenu;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.menu.select.SelectGameModeMenu;
import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.menu.select.SelectArenaMenu;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.party.menu.oddmanout.OddManOutMenu;
import com.elevatemc.potpvp.pvpclasses.PvPClasses;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@UtilityClass
public final class PartyUtils {

    public static void startTeamSplit(Party party, Player initiator) {
        // will be called again but we fail fast if possible
        if (!PotPvPValidation.canStartTeamSplit(party, initiator)) {
            return;
        }

        new SelectGameModeMenu(gameMode -> {
            if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                new SelectArenaTeamfightCategoryMenu(category -> {
                    new SelectArenaMenu(gameMode, category, arenaName -> {
                        initiator.closeInventory();

                        if (party.getMembers().size() % 2 == 0) {
                            startTeamSplit(party, initiator, gameMode, arenaName, false);
                        } else {
                            new OddManOutMenu(oddManOut -> {
                                initiator.closeInventory();
                                startTeamSplit(party, initiator, gameMode, arenaName, oddManOut);
                            }).openMenu(initiator);
                        }
                    }).openMenu(initiator);
                }).openMenu(initiator);
            } else {
                new SelectArenaMenu(gameMode, arenaName -> {
                    initiator.closeInventory();

                    if (party.getMembers().size() % 2 == 0) {
                        startTeamSplit(party, initiator, gameMode, arenaName, false);
                    } else {
                        new OddManOutMenu(oddManOut -> {
                            initiator.closeInventory();
                            startTeamSplit(party, initiator, gameMode, arenaName, oddManOut);
                        }).openMenu(initiator);
                    }
                }).openMenu(initiator);
            }
        }, "Select gamemode for teamsplit").openMenu(initiator);
    }

    public static void startTeamSplit(Party party, Player initiator, GameMode gameMode, String arenaName, boolean oddManOut) {
        if (!PotPvPValidation.canStartTeamSplit(party, initiator)) {
            return;
        }

        List<UUID> members = new ArrayList<>(party.getMembers());
        Collections.shuffle(members);

        Set<UUID> team1 = new HashSet<>();
        Set<UUID> team2 = new HashSet<>();
        Player spectator = null; // only can be one

        if (gameMode.equals(GameModes.TEAMFIGHT)) {
            members.sort((a, b) -> {
                PvPClasses firstPvPClass = party.getKits().getOrDefault(a, PvPClasses.DIAMOND);
                PvPClasses secondPvPClass = party.getKits().getOrDefault(b, PvPClasses.DIAMOND);
                int firstWeight = 0;
                int secondWeight = 0;
                switch (firstPvPClass) {
                    case DIAMOND:
                        firstWeight = 1;
                        break;
                    case ROGUE:
                        firstWeight = 2;
                        break;
                    case ARCHER:
                        firstWeight = 3;
                        break;
                    case BARD:
                        firstWeight = 4;
                }
                switch (secondPvPClass) {
                    case DIAMOND:
                        secondWeight = 1;
                        break;
                    case ROGUE:
                        secondWeight = 2;
                        break;
                    case ARCHER:
                        secondWeight = 3;
                        break;
                    case BARD:
                        secondWeight = 4;
                        break;
                }
                return secondWeight - firstWeight;
            });
        }

        while (members.size() >= 2) {
            team1.add(members.remove(0));
            team2.add(members.remove(0));
        }

        if (!members.isEmpty()) {
            if (oddManOut) {
                spectator = Bukkit.getPlayer(members.remove(0));
                party.message(ChatColor.DARK_AQUA + "✎ " + ChatColor.AQUA + spectator.getName() + " was selected as the odd-man out.");
            } else {
                team1.add(members.remove(0));
            }
        }

        Match match = PotPvPSI.getInstance().getMatchHandler().startMatch(
            ImmutableList.of(
                new MatchTeam(team1),
                new MatchTeam(team2)
            ),
                gameMode,
            arenaName,
            false,
            false
        );

        if (match == null) {
            initiator.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Failed to start team split.");
            return;
        }

        if (spectator != null) {
            match.addSpectator(spectator, null);
        }
    }

}