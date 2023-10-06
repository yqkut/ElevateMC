package dev.apposed.prime.proxy.module.bungee;

import dev.apposed.prime.proxy.module.Module;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

@Getter
public class BungeeHandler extends Module {

    private Map<ProxiedPlayer, String> prevServer;

    @Override
    public void onEnable() {
        this.prevServer = new HashMap<>();
    }
    public String getPreviousServer(ProxiedPlayer player) {
        return this.prevServer.getOrDefault(player, null);
    }

    public void setPrevServer(ProxiedPlayer player, String server) {
        if(server == null) {
            this.prevServer.remove(player);
            return;
        }

        prevServer.put(player, server);
    }
}
