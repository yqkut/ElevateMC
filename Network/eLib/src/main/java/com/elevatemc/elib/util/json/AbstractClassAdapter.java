package com.elevatemc.elib.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class AbstractClassAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_KEY = "@CLASS";
    private static final String DATA_KEY = "@DATA";

    @Override
    public Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get(CLASS_KEY).getAsString();
        try {
            Class<?> clazz = Class.forName(className);
            return GsonProvider.getGsonHierarchyAbsent().fromJson(jsonObject.get(DATA_KEY), clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(Object src, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CLASS_KEY, src.getClass().getName());
        jsonObject.add(DATA_KEY, GsonProvider.getGsonHierarchyAbsent().toJsonTree(src));
        return jsonObject;
    }

}
