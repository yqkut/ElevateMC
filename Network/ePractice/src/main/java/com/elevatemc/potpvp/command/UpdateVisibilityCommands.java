package com.elevatemc.potpvp.command;

import com.elevatemc.potpvp.util.VisibilityUtils;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UpdateVisibilityCommands {

    @Command(names = {"updatevisibility", "updatevis", "upvis", "uv"}, permission = "")
    public static void updateVisibility(Player sender) {
        VisibilityUtils.updateVisibility(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your visibility.");
    }

    @Command(names = {"updatevisibilityFlicker", "updatevisFlicker", "upvisFlicker", "uvf"}, permission = "uvf")
    public static void updateVisibilityFlicker(Player sender) {
        VisibilityUtils.updateVisibilityFlicker(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your visibility (flicker mode).");
    }

}