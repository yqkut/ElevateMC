package com.elevatemc.potpvp.events.game;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.events.bukkit.event.PlayerQuitGameEvent;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;

public class GameListeners implements Listener {

    private static final PotPvPSI plugin = PotPvPSI.getInstance();
    private static final GameQueue gameQueue = plugin.getEventHandler().getGameQueue();

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            final Game game = gameQueue.getCurrentGame((Player) event.getEntity());

            if(game != null && game.getState() == GameState.STARTING && game.getPlayers().contains((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            final Game game = gameQueue.getCurrentGame((Player) event.getDamager());
            if(game != null && game.getSpectators().contains((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        final Game game = gameQueue.getCurrentGame(event.getPlayer());

        if(game != null && game.getPlayers().contains(event.getPlayer())) {
            Bukkit.getPluginManager().callEvent(new PlayerQuitGameEvent(event.getPlayer(), game));
        }

        final Iterator<Game> iterator = gameQueue.getGames().iterator();
        while(iterator.hasNext()) {
            final Game other = iterator.next();
            if(other.getHost() == event.getPlayer() && other.getState() == GameState.QUEUED) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        final Game game = gameQueue.getCurrentGame(event.getPlayer());

        if(game != null && game.getPlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInventoryClickEvent(InventoryClickEvent event) {
        final Game game = gameQueue.getCurrentGame((Player)event.getWhoClicked());

        if(game != null && game.getState() == GameState.STARTING && game.getPlayers().contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }

        if(game != null && game.getSpectators().contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        final Game game = gameQueue.getCurrentGame(event.getPlayer());

        if(game != null && game.getSpectators().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        final Game game = gameQueue.getCurrentGame((Player)event.getEntity());
        if(game != null && game.getState() == GameState.STARTING && game.getSpectators().contains((Player)event.getEntity())) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        final Game game = gameQueue.getCurrentGame(event.getPlayer());

        if(game == null) return;

        if(event.getItem() != null && event.getItem().isSimilar(com.elevatemc.potpvp.events.EventHandler.getLeaveItem())) {
            Bukkit.getPluginManager().callEvent(new PlayerQuitGameEvent(event.getPlayer(), game));
            event.getPlayer().sendMessage(Color.translate("&cYou left the " + game.getEvent().getName() + " event."));
            return;
        }

        if(game.getSpectators().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
