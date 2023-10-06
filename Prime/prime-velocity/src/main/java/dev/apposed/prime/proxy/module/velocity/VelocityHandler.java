package dev.apposed.prime.proxy.module.velocity;

import com.velocitypowered.api.proxy.Player;
import dev.apposed.prime.proxy.module.Module;
import lombok.Getter;

import java.util.*;

@Getter
public class VelocityHandler extends Module {

    private Map<Player, String> prevServer;

    @Override
    public void onEnable() {
        this.prevServer = new HashMap<>();
    }
    public String getPreviousServer(Player player) {
        return this.prevServer.getOrDefault(player, null);
    }

    public void setPrevServer(Player player, String server) {
        if(server == null) {
            this.prevServer.remove(player);
            return;
        }

        prevServer.put(player, server);
    }
}
