package com.elevatemc.potpvp.events.event.impl.sumo;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.events.event.impl.lms.LastManStandingGameEventLogic;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class SumoGameEventListeners implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
            if(game == null) return;

            if(!(game.getLogic() instanceof SumoGameEventLogic)) return;
            final SumoGameEventLogic logic = (SumoGameEventLogic) game.getLogic();

            final GameTeam participant = logic.get(event.getPlayer());
            if(participant == null) return;

            if(event.getTo().getBlockY() + 5 < game.getFirstSpawnLocations().get(0).getBlockY() && participant.isFighting()) {
                participant.died(event.getPlayer());

                if(participant.isFinished()) {
                    logic.check();

                    participant.getPlayers().forEach(player -> TaskUtil.runTaskLater(() -> game.reset(player), 2));
                    game.getPlayers().forEach(player -> game.getPlayers().forEach(otherPlayer -> {
                        if(player.getUniqueId().equals(otherPlayer.getUniqueId())) return;
                        player.teleport(game.getArena().getSpectatorSpawn());
                        player.showPlayer(otherPlayer);
                    }));
                } else {
                    game.addSpectator(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();
        final Game game = GameQueue.INSTANCE.getCurrentGame(player);
        if(game == null) return;

        if(!(game.getLogic() instanceof SumoGameEventLogic)) return;
        final SumoGameEventLogic logic = (SumoGameEventLogic) game.getLogic();

        if(!(game.getEvent() instanceof SumoGameEvent)) return;

        if(game.getPlayers().contains(player) && game.getState() != GameState.STARTING) {
            final GameTeam participant = logic.get(player);

            if(participant != null) {
                if(participant.isFighting() && !participant.hasDied(player)) {
                    event.setDamage(0.0D);
                    event.setCancelled(false);
                    return;
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();
        final Game game = GameQueue.INSTANCE.getCurrentGame(player);
        if(game == null) return;

        if(!(game.getLogic() instanceof SumoGameEventLogic)) return;
        final SumoGameEventLogic logic = (SumoGameEventLogic) game.getLogic();

        if(!(game.getEvent() instanceof SumoGameEvent)) return;

        if(game.getPlayers().contains(player)) {
            event.setFoodLevel(20);
        }
    }
}
