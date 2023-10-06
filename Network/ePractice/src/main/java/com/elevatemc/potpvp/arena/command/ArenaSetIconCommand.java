package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;

public final class ArenaSetIconCommand {

    @Command(names = { "arena seticon" }, permission = "op")
    public static void arenaSetIcon(Player player, @Parameter(name="schematic") ArenaSchematic schematic) {
        if (player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Please hold an item in your hand.");
            return;
        }

        schematic.setIcon(player.getItemInHand().getType());

        try {
            PotPvPSI.getInstance().getArenaHandler().saveSchematics();
        } catch (IOException ex) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Failed to save " + schematic.getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }

        player.sendMessage(ChatColor.GREEN + "You've updated this arena's icon.");
    }

}