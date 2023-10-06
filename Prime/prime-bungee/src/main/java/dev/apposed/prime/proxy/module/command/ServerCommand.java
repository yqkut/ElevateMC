package dev.apposed.prime.proxy.module.command;

import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import dev.apposed.prime.proxy.module.server.ServerHandler;
import dev.apposed.prime.proxy.util.Color;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;


public class ServerCommand extends Command {

    private final PrimeProxy primeProxy;
    private final ProfileHandler profileHandler;
    private final ServerHandler serverHandler;

    public ServerCommand(PrimeProxy primeProxy) {
        super("server");

        this.primeProxy = primeProxy;
        this.profileHandler = primeProxy.getModuleHandler().getModule(ProfileHandler.class);
        this.serverHandler = primeProxy.getModuleHandler().getModule(ServerHandler.class);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;

        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) {
            player.sendMessage(Color.translate("&cProfile not loaded properly."));
            return;
        }

        final Profile profile = profileOptional.get();

        if(!profile.hasServerPerm()) {
            player.sendMessage(Color.translate("&cNo Permission."));
            return;
        }

        if(args.length == 0) {
            player.sendMessage(Color.translate("&6You are currently connected to &f" + player.getServer().getInfo().getName() + "&6."));
            String servers = Color.translate("&6Servers: &f");
            boolean first = true;

            for(String name : this.primeProxy.getProxy().getServers().keySet()) {
                if(first) {
                    servers += name;
                } else {
                    servers += Color.translate("&7, &f" + name);
                }

                first = false;
            }

            player.sendMessage(servers);
            player.sendMessage(Color.translate("&6Connect to a server with &e/server <name>"));
            return;
        }

        ServerInfo server = this.primeProxy.getProxy().getServerInfo(args[0]);
        if(server == null) {
            player.sendMessage(Color.translate("&cNo server by the name &f" + args[0] + "&c found!"));
            return;
        }

        player.connect(server);
        player.sendMessage(Color.translate("&6Connecting to &f" + server.getName() + "&6!"));
    }
}
