package com.elevatemc.elib.util.json;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

/**
 * @author ImHacking
 * @date 4/17/2022
 */
public class ColorAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {

    @Override
    public Color deserialize(JsonElement src, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return fromJson(src);
    }


    @Override
    public JsonElement serialize(Color src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }


    public static JsonObject toJson(Color src) {
        if (src == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        json.addProperty("r", src.getRed());
        json.addProperty("g", src.getGreen());
        json.addProperty("b", src.getBlue());
        return json;
    }
    public static Color fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        final JsonObject json = src.getAsJsonObject();
        int red = json.get("r").getAsInt();
        int green = json.get("g").getAsInt();
        int blue = json.get("b").getAsInt();
        return new Color(red, green, blue);
    }
}
