package com.elevatemc.potpvp.arena;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.elib.command.param.ParameterType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ArenaSchematicParameterType implements ParameterType<ArenaSchematic> {

    @Override
    public ArenaSchematic transform(CommandSender sender, String source) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        for (ArenaSchematic arenaSchematic : arenaHandler.getSchematics()) {
            if (arenaSchematic.getName().equalsIgnoreCase(source)) {
                return arenaSchematic;
            }
        }

        sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No arena with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        for (ArenaSchematic arenaSchematic : arenaHandler.getSchematics()) {
            if (StringUtils.startsWithIgnoreCase(arenaSchematic.getName(), source)) {
                completions.add(arenaSchematic.getName());
            }
        }

        return completions;
    }
}