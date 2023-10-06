package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaGrid;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaRepasteSchematicCommand {

    @Command(names = { "arena repasteSchematic" }, permission = "op", description = "Repaste all instances of a schematic")
    public static void arenaRepasteSchematic(Player sender, @Parameter(name="schematic") ArenaSchematic schematic) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        int currentCopies = arenaHandler.countArenas(schematic);

        if (currentCopies == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No copies of " + schematic.getName() + " exist.");
            return;
        }

        ArenaGrid arenaGrid = arenaHandler.getGrid();

        sender.sendMessage(ChatColor.GREEN + "Starting...");

        arenaGrid.scaleCopies(schematic, 0, () -> {
            sender.sendMessage(ChatColor.GREEN + "Removed old maps, creating new copies...");

            arenaGrid.scaleCopies(schematic, currentCopies, () -> {
                sender.sendMessage(ChatColor.GREEN + "Repasted " + currentCopies + " arenas using the newest " + schematic.getName() + " schematic.");
            });
        });
    }

}