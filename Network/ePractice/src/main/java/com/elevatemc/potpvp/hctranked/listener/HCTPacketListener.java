package com.elevatemc.potpvp.hctranked.listener;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.pidgin.packet.handler.IncomingPacketHandler;
import com.elevatemc.elib.pidgin.packet.listener.PacketListener;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.hctranked.game.RankedGameTeam;
import com.elevatemc.potpvp.hctranked.packet.LoadGamePacket;
import com.elevatemc.potpvp.hctranked.packet.PingPacket;
import com.elevatemc.potpvp.hctranked.packet.PongPacket;
import com.elevatemc.potpvp.hctranked.packet.VoidGamePacket;
import org.bukkit.Bukkit;

public class HCTPacketListener implements PacketListener {
    @IncomingPacketHandler
    public void onPingPacket(PingPacket packet) {
        eLib.getInstance().getPidginHandler().sendPacket(new PongPacket());
    }

    @IncomingPacketHandler
    public void onLoadGame(LoadGamePacket packet) {
        Bukkit.broadcastMessage("LOAD GAME");
        RankedGameTeam team1 = new RankedGameTeam(packet.getTeam1(), packet.getTeam1Captain());
        RankedGameTeam team2 = new RankedGameTeam(packet.getTeam2(), packet.getTeam2Captain());
        Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), () -> {
            Bukkit.broadcastMessage("PROCESS");
            PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().createGame(packet.getId(), packet.getPlayers(), team1, team2, packet.getMap());
        });
    }

    @IncomingPacketHandler
    public void onVoidGame(VoidGamePacket packet) {
        Bukkit.broadcastMessage("VOID GAME PACKET!");
        Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), () -> {
            Bukkit.broadcastMessage("PROCESS VOIDD");
            PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().voidGame(packet.getId());
        });
    }
}
