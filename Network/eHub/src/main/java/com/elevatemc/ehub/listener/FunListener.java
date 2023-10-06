package com.elevatemc.ehub.listener;

import com.elevatemc.ehub.eHub;
import com.elevatemc.elib.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class FunListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onDoubleJumpMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (!((LivingEntity) player).isOnGround())
        if (player.getAllowFlight()) return;

        boolean onGround = ((Entity)player).isOnGround();
        if (player.hasMetadata("DOUBLE_JUMPED") && onGround) {
            player.removeMetadata("DOUBLE_JUMPED", eHub.getInstance());
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDoubleJumpFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        e.setCancelled(true);
        player.setFlying(false);
        player.setAllowFlight(false);

        if (player.hasMetadata("BOOSTED")) {
            player.removeMetadata("BOOSTED", eHub.getInstance());
        }

        if (!player.hasMetadata("DOUBLE_JUMPED")) {
            player.setMetadata("DOUBLE_JUMPED", new FixedMetadataValue(eHub.getInstance(), true));
            final Location loc = player.getLocation();
            final double otherBoost = 2.5;
            final Sound sound = Sound.PISTON_EXTEND;
            final Vector vector = loc.getDirection().multiply(otherBoost).setY(1.75);
            player.setVelocity(vector);
            player.playSound(loc, sound, 2,2);
            ParticleEffect.CLOUD.display(0 ,-0.5F, 0, 0.05F, 25, loc, player);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
             Player player = e.getPlayer();

            if (player.getGameMode().equals(GameMode.CREATIVE)) return;

            boolean onGround = ((Entity)player).isOnGround();
             if (!player.hasMetadata("BOOSTED") && !onGround) {
                 player.setMetadata("BOOSTED", new FixedMetadataValue(eHub.getInstance(), true));
                 final Location loc = player.getLocation();
                 final double otherBoost = 3;
                 final Sound sound = Sound.BAT_TAKEOFF;
                 final Vector vector = loc.getDirection().multiply(otherBoost);
                 player.setVelocity(vector);
                 player.playSound(loc, sound, 2,2);
                 ParticleEffect.FLAME.display(0 ,-0.5F, 0, 0.05F, 25, loc, player);
             }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            final Player player = (Player)e.getEntity().getShooter();
            if (e.getEntity() instanceof EnderPearl) {
                Entity pearl = player.getVehicle();
                if (pearl != null) {
                    pearl.remove();
                }
                Projectile proj = e.getEntity();
                if (proj.getType() == EntityType.ENDER_PEARL) {
                    Bukkit.getScheduler().runTaskLater(eHub.getInstance(), () -> {
                        if (!proj.isDead()) {
                            proj.setPassenger(player);
                        }
                        player.getInventory().getItem(player.getInventory().getHeldItemSlot()).setAmount(64);
                    }, 3L);
                }
            } else {
                e.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            if (player != null && player.getVehicle() instanceof EnderPearl) {
                Entity pearl = player.getVehicle();
                if (pearl != null) {
                    player.eject();
                    pearl.remove();
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Entity pearl = player.getVehicle();
        if (pearl != null) {
            pearl.remove();
            return;
        }
    }
}
