package dev.apposed.prime.spigot.module.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data @AllArgsConstructor
public class Server {

    private final String name, group;
    private boolean whitelisted;
    private int players, maxPlayers;
    private long lastHeartbeat;
    private boolean chatMuted;
    private long chatSlow;

    public boolean isAlive() {
        return (System.currentTimeMillis() - lastHeartbeat) <= TimeUnit.SECONDS.toMillis(15L);
    }

    public Server(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public void update() {
        this.whitelisted = Bukkit.hasWhitelist();
        this.players = Bukkit.getOnlinePlayers().size();
        this.maxPlayers = Bukkit.getMaxPlayers();
        this.lastHeartbeat = System.currentTimeMillis();
    }
}
