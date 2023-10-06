package com.elevatemc.ehub.queue.listener;

import com.elevatemc.ehub.eHub;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class MessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player p, byte[] bytes) {
        if (!s.equalsIgnoreCase("equeue:main")) {
            return;
        }
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String subchannel = in.readUTF();
            if (subchannel.equals("position")) {
                String playerUUIDString = in.readUTF();
                UUID uuid;
                try {
                    uuid = UUID.fromString(playerUUIDString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
                Player player = Bukkit.getPlayer(uuid);
                String server = in.readUTF();
                int position = in.readInt();
                int total = in.readInt();
                eHub.getInstance().getQueueHandler().setPosition(player, server, position, total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
