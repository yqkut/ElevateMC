package com.elevatemc.potpvp.events.event.impl.lms;

import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.events.event.GameEventLogic;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.parameter.GameParameter;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.events.util.team.GameTeamEventLogic;
import com.elevatemc.potpvp.events.util.team.GameTeamSizeParameter;
import com.elevatemc.potpvp.util.Color;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LastManStandingGameEvent implements GameEvent {
    private static final String NAME = "LMS";
    private static final String PERMISSION = "potpvp.host.lms";
    private static final String DESCRIPTION = "Compete against other players to be the last man standing.";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getPermission() {
        return PERMISSION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.TNT);
    }

    @Override
    public boolean canStart(Game game) {
        if(game.getParameter(GameTeamSizeParameter.Duos.class) != null) {
            return game.getPlayers().size() >= 4;
        }

        return game.getPlayers().size() >= 2;
    }

    @Override
    public GameEventLogic getLogic(Game game) {
        return new LastManStandingGameEventLogic(game);
    }

    @Override
    public String getNameTag(Game game, Player player, Player viewer) {
        if(game.getLogic() instanceof GameTeamEventLogic) {
            final GameTeamEventLogic logic = (GameTeamEventLogic) game.getLogic();

            if((logic.getInvites().containsKey(player.getUniqueId()) && logic.getInvites().get(player.getUniqueId()).equals(viewer.getUniqueId())) || (logic.getInvites().containsKey(viewer.getUniqueId()) && logic.getInvites().get(viewer.getUniqueId()).equals(player.getUniqueId()))) {
                return ChatColor.YELLOW.toString();
            }

            final GameTeam team = logic.get(player);
            // both players are on the same team, and its a team based gamemode, therefore they need green colors to denote that they are on the same team
            if(team != null && team.getPlayers().contains(viewer)) {
                return ChatColor.GREEN.toString();
            }
        }

        // player is not on the same team, or its not a team gamemode, so the player is an enemy
        return ChatColor.RED.toString();
    }

    @Override
    public List<String> getScoreboardScores(Player player, Game game) {
        final List<String> lines = new ArrayList<>();
        final LastManStandingGameEventLogic logic = (LastManStandingGameEventLogic) game.getLogic();
        String name = NAME;

        if(game.getParameter(GameTeamSizeParameter.Duos.class) != null) {
            name = "2v2 " + name;
        }

        lines.add("&cEvent &7(" + name + ")");
        if(game.getState() == GameState.STARTING) {
            lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fStarting: &7" + (TimeUtils.formatIntoDetailedString((int)((game.getStartingAt() + 500 - System.currentTimeMillis()) / 1000))));
        } else {
            lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fTime: &7" + TimeUtils.formatIntoDetailedString((int)((System.currentTimeMillis() - game.getStartingAt()) / 1000)));
        }
        lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fPlayers: &7" + logic.getPlayersLeft() + "/" + game.getMaxPlayers());

        if(game.getState() == GameState.RUNNING) {
            lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fKills: &7" + player.getStatistic(Statistic.PLAYER_KILLS));
        }

        return lines;
    }

    @Override
    public List<Listener> getListeners() {
        return ImmutableList.of(new LastManStandingGameEventListeners());
    }

    @Override
    public List<GameParameter> getParameters() {
        return ImmutableList.of(
                new GameTeamSizeParameter(),
                new LastManStandingGameEventKitParameter()
        );
    }

    @Override
    public List<ItemStack> getLobbyItems() {
        return ImmutableList.of();
    }

    @Override
    public int getMaxInstances() {
        return 5;
    }
}
