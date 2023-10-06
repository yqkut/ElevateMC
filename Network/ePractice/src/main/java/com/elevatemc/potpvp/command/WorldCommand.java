package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class WorldCommand {

    @Command(names = {"world tp"}, permission = "op", description = "Teleport to a world")
    public static void worldTp(@NotNull Player player, @Parameter(name = "world") World world) {
        player.sendMessage(ChatColor.GOLD + "Teleporting you to " + ChatColor.WHITE + world.getName());
        player.teleport(world.getSpawnLocation());
    }

    @Command(names = {"world list"}, permission = "op", description = "Get a list of all worlds")
    public static void worldList(Player player) {
        for (World world : Bukkit.getWorlds()) {
            String name = world.getName();
            Location spawn = world.getSpawnLocation();

            ChunkGenerator generator = world.getGenerator();
            String generatorName;
            if (generator == null) { // Edge cases when it fails to load a generator
                generatorName = "null";
            } else {
                generatorName = generator.getClass().getSimpleName();
            }

            String type = world.getWorldType().toString();
            int entities = world.getEntities().size();
            int players = world.getPlayers().size();
            int loadedChunks = world.getLoadedChunks().length;

            player.sendMessage(ChatColor.GRAY.toString() + net.md_5.bungee.api.ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
            player.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + name);
            player.sendMessage(ChatColor.GOLD + "Spawn: " + ChatColor.WHITE + spawn.getBlockX() + " " + spawn.getBlockY() + " " + spawn.getBlockZ());
            player.sendMessage(ChatColor.GOLD + "Generator: " + ChatColor.WHITE + generatorName);
            player.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.WHITE + type);
            player.sendMessage(ChatColor.GOLD + "Entities: " + ChatColor.WHITE + entities);
            player.sendMessage(ChatColor.GOLD + "Players: " + ChatColor.WHITE + players);
            player.sendMessage(ChatColor.GOLD + "Loaded chunks: " + ChatColor.WHITE + loadedChunks);
        }
        player.sendMessage(ChatColor.GRAY.toString() + net.md_5.bungee.api.ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
    }
}
