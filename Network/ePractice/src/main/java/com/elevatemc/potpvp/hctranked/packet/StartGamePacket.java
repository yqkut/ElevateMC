package com.elevatemc.potpvp.hctranked.packet;

import com.elevatemc.elib.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.Getter;

public class StartGamePacket implements Packet {
    @Getter private String gameId;
    @Getter private String match;

    private JsonObject jsonObject;

    public StartGamePacket() {

    }

    public StartGamePacket(String gameId, String match) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("id", gameId);
        this.jsonObject.addProperty("match", match);
    }

    @Override
    public int id() {
        return 304;
    }

    @Override
    public JsonObject serialize() {
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject object) {

    }
}
