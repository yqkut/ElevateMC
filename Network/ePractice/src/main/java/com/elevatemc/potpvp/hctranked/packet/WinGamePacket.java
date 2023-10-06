package com.elevatemc.potpvp.hctranked.packet;

import com.elevatemc.elib.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.Getter;

public class WinGamePacket implements Packet {
    @Getter private String id;
    @Getter private int team;

    private JsonObject jsonObject;

    public WinGamePacket() {

    }
    public WinGamePacket(String gameId, int team) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("id", gameId);
        this.jsonObject.addProperty("team", team);
    }

    @Override
    public int id() {
        return 305;
    }

    @Override
    public JsonObject serialize() {
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject object) {

    }
}
