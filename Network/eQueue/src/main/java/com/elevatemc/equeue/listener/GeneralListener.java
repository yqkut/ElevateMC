package com.elevatemc.equeue.listener;

import com.elevatemc.equeue.eQueue;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;

public class GeneralListener {
    @Subscribe
    public void onLeave(DisconnectEvent e){
        Player player = e.getPlayer();
        eQueue.getInstance().getQueueHandler().removeFromQueue(player.getUniqueId());
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent e) {
        if (e.getPreviousServer().isPresent()) {
            // TODO: do not remove the player if its from hub to hub
            Player player = e.getPlayer();
            eQueue.getInstance().getQueueHandler().removeFromQueue(player.getUniqueId());
        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {
        if (e.getIdentifier().getId().equals(eQueue.getInstance().getChannel().getId())) {
            ByteArrayDataInput in = ByteStreams.newDataInput( e.getData() );
            String subChannel = in.readUTF();
            if ( subChannel.equalsIgnoreCase( "joinqueue" )) {
                // the receiver is a ProxiedPlayer when a server talks to the proxy
                if ( e.getSource() instanceof ServerConnection)
                {

                    Player player = ((ServerConnection) e.getSource()).getPlayer();
                    String queueName = in.readUTF();
                    eQueue.getInstance().getQueueHandler().joinQueue(player.getUniqueId(), queueName);
                }
            }
        }

    }
}
