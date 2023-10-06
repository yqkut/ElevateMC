package com.elevatemc.potpvp.hctranked.packet;

import com.elevatemc.elib.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.Getter;

public class VoidGamePacket implements Packet {
    @Getter private String id;

    @Override
    public int id() {
        return 303;
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject object) {
        id = object.get("id").getAsString();
    }
}
