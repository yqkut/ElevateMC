package com.elevatemc.potpvp.tournament;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.setting.Setting;
import com.elevatemc.potpvp.util.Color;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Tournament {

    // TODO: EDIT WHOLE CLASS'S STYLING

    @Getter private int currentRound = -1;

    @Getter private List<Party> activeParties = Lists.newArrayList();
    private List<Party> lost = Lists.newArrayList();

    @Getter private int requiredPartySize;
    @Getter private GameMode type;
    @Getter private int bards;
    @Getter private int archers;

    @Getter private List<Match> matches = Lists.newArrayList();

    @Getter private int beginNextRoundIn = 31;

    // We do this because players can leave a party or the server during the tournament
    // We will need to ensure that at the end of the tournament we clear this
    // (or make sure the Tournament object is unreachable)
    private Map<UUID, Party> partyMap = Maps.newHashMap();

    @Getter private TournamentStage stage = TournamentStage.WAITING_FOR_TEAMS;

    @Getter private long roundStartedAt;

    public Tournament(GameMode type, int partySize) {
        this.type = type;
        this.requiredPartySize = partySize;
        this.bards = 0;
        this.archers = 0;
    }

    public Tournament(GameMode type, int partySize, int bards, int archers) {
        this.type = type;
        this.requiredPartySize = partySize;
        this.bards = bards;
        this.archers = archers;
    }

    public void addParty(Party party) {
        activeParties.add(party);
        checkActiveParties();
        joinedTournament(party);
    }

    public boolean isInTournament(Party party) {
        return activeParties.contains(party);
    }

    public void check() {
        checkActiveParties();
        populatePartyMap();
        checkMatches();

        if (matches.stream().anyMatch(s -> s != null && s.getState() != MatchState.TERMINATED)) return; // We don't want to advance to the next round if any matches are ongoing
        matches.clear();

        if (currentRound == -1) return;

        if (activeParties.isEmpty()) {
            if (lost.isEmpty()) {
                stage = TournamentStage.FINISHED;
                PotPvPSI.getInstance().getTournamentHandler().setTournament(null);
                return;
            }

            // shouldn't happen, meant that the two last parties disconnected at the last second
            Bukkit.broadcastMessage(Color.translate("&cThe tournament's last two teams forfeited. Winner by default: " + PatchedPlayerUtils.getFormattedName((lost.get(lost.size() - 1)).getLeader()) + "'s team!"));
            PotPvPSI.getInstance().getTournamentHandler().setTournament(null); // Removes references to this tournament, will get cleaned up by GC
            stage = TournamentStage.FINISHED;
            return;
        }

        if (activeParties.size() == 1) {
            Party party = activeParties.get(0);
            if (party.getMembers().size() == 1) {
                repeatMessage(Color.translate("&3&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + " &fwon the tournament!"), 4, 2);
            } else if (party.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = party.getMembers().iterator();
                UUID[] members = new UUID[] { membersIterator.next(), membersIterator.next() };
                repeatMessage(Color.translate("&3&l" + PatchedPlayerUtils.getFormattedName(members[0]) + " &fand &3&l" + PatchedPlayerUtils.getFormattedName(members[1]) + " &fwon the tournament!"), 4, 2);
            } else {
                repeatMessage(Color.translate("&3&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + "&f's team won the tournament!"), 4, 2);
            }

            activeParties.clear();
            PotPvPSI.getInstance().getTournamentHandler().setTournament(null);
            stage = TournamentStage.FINISHED;
            return;
        }

        if (--beginNextRoundIn >= 1) {
            switch (beginNextRoundIn) {
                case 30:
                case 15:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                case 1:
                    if (currentRound == 0) {
                        int teamSize = this.getRequiredPartySize();
                        int multiplier = teamSize < 3 ? teamSize : 1;

                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(Color.translate("&3&lElevate Tournament"));
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(Color.translate("&fMode: &3" + type.getName()));
                        Bukkit.broadcastMessage(Color.translate("&fStarting: &3" + beginNextRoundIn + " &3second" + (beginNextRoundIn == 1 ? "" : "s") + "."));
                        Bukkit.broadcastMessage("");
                    } else {
                        Bukkit.broadcastMessage(Color.translate("&3&lRound " + (currentRound + 1) + " &fwill begin in &3" + beginNextRoundIn + " &fsecond" + (beginNextRoundIn == 1 ? "" : "s") + "."));
                    }
            }

            stage = TournamentStage.COUNTDOWN;
            return;
        }

        startRound();
    }

    private void checkActiveParties() {
        Set<UUID> realParties = PotPvPSI.getInstance().getPartyHandler().getParties().stream().map(Party::getPartyId).collect(Collectors.toSet());
        Iterator<Party> activePartyIterator = activeParties.iterator();
        while (activePartyIterator.hasNext()) {
            Party activeParty = activePartyIterator.next();
            if (!realParties.contains(activeParty.getPartyId())) {
                activePartyIterator.remove();

                if (!lost.contains(activeParty)) {
                    lost.add(activeParty);
                }
            }
        }
    }

    private void repeatMessage(String message, int times, int interval) {
        new BukkitRunnable() {

            private int runs = times;

            @Override
            public void run() {
                if (0 <= --runs) {
                    Bukkit.broadcastMessage(message);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(PotPvPSI.getInstance(), 0, interval * 20);
    }

    public void start() {
        if (currentRound == -1) {
            currentRound = 0;
        }
    }

    private void joinedTournament(Party party) {
        broadcastJoinMessage(party);
    }

    private void populatePartyMap() {
        activeParties.forEach(p -> p.getMembers().forEach(u -> {
            partyMap.put(u, p);
        }));
    }

    private void startRound() {
        beginNextRoundIn = 31;
        // Next round has begun...

        Bukkit.broadcastMessage(Color.translate("&3&lRound " + ++currentRound + " &fhas begun. Good luck!"));
        Bukkit.broadcastMessage(Color.translate("&fUse &3/status &fto see who is fighting."));

        List<Party> oldPartyList = Lists.newArrayList(activeParties);
        Collections.shuffle(oldPartyList);
        // Doing it this way will ensure that the tournament runs BUT if one party
        // disconnects every round, the bottom party could get to the final round without
        // winning a single duel. Could shuffle? But would remove the predictability & pseudo-bracket system
        while (1 < oldPartyList.size()) {
            Party firstParty = oldPartyList.remove(0);
            Party secondParty = oldPartyList.remove(0);

            matches.add(PotPvPSI.getInstance().getMatchHandler().startMatch(ImmutableList.of(new MatchTeam(firstParty.getMembers()), new MatchTeam(secondParty.getMembers())), type, null, false, false));
        }

        if (oldPartyList.size() == 1) {
            oldPartyList.get(0).message(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There were an odd number of teams in this round - so your team has advanced to the next round.");
        }

        stage = TournamentStage.IN_PROGRESS;
        roundStartedAt = System.currentTimeMillis();
    }

    private void checkMatches() {
        Iterator<Match> matchIterator = matches.iterator();
        while (matchIterator.hasNext()) {
            Match match = matchIterator.next();
            if (match == null) {
                matchIterator.remove();
                continue;
            }

            if (match.getState() != MatchState.TERMINATED) continue;
            MatchTeam winner = match.getWinner();
            List<MatchTeam> losers = Lists.newArrayList(match.getTeams());
            losers.remove(winner);
            MatchTeam loser = losers.get(0);
            Party loserParty = partyMap.get(loser.getFirstMember());
            if (loserParty != null) {
                activeParties.remove(loserParty);
                broadcastEliminationMessage(loserParty);
                lost.add(loserParty);
                matchIterator.remove();
            }
        }
    }

    public void broadcastJoinMessage() {
        int teamSize = this.getRequiredPartySize();

        if (this.getCurrentRound() != -1) return;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Elevate Tournament");
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "❘ " + ChatColor.WHITE + "Team Size: " + ChatColor.DARK_AQUA + teamSize + "v" + teamSize);
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "❘ " + ChatColor.WHITE + "Mode: " + ChatColor.DARK_AQUA + type.getName());
        if (type.equals(GameModes.TEAMFIGHT) || type.equals(GameModes.TEAMFIGHT_DEBUFF)) {
            String kits;
            if (bards == 0 && archers == 0) {
                kits = "All diamonds";
            } else if (bards == 1 && archers == 0) {
                kits = bards + " Bard & No Archers";
            } else if (bards > 0 && archers == 0) {
                kits = bards + " Bards & No Archers";
            } else if (bards == 0 && archers == 1) {
                kits = archers + " Archer & No Bards";
            } else if (bards == 0 && archers > 0) {
                kits = archers + " Archers & No Bards";
            } else if (bards == 1 && archers == 1) {
                kits = bards + " Bard " + archers + " Archer";
            } else {
                kits = bards + " Bards " + archers + " Archers";
            }
            Bukkit.broadcastMessage(ChatColor.GRAY + " » " + ChatColor.DARK_AQUA + "Kits: " + ChatColor.WHITE + kits);
        }


        TextComponent JOIN_TOURNAMENT = new TextComponent("[Click to Join]");

        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT; // readability
        BaseComponent[] acceptTooltip = new ComponentBuilder("Click to join").color(net.md_5.bungee.api.ChatColor.GREEN).create();

        JOIN_TOURNAMENT.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        JOIN_TOURNAMENT.setHoverEvent(new HoverEvent(showText, acceptTooltip));

        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        JOIN_TOURNAMENT.setClickEvent(new ClickEvent(runCommand, "/join"));

        Bukkit.spigot().broadcast(JOIN_TOURNAMENT);
        Bukkit.broadcastMessage("");
    }

    private void broadcastJoinMessage(Party joiningParty) {
        String message;
        if (joiningParty.getMembers().size() == 1) {
            message = "&3" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&f has joined the &3tournament&f.";
        } else if (joiningParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
            message ="&3" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&f and &3" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&f have joined the &3tournament&7.";
        } else {
            message = "&3" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&f's team has joined the &3tournament&f.";
        }

        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (joiningParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_JOIN_MESSAGE)) {
                player.sendMessage(Color.translate(message));
            }
        }
    }

    private void broadcastEliminationMessage(Party loserParty) {
        String message;

        if (loserParty.getMembers().size() == 1) {
            message = "&3" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&f has been eliminated.";
        } else if (loserParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = loserParty.getMembers().iterator();
            message = "&3" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&f and &3" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + " &fwere eliminated.";
        } else {
            message = "&3" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&f's team has been eliminated.";
        }

        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (loserParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES)) {
                player.sendMessage(Color.translate(message));
            }
        }
    }

    public enum TournamentStage {
        WAITING_FOR_TEAMS,
        COUNTDOWN,
        IN_PROGRESS,
        FINISHED
    }
}