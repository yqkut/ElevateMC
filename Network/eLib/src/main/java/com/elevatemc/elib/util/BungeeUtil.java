package com.elevatemc.elib.util;

import com.elevatemc.elib.eLib;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class BungeeUtil {
    public static void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server.toLowerCase());
        player.sendPluginMessage(eLib.getInstance(), "BungeeCord", out.toByteArray());
    }
}
