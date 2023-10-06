package com.elevatemc.elib.command.param;

import com.elevatemc.elib.eLib;
import com.mysql.jdbc.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ParameterType<T> {

    T transform(CommandSender sender,String source);

    default List<String> tabComplete(Player sender,Set<String> flags,String source) {
        return eLib.getInstance().getServer().getOnlinePlayers()
                .stream()
                .filter(loopPlayer -> StringUtils.startsWithIgnoreCase(loopPlayer.getName(),source) && eLib.getInstance().getVisibilityHandler().treatAsOnline(loopPlayer,sender))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

}