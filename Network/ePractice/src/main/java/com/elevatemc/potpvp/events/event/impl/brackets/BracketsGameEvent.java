package com.elevatemc.potpvp.events.event.impl.brackets;

import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.events.event.GameEventLogic;
import com.elevatemc.potpvp.events.event.impl.sumo.SumoGameEventLogic;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.parameter.GameParameter;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.events.util.team.GameTeamSizeParameter;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BracketsGameEvent implements GameEvent {

    private static final String NAME = "Brackets";
    private static final String PERMISSION = "potpvp.host.brackets";
    private static final String DESCRIPTION = "Compete against other players in brackets.";

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
        return new ItemStack(Material.IRON_SWORD);
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
        return new SumoGameEventLogic(game);
    }

    @Override
    public String getNameTag(Game game, Player player, Player viewer) {
        if(!(game.getLogic() instanceof BracketsGameEventLogic)) return "";
        final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

        if((logic.getInvites().containsKey(player.getUniqueId()) && logic.getInvites().get(player.getUniqueId()).equals(viewer.getUniqueId())) || (logic.getInvites().containsKey(viewer.getUniqueId()) && logic.getInvites().get(viewer.getUniqueId()).equals(player.getUniqueId()))) {
            return ChatColor.YELLOW.toString();
        }

        final GameTeam participant = logic.get(player);
        if(participant != null && participant.getPlayers().contains(viewer)) {
            return ChatColor.GREEN.toString();
        }

        if(participant == null && game.getState() != GameState.STARTING) {
            return ChatColor.GRAY.toString();
        }

        return ChatColor.RED.toString();
    }

    @Override
    public List<String> getScoreboardScores(Player player, Game game) {
        final List<String> lines = new ArrayList<>();
        final SumoGameEventLogic logic = (SumoGameEventLogic) game.getLogic();
        String name = NAME;

        if(game.getParameter(GameTeamSizeParameter.Duos.class) != null) {
            name = "2v2 " + name;
        }

        lines.add("&cEvent &7(" + name + ")");
        if(game.getState() == GameState.STARTING) {
            lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fStarting: &7" + (TimeUtils.formatIntoDetailedString((int) ((game.getStartingAt() + 500 - System.currentTimeMillis()) / 1000))));
        }

        lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fPlayers: &7" + logic.getPlayersLeft() + "/" + game.getMaxPlayers());


        if(game.getState() == GameState.RUNNING) {
            lines.add("&6 " + PotPvPLang.LEFT_ARROW_NAKED + " &fRound: &7" + logic.getRound());
            if(game.getParameter(GameTeamSizeParameter.Duos.class) == null) {
                final GameTeam fighter = logic.getNextParticipant(null);
                final GameTeam opponent = logic.getNextParticipant(fighter);

                if(opponent != null && fighter != null) {
                    lines.add(fighter.getName() + " &7vs " + opponent.getName());
                }
            } else {
                // TODO: Duos lines
                final GameTeam fighter = logic.getNextParticipant(null);
                final GameTeam opponent = logic.getNextParticipant(fighter);

                if(opponent != null && fighter != null) {
                    lines.add(fighter.getName());
                    lines.add("&7vs");
                    lines.add(opponent.getName());
                }
            }
        }

        return lines;
    }

    @Override
    public List<Listener> getListeners() {
        return ImmutableList.of(new BracketsGameEventListener());
    }

    @Override
    public List<GameParameter> getParameters() {
        return ImmutableList.of(
                new GameTeamSizeParameter(),
                new BracketsGameKitParameter()
        );
    }

    @Override
    public List<ItemStack> getLobbyItems() {
        return ImmutableList.of();
    }
}
