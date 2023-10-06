package com.elevatemc.elib.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ClassAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Override
    public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        try {
            return Class.forName(jsonObject.get("classname").getAsString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("classname", aClass.getName());
        return jsonObject;
    }

}
