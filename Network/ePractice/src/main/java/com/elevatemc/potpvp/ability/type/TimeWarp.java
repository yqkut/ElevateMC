package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.spigot.event.PlayerPearlRefundEvent;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TimeWarp extends Ability {
    public static Map<UUID, Location> oldPearlLocations = new HashMap<>();
    public static Map<UUID, Location> pearlLocations = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.WATCH;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Time Warp";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Right Click to go back to where you");
        toReturn.add(ChatColor.GRAY + "pearled from 15 seconds ago.");
        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (!this.isSimilar(event.getItem()) || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (!pearlLocations.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have not thrown a pearl in the last 16 seconds...");
            return;
        }

        if (player.hasMetadata("NINJASTAR")) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use a " + this.getDisplayName() + ChatColor.RED + " whilst someone is using a Ninja Star on you!");
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        final Location location = pearlLocations.remove(player.getUniqueId()).clone();

        new BukkitRunnable() {
            private int seconds = 4;

            @Override
            public void run() {
                this.seconds--;

                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }

                if (!PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
                    return;
                }

                if (player.hasMetadata("NINJASTAR")) {
                    player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use a " + getDisplayName() + ChatColor.RED + " whilst someone is using a Ninja Star on you!");
                    this.cancel();
                    refund(player);
                    return;
                }

                if (this.seconds <= 0) {
                    player.teleport(location);

                    this.cancel();
                    return;
                }

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Teleporting in " + ChatColor.RED + this.seconds + ChatColor.YELLOW + " second" + (this.seconds == 1 ? "":"s") + "...");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }.runTaskTimer(PotPvPSI.getInstance(),0L,20L);

        this.applyCooldown(player);
    }

    public void refund(Player player) {
        player.getInventory().addItem(this.hassanStack.clone());

        this.removeCooldown(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof EnderPearl) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getEntity().getShooter();
        final UUID shooterUUID = shooter.getUniqueId();

        final Location location = shooter.getLocation();

        pearlLocations.remove(shooterUUID);
        if (pearlLocations.containsKey(shooterUUID)) {
            oldPearlLocations.put(shooterUUID, pearlLocations.get(shooterUUID));
        }
        pearlLocations.put(shooterUUID, shooter.getLocation());

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            if (!pearlLocations.containsKey(shooter.getUniqueId())) {
                return;
            }

            final Location newLocation = pearlLocations.get(shooter.getUniqueId());

            if (location.getX() != newLocation.getX() || location.getY() != newLocation.getY() || location.getZ() != newLocation.getZ()) {
                return;
            }

            pearlLocations.remove(shooter.getUniqueId());
        }, 20*16);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPearl(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getItem() == null || !event.getAction().name().contains("RIGHT")) {
            return;
        }

        if (event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }

        final Location location = player.getLocation().clone();

        pearlLocations.remove(player.getUniqueId());
        pearlLocations.put(player.getUniqueId(), location);

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            if (!pearlLocations.containsKey(player.getUniqueId())) {
                return;
            }

            final Location newLocation = pearlLocations.get(player.getUniqueId());

            if (location.getX() != newLocation.getX() || location.getY() != newLocation.getY() || location.getZ() != newLocation.getZ()) {
                return;
            }

            pearlLocations.remove(player.getUniqueId());
        }, 20*15);
    }

    @EventHandler
    private void onPearlRefund(PlayerPearlRefundEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (oldPearlLocations.containsKey(uuid)) {
            oldPearlLocations.remove(uuid);
            pearlLocations.put(uuid, oldPearlLocations.get(uuid));
        }
    }
}