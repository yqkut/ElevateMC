package com.elevatemc.elib.command.defaults;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class EvalCommand {

    @Command(names = {"eval"}, permission = "console", description = "Evaluates a commands", hidden = true)
    public static void execute(CommandSender sender,@Parameter(name = "command",wildcard = true) String commandLine) {

        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(eLib.getInstance().getCommandHandler().getCommandConfiguration().getConsoleOnlyCommandMessage());
        } else {
            Bukkit.dispatchCommand(eLib.getInstance().getServer().getConsoleSender(), commandLine);
        }
    }

}
