package com.elevatemc.potpvp.hctranked.packet;

import com.elevatemc.elib.pidgin.packet.Packet;
import com.google.gson.JsonObject;

public class PingPacket implements Packet {
    @Override
    public int id() {
        return 300;
    }

    @Override
    public JsonObject serialize() {
        return new JsonObject();
    }

    @Override
    public void deserialize(JsonObject object) {

    }
}
