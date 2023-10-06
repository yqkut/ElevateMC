package com.elevatemc.equeue.command;

import com.elevatemc.equeue.eQueue;
import com.elevatemc.equeue.queue.Queue;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class eQueueCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sendDefaultHelpMessage(source);
        } else {
            String subcmd = args[0];
            switch (subcmd) {
                case "reload":
                    eQueue.getInstance().reloadConfigAndQueues();
                    source.sendMessage(Component.text("Reloaded the config & queues.!", NamedTextColor.GREEN));
                    break;
                case "list":
                    sendQueueList(source);
                    break;
                default:
                    sendDefaultHelpMessage(source);
            }
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("equeue.admin");
    }

    private void sendQueueList(CommandSource commandSender) {
        for (Queue queue : eQueue.getInstance().getQueueHandler().getQueues().values()) {
            commandSender.sendMessage(Component.text(queue.getServer(), NamedTextColor.YELLOW));
            commandSender.sendMessage(Component.text(" * Size: " + queue.getQueueEntries().size(), NamedTextColor.YELLOW));
        }
    }

    private void sendDefaultHelpMessage(CommandSource commandSender) {
        commandSender.sendMessage(Component.text("All eQueue Commands:", NamedTextColor.RED));
        commandSender.sendMessage(Component.text("/equeue reload - Reload the config (ALL QUEUES WILL BE RESET)", NamedTextColor.RED));
        commandSender.sendMessage(Component.text("/equeue list - Get a list of all queues + status", NamedTextColor.RED));
    }
}
