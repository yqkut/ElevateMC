package com.elevatemc.potpvp.events;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.bukkit.event.GameStateChangeEvent;
import com.elevatemc.potpvp.events.bukkit.event.PlayerGameInteractionEvent;
import com.elevatemc.potpvp.events.bukkit.event.PlayerJoinGameEvent;
import com.elevatemc.potpvp.events.bukkit.event.PlayerQuitGameEvent;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.menu.EventsMenu;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.events.util.team.GameTeamEventLogic;
import com.elevatemc.potpvp.events.util.team.GameTeamSizeParameter;
import com.elevatemc.potpvp.lobby.LobbyUtils;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import com.elevatemc.potpvp.util.VisibilityUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getItem() != null && event.getItem().equals(EventItems.getEventItem()) && PotPvPSI.getInstance().getLobbyHandler().isInLobby(player)) {
            new EventsMenu().openMenu(player);
        }

    }

    @EventHandler
    public void onGameStateChangeEvent(GameStateChangeEvent event) {
        Game game = event.getGame();

        if (event.getTo() == GameState.ENDED) {
            if(game.getArena().isInUse()) PotPvPSI.getInstance().getArenaHandler().releaseArena(game.getArena());
            for (Player player : game.getPlayers()) {
                eLib.getInstance().getNameTagHandler().reloadPlayer(player);
                eLib.getInstance().getNameTagHandler().reloadOthersFor(player);
                VisibilityUtils.updateVisibility(player);
                PotPvPSI.getInstance().getLobbyHandler().returnToLobby(player);
                LobbyUtils.resetInventory(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinGameEvent(PlayerJoinGameEvent event) {
        eLib.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        eLib.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
        for (Player player : event.getGame().getPlayers()) {
            VisibilityUtils.updateVisibility(player);
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerQuitGameEvent event) {
        eLib.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        eLib.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
        PotPvPSI.getInstance().getLobbyHandler().returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameInteractionEvent(PlayerGameInteractionEvent event) {
        eLib.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        eLib.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
        VisibilityUtils.updateVisibility(event.getPlayer());
    }

    // PlayerInteractAtEntityEvent - Working on 1.8

    @EventHandler
    public void onInteractPlayerEvent(PlayerInteractEntityEvent event) {
        final Player sender = event.getPlayer();
        if(!(event.getRightClicked() instanceof Player)) return;
        final Player receiver = (Player) event.getRightClicked();

        final Game game = GameQueue.INSTANCE.getCurrentGame(sender);
        if(game == null) return; // Player is not in game
        if(game.getState() != GameState.STARTING) return; // Game is not in starting state
        if(!game.getPlayers().contains(receiver)) return; // Receiver is not in game somehow

        if(!(game.getLogic() instanceof GameTeamEventLogic)) return; // Does not use the team based event layout
        if(game.getParameter(GameTeamSizeParameter.Duos.class) == null) return; // Solo
        final GameTeamEventLogic logic = (GameTeamEventLogic) game.getLogic();

        /*
        Invites Map
            key - sender
            value - receiver
         */

        // check to see if the players are already on the same team, if they are ignore
        if(logic.get(sender) != null && logic.get(sender).getPlayers().contains(receiver)) return;

        // check to see if the sender had a request from the receiver
        if(logic.getInvites().containsKey(receiver.getUniqueId()) && logic.getInvites().get(receiver.getUniqueId()).equals(sender.getUniqueId())) {
            // if one of the players was already in a team, disband that team and create this new one
            removeExistingTeam(sender, logic);
            removeExistingTeam(receiver, logic);

            // create their team
            final GameTeam team = new GameTeam(ImmutableList.of(sender, receiver));
            logic.getParticipants().add(team);

            // send message letting them know that they are now on the same team
            sender.sendMessage(Color.translate("&d" + PatchedPlayerUtils.getFormattedName(receiver.getUniqueId()) + " &eis now on your team."));
            receiver.sendMessage(Color.translate("&d" + PatchedPlayerUtils.getFormattedName(sender.getUniqueId()) + " &eis now on your team."));
            eLib.getInstance().getNameTagHandler().reloadPlayer(sender);
            eLib.getInstance().getNameTagHandler().reloadOthersFor(receiver);
            return;
        }

        // if they didn't have a request, send the request
        logic.getInvites().put(sender.getUniqueId(), receiver.getUniqueId());

        // send messages to both parties letting them know about the request
        sender.sendMessage(Color.translate("&eYou have sent a team request to &d" + PatchedPlayerUtils.getFormattedName(receiver.getUniqueId()) + "&e."));
        receiver.sendMessage(Color.translate("&eYou have received a team request from &d" + PatchedPlayerUtils.getFormattedName(sender.getUniqueId()) + "&e."));

    }

    private void removeExistingTeam(Player receiver, GameTeamEventLogic logic) {
        if(logic.get(receiver) != null) {
            final GameTeam team = logic.get(receiver);
            logic.getParticipants().remove(team);
            team.getOthers(receiver).forEach(other -> {
                other.sendMessage(Color.translate("&d" + PatchedPlayerUtils.getFormattedName(receiver.getUniqueId()) + " &chas left your team."));
                eLib.getInstance().getNameTagHandler().reloadPlayer(other);
                eLib.getInstance().getNameTagHandler().reloadOthersFor(other);
            });
            eLib.getInstance().getNameTagHandler().reloadPlayer(receiver);
            eLib.getInstance().getNameTagHandler().reloadOthersFor(receiver);
        }
    }
}