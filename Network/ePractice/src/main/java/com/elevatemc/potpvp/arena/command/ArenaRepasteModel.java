package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaRepasteModel {

    @Command(names = { "arena repastemodel" }, permission = "op", description = "Repaste the model arena")
    public static void execute(Player player, @Parameter(name = "schematic") ArenaSchematic schematic) {
        try {
            schematic.pasteModelArena();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        player.sendMessage(ChatColor.GREEN + "Repasted the model arena.");
    }

}
