package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class MatchEnderPearlListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL || !event.getAction().name().contains("RIGHT_")) {
            return;
        }
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(player);
        if (match == null) {
            return;
        }
        ArenaSchematic schematic = PotPvPSI.getInstance().getArenaHandler().getSchematic(match.getArena().getSchematic());
        if (!schematic.isPearlsAllowed()) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into this region!");
            event.setCancelled(true);
            player.updateInventory();
        }
    }

}