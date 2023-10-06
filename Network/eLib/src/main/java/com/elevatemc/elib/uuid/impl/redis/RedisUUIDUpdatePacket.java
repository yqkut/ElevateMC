package com.elevatemc.elib.uuid.impl.redis;

import com.elevatemc.elib.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class RedisUUIDUpdatePacket implements Packet {

    @Getter private JsonObject jsonObject;

    RedisUUIDUpdatePacket(UUID uuid,String name) {
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("uuid",uuid.toString());
        this.jsonObject.addProperty("name",name);
    }

    @Override
    public int id() {
        return 999;
    }

    @Override
    public JsonObject serialize() {
        return this.jsonObject;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.jsonObject = object;
    }

    public UUID uuid() {
        return UUID.fromString(this.jsonObject.get("uuid").getAsString());
    }

    public String name() {
        return this.jsonObject.get("name").getAsString();
    }
}
