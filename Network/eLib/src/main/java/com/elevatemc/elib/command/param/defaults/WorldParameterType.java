package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.eLib;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldParameterType implements ParameterType<World> {

    public World transform(CommandSender sender, String source) {

        final World world = eLib.getInstance().getServer().getWorld(source);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "No world with the name " + source + " found.");
            return null;
        }

        return world;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return eLib.getInstance().getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }

}