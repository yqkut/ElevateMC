package com.elevatemc.elib.skin;

import com.elevatemc.elib.eLib;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import com.elevatemc.elib.util.FileConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class MojangSkinHandler {

    private static final JsonParser jsonParser = new JsonParser();
    private static final String mojangURL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    private final Map<UUID, MojangSkin> uuidToMojangSkinMap;
    private final Map<UUID, MojangSkin> tempUuidToMojangSkinMap;
    private final Map<String, MojangSkin> preSetKeyToMojangSkinMap;

    private final FileConfig cacheFile;

    public MojangSkinHandler(eLib eLib) {
        this.uuidToMojangSkinMap = new HashMap<>();
        this.tempUuidToMojangSkinMap = new HashMap<>();
        this.preSetKeyToMojangSkinMap = new HashMap<>();
        this.cacheFile = new FileConfig(eLib, "skinsCache");

        this.loadUUIDSkinCache();
        this.loadSkinCache("/skins/premadeskins.json", "premade");
        this.loadSkinCache("/skins/chars/blue.json", "blue");
        this.loadSkinCache("/skins/chars/cyan.json", "cyan");
        this.loadSkinCache("/skins/chars/darkgreen.json", "darkgreen");
        this.loadSkinCache("/skins/chars/gray.json", "gray");
        this.loadSkinCache("/skins/chars/green.json", "green");
        this.loadSkinCache("/skins/chars/magenta.json", "magenta");
        this.loadSkinCache("/skins/chars/orange.json", "orange");
        this.loadSkinCache("/skins/chars/purple.json", "purple");
        this.loadSkinCache("/skins/chars/red.json", "red");
        this.loadSkinCache("/skins/chars/white.json", "white");
        this.loadSkinCache("/skins/chars/yellow.json", "yellow");

        PluginManager pluginManager = eLib.getServer().getPluginManager();
        pluginManager.registerEvents(new MojangSkinListener(this), eLib);
    }

    public MojangSkin getPremadeSkin(String string) {
        return this.preSetKeyToMojangSkinMap.get(string);
    }

    public MojangSkin getLetterSkin(String mapKey, char character) {
        return this.getPremadeSkin((mapKey + "-" + character).toLowerCase());
    }

    public MojangSkin getCachedMojangSkin(UUID uuid) {
        return this.uuidToMojangSkinMap.get(uuid);
    }

    public void removeTemporarySkinEntry(UUID uuid) {
        this.tempUuidToMojangSkinMap.remove(uuid);
    }

    public MojangSkin getMojangSkinFromPlayer(Player player) {
        if (player == null) {
            return null;
        }

        MojangSkin tempSkin = this.tempUuidToMojangSkinMap.get(player.getUniqueId());
        if (tempSkin != null) {
            return tempSkin;
        }

        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        Collection<Property> properties = profile.getProperties().get("textures");
        for (Property proper : properties) {
            String sig = proper.getSignature();
            String val = proper.getValue();
            MojangSkin skin = new MojangSkin(player.getUniqueId(), val, sig);
            this.tempUuidToMojangSkinMap.put(skin.getUuid(), skin);
            return skin;
        }

        return null;
    }

    public MojangSkin getMojangSkin(UUID uuid) {
        MojangSkin skin = this.getCachedMojangSkin(uuid);

        if (skin != null) {
            return skin;
        }

        try {
            MojangSkin mojangSkin = this.fetchSkin(uuid);
            if (mojangSkin != null) {
                this.uuidToMojangSkinMap.put(uuid, mojangSkin);
            }
            return mojangSkin;
        } catch (Exception e) {
            System.out.println("Failed to fetch mojang skin for " + uuid.toString());
            return null;
        }
    }

    public void saveUUIDSkinCache() {
        if (this.cacheFile == null) {
            return;
        }

        JsonObject jsonObject = new JsonObject();

        this.uuidToMojangSkinMap.entrySet().iterator().forEachRemaining(entry -> {
            UUID uuid = entry.getKey();
            MojangSkin skin = entry.getValue();
            String encoded = eLib.GSON.toJson(skin);
            jsonObject.addProperty(uuid.toString(), encoded);
        });

        this.cacheFile.getConfiguration().set("cache", jsonObject.toString());
        this.cacheFile.save();
    }

    private MojangSkin fetchSkin(UUID uuid) throws IOException {
        HttpURLConnection connection = this.createHttpURLConnection(uuid);
        JsonElement jsonElement = this.readHttpURLConnection(connection);
        connection.disconnect();

        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject propertyObject = jsonObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String value = propertyObject.get("value").getAsString();
        String signature = propertyObject.get("signature").getAsString();
        return new MojangSkin(uuid, value, signature);
    }

    private void loadSkinCache(String resource, String mapKey) {
        InputStream stream = eLib.class.getResourceAsStream(resource);

        if (stream == null) {
            return;
        }

        Scanner scanner = new Scanner(stream);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        JsonObject jsonObject = jsonParser.parse(builder.toString()).getAsJsonObject();
        jsonObject.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonObject object = entry.getValue().getAsJsonObject();
            String val = object.get("value").getAsString();
            String sig = object.get("signature").getAsString();
            MojangSkin skin = new MojangSkin(null, val, sig);
            this.preSetKeyToMojangSkinMap.put(mapKey + "-" + key.toLowerCase(), skin);
        });
    }

    private void loadUUIDSkinCache() {
        if (this.cacheFile == null || this.cacheFile.getConfiguration() == null) {
            return;
        }

        if (!this.cacheFile.getConfiguration().contains("cache")) {
            return;
        }

        this.uuidToMojangSkinMap.clear();

        String jsonString = this.cacheFile.getString("cache");
        JsonElement jsonElement = jsonParser.parse(jsonString);

        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.entrySet().iterator().forEachRemaining(entry -> {
            String key = entry.getKey();
            UUID uuid = UUID.fromString(key);
            String value = entry.getValue().getAsString();
            MojangSkin mojangSkin = eLib.GSON.fromJson(value, MojangSkin.class);
            this.uuidToMojangSkinMap.put(uuid, mojangSkin);
        });
    }

    private JsonElement readHttpURLConnection(HttpURLConnection connection) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        reader.close();
        return jsonParser.parse(output.toString());
    }

    private HttpURLConnection createHttpURLConnection(UUID uuid) throws IOException {
        String shortUUID = uuid.toString().replace("-", "");
        HttpURLConnection connection = (HttpURLConnection) new URL(String.format(mojangURL, shortUUID)).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "ElevateMC eLib Mojang Fetcher");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        return connection;
    }
}
