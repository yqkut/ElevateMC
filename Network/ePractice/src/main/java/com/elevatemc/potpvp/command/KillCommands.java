package com.elevatemc.potpvp.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KillCommands {

    @Command(names = {"kill"}, permission = "core.adminteam")
    public static void kill(Player sender, @Parameter(name="target") Player target) {
        target.setHealth(0);
        sender.sendMessage(target.getDisplayName() + ChatColor.RED + " has been killed.");
    }
}