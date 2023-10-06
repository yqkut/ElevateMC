package com.elevatemc.ehub.command;

import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SpawnCommands {
    @Command(names = {"setspawn"}, permission = "op")
    public static void setSpawn(Player sender) {
        Location loc = sender.getLocation();

        sender.getWorld().setSpawnLocation(
                loc.getBlockX() + 0.5,
                loc.getBlockY(),
                loc.getBlockZ() + 0.5,
                loc.getYaw(),
                loc.getPitch()
        );

        sender.sendMessage(ChatColor.YELLOW + "Spawn point updated!");
    }

    @Command(names = {"spawn"}, permission = "op")
    public static void teleportToSpawn(Player sender) {
        sender.teleport(sender.getWorld().getSpawnLocation());
        sender.sendMessage(ChatColor.GREEN + "You have been teleported to spawn!");
    }
}