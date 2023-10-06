package dev.apposed.prime.packet;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.module.server.filter.ChatFilterHandler;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class ServerUpdatePacket extends Packet {

    private String serverName;

    @Override
    public void onReceive() {
        final Prime plugin = JavaPlugin.getPlugin(Prime.class);
        final ServerHandler serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);

        if(this.serverName.equalsIgnoreCase(serverHandler.getCurrentName())) {
            plugin.reloadConfig();
            plugin.getModuleHandler().getModule(ChatFilterHandler.class).loadFilters();
        }
    }

    @Override
    public void onSend() {

    }
}
