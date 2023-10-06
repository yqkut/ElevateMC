package dev.apposed.prime.spigot.module.server.task;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.server.Server;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.packet.ServerHeartbeatPacket;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerHeartbeatTask extends BukkitRunnable {

    private final Prime plugin;

    private final JedisModule jedisModule;
    private final ServerHandler serverHandler;

    public ServerHeartbeatTask(Prime plugin) {
        this.plugin = plugin;

        this.jedisModule = plugin.getModuleHandler().getModule(JedisModule.class);
        this.serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);
    }

    @Override
    public void run() {
        Server server = this.serverHandler.getCurrentServer();
        server.update();
        this.jedisModule.sendPacket(new ServerHeartbeatPacket(server));
    }
}
