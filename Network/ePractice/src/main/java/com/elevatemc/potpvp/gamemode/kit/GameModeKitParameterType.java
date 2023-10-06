package com.elevatemc.potpvp.gamemode.kit;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.potpvp.gamemode.GameMode;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class GameModeKitParameterType implements ParameterType<GameModeKit> {

    @Override
    public GameModeKit transform(CommandSender sender, String source) {
        for (GameModeKit kit : GameModeKit.getAllKits()) {
            if (kit.getId().equalsIgnoreCase(source)) {
                return kit;
            }
        }

        sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No kit with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (GameModeKit gameMode : GameModeKit.getAllKits()) {
            if (StringUtils.startsWithIgnoreCase(gameMode.getId(), source)) {
                completions.add(gameMode.getId());
            }
        }

        return completions;
    }

}