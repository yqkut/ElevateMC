package com.elevatemc.equeue.queue;

import com.elevatemc.equeue.eQueue;
import com.elevatemc.equeue.util.SortedList;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class QueueHandler {

    @Getter private final ConcurrentHashMap<String, Queue> queues = new ConcurrentHashMap<>();

    public QueueHandler () {
        eQueue.getInstance().getServer().getScheduler().buildTask(eQueue.getInstance(), this::runQueueUpdate).repeat(eQueue.getInstance().getConfig().getNode("speed").getInt(), TimeUnit.SECONDS).schedule();
    }

    public void loadQueues() {
        queues.clear();
        ConfigurationNode config = eQueue.getInstance().getConfig();
        for (ConfigurationNode node : config.getNode("queues").getChildrenMap().values()) {
            String server = node.getNode("server").getString();
            if (server.equals("")) {
                eQueue.getInstance().getLogger().warn("The server name for queue " + server + " is undefined.");
                return;
            }
            Optional<RegisteredServer> info = eQueue.getInstance().getServer().getServer(server);
            if (!info.isPresent()) {
                eQueue.getInstance().getLogger().warn("No server exists with name " + server);
                return;
            }

            boolean enabled = node.getNode("enabled").getBoolean();
            if (enabled) {
                Queue queue = new Queue(server);
                queues.put(server, queue);
            } else {
                eQueue.getInstance().getLogger().info("Queue for server " + server + " is disabled.");
            }

        }
    }

    public void runQueueUpdate() {
        for (Queue queue : queues.values()) {
            Optional<RegisteredServer> info = eQueue.getInstance().getServer().getServer(queue.getServer());
            if (!info.isPresent()) {
                eQueue.getInstance().getLogger().warn("No server exists with name " + queue.getServer());
                return;
            }

            SortedList<QueueEntry> queueEntries = queue.getQueueEntries();
            if (queueEntries.size() == 0) {
                return;
            }
            QueueEntry entry = queueEntries.get(0);
            UUID playerUUID = entry.getUUID();
            Optional<Player> player = eQueue.getInstance().getServer().getPlayer(playerUUID);
            if (!player.isPresent()) { // Somehow a player is still in queue
                eQueue.getInstance().getLogger().error("this shouldnt happen");
                queueEntries.remove(entry);
                sendQueuePositions(queue);
                return;
            }
            player.get().createConnectionRequest(info.get()).connect();
        }
    }

    private void sendQueuePositions(Queue queue) {
        for (int i = 0; i < queue.getQueueEntries().size(); i ++) {
            QueueEntry entry = queue.getQueueEntries().get(i);
            UUID playerUUID = entry.getUUID();
            Optional<Player> playerOpt = eQueue.getInstance().getServer().getPlayer(playerUUID);
            if (!playerOpt.isPresent()) {
                queue.getQueueEntries().remove(entry);
                return;
            }

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF( "position" );
            out.writeUTF(playerOpt.get().getUniqueId().toString());
            out.writeUTF(queue.getServer());
            out.writeInt(i + 1); // Array is zero indexed
            out.writeInt(queue.getQueueEntries().size());
            Optional<ServerConnection> serverConnectionOpt = playerOpt.get().getCurrentServer();
            if (serverConnectionOpt.isPresent()) {
                serverConnectionOpt.get().sendPluginMessage(eQueue.getInstance().getChannel(), out.toByteArray());
            }
        }
    }

    public void joinQueue(UUID uuid, String queueName) {
        boolean alreadyInQueue = false;
        for (Queue q : queues.values()) {
            alreadyInQueue = q.getQueueEntries().stream().anyMatch(entry -> entry.getUUID().equals(uuid));
            if (alreadyInQueue) break;
        }
        Optional<Player> playerOpt = eQueue.getInstance().getServer().getPlayer(uuid);
        if (alreadyInQueue) {
            playerOpt.ifPresent(player -> player.sendMessage(Component.text("You are already in queue.", NamedTextColor.RED)));
            return;
        }
        Queue queue = queues.get(queueName);
        if (queue == null) {
            playerOpt.ifPresent(player -> player.sendMessage(Component.text("The queue does not exists.", NamedTextColor.RED)));
            return;
        }
        queue.getQueueEntries().add(new QueueEntry(uuid, 0));
        sendQueuePositions(queue);
    }

    // This checks all queues because we do not keep track of PLAYER -> QUEUE but only QUEUE -> PLAYER
    public void removeFromQueue(UUID uuid) {
        Queue leftQueue = null;
        for (Queue q : queues.values()) {
            boolean result = q.getQueueEntries().removeIf(entry -> entry.getUUID().equals(uuid));
            if (result) leftQueue = q;
        }
        if (leftQueue != null) {
            sendQueuePositions(leftQueue);
            Optional<Player> playerOpt = eQueue.getInstance().getServer().getPlayer(uuid);
            playerOpt.ifPresent(player -> player.sendMessage(Component.text("You left the queue", NamedTextColor.RED)));
        }

    }
}
