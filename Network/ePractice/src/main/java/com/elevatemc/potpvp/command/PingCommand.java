package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PingCommand {
    @Command(names = {"ping"}, permission = "")
    public static void ping(Player sender, @Parameter(name = "player", defaultValue = "self") Player player) {
        int ping = PlayerUtils.getPing(player);
        if(sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(Color.translate("&6Ping: &f" + ping));
            return;
        }

        sender.sendMessage(PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "'s" + ChatColor.GOLD + " ping: " + ChatColor.WHITE + ping);
    }
}
