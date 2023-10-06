package com.elevatemc.elib.autoreboot.command;

import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RebootScheduleCommand {

    @Command(
            names = {"shutdown","shutdown schedule", "reboot", "reboot schedule"},
            permission = "elib.command.reboot.schedule"
    )
    public static void execute(CommandSender sender,@Parameter(name = "time")long time) {
        if(eLib.getInstance().getAutoRebootHandler().isRebooting()) {
            sender.sendMessage(ChatColor.RED + "Server is currently restarting already!");
            return;
        }
        eLib.getInstance().getAutoRebootHandler().rebootServer(time);
        sender.sendMessage(ChatColor.GOLD + "Scheduled a reboot in " + TimeUtils.formatIntoDetailedString((int)(time / 1000)));

    }

}
