package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.eLib;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    public OfflinePlayer transform(CommandSender sender, String source) {

        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender);
        }

        return eLib.getInstance().getServer().getOfflinePlayer(source);
    }

}