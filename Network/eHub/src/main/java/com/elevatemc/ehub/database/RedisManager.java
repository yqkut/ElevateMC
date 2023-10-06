package com.elevatemc.ehub.database;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.armor.ArmorType;
import com.elevatemc.ehub.type.particle.ParticleType;
import com.elevatemc.elib.eLib;
import com.google.gson.*;
import lombok.Getter;

@Getter
public class RedisManager {

    private final eHub instance = eHub.getInstance();


    private final JsonParser parser;

    public RedisManager() {
        parser = new JsonParser();

    }

    //How we load all the cosmetics for the player via redis
    public void load (Profile profile) {
        eLib.getInstance().runRedisCommand(jedis -> {

            if (jedis.hexists("user", profile.getUuid().toString())) {
                String stringObject = jedis.hget("users", profile.getUuid().toString());
                JsonObject object = parser.parse(stringObject).getAsJsonObject();

                String armorType = object.get("armorType").getAsString().toUpperCase();
                profile.setArmorType(armorType.isEmpty() ? null : ArmorType.valueOf(armorType));

                if (profile.getArmorType() != null) {
                    profile.setEnchanted(object.get("enchanted").getAsBoolean());
                    profile.setAstronaut(object.get("astronaut").getAsBoolean());

                    JsonArray array = object.get("armors").getAsJsonArray();

                    int i = 0;
                    for (JsonElement element : array) {
                        profile.getArmors() [i] = element.getAsBoolean();
                        i++;
                    }
                }

                String name = object.get("particleType").getAsString();

                profile.setParticleType(name == null ? null : name.isEmpty() ? null : ParticleType.valueOf(name.replace(" Particle", "").toUpperCase()));

            } else {
                JsonObject object = new JsonObject();
                JsonArray array = new JsonArray();

                object.addProperty("armorType", "");
                object.addProperty("particleType", "");

                for (boolean value : profile.getArmors()) {
                    array.add(new JsonPrimitive(value));
                }

                object.add("armors", array);
                object.addProperty("enchanted", false);
                object.addProperty("astronaut", false);

                jedis.hset("users", profile.getUuid().toString(), object.toString());
            }
            return null;
        });

    }

    //How we save armor via redis
    public void saveArmor (Profile profile, ArmorType type) {
        eLib.getInstance().runRedisCommand(jedis -> {

            String stringObject = jedis.hget("users", profile.getUuid().toString());
            JsonObject object = parser.parse(stringObject).getAsJsonObject();

            if (type != null) {
                JsonArray array = new JsonArray();

                object.addProperty("enchanted", profile.isEnchanted());

                for (boolean value : profile.getArmors()) {
                    array.add(new JsonPrimitive(value));
                }

                object.add("armors", array);
            }

            object.addProperty("astronaut", profile.isAstronaut());
            object.addProperty("armorType", type == null ? "" : type.getName());

            jedis.hset("users", profile.getUuid().toString(), object.toString());
            return null;
        });
    }


    //How we save Particles via redis
    public void saveParticle (Profile profile, ParticleType type) {
        eLib.getInstance().runRedisCommand(jedis -> {

            String stringObject = jedis.hget("users", profile.getUuid().toString());
            JsonObject object = parser.parse(stringObject).getAsJsonObject();

            object.addProperty("particleType", type == null ? "" : type.getName());

            jedis.hset("users", profile.getUuid().toString(), object.toString());
            return null;
        });
    }
}
