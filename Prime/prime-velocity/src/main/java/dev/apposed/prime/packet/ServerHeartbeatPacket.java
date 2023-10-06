package dev.apposed.prime.packet;

import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.database.redis.packet.Packet;
import dev.apposed.prime.proxy.module.server.Server;
import dev.apposed.prime.proxy.module.server.ServerHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerHeartbeatPacket extends Packet {

    private Server server;

    @Override
    public void onSend() {

    }

    @Override
    public void onReceive() {
        final ServerHandler serverHandler = PrimeProxy.getInstance().getModuleHandler().getModule(ServerHandler.class);
        serverHandler.updateServer(server);
    }
}
