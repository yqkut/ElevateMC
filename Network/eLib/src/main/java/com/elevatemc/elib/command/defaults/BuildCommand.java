package com.elevatemc.elib.command.defaults;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.eLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BuildCommand {
    @Command(names = {"build"}, permission = "op")
    public static void build(final Player sender) {
        if (sender.hasMetadata("build")) {
            sender.removeMetadata("build", eLib.getInstance());
        } else {
            sender.setMetadata("build", new FixedMetadataValue(eLib.getInstance(), true));
        }
        sender.sendMessage(ChatColor.YELLOW + "You are " + (sender.hasMetadata("build") ? (ChatColor.GREEN + "now") : (ChatColor.RED + "no longer")) + ChatColor.YELLOW + " in build mode.");
    }
}