package com.elevatemc.elib.util;

import com.elevatemc.spigot.handler.PacketHandler;
import net.minecraft.server.v1_8_R3.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
<<<<<<< HEAD
 * @author ImHacking
 * @date 5/10/2022
 */


public class InventoryAdapter implements PacketHandler
{
    private static final Set<UUID> currentlyOpen = new HashSet<>();

    @Override
    public void handleReceivedPacket(PlayerConnection connection, Packet<?> packet) {
        if (packet instanceof PacketPlayInClientCommand) {
            PacketPlayInClientCommand packetPlayInClientCommand = (PacketPlayInClientCommand) packet;
            if (packetPlayInClientCommand.getCommand().equals(PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT)) {
                InventoryAdapter.currentlyOpen.add(connection.getPlayer().getUniqueId());
            }
        }

        if (packet instanceof PacketPlayInCloseWindow) {
            InventoryAdapter.currentlyOpen.remove(connection.getPlayer().getUniqueId());
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection connection, Packet<?> packet) {
    }

    public static Set<UUID> getCurrentlyOpen() {
        return InventoryAdapter.currentlyOpen;
    }

}
