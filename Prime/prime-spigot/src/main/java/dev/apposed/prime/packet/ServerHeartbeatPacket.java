package dev.apposed.prime.packet;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import dev.apposed.prime.spigot.module.server.Server;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor @Getter
public class ServerHeartbeatPacket extends Packet {

    private Server server;

    @Override
    public void onSend() {

    }

    @Override
    public void onReceive() {
        final ServerHandler serverHandler = JavaPlugin.getPlugin(Prime.class).getModuleHandler().getModule(ServerHandler.class);
        serverHandler.updateServer(server);
    }
}
