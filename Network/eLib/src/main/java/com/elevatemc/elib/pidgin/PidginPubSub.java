package com.elevatemc.elib.pidgin;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.pidgin.packet.Packet;
import com.elevatemc.elib.pidgin.packet.listener.PacketListenerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

@AllArgsConstructor
public class PidginPubSub extends JedisPubSub {

    @Getter private String channel;

    @Override
    public void onMessage(String channel,String message) {

        if (!channel.equalsIgnoreCase(this.channel)) {
            return;
        }

        try {
            final String[] args = message.split(";");
            final Integer id = Integer.valueOf(args[0]);
            final Packet packet = eLib.getInstance().getPidginHandler().buildPacket(id);
            if (packet == null) {
                return;
            }

            packet.deserialize(PidginHandler.PARSER.parse(args[1]).getAsJsonObject());

            for (PacketListenerData listener : eLib.getInstance().getPidginHandler().getListeners()) {

                if (!listener.matches(packet)) {
                    continue;
                }

                listener.getMethod().invoke(listener.getInstance(),packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
