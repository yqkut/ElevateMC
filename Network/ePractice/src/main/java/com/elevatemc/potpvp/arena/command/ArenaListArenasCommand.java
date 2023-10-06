package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.util.LocationUtils;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaListArenasCommand {

    @Command(names = { "arena listArenas" }, permission = "op", description = "List all the arena instances of the schematic")
    public static void arenaListArenas(Player sender, @Parameter(name="schematic") ArenaSchematic schematic) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "------ " + ChatColor.WHITE + schematic.getName() + " Arenas" + ChatColor.RED + " ------");

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            String locationStr = LocationUtils.locToStr(arena.getSpectatorSpawn());
            String occupiedStr = arena.isInUse() ? ChatColor.RED + "In Use" : ChatColor.GREEN + "Open";

            sender.sendMessage(arena.getCopy() + ": " + ChatColor.GREEN + locationStr + ChatColor.GRAY + " - " + occupiedStr);
        }
    }

}