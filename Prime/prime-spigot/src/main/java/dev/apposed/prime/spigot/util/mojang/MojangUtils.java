package dev.apposed.prime.spigot.util.mojang;

import dev.apposed.prime.spigot.Prime;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// skidded half this class bc im too lazy lol
public class MojangUtils {

    private static final Map<String, String[]> cachedSkinResponses = new HashMap();

    private interface JSONResponseCallback {
        void handle(JSONObject response);
    }

    public interface UUIDResponseCallback {
        void handle(String uuid);
    }

    public interface GetTextureResponse {
        void handle(String texture, String signature);
    }

    public static void getTextureAndSignature(String playerName, GetTextureResponse response) {
        String[] previousResponse = cachedSkinResponses.get(playerName);
        if (previousResponse != null) {
            response.handle(previousResponse[0], previousResponse[1]);
            return;
        }

        getUUIDForPlayerName(playerName, (uuid -> {
            if (uuid == null) {
                response.handle(null, null);
                return;
            }

            getTextureAndSignatureFromUUID(uuid, ((texture, signature) -> {
                cachedSkinResponses.put(playerName, new String[]{texture, signature});
                response.handle(texture, signature);
            }));
        }));
    }

    public static void getUUIDForPlayerName(String playerName, UUIDResponseCallback response) {
        get("https://api.mojang.com/users/profiles/minecraft/" + playerName, (uuidReply) -> {
            if (uuidReply == null) {
                response.handle(null);
                return;
            }

            String uuidString = (String) uuidReply.get("id");
            if (uuidString == null) {
                response.handle(null);
                return;
            }

            response.handle(formatUUIDWithHyphens(uuidString));
        });
    }

    public static String formatUUIDWithHyphens(String uuid) {
         return uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
    }

    public static void getTextureAndSignatureFromUUID(String uuidString, GetTextureResponse response) {
        get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString + "?unsigned=false", (profileReply) -> {
            if (!profileReply.containsKey("properties")) {
                response.handle(null, null);
                return;
            }

            JSONArray propertiesArray = (JSONArray) profileReply.get("properties");
            JSONObject properties = (JSONObject) propertiesArray.get(0);
            String texture = (String) properties.get("value");
            String signature = (String) properties.get("signature");
            response.handle(texture, signature);
        });
    }
    
    private static void get(String url, JSONResponseCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Prime.getInstance(), () -> {
            try {
                URL rawURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) rawURL.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                if (content.toString().isEmpty()) { // Mojang API 204 fix
                    Bukkit.getScheduler().runTask(Prime.getInstance(), () -> callback.handle(null));
                    return;
                }

                JSONObject jsonObject = (JSONObject) new JSONParser().parse(content.toString());
                Bukkit.getScheduler().runTask(Prime.getInstance(), () -> callback.handle(jsonObject));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(Prime.getInstance(), () -> callback.handle(null));
            }
        });
    }
}
