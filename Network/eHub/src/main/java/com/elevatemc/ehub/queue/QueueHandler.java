package com.elevatemc.ehub.queue;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.queue.listener.MessageListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class QueueHandler {
    public QueueHandler() {
        eHub.getInstance().getServer().getMessenger().registerOutgoingPluginChannel( eHub.getInstance(), "equeue:main");
        eHub.getInstance().getServer().getMessenger().registerIncomingPluginChannel( eHub.getInstance(), "equeue:main", new MessageListener());
    }

    @Getter private HashMap<UUID, QueuePosition> positions = new HashMap<>();

    public void setPosition(Player player, String server, int position, int total) {
        getPositions().put(player.getUniqueId(), new QueuePosition(server, position, total));
    }

    public void joinQueue(Player player, String queueName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "joinqueue" );
        out.writeUTF(queueName);

        player.sendPluginMessage(eHub.getInstance(), "equeue:main", out.toByteArray());
    }
}
