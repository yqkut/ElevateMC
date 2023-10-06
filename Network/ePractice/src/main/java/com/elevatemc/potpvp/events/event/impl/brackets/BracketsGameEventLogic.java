package com.elevatemc.potpvp.events.event.impl.brackets;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.events.bukkit.event.PlayerGameInteractionEvent;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.elevatemc.potpvp.events.util.GameEventCountdown;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.events.util.team.GameTeamEventLogic;
import com.elevatemc.potpvp.util.VisibilityUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.stream.Collectors;

public class BracketsGameEventLogic extends GameTeamEventLogic {

    public BracketsGameEventLogic(Game game) {
        super(game);
    }

    @Override
    public void start() {
        super.start();

        next();
    }

    public void check() {
        final GameTeam winner = getWinner();
        if(winner == null) return;
        final GameTeam loser = getLoser();
        if(loser == null) return;

        winner.reset();
        winner.setRound(winner.getRound() + 1);
        winner.setFighting(false);

        participants.remove(loser);

        for(Player player : winner.getPlayers()) {
            game.reset(player);
        }

        for(Player player : loser.getPlayers()) {
            game.getSpectators().add(player);
            game.reset(player);
        }

        if(getNextParticipant(winner) == null) {
            broadcastWinner(winner);

            TaskUtil.runTaskLater(game::end, 40);
            game.end();

            return;
        }

        game.sendMessage("", winner.getName() + " &ebeat " + loser.getName() + "&e!", "");
        next();
    }

    private void broadcastWinner(GameTeam winner) {
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

    private void next() {
        final GameTeam fighter = getNextParticipant(null);
        final GameTeam opponent = getNextParticipant(fighter);

        if(fighter != opponent && fighter != null && opponent != null) {
            if(fighter.getRound() != opponent.getRound()) {
                fighter.setRound(Math.max(fighter.getRound(), opponent.getRound()));
                opponent.setRound(fighter.getRound());
            }

            game.sendMessage("", "&e&lNext Matchup:", fighter.getName() + "&e vs. " + opponent.getName() + "&e!", "");

            fighter.setStarting(true);
            opponent.setStarting(true);

            new GameEventCountdown(5, () -> {
                fighter.setStarting(false);
                opponent.setStarting(false);

                fighter.setFighting(true);
                opponent.setFighting(true);

                fighter.showTeamsPlayers(opponent);
                opponent.showTeamsPlayers(fighter);

                fighter.getPlayers().forEach(player -> player.removePotionEffect(PotionEffectType.INVISIBILITY));
                opponent.getPlayers().forEach(player -> player.removePotionEffect(PotionEffectType.INVISIBILITY));
            }, ImmutableList.of(fighter, opponent));

            if(game.getEvent() instanceof BracketsGameEvent && game.getParameter(BracketsGameKitParameter.BracketsGameKitOption.class) == null) {
                game.end(true);
                game.sendMessage("&cFailed to start round. Kit parameter is not set up properly.");
                return;
            }

            final BracketsGameKitParameter.BracketsGameKitOption kit = (BracketsGameKitParameter.BracketsGameKitOption) game.getParameter(BracketsGameKitParameter.BracketsGameKitOption.class);

            for(int i=0; i<fighter.getPlayers().size(); i++) {
                final Player player = fighter.getPlayers().get(i);

                game.getSpectators().remove(player);
                player.getInventory().clear();
                player.setSprinting(false);
                player.updateInventory();
                player.setVelocity(new Vector());
                player.teleport(game.getFirstSpawnLocations().get(i));

                game.getSpectators().remove(player);
                Bukkit.getPluginManager().callEvent(new PlayerGameInteractionEvent(player, game));

                if(kit != null) kit.apply(player);
            }

            for(int i=0; i<opponent.getPlayers().size(); i++) {
                final Player player = opponent.getPlayers().get(i);

                game.getSpectators().remove(player);
                player.getInventory().clear();
                player.setSprinting(false);
                player.updateInventory();
                player.setVelocity(new Vector());
                player.teleport(game.getSecondSpawnLocations().get(i));

                game.getSpectators().remove(player);
                Bukkit.getPluginManager().callEvent(new PlayerGameInteractionEvent(player, game));

                if(kit != null) kit.apply(player);
            }

            return;
        }

        game.end();
    }

    public int getRound() {
        final GameTeam next = getNextParticipant(null);
        return 1 + (next == null ? 0 : next.getRound());
    }

    public GameTeam getNextParticipant(GameTeam exclude) {
        GameTeam current = null;

        for(GameTeam participant : participants) {
            if(participant != exclude) {
                if(current == null || participant.getRound() < current.getRound()) {
                    current = participant;
                }
            }
        }

        return current;
    }

    private GameTeam getWinner() {
        for(GameTeam participant : participants) {
            if(participant.isFighting() && !participant.isFinished()) {
                return participant;
            }
        }

        return null;
    }

    private GameTeam getLoser() {
        for(GameTeam participant : participants) {
            if(participant.isFighting() && participant.isFinished()) {
                return participant;
            }
        }

        return null;
    }

    public int getPlayersLeft() {
        if(game.getState() == GameState.STARTING) return game.getPlayers().size();

        int toReturn = 0;

        for(GameTeam participant : participants) {
            toReturn += participant.getPlayers().size();
        }

        return toReturn;
    }
}
