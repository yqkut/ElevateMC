package com.elevatemc.potpvp.tournament;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.menu.extra.SelectNoDebuffOrDebuff;
import com.elevatemc.potpvp.gamemode.menu.select.SelectGameModeMenu;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.pvpclasses.PvPClasses;
import com.elevatemc.potpvp.tournament.menu.StatusMenu;
import com.elevatemc.potpvp.util.Color;
import lombok.Getter;
import lombok.Setter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TournamentHandler implements Listener {

    @Getter @Setter private Tournament tournament = null;
    private static TournamentHandler instance;

    public TournamentHandler() {
        instance = this;
        eLib.getInstance().getCommandHandler().registerClass(this.getClass());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PotPvPSI.getInstance(), () -> {
            if (tournament != null) tournament.check();
        }, 20L, 20L);
    }

    public boolean isInTournament(Party party) {
        return tournament != null && tournament.isInTournament(party);
    }

    public boolean isInTournament(Match match) {
        return tournament != null && tournament.getMatches().contains(match);
    }

    @Command(names = { "tournament createteamfight" }, permission = "tournament.create")
    public static void tournamentCreateteamfight(Player sender, @Parameter(name = "team-size") int teamSize, @Parameter(name = "bards") int bards, @Parameter(name = "archers") int archers) {
        if (instance.getTournament() != null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There's already an ongoing tournament!");
            return;
        }

        if (teamSize < 1 || teamSize > 50) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The team size must be between 1 and 100.");
            return;
        }

        new SelectNoDebuffOrDebuff(isDebuff -> {
            sender.closeInventory();
            Tournament tournament;
            instance.setTournament(tournament = new Tournament(isDebuff ? GameModes.TEAMFIGHT_DEBUFF : GameModes.TEAMFIGHT, teamSize, bards, archers));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (instance.getTournament() == tournament) {
                        tournament.broadcastJoinMessage();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(PotPvPSI.getInstance(), 0, 60 * 20);
        }).openMenu(sender);
    }

    @Command(names = { "tournament create" }, permission = "tournament.create")
    public static void tournamentCreate(Player sender, @Parameter(name = "team-size") int teamSize) {
        if (instance.getTournament() != null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There's already an ongoing tournament!");
            return;
        }

        if (teamSize < 1 || teamSize > 50) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "The team size must be between 1 and 50.");
            return;
        }

        new SelectGameModeMenu(gameMode -> {
            sender.closeInventory();
            Tournament tournament;
            instance.setTournament(tournament = new Tournament(gameMode, teamSize));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (instance.getTournament() == tournament) {
                        tournament.broadcastJoinMessage();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(PotPvPSI.getInstance(), 0, 60 * 20);
        }, "Select a kit type").openMenu(sender);
    }

    @Command(names = { "tournament join", "join", "jointournament" }, description = "Join a running tournament.", permission = "")
    public static void tournamentJoin(Player sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There is no running tournament to join.");
            return;
        }

        int tournamentTeamSize = instance.getTournament().getRequiredPartySize();

        if ((instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) && (instance.getTournament().getCurrentRound() != 0 || !sender.hasPermission("tournaments.joinduringcountdown"))) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        Party senderParty = PotPvPSI.getInstance().getPartyHandler().getParty(sender);
        if (senderParty == null) {
            if (tournamentTeamSize == 1) {
                senderParty = PotPvPSI.getInstance().getPartyHandler().getOrCreateParty(sender); // Will auto put them in a party
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You don't have a team to join the tournament with!");
                return;
            }
        }

        int notInLobby = 0;
        int queued = 0;
        for (UUID member : senderParty.getMembers()) {
            if (!PotPvPSI.getInstance().getLobbyHandler().isInLobby(Bukkit.getPlayer(member))) {
                notInLobby++;
            }

            if (PotPvPSI.getInstance().getQueueHandler().getQueueEntry(member) != null) {
                queued++;
            }
        }

        if (notInLobby != 0) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team aren't in the lobby.");
            return;
        }

        if (queued != 0) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team are currently queued.");
            return;
        }

        if (!senderParty.getLeader().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must be the leader of your team to join the tournament.");
            return;
        }

        if (senderParty.getMembers().size() != instance.getTournament().getRequiredPartySize()) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need exactly " + instance.getTournament().getRequiredPartySize() + " members in your party to join the tournament.");
            return;
        }

        if (instance.isInTournament(senderParty)) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your team is already in the tournament!");
            return;
        }

        if (PotPvPSI.getInstance().getQueueHandler().getQueueEntry(senderParty) != null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't join the tournament if your party is currently queued.");
            return;
        }

        Collection<PvPClasses> kits = senderParty.getKits().values();
        GameMode gameMode = instance.getTournament().getType();
        if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
            int bards = Collections.frequency(kits, PvPClasses.BARD);
            int archers = Collections.frequency(kits, PvPClasses.ARCHER);
            int rogues = Collections.frequency(kits, PvPClasses.ROGUE);

            int tournBards = instance.getTournament().getBards();
            int tournArchers = instance.getTournament().getArchers();

            if (rogues > 0) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Rogues are not allowed during a tournament.");
                return;
            }

            if (tournBards == 0 && tournArchers == 0 && bards > 0 && archers > 0) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "This tournament is all diamond only.");
                return;
            }

            if (bards != tournBards) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need exactly " + tournBards + " bard" + ((tournBards == 1) ? "" : "s") + " for this tournament.");
                return;
            }

            if (archers != tournArchers) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You need exactly " + tournArchers + " archer" + ((tournArchers == 1) ? "" : "s") + " for this tournament.");
                return;
            }
        } else if (kits.size() > 0) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "This tournament does not support teamfight kits.");
            return;
        }

        senderParty.message(ChatColor.GREEN + "Joined the tournament.");
        instance.getTournament().addParty(senderParty);
    }

    @Command(names = { "tournament status", "tstatus", "status" }, description = "Check the tournament status.", permission = "")
    public static void tournamentStatus(Player sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There is no ongoing tournament to get the status of.");
            return;
        }

        long matches = PotPvPSI.getInstance().getTournamentHandler().getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED && m.getState() != MatchState.ENDING).count();

        if (matches > 0) {
            new StatusMenu().openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There are no matches to spectate at this moment.");
        }
    }

    @Command(names = { "tournament cancel"},  permission = "tournament.cancel")
    public static void tournamentCancel(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There is no running tournament to cancel.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(Color.translate("&fThe &3tournament&f was cancelled."));
        Bukkit.broadcastMessage("");
        instance.setTournament(null);
    }

    @Command(names = { "tournament start"}, permission = "tournament.start")
    public static void tournamentForceStart(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There is no tournament to force start.");
            return;
        }

        if (instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        instance.getTournament().start();
        sender.sendMessage(ChatColor.GREEN + "Force started tournament.");
    }
}