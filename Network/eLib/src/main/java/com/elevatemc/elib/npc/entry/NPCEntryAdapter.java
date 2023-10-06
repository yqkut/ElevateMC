package com.elevatemc.elib.npc.entry;

import com.elevatemc.elib.skin.MojangSkin;
import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import com.elevatemc.elib.util.json.GsonProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ImHacking
 * @date 6/7/2022
 */
public class NPCEntryAdapter implements JsonSerializer<NPCEntry>, JsonDeserializer<NPCEntry> {

    @Override
    public NPCEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int id = object.get("id").getAsInt();
        String command = null;
        if(object.has("command")) {
            command = object.get("command").getAsString();
        }
        String displayName = object.get("displayName").getAsString();
        Location location = GsonProvider.fromJson(object.getAsJsonObject("location").toString(), Location.class);
        ItemStack[] inventory = new ItemStack[5];
        if (object.has("inventory")) {
            inventory = GsonProvider.fromJson(object.getAsJsonArray("inventory").toString(), ItemStack[].class);
        }
        MojangSkin skin = null;
        if (object.has("mojangSkin")) {
            skin = GsonProvider.fromJson(object.getAsJsonObject("mojangSkin").toString(), MojangSkin.class);
        }


        List<String> lines = new ArrayList<>();
        for (JsonElement element : object.getAsJsonArray("lines")) {
            lines.add(element.getAsString());
        }
        boolean sitting = object.get("sitting").getAsBoolean();
        return new NPCEntry(id, displayName, command, location, inventory, skin, lines, sitting);
    }

    @Override
    public JsonElement serialize(NPCEntry entry, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", entry.getId());
        jsonObject.addProperty("command", entry.getCommand());
        jsonObject.addProperty("displayName", entry.getDisplayName());
        jsonObject.add("location", GsonProvider.fromJson(GsonProvider.toJson(entry.getLocation()), JsonObject.class));
        if (entry.getInventory() != null) {
            JsonArray inventory = new JsonArray();

            for (ItemStack itemStack : entry.getInventory()) {
                if (itemStack == null) {
                    inventory.add(JsonNull.INSTANCE);
                } else {
                    inventory.add(GsonProvider.fromJson(GsonProvider.toJson(itemStack), JsonObject.class));
                }

            }
            jsonObject.add("inventory", inventory);
        }
        if (entry.getMojangSkin() != null) {
            jsonObject.add("mojangSkin", GsonProvider.fromJson(GsonProvider.toJson(entry.getMojangSkin()), JsonObject.class));
        }
        JsonArray lines = new JsonArray();
        for (String hologramLine : entry.getHologramLines()) {
            lines.add(new JsonPrimitive(hologramLine));
        }
        jsonObject.add("lines", lines);
        jsonObject.addProperty("sitting", entry.isSitting());
        return jsonObject;
    }

}