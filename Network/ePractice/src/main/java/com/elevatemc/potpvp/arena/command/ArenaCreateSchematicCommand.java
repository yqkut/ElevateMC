package com.elevatemc.potpvp.arena.command;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;

public final class ArenaCreateSchematicCommand {

    @Command(names = { "arena createSchematic" }, permission = "op", description = "Create a new arena schematic")
    public static void arenaCreateSchematic(Player sender, @Parameter(name="schematic") String schematicName, @Parameter(name="displayname", wildcard = true) String displayName) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        if (arenaHandler.getSchematic(schematicName) != null) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Schematic " + schematicName + " already exists");
            return;
        }

        ArenaSchematic schematic = new ArenaSchematic(schematicName, displayName);
        File schemFile = schematic.getSchematicFile();

        if (!schemFile.exists()) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No file for " + schematicName + " found. (" + schemFile.getPath() + ")");
            return;
        }

        arenaHandler.registerSchematic(schematic);

        try {
            schematic.pasteModelArena();
            arenaHandler.saveSchematics();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        sender.sendMessage(ChatColor.GREEN + "Schematic created.");
    }

}