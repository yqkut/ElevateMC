package com.elevatemc.elib.autoreboot.command;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RebootCancelCommand {

    @Command(
            names = {"shutdown cancel"},
            permission = "elib.command.reboot.cancel"
    )
    public static void execute(CommandSender sender) {

        if (!eLib.getInstance().getAutoRebootHandler().isRebooting()) {
            sender.sendMessage(ChatColor.RED + "No reboot has been scheduled.");
            return;
        }

        eLib.getInstance().getAutoRebootHandler().cancelReboot();
        eLib.getInstance().getServer().broadcastMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
        eLib.getInstance().getServer().broadcastMessage(ChatColor.RED + "The server reboot has been cancelled.");
        eLib.getInstance().getServer().broadcastMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
    }

}
