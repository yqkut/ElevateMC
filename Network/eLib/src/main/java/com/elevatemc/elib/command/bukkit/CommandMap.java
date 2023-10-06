package com.elevatemc.elib.command.bukkit;

import com.elevatemc.elib.command.command.CommandNode;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandMap extends SimpleCommandMap {

    public CommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");

        final int spaceIndex = cmdLine.indexOf(32);

        String prefix;

        if (spaceIndex == -1) {

            final ArrayList<String> completions = new ArrayList();
            final Map<String, org.bukkit.command.Command> knownCommands = this.knownCommands;
            prefix = sender instanceof Player ? "/" : "";

            for (Map.Entry<String, org.bukkit.command.Command> entry : knownCommands.entrySet()) {

                final String name = entry.getKey();

                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {

                    final org.bukkit.command.Command command = entry.getValue();

                    if (command instanceof Command) {

                        CommandNode executionNode = ((Command)command).node.getCommand(name);

                        if (executionNode == null) {
                            executionNode = ((Command)command).node;
                        }

                        if (!executionNode.hasCommands()) {

                            CommandNode testNode = executionNode.getCommand(name);

                            if (testNode == null) {
                                testNode = ((Command)command).node.getCommand(name);
                            }

                            if (testNode.canUse(sender)) {
                                completions.add(prefix + name);
                            }
                        } else if (executionNode.getSubCommands(sender, false).size() != 0) {
                            completions.add(prefix + name);
                        }
                    } else if (command.testPermissionSilent(sender)) {
                        completions.add(prefix + name);
                    }
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        } else {

            final String commandName = cmdLine.substring(0, spaceIndex);

            final org.bukkit.command.Command target = this.getCommand(commandName);

            if (target == null) {
                return null;
            } else if (!target.testPermissionSilent(sender)) {
                return null;
            } else {
                prefix = cmdLine.substring(spaceIndex + 1);
                final String[] args = prefix.split(" ");

                try {

                    final List<String> completions = target instanceof Command ? ((Command)target).tabComplete(sender, cmdLine) : target.tabComplete(sender, commandName, args, location);

                    if (completions != null && completions.size() > 0) {
                        completions.sort(String.CASE_INSENSITIVE_ORDER);
                    }

                    return completions;
                } catch (CommandException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
                }
            }
        }
    }
}
