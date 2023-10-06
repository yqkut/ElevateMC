package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDParameterType implements ParameterType<UUID> {

    public UUID transform(CommandSender sender, String source) {

        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender).getUniqueId();
        }

        final UUID uuid = UUIDUtils.uuid(source);

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + source + " has never joined the server.");
            return null;
        }

        return uuid;
    }

}