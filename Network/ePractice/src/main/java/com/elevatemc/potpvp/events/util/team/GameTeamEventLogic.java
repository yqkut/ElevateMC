package com.elevatemc.potpvp.events.util.team;

import com.elevatemc.potpvp.events.event.GameEventLogic;
import com.elevatemc.potpvp.events.event.impl.lms.LastManStandingGameEvent;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class GameTeamEventLogic implements GameEventLogic {

    protected final Game game;

    protected Map<UUID, UUID> invites = Maps.newHashMap();
    protected Set<GameTeam> participants = Sets.newHashSet();

    public GameTeamEventLogic(Game game) {
        this.game = game;
    }

    @Override
    public void start() {
        if(game.getParameter(GameTeamSizeParameter.class) != null) {
            generateTeams();
        } else {
            for(Player player : game.getPlayers()) {
                participants.add(new GameTeam(ImmutableList.of(player)));
            }
        }

        invites.clear();
    }

    private void generateTeams() {
        for(Player player : game.getPlayers()) {
            if(!contains(player)) {
                for(Player other : game.getPlayers()) {
                    if(player != other && !contains(other)) {
                        player.sendMessage(Color.translate("&eYou were automatically put into a team with &d" + PatchedPlayerUtils.getFormattedName(other.getUniqueId()) + "&e."));
                        other.sendMessage(Color.translate("&eYou were automatically put into a team with &d" + PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "&e."));

                        final GameTeam team = new GameTeam(ImmutableList.of(player, other));
                        participants.add(team);

                        continue;
                    }
                }

                // TODO: lms condition
                if(game.getEvent() instanceof LastManStandingGameEvent) {
                    final GameTeam team = new GameTeam(ImmutableList.of(player));
                    participants.add(team);
                } else {
                    player.sendMessage(Color.translate("&cWe couldn't find a player for you to team up with, so you were sent to the lobby."));
                    game.addSpectator(player);
                }
            }
        }
    }

    public GameTeam get(Player player) {
        for(GameTeam participant : participants) {
            if(participant.getPlayers().contains(player)) {
                return participant;
            }
        }

        return null;
    }

    public boolean contains(Player player) {
        for(GameTeam participant : participants) {
            if(participant.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }
}
