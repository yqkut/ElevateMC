package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.eLib;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerParameterType implements ParameterType<Player> {

    public Player transform(CommandSender sender, String source) {
        if (!(sender instanceof Player) || !source.equalsIgnoreCase("self") && !source.equals("")) {

            final Player player = eLib.getInstance().getServer().getPlayer(source);

            if (player != null && (!(sender instanceof Player) || eLib.getInstance().getVisibilityHandler().treatAsOnline(player, (Player)sender))) {
                return player;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No player with the name \"" + source + "\" found.");
                return null;
            }
        } else {
            return (Player)sender;
        }
    }
}