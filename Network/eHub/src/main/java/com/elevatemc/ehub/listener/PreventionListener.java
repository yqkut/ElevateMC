package com.elevatemc.ehub.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class PreventionListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().hasMetadata("build")) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().hasMetadata("build")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getWhoClicked().hasMetadata("build")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(e.toWeatherState());
    }

    @EventHandler
    public void onDamage(FoodLevelChangeEvent e) {
        if(e.getEntity() instanceof Player) {
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location location = e.getTo();
        if (location.getY() < 5 || location.getX() > 300 || location.getZ() > 300) {
            Player player = e.getPlayer();
            if (player.getVehicle() != null) {
                Entity pearl = player.getVehicle();
                if (pearl != null) {
                    player.eject();
                    pearl.remove();
                }
            }
            player.teleport(player.getWorld().getSpawnLocation());
        }

    }
}
