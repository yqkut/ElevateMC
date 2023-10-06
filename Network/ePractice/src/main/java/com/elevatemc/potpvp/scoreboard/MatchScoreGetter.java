package com.elevatemc.potpvp.scoreboard;

import com.elevatemc.elib.scoreboard.construct.ScoreFunction;
import com.elevatemc.elib.util.Pair;
import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.util.ClickTracker;
import com.google.common.collect.ImmutableMap;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.HealingMethod;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.pvpclasses.pvpclasses.ArcherClass;
import com.elevatemc.potpvp.pvpclasses.pvpclasses.BardClass;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.*;
import java.util.function.BiConsumer;

final class MatchScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    private Map<UUID, Integer> healsLeft = ImmutableMap.of();

    MatchScoreGetter() {
        Bukkit.getScheduler().runTaskTimer(PotPvPSI.getInstance(), () -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
            Map<UUID, Integer> newHealsLeft = new HashMap<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Match playing = matchHandler.getMatchPlaying(player);

                if (playing == null) {
                    continue;
                }

                HealingMethod healingMethod = playing.getGameMode().getHealingMethod();

                if (healingMethod == null) {
                    continue;
                }

                int count = healingMethod.count(player.getInventory().getContents());
                newHealsLeft.put(player.getUniqueId(), count);
            }

            this.healsLeft = newHealsLeft;
        }, 10L, 10L);
    }

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);

        boolean participant = match.getTeam(player.getUniqueId()) != null;

        if (participant) {
            renderParticipantLines(scores, match, player, style);
        } else {
            MatchTeam previousTeam = match.getPreviousTeam(player.getUniqueId());
            renderSpectatorLines(scores, match, previousTeam, style);
        }
    }

    private void renderParticipantLines(List<String> scores, Match match, Player player, Pair<ChatColor, ChatColor> style) {
        List<MatchTeam> teams = match.getTeams();
        GameMode gameMode = match.getGameMode();

        // only render scoreboard if we have two teams
        if (teams.size() != 2) {

            if (gameMode.equals(GameModes.BOXING)) {
                scores.add("First to 100 hits wins");
            } else if (gameMode.equals(GameModes.PEARL_FIGHT)) {
                scores.add("Everyone starts with 3 lives");
            } else {
                scores.add("FFA Match");
            }
            return;
        }

        // this method won't be called if the player isn't a participant
        MatchTeam ourTeam = match.getTeam(player.getUniqueId());
        if (ourTeam == null) {
            return;
        }
        MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

        // we use getAllMembers instead of getAliveMembers to avoid
        // mid-match scoreboard changes as players die / disconnect
        int ourTeamSize = ourTeam.getAllMembers().size();
        int otherTeamSize = otherTeam.getAllMembers().size();

        if (ourTeamSize == 1 && otherTeamSize == 1) {
            render1v1MatchLines(scores, ourTeam, otherTeam, match.getState(), gameMode, style);
        } else if (ourTeamSize <= 2 && otherTeamSize <= 2) {
            render2v2MatchLines(scores, ourTeam, otherTeam, player, match.getGameMode().getHealingMethod(), gameMode, style);
        } else if (ourTeamSize <= 4 && otherTeamSize <= 4) {
            // We don't want to make the scoreboard too large if we also have boxing lines
            if (!match.getGameMode().equals(GameModes.BOXING)) {
                render4v4MatchLines(scores, ourTeam, otherTeam, style);
            } else {
                renderLargeMatchLines(scores, ourTeam, otherTeam, gameMode, style);
            }

        } else if (ourTeam.getAllMembers().size() <= 9) {
            renderLargeMatchLines(scores, ourTeam, otherTeam, gameMode, style);
        } else {
            renderJumboMatchLines(scores, ourTeam, otherTeam, gameMode, style);
        }

        String archerMarkScore = getArcherMarkScore(player);
        String bardEnergyScore = getBardEnergyScore(player);

        if (archerMarkScore != null) {
            scores.add("&6Archer Mark: " + style.getValue() + archerMarkScore);
        }

        if (bardEnergyScore != null) {
            scores.add("&eBard Energy: " + style.getValue() + bardEnergyScore);
        }
    }


    private void render1v1MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, MatchState state, GameMode gameMode, Pair<ChatColor, ChatColor> style) {
        Player otherPlayer = Bukkit.getPlayer(otherTeam.getFirstMember());
        Player ourPlayer = Bukkit.getPlayer(ourTeam.getFirstMember());

        String ping;
        String cps;
        if (otherPlayer != null) {
            ping = String.valueOf(PlayerUtils.getPing(otherPlayer));
            cps = String.valueOf(ClickTracker.getCPS(otherPlayer));
        } else {
            ping = "LOGGED";
            cps = "LOGGED";
        }

        scores.add(style.getKey() + ChatColor.BOLD.toString() + "Match");
        scores.add(style.getKey() + "❘" + style.getValue() + " Opponent: " + style.getKey() + UUIDUtils.name(otherTeam.getFirstMember()));
        scores.add(style.getKey() + "❘" + style.getValue() + " CPS: " + GREEN +
                        ClickTracker.getCPS(ourPlayer) + " CPS" + ChatColor.GRAY + " ❘ " + RED + cps  + " CPS");

        scores.add(style.getKey() + "❘" + style.getValue() + " Ping: " + GREEN +
                PlayerUtils.getPing(ourPlayer) + " ms" + ChatColor.GRAY + " ❘ " + RED + ping + " ms");



        if (gameMode.equals(GameModes.BOXING)) {
            int ourHits = ourTeam.getHits();
            int otherHits = otherTeam.getHits();
            String extra = (ourHits >= otherHits) ? "&a(+" + (ourHits - otherHits) + ")" : "&c(" + (ourHits - otherHits) + ")";
            scores.add("");
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Boxing " + extra);
            scores.add(style.getKey() + "❘" + style.getValue() + " You: " + GREEN + ourHits);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED + otherHits);

        } else if (gameMode.equals(GameModes.PEARL_FIGHT)) {
            int ourLives = ourTeam.getLives();
            int otherLives = otherTeam.getLives();
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Lives: ");
            scores.add(style.getKey() + "❘" + style.getValue() + " You: " + GREEN + ourLives);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED +  otherLives);
            scores.add("");
        }
    }

    private void render2v2MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, Player player, HealingMethod healingMethod, GameMode gameMode, Pair<ChatColor, ChatColor> style) {
        // 2v2, but potentially 1v2 / 1v1 if players have died
        UUID partnerUuid = null;

        scores.add("&aTeam");

        for (UUID teamMember : ourTeam.getAllMembers()) {
            if (teamMember != player.getUniqueId()) {
                partnerUuid = teamMember;
                break;
            }
        }

        if (partnerUuid != null) {
            String healthStr;
            String healsStr;
            String namePrefix;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;
                int heals = healsLeft.getOrDefault(partnerUuid, 0);

                ChatColor healthColor;
                ChatColor healsColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = ChatColor.GOLD;
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                if (heals > 20) {
                    healsColor = ChatColor.GREEN;
                } else if (heals > 12) {
                    healsColor = ChatColor.YELLOW;
                } else if (heals > 8) {
                    healsColor = ChatColor.GOLD;
                } else if (heals > 3) {
                    healsColor = ChatColor.RED;
                } else {
                    healsColor = ChatColor.DARK_RED;
                }

                namePrefix = "&f";
                healthStr = healthColor.toString() + health + " *❤*" + ChatColor.GRAY;

                if (healingMethod != null) {
                    healsStr = " &l⏐ " + healsColor + heals + " " + (heals == 1 ? healingMethod.getShortSingular() : healingMethod.getShortPlural());
                } else {
                    healsStr = "";
                }
            } else {
                namePrefix = "&7&m";
                healthStr = "&cDead";
                healsStr = "";
            }

            scores.add(namePrefix + UUIDUtils.name(partnerUuid));
            scores.add(healthStr + healsStr);
            scores.add("&b");
        }

        scores.add("&cOpponents");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam, style));

        if (gameMode.equals(GameModes.BOXING)) {
            scores.add("");
            int ourHits = ourTeam.getHits();
            int otherHits = otherTeam.getHits();
            String extra = (ourHits >= otherHits) ? "&a(+" + (ourHits - otherHits) + ")" : "&c(" + (ourHits - otherHits) + ")";
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Boxing " + extra);
            scores.add(style.getKey() + "❘" + style.getValue() + " Team: " + GREEN + ourHits);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED + otherHits);
        } else if (gameMode.equals(GameModes.PEARL_FIGHT)) {
            int ourLives = ourTeam.getLives();
            int otherLives = otherTeam.getLives();
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Lives: ");
            scores.add(style.getKey() + "❘" + style.getValue() + " Team: " + GREEN + ourLives);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED +  otherLives);
            scores.add("");
        }
    }

    private void render4v4MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, Pair<ChatColor, ChatColor> style) {
        // Above a 2v2, but up to a 4v4.
        scores.add("&aTeam " + style.getValue() + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add("");
        scores.add("&cOpponents " + style.getValue() + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam, style));

    }

    private void renderLargeMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, GameMode gameMode, Pair<ChatColor, ChatColor> style) {
        // We just display THEIR team's names, and the other team is a number.
        scores.add("&aTeam " + style.getValue() + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add("");
        scores.add("&cOpponents: " + style.getValue() + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
        if (gameMode.equals(GameModes.BOXING)) {
            scores.add("");
            int ourHits = ourTeam.getHits();
            int otherHits = otherTeam.getHits();
            String extra = (ourHits >= otherHits) ? "&a(+" + (ourHits - otherHits) + ")" : "&c(" + (ourHits - otherHits) + ")";
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Boxing " + extra);
            scores.add(style.getKey() + "❘" + style.getValue() + " Team: " + GREEN + ourHits);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED + otherHits);
        } else if (gameMode.equals(GameModes.PEARL_FIGHT)) {
            int ourLives = ourTeam.getLives();
            int otherLives = otherTeam.getLives();
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Lives: ");
            scores.add(style.getKey() + "❘" + style.getValue() + " Team: " + GREEN + ourLives);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED +  otherLives);
            scores.add("");
        }
    }

    private void renderJumboMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, GameMode gameMode, Pair<ChatColor, ChatColor> style) {
        // We just display numbers.
        scores.add("&aTeam: " + style.getValue() + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size());
        scores.add("&cOpponents: " + style.getValue() + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
        if (gameMode.equals(GameModes.BOXING)) {
            scores.add("");
            int ourHits = ourTeam.getHits();
            int otherHits = otherTeam.getHits();
            String extra = (ourHits >= otherHits) ? "&a(+" + (ourHits - otherHits) + ")" : "&c(" + (ourHits - otherHits) + ")";
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Boxing " + extra);
            scores.add(style.getKey() + "❘" + style.getValue() + " Team: " + GREEN + ourHits);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED + otherHits);
        } else if (gameMode.equals(GameModes.PEARL_FIGHT)) {
            int ourLives = ourTeam.getLives();
            int otherLives = otherTeam.getLives();
            scores.add(style.getKey() + ChatColor.BOLD.toString() + "Lives: ");
            scores.add(style.getKey() + "❘" + style.getValue() + " Team: " + GREEN + ourLives);
            scores.add(style.getKey() + "❘" + ChatColor.RESET + " Opponent: " + RED +  otherLives);
            scores.add("");
        }
    }

    private void renderSpectatorLines(List<String> scores, Match match, MatchTeam oldTeam, Pair<ChatColor, ChatColor> style) {
        List<MatchTeam> teams = match.getTeams();

        // only render team overview if we have two teams
        if (teams.size() == 2) {
            MatchTeam teamOne = teams.get(0);
            MatchTeam teamTwo = teams.get(1);

            if (teamOne.getAllMembers().size() != 1 && teamTwo.getAllMembers().size() != 1) {
                // spectators who were on a team see teams as they releate
                // to them, not just one/two.
                if (oldTeam == null) {
                    scores.add("&aTeam One&7: " + style.getValue() + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size());
                    scores.add("&cTeam Two&7: " + style.getValue() + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size());
                } else {
                    MatchTeam otherTeam = oldTeam == teamOne ? teamTwo : teamOne;

                    scores.add("&aYour Team&7: " + style.getValue() + oldTeam.getAliveMembers().size() + "/" + oldTeam.getAllMembers().size());
                    scores.add("&cTheir Team&7: " + style.getValue() + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
                }

                if (match.getGameMode().equals(GameModes.BOXING)) {
                    scores.add("");
                    int teamOneHitsHits = teamOne.getHits();
                    int teamTwoHits = teamTwo.getHits();
                    String extra = (teamOneHitsHits >= teamTwoHits) ? "&a(+" + (teamOneHitsHits - teamTwoHits) + ")" : "&c(" + (teamOneHitsHits - teamTwoHits) + ")";
                    scores.add(style.getKey() + ChatColor.BOLD.toString() + "Boxing " + extra);
                    scores.add(style.getKey() + "❘" + " &aTeam One&7: " + style.getValue() + teamOneHitsHits);
                    scores.add(style.getKey() + "❘" + " &cTeam Two&7: " + style.getValue() + teamTwoHits);
                } else if (match.getGameMode().equals(GameModes.PEARL_FIGHT)) {
                    int ourLives = teamOne.getLives();
                    int otherLives = teamTwo.getLives();
                    scores.add(style.getKey() + ChatColor.BOLD.toString() + "Lives: ");
                    scores.add(style.getKey() + "❘" + " &dTeam One&7: " + style.getKey() + ourLives);
                    scores.add(style.getKey() + "❘" + " &bTeam Two&7: " + style.getKey() + otherLives);
                    scores.add("");
                }
            } else {
                Player firstPlayer = Bukkit.getPlayer(teamOne.getFirstMember());
                Player otherPlayer = Bukkit.getPlayer(teamTwo.getFirstMember());
                if (firstPlayer != null) scores.add(firstPlayer.getName() + "'s Ping: " + style.getKey() + PlayerUtils.getPing(firstPlayer) + "ms");
                if (otherPlayer != null) scores.add(otherPlayer.getName() + "'s Ping: " + style.getKey() + PlayerUtils.getPing(otherPlayer) + "ms");
            }
        } else {
            scores.add("FFA Match");
        }

    }

    /* Returns the names of all alive players, colored + indented, followed
       by the names of all dead players, colored + indented. */

    private List<String> renderTeamMemberOverviewLinesWithHearts(MatchTeam team) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + UUIDUtils.name(teamMember) + " " + getHeartString(team, teamMember));
            } else {
                deadLines.add(" &7&m" + UUIDUtils.name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

    private List<String> renderTeamMemberOverviewLines(MatchTeam team, Pair<ChatColor, ChatColor> style) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + UUIDUtils.name(teamMember));
            } else {
                deadLines.add(" &7&m" + UUIDUtils.name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

    private String getHeartString(MatchTeam ourTeam, UUID partnerUuid) {
        if (partnerUuid != null) {
            String healthStr;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;

                ChatColor healthColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = ChatColor.GOLD;
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                healthStr = healthColor + "(" + health + " ❤)";
            } else {
                healthStr = "&c(Dead)";
            }

            return healthStr;
        } else {
            return "&c(Dead)";
        }
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }
}