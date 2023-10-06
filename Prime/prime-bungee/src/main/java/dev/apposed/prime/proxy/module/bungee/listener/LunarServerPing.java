package dev.apposed.prime.proxy.module.bungee.listener;

import net.md_5.bungee.api.ServerPing;

public class LunarServerPing extends ServerPing {
    private final String lcServer = "hugecock";

    public LunarServerPing(ServerPing parent) {
        this.setDescription(parent.getDescription());
        this.setFavicon(parent.getFaviconObject());
        this.setPlayers(parent.getPlayers());
        this.setVersion(parent.getVersion());
    }
}
