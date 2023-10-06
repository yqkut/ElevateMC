package dev.apposed.prime.proxy.module.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.util.Color;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceCommand implements SimpleCommand {

    private final PrimeProxy primeProxy;

    public MaintenanceCommand(PrimeProxy primeProxy) {
        this.primeProxy = primeProxy;
    }

    @Override
    public void execute(Invocation invocation) {
        final CommandSource source = invocation.source();
        final String[] args = invocation.arguments();

        if(args.length == 0) {
            source.sendMessage(Color.translate("&cUsage: /maintenance toggle/status/list/add/remove [player]"));
            return;
        }

        switch(args[0].toLowerCase()) {
            case "toggle": {
                final ConfigurationNode statusNode = primeProxy.getMaintenance().getNode("enabled");
                if(primeProxy.isInMaintenance()) {
                    statusNode.setValue(false);
                    source.sendMessage(Color.translate("&aProxy maintenance has been disabled"));
                } else {
                    statusNode.setValue(true);
                    source.sendMessage(Color.translate("&aProxy maintenance has been enabled"));
                }
                break;
            }
            case "status": {
                source.sendMessage(Color.translate("&eMaintenance Status: " + (primeProxy.isInMaintenance() ? "&aEnabled" : "&cDisabled")));
                break;
            }
            case "list": {
                source.sendMessage(Color.translate("&a" + primeProxy.getWhitelistedUsernames().size() + " whitelisted players"));
                primeProxy.getWhitelistedUsernames().forEach(name -> source.sendMessage(Color.translate("&7- &a" + name)));
                break;
            }
            case "add": {
                if(args.length < 2) {
                    source.sendMessage(Color.translate("&cYou did not specify a username to add to the whitelist."));
                    return;
                }

                final String username = args[1].toLowerCase();

                final List<String> usernames = new ArrayList<>(primeProxy.getWhitelistedUsernames());
                if(usernames.contains(username)) {
                    source.sendMessage(Color.translate("&cThat player is already on the maintenance whitelist."));
                    return;
                }

                usernames.add(username);

                final ConfigurationNode whitelistNode = primeProxy.getMaintenance().getNode("whitelist");
                whitelistNode.setValue(usernames);

                source.sendMessage(Color.translate("&aAdded " + username + " to the maintenance whitelist."));
                break;
            }
            case "remove": {
                if(args.length < 2) {
                    source.sendMessage(Color.translate("&cYou did not specify a username to remove from the whitelist."));
                    return;
                }

                final String username = args[1].toLowerCase();

                final List<String> usernames = new ArrayList<>(primeProxy.getWhitelistedUsernames());
                if(!usernames.contains(username)) {
                    source.sendMessage(Color.translate("&cThat player is not on a maintenace whitelist."));
                    return;
                }

                usernames.remove(username);

                final ConfigurationNode whitelistNode = primeProxy.getMaintenance().getNode("whitelist");
                whitelistNode.setValue(usernames);

                source.sendMessage(Color.translate("&cRemoved " + username + " from the maintenance whitelist."));
                break;
            }
            default: {
                source.sendMessage(Color.translate("&cUsage: /maintenance toggle/status/list/add/remove [player]"));
                break;
            }
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        final CommandSource source = invocation.source();
        if(!(source instanceof Player)) return true;

        final Player player = (Player) source;
        return player.hasPermission("maintenance.admin");
    }
}
