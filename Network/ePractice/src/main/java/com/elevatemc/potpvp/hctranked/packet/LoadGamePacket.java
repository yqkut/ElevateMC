package com.elevatemc.potpvp.hctranked.packet;

import com.elevatemc.elib.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Hash;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LoadGamePacket implements Packet {

    @Getter private String id;
    @Getter private Set<UUID> players = new HashSet<>();
    @Getter private UUID team1Captain;
    @Getter private Set<UUID> team1 = new HashSet<>();
    @Getter private UUID team2Captain;
    @Getter private Set<UUID> team2 = new HashSet<>();
    @Getter private String map;

    @Override
    public int id() {
        return 302;
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject object) {
        id = object.get("id").getAsString();

        object.getAsJsonArray("players").forEach(obj -> {
            players.add(UUID.fromString(obj.getAsString()));
        });

        JsonObject team1Obj = object.get("team1").getAsJsonObject();
        JsonObject team2Obj = object.get("team2").getAsJsonObject();

        team1Obj.get("players").getAsJsonArray().forEach(obj -> {
            team1.add(UUID.fromString(obj.getAsString()));
        });
        team2Obj.get("players").getAsJsonArray().forEach(obj -> {
            team2.add(UUID.fromString(obj.getAsString()));
        });

        team1Captain = UUID.fromString(team1Obj.get("captain").getAsString());
        team2Captain = UUID.fromString(team2Obj.get("captain").getAsString());

        map = object.get("map").getAsString();
    }
}
