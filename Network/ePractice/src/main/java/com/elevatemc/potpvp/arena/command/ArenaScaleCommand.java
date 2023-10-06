package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaScaleCommand {

    @Command(names = { "arena scale" }, permission = "op", description = "Scale an arena to a specific number")
    public static void arenaScale(Player sender, @Parameter(name="schematic") ArenaSchematic schematic, @Parameter(name="count") int count) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        sender.sendMessage(ChatColor.GREEN + "Starting...");

        arenaHandler.getGrid().scaleCopies(schematic, count, () -> sender.sendMessage(ChatColor.GREEN + "Scaled " + schematic.getName() + " to " + count + " copies."));
    }

    @Command(names = "arena rescaleall", permission = "op", description = "Rescale all the arenas")
    public static void arenaRescaleAll(Player sender) {
        PotPvPSI.getInstance().getArenaHandler().getSchematics().forEach(schematic -> {
            ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
            int totalCopies = 0;

            for (Arena arena : arenaHandler.getArenas(schematic)) {
                totalCopies++;
            }

            arenaScale(sender, schematic, 0);
            arenaScale(sender, schematic, totalCopies);
        });
    }

}