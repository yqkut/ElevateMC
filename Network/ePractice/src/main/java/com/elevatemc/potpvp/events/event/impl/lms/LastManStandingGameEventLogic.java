package com.elevatemc.potpvp.events.event.impl.lms;

import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.elevatemc.potpvp.events.util.GameEventCountdown;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.events.util.team.GameTeamEventLogic;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LastManStandingGameEventLogic extends GameTeamEventLogic {

    public LastManStandingGameEventLogic(Game game) {
        super(game);
    }

    @Override
    public void start() {
        super.start();

        final AtomicInteger index = new AtomicInteger();

        participants.forEach(team -> {
            team.setStarting(true);
            team.getPlayers().forEach(player -> {
                player.getInventory().clear();
                player.setStatistic(Statistic.PLAYER_KILLS, 0);
                player.teleport(game.getArena().getEventSpawns().get(index.get()));
            });

            index.getAndIncrement();
        });

        if(game.getParameter(LastManStandingGameEventKitParameter.LastManStandingKitOption.class) == null) {
            game.end(true);
            game.sendMessage("&cFailed to start match. Kit parameter is not set up properly.");
            return;
        }

        final LastManStandingGameEventKitParameter.LastManStandingKitOption kit = (LastManStandingGameEventKitParameter.LastManStandingKitOption) game.getParameter(LastManStandingGameEventKitParameter.LastManStandingKitOption.class);

        new GameEventCountdown(5, () -> {
            for(GameTeam participant : participants) {
                participant.setStarting(false);
                participant.setFighting(true);

                participant.getPlayers().forEach(((LastManStandingGameEventKitParameter.LastManStandingKitOption) kit)::apply);
            }
        }, new ArrayList<>(participants));
    }

    public void check() {
        final List<GameTeam> alive = new ArrayList<>();

        for(GameTeam team : participants) {
            if(!team.isFinished()) {
                alive.add(team);
            }
        }

        if(alive.size() == 1) {
            final GameTeam winner = alive.get(0);
            broadcastWinner(winner);
            end();
        } else if(alive.size() == 0) {
            end();
        }
    }

    public void end() {
        game.end();

        for(Player player : game.getPlayers()) {
            player.setStatistic(Statistic.PLAYER_KILLS, 0);
        }
    }

    public int getPlayersLeft() {
        if(game.getState() == GameState.STARTING) return game.getPlayers().size();

        int toReturn = 0;

        for(GameTeam participant : participants) {
            for(Player player : participant.getPlayers()) {
                if(!(participant.hasDied(player))) {
                    toReturn++;
                }
            }
        }

        return toReturn;
    }

    public void broadcastWinner(GameTeam winner) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(new String[]{"",
                ChatColor.GRAY + "███████",
                ChatColor.GRAY + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GOLD + "[" + game.getEvent().getName() + " Event Winner]",
                ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█████" + " ",
                ChatColor.GRAY + "█" + ChatColor.GOLD + "████" + ChatColor.GRAY + "██" + " " + winner.getName() + ChatColor.GRAY + " has won the event!",
                ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█████" + " ",
                ChatColor.GRAY + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GRAY + ChatColor.ITALIC + "Event Type: (" + game.getParameters().stream().map(GameParameterOption::getDisplayName).collect(Collectors.joining(", ")) + ")",
                ChatColor.GRAY + "███████",
                ""}
        ));
    }
}
