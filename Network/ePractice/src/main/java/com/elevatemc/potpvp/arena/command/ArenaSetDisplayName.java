package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaSetDisplayName {

    @Command(names = { "arena setdisplayname" }, permission = "op", description = "Sets an arena display name")
    public static void execute(Player player, @Parameter(name = "schematic") ArenaSchematic schematic, @Parameter(name = "displayName", wildcard = true) String displayName) {
        schematic.setDisplayName(displayName);
        PotPvPSI.getInstance().getArenaHandler().sortSchematics();

        player.sendMessage(ChatColor.GREEN + "You've updated this arena's display name.");
    }

}
