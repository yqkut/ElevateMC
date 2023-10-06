package com.elevatemc.elib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class MojangUtil {

    public static UUID getFromMojang(String name) throws IOException {

        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        final URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final String line = reader.readLine();

        if (line == null) {
            return null;
        }

        final String[] id = line.split(",");

        String part = id[1];
        part = part.substring(6,38);

        return UUID.fromString(part.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public static String getFromMojang(UUID uuid) throws IOException {

        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + uuid.toString().replace("-",""));
        final URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final String line = reader.readLine();

        if (line == null) {
            return null;
        }

        return line;
    }
}
