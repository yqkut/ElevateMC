package com.elevatemc.potpvp.events.event.impl.brackets;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.events.bukkit.event.PlayerGameInteractionEvent;
import com.elevatemc.potpvp.events.bukkit.event.PlayerQuitGameEvent;
import com.elevatemc.potpvp.events.event.impl.lms.LastManStandingGameEvent;
import com.elevatemc.potpvp.events.event.impl.lms.LastManStandingGameEventLogic;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BracketsGameEventListener implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
            if(game == null) return;

            if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
            final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

            final GameTeam participant = logic.get(event.getPlayer());
            if(participant == null) return;

            if(participant.isStarting()) {
                if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                    event.getPlayer().teleport(event.getFrom());
                    event.getPlayer().setVelocity(new Vector(0, -1, 0));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Game game = GameQueue.INSTANCE.getCurrentGame(player);
            if(game == null) return;
            if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
            final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

            final GameTeam participant = logic.get(player);

            if(game.getSpectators().contains(player)) {
                event.setCancelled(true);
                return;
            }

            if(event.getDamager() instanceof Player && game.getSpectators().contains((Player) event.getDamager())) {
                event.setCancelled(true);
                return;
            }

            if(game.getPlayers().contains(player)) {
                Player opponent = null;

                if(event.getDamager() instanceof Player) {
                    opponent = (Player) event.getDamager();
                }

                if(event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    opponent = (Player)((Projectile) event.getDamager()).getShooter();
                }

                if(participant != null && opponent != null) {
                    if(participant.getPlayers().contains(opponent)) {
                        event.setCancelled(true);
                        return;
                    }

                    if(participant.isFighting()) {
                        final GameTeam opponentParticipating = logic.get(opponent);
                        if(opponentParticipating != null && opponentParticipating.isFighting()) {
                            event.setCancelled(false);
                            return;
                        }
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Game game = GameQueue.INSTANCE.getCurrentGame(player);
            if(game == null) return;
            if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
            final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

            if(game.getPlayers().contains(player) && game.getState() != GameState.STARTING) {
                final GameTeam participant = logic.get(player);

                if(participant != null) {
                    if(participant.isFighting() && !participant.hasDied(player)) {
                        event.setCancelled(false);
                        return;
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
        if(game == null) return;
        if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
        final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

        final GameTeam participant = logic.get(event.getPlayer());
        if(participant == null) return;

        if(participant.isFighting() || participant.isStarting()) {
            participant.died(event.getPlayer());

            logic.check();
        } else {
            if(participant.getPlayers().size() == 1 || game.getState() == GameState.STARTING) {
                logic.getParticipants().remove(participant);
            } else {
                final List<Player> newPlayers = participant.getPlayers();
                newPlayers.remove(event.getPlayer());
                participant.setPlayers(newPlayers);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerQuitGameEvent event) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
        if(game == null) return;
        if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
        final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

        final GameTeam participant = logic.get(event.getPlayer());
        if(participant == null) return;

        if(participant.isFighting() || participant.isStarting()) {
            participant.died(event.getPlayer());

            logic.check();
        } else {
            if(participant.getPlayers().size() == 1 || game.getState() == GameState.STARTING) {
                logic.getParticipants().remove(participant);
            } else {
                final List<Player> newPlayers = participant.getPlayers();
                newPlayers.remove(event.getPlayer());
                participant.setPlayers(newPlayers);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(event.getEntity());
        if(game == null) return;
        if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
        final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

        final GameTeam participant = logic.get(event.getEntity());
        if(participant == null) return;

        event.getDrops().clear();

        if(participant.isFighting()) {
            participant.died(event.getEntity());

            if(participant.isFinished()) {
                event.getEntity().setHealth(event.getEntity().getMaxHealth());
                logic.check();

                participant.getPlayers().forEach(player -> TaskUtil.runTaskLater(() -> game.reset(player), 2));
                game.getPlayers().forEach(player -> game.getPlayers().forEach(otherPlayer -> {
                    if(player.getUniqueId().equals(otherPlayer.getUniqueId())) return;
                    player.teleport(game.getArena().getSpectatorSpawn());
                    player.showPlayer(otherPlayer);
                }));
            } else {
                TaskUtil.runTaskLater(() -> {
                    event.getEntity().spigot().respawn();
                    event.getEntity().teleport(game.getArena().getSpectatorSpawn());
                    Bukkit.getPluginManager().callEvent(new PlayerGameInteractionEvent(event.getEntity(), game));
                }, 2);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Game game = GameQueue.INSTANCE.getCurrentGame(player);
            if(game == null) return;
            if(!(game.getLogic() instanceof BracketsGameEventLogic)) return;
            final BracketsGameEventLogic logic = (BracketsGameEventLogic) game.getLogic();

            final GameTeam participant = logic.get(player);
            if(participant == null) return;

            event.setFoodLevel(20);
        }
    }
}
