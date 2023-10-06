package dev.apposed.prime.proxy.module.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data @AllArgsConstructor
public class Server {

    private final String name, group;
    private boolean whitelisted;
    private int players, maxPlayers;
    private long lastHeartbeat;

    public boolean isAlive() {
        return (System.currentTimeMillis() - lastHeartbeat) <= TimeUnit.SECONDS.toMillis(15L);
    }

    public Server(String name, String group) {
        this.name = name;
        this.group = group;
    }
}
