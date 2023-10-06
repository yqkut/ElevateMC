package com.elevatemc.elib.util;

import com.elevatemc.elib.eLib;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class UUIDFetcher {

    private static final JsonParser PARSER = new JsonParser();
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    /**
     * Fetches the uuid synchronously for a specified name and time
     *
     * @param name The name
     */
    public static UUID getUUID(String name) {
        if (name.length() > 16){
            return null;
        }

        final UUID value = getCachedUUID(name);

        if (value != null){
            return value;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name)).openConnection();
            connection.setReadTimeout(5000);

            JsonElement jsonElement = PARSER.parse(new BufferedReader(new InputStreamReader(connection.getInputStream())));

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String correctName = jsonObject.get("name").getAsString();
                UUID uuid = fromString(jsonObject.get("id").getAsString());
                if (correctName != null) {
                    updateCache(correctName, uuid);
                    return uuid;
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Fetches the uuid and checks if its cached synchronously for a specified name and time
     *
     * @param name the name of the player to get the uuid for
     */
    public static UUID getCachedUUID(String name) {
        if (name.length() > 16){
            return null;
        }
        return eLib.getInstance().getUuidCache().uuid(name);
    }

    /**
     * Fetches the name synchronously and returns it
     *
     * @param uuid The uuid
     * @return The name
     */
    public static String getName(UUID uuid) {
        final String value = getCachedName(uuid);

        if (value != null){
            return value;
        }

        try {
            URL obj = new URL(String.format(UUID_URL, uuid.toString()));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            JsonElement object = PARSER.parse(response.toString());
            JsonObject parsedObject = object.getAsJsonObject();

            String name = parsedObject.get("username").getAsString();
            updateCache(name, uuid);
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Fetches the name in caches synchronously and returns it
     *
     * @param uuid The uuid
     * @return The name
     */
    public static String getCachedName(UUID uuid) {
       return eLib.getInstance().getUuidCache().name(uuid);
    }

    public static void updateCache(String playerName, UUID uuid){
        updateCache(playerName, uuid, true);
    }

    public static void updateCache(String playerName, UUID uuid, boolean updateRedis) {
        if(updateRedis) {
            eLib.getInstance().getUuidCache().updateAll(uuid, playerName);
        }
    }

    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
