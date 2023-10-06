package com.elevatemc.ehub.utils;

import com.elevatemc.ehub.type.particle.impl.ParticleMeta;
import com.elevatemc.elib.util.Pair;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ParticleUtil {

    public static void sendsParticleToAll(ParticleMeta... particleMetas) {
        ArrayList<Pair<Location, Packet>> packets = new ArrayList<>();

        for (ParticleMeta meta : particleMetas) {
            PacketPlayOutWorldParticles packet;
            packet = new PacketPlayOutWorldParticles();

            packet.a  =  EnumParticle.values()[meta.getEffect().getId()];
            packet.j = false;

            packet.b = (float) meta.getLocation().getX();
            packet.c = (float) meta.getLocation().getY();
            packet.d = (float) meta.getLocation().getZ();
            packet.e = meta.getOffsetX();
            packet.f =  meta.getOffsetY();
            packet.g = meta.getOffsetZ();
            packet.h = meta.getSpeed();
            packet.i =  meta.getAmount();
            packets.add(new Pair<>(meta.getLocation(), packet));
        }

        for (Pair<Location, Packet> pair : packets) {
            double squared = 256 * 256;

            Location center = pair.getKey();
            String worldName = center.getWorld().getName();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().getName().equals(worldName) || player.getLocation().distanceSquared(center) > squared) {
                    continue;
                }
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(pair.getValue());
            }

        }
    }

    private ParticleUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

