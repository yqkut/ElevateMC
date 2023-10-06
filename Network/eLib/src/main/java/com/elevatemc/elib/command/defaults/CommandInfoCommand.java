package com.elevatemc.elib.command.defaults;

import com.elevatemc.elib.command.argument.ArgumentProcessor;
import com.elevatemc.elib.command.argument.Arguments;
import com.elevatemc.elib.command.command.CommandNode;
import com.elevatemc.elib.command.CommandHandler;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandInfoCommand {

    @Command(names = {"cmdinfo"}, permission = "elib.command.commandinfo", hidden = true)
    public static void execute(CommandSender sender,@Parameter(name = "command",wildcard = true) String command) {

        final String[] args = command.split(" ");
        final ArgumentProcessor processor = new ArgumentProcessor();
        final Arguments arguments = processor.process(args);
        final CommandNode node = CommandHandler.ROOT_NODE.getCommand(arguments.getArguments().get(0));

        if (node != null) {

            final CommandNode realNode = node.findCommand(arguments);

            if (realNode != null) {

                final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(realNode.getOwningClass());

                sender.sendMessage(ChatColor.WHITE + realNode.getFullLabel() + ChatColor.GRAY + ":");
                sender.sendMessage(ChatColor.GRAY + "-> " + ChatColor.AQUA + "Plugin: " + ChatColor.WHITE + plugin.getName());
                sender.sendMessage(ChatColor.GRAY + "-> " + ChatColor.AQUA + "Sub commands:");

                for (CommandNode value : realNode.getChildren().values()) {
                    sender.sendMessage("  " + ChatColor.GRAY + "-> " + ChatColor.AQUA + value.getSubCommands(sender,false));
                }

                return;
            }

        }

        sender.sendMessage(ChatColor.RED + "Command not found.");
    }

}
