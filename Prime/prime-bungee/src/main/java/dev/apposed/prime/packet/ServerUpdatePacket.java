package dev.apposed.prime.packet;

import dev.apposed.prime.proxy.module.database.redis.packet.Packet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerUpdatePacket extends Packet {

    private String serverName;

    @Override
    public void onReceive() {

    }

    @Override
    public void onSend() {

    }
}
