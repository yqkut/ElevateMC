package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LongParameterType implements ParameterType<Long> {

    @Override
    public Long transform(CommandSender sender,String source) {

        if (source.equalsIgnoreCase("perm") || source.equalsIgnoreCase("permanent")) {
            return Long.valueOf(Integer.MAX_VALUE);
        }

        try {

            final int toReturn = TimeUtils.parseTime(source);

            if ((toReturn * 1000L) <= 0) {
                sender.sendMessage(ChatColor.RED + "Duration must be higher then 0.");
                return null;
            }

            return toReturn * 1000L;
        } catch (NullPointerException | IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED + "Invalid duration");
            return null;
        }
    }

    @Override
    public List<String> tabComplete(Player sender,Set<String> flags,String source) {
        return new ArrayList<>();
    }
}
