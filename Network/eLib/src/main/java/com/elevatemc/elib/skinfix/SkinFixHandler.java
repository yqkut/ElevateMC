package com.elevatemc.elib.skinfix;


import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.spigot.handler.PacketHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SkinFixHandler implements Listener, PacketHandler {
    public static boolean skinFix = true;

    private final Map<UUID, Set<UUID>> sentInfo = new HashMap<>();
    public void handleSentPacket(PlayerConnection connection, Packet incoming) {
        if(!(incoming instanceof PacketPlayOutPlayerInfo)) {
            return;
        }
        if (!skinFix) {
            return;
        }

        PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) incoming;
        Player player = connection.getPlayer();

        PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = packet.getA();
        GameProfile updated = packet.getPlayer();

        if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) { // add
            Player updatedPlayer = Bukkit.getPlayer(updated.getId());
            if (updatedPlayer == null) {
                return;
            }

            if (!sentInfo.containsKey(player.getUniqueId())) {
                sentInfo.put(player.getUniqueId(), new HashSet<>());
            }

            Set<UUID> sent = sentInfo.get(player.getUniqueId());

            if (sent.add(updated.getId())) {
                TaskUtil.scheduleOnPool(() -> sendUpdate(player, updatedPlayer), 500, TimeUnit.MILLISECONDS);
            }
        } else if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) { // remove
            if (sentInfo.containsKey(player.getUniqueId())) {
                sentInfo.get(player.getUniqueId()).remove(updated.getId());
            }
        }
    }


    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!skinFix) {
            return;
        }

        TaskUtil.scheduleOnPool(() ->
                sendUpdate(event.getPlayer(), event.getPlayer()), 500, TimeUnit.MILLISECONDS);
    }
    @EventHandler
    public void onPlayerWorld(PlayerChangedWorldEvent event) {
        if (!skinFix) {
            return;
        }

        TaskUtil.scheduleOnPool(() ->
            sendUpdate(event.getPlayer(), event.getPlayer()), 500, TimeUnit.MILLISECONDS);
    }
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        sentInfo.remove(event.getPlayer().getUniqueId());
    }

    public void sendUpdate(Player viewer, Player player) {
        if (!skinFix) {
            return;
        }

        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle());

        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(remove);
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(add);
    }



}
