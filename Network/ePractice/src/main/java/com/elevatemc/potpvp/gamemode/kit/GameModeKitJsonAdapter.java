package com.elevatemc.potpvp.gamemode.kit;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class GameModeKitJsonAdapter extends TypeAdapter<GameModeKit> {

    @Override
    public void write(JsonWriter writer, GameModeKit kit) throws IOException {
        writer.value(kit.getId());
    }

    @Override
    public GameModeKit read(JsonReader reader) throws IOException {
        return GameModeKit.byId(reader.nextString());
    }
}