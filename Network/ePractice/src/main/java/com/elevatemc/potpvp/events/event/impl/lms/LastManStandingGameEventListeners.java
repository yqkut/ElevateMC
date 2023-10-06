package com.elevatemc.potpvp.events.event.impl.lms;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.events.bukkit.event.PlayerGameInteractionEvent;
import com.elevatemc.potpvp.events.bukkit.event.PlayerQuitGameEvent;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class LastManStandingGameEventListeners implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
            if(game == null) return;

            if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;
            final LastManStandingGameEventLogic logic = (LastManStandingGameEventLogic) game.getLogic();

            if(game.getSpectators().contains(event.getPlayer()) && event.getTo().getBlockY() <= 0) {
                event.getPlayer().teleport(game.getArena().getTeam1Spawn());
                return;
            }

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
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Game game = GameQueue.INSTANCE.getCurrentGame(player);
            if(game == null) return;
            if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;

            final GameTeam participant = ((LastManStandingGameEventLogic) game.getLogic()).get(player);
            if(participant == null) return;

            if(participant.isStarting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Game game = GameQueue.INSTANCE.getCurrentGame(player);
            if(game == null) return;
            if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;

            final GameTeam participant = ((LastManStandingGameEventLogic) game.getLogic()).get(player);
            if(participant == null) return;

            if(game.getPlayers().contains(player)) {
                boolean allowed = event.getDamager() instanceof Player;

                if(event.getDamager() instanceof Projectile) {
                    allowed = ((Projectile) event.getDamager()).getShooter() instanceof Player;
                }

                if(allowed) {
                    if(event.getDamager() instanceof EnderPearl) {
                        event.setCancelled(true);
                        return;
                    }

                    if(participant.getPlayers().contains((Player)event.getDamager())) {
                        event.setCancelled(true);
                        return;
                    }

                    if(event.getDamager() instanceof Projectile && participant.getPlayers().contains(((Projectile) event.getDamager()).getShooter())) {
                        event.setCancelled(true);
                        return;
                    }

                    if(event.getDamager() instanceof Projectile && game.getSpectators().contains(((Projectile) event.getDamager()).getShooter())) {
                        event.setCancelled(true);
                        return;
                    }

                    if(event.getDamager() instanceof Player && game.getSpectators().contains((Player) event.getDamager())) {
                        event.setCancelled(true);
                        return;
                    }

                    if(participant.isFighting()) {
                        event.setCancelled(false);
                        return;
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
        if(game == null) return;
        if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;
        final GameTeam participant = ((LastManStandingGameEventLogic) game.getLogic()).get(event.getPlayer());

        if(!(participant.hasDied(event.getPlayer())) && game.getEvent() instanceof LastManStandingGameEvent) {
            event.setCancelled(false);
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player && event.getClick() == ClickType.DROP && event.getInventory() == event.getWhoClicked().getInventory()) {
            final Player player = (Player) event.getWhoClicked();
            final Game game = GameQueue.INSTANCE.getCurrentGame(player);
            if(game == null) return;
            if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;
            final GameTeam participant = ((LastManStandingGameEventLogic) game.getLogic()).get(player);
            if(participant == null) return;

            if(!(participant.hasDied(player)) && game.getEvent() instanceof LastManStandingGameEvent) {
                final ItemStack item = event.getCurrentItem();

                if(item != null) {
                    if(item.getAmount() > 1) {
                        item.setAmount(item.getAmount()-1);
                    } else {
                        event.getInventory().setItem(event.getRawSlot(), new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(event.getPlayer());
        if(game == null) return;
        if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;
        final GameTeam participant = ((LastManStandingGameEventLogic) game.getLogic()).get(event.getPlayer());
        if(participant == null) return;

        participant.died(event.getPlayer());
        Bukkit.getPluginManager().callEvent(new PlayerQuitGameEvent(event.getPlayer(), game));
        ((LastManStandingGameEventLogic) game.getLogic()).check();
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(event.getEntity());
        if(game == null) return;
        if(!(game.getLogic() instanceof LastManStandingGameEventLogic)) return;
        final GameTeam participant = ((LastManStandingGameEventLogic) game.getLogic()).get(event.getEntity());
        if(participant == null) return;

        Location location = event.getEntity().getLocation();
        if(location.getBlockY() < 0) {
            location = game.getArena().getTeam1Spawn();
        }

        if(game.getEvent() instanceof LastManStandingGameEvent) {
            event.getDrops().removeIf(item -> item != null && item.getType() != Material.POTION);
        }

        if(event.getEntity().getKiller() != null) {
            event.getEntity().getKiller().setHealth(event.getEntity().getKiller().getMaxHealth());
            game.sendMessage("", Color.translate("&d" + PatchedPlayerUtils.getFormattedName(event.getEntity().getUniqueId()) + " &7was killed by &d" + PatchedPlayerUtils.getFormattedName(event.getEntity().getKiller().getUniqueId()) + "&7."), "");
        } else {
            game.sendMessage("", Color.translate("&d" + PatchedPlayerUtils.getFormattedName(event.getEntity().getUniqueId()) + " &7died."), "");
        }

        participant.died(event.getEntity());

        final Location finalLocation = location;
        TaskUtil.runTaskLater(() -> {
            game.getSpectators().add(event.getEntity());
            event.getEntity().spigot().respawn();
            event.getEntity().teleport(finalLocation);
            game.reset(event.getEntity());
            Bukkit.getPluginManager().callEvent(new PlayerGameInteractionEvent(event.getEntity(), game));
            ((LastManStandingGameEventLogic) game.getLogic()).check();
        }, 2);
    }
}