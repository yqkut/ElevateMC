package dev.apposed.prime.proxy.module.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import dev.apposed.prime.proxy.module.server.ServerHandler;
import dev.apposed.prime.proxy.util.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Optional;


public class ServerCommand implements SimpleCommand {

    private final PrimeProxy primeProxy;
    private final ProfileHandler profileHandler;

    public ServerCommand(PrimeProxy primeProxy) {
        this.primeProxy = primeProxy;
        this.profileHandler = primeProxy.getModuleHandler().getModule(ProfileHandler.class);
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if(!(source instanceof Player)) return;
        Player player = (Player) source;



        if(args.length == 0) {
            player.sendMessage(Color.translate("&6You are currently connected to &f" + player.getCurrentServer().get().getServerInfo().getName() + "&6."));
            TextComponent servers = Color.translate("&6Servers: &f");
            boolean first = true;

            for (RegisteredServer srv : this.primeProxy.getServer().getAllServers()) {
                if(first) {
                    servers = servers.append(Component.text(srv.getServerInfo().getName()));
                } else {
                    servers = servers.append(Color.translate("&7, &f" + srv.getServerInfo().getName()));
                }

                first = false;
            }

            player.sendMessage(servers);
            player.sendMessage(Color.translate("&6Connect to a server with &e/server <name>"));
            return;
        }

        Optional<RegisteredServer> serverOptional = this.primeProxy.getServer().getServer(args[0]);
        if (!serverOptional.isPresent()) {
            player.sendMessage(Color.translate("&cNo server by the name &f" + args[0] + "&c found!"));
            return;
        }

        player.createConnectionRequest(serverOptional.get()).connect();
        player.sendMessage(Color.translate("&6Connecting to &f" + serverOptional.get().getServerInfo().getName() + "&6!"));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        CommandSource source = invocation.source();
        if(!(source instanceof Player)) return false;

        Player player = (Player) invocation.source();
        final Profile profile = profileHandler.getProfile(player.getUniqueId()).orElse(null);
        if(profile == null) return false;
        return profile.hasServerPerm();
    }
}
