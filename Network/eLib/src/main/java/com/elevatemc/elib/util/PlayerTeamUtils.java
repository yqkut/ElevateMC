package com.elevatemc.elib.util;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collection;


public class PlayerTeamUtils {

    public static void sendPlayerInfoPacket(Player player, int ping, String username, GameProfile profile, int mode) {
        PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.values()[mode];

        playerInfo.setA(action);
        playerInfo.getB().add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, ping, WorldSettings.EnumGamemode.SURVIVAL, CraftChatMessage.fromString(username)[0]));
        sendPacket(player, playerInfo);
    }

    public static void sendUpdatePlayers(Player viewer, String teamName, Collection<String> players, int mode) {
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam();
        teamPacket.a = (teamName);
        teamPacket.b = (teamName);
        teamPacket.c = ("");
        teamPacket.d = ("");

        if (mode == 0 || mode == 3 || mode == 4) {
            teamPacket.g = (players);
        }

        teamPacket.h = (mode);
        teamPacket.i = (0);

        sendPacket(viewer, teamPacket);
    }

    //a = name
    //b = display //16
    //c = prefix //16
    //d = suffix //16
    //e = players
    //f = mode
    //g = friendlyFire 1 = on 0 = off
    //mode 0 = create, 1 = remove, 2 = update, 3 = new players, 4 = remove players
    public static void sendTeamPacket(Player viewer, String targetName, String teamName, String prefix, String suffix, boolean friendly, int mode) {
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam();
        teamPacket.a = (teamName);
        teamPacket.b = (teamName);

        if (mode == 0 || mode == 2) {
            teamPacket.c = (prefix);
            teamPacket.d = (suffix);
        }

        if (mode == 0 || mode == 3 || mode == 4) {
            if (mode == 0 || mode == 3) {
                teamPacket.g = (new ArrayList<>());
                teamPacket.g.add(targetName);
            } else {
                teamPacket.g.add(targetName);
            }
        }

        teamPacket.h = (mode);

        if (mode == 0 || mode == 2) {
            teamPacket.i = (friendly ? 3 : 0);
        }

        sendPacket(viewer, teamPacket);
    }

    public static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
