package com.elevatemc.potpvp.lobby.listener;

import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.lobby.listener.LobbyParkourListener.Parkour;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.spigot.viaversion.api.Via;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.serverrule.LunarClientAPIServerRule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.github.paperspigot.Title;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public final class LobbyGeneralListener implements Listener {

    private final LobbyHandler lobbyHandler;

    public LobbyGeneralListener(LobbyHandler lobbyHandler) {
        this.lobbyHandler = lobbyHandler;
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Parkour parkour = LobbyParkourListener.getParkourMap().get(event.getPlayer().getUniqueId());
        if (parkour != null && parkour.getCheckpoint() != null) {
            event.setSpawnLocation(parkour.getCheckpoint().getLocation());
            return;
        }

        event.setSpawnLocation(lobbyHandler.getLobbyLocation());
    }

    private Title WELCOME_TITLE = Title.builder().title(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Elevate Practice").subtitle("Welcome to Season 3!").stay(100).build();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LunarClientAPIServerRule.sendServerRule(player);
        PlayerUtils.sendTitle(player, WELCOME_TITLE);
        lobbyHandler.returnToLobby(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (lobbyHandler.isInLobby(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                lobbyHandler.returnToLobby(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (lobbyHandler.isInLobby((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (lobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!lobbyHandler.isInLobby(player)) {
            return;
        }

        Menu openMenu = Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());
        if (player.hasMetadata("build") || (openMenu != null && openMenu.isNoncancellingInventory())) {
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    // cancel inventory interaction in the lobby except for menus
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (!lobbyHandler.isInLobby(clicked) || clicked.hasMetadata("build") || Menu.getCurrentlyOpenedMenus().containsKey(clicked.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (!lobbyHandler.isInLobby(clicked) || clicked.hasMetadata("build") || Menu.getCurrentlyOpenedMenus().containsKey(clicked.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (lobbyHandler.isInLobby(event.getEntity())) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();

        if (inventoryHolder instanceof Player) {
            Player player = (Player) inventoryHolder;

            if (!lobbyHandler.isInLobby(player) || Menu.getCurrentlyOpenedMenus().containsKey(player.getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameMode gameMode = event.getPlayer().getGameMode();

        if (lobbyHandler.isInLobby(event.getPlayer()) && gameMode != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (lobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}