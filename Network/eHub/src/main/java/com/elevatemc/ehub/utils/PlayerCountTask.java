package com.elevatemc.ehub.utils;

import com.elevatemc.ehub.eHub;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

public class PlayerCountTask implements Runnable {
    List<String> servers;

    public PlayerCountTask(List<String> servers) {
        this.servers = servers;
    }

    public void run() {
        if (eHub.getInstance().getServer().getOnlinePlayers().size() == 0) {
            return;
        }
        for (String server : servers) {
            pingBungee(server);
        }
    }

    private void pingBungee(String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Bukkit.getServer().sendPluginMessage(eHub.getInstance(), "BungeeCord", b.toByteArray());
    }
}
