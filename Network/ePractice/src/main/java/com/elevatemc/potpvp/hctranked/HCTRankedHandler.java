package com.elevatemc.potpvp.hctranked;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.hctranked.game.RankedGameHandler;
import com.elevatemc.potpvp.hctranked.listener.HCTPacketListener;
import com.elevatemc.potpvp.hctranked.packet.*;
import com.elevatemc.potpvp.hctranked.sync.SyncHandler;
import lombok.Getter;

public class HCTRankedHandler {

    @Getter
    private SyncHandler syncHandler;
    @Getter
    private RankedGameHandler gameHandler;

    public HCTRankedHandler() {
        syncHandler = new SyncHandler();
        gameHandler = new RankedGameHandler();
        eLib.getInstance().getPidginHandler().registerListener(new HCTPacketListener());
        eLib.getInstance().getPidginHandler().registerPacket(PingPacket.class);
        eLib.getInstance().getPidginHandler().registerPacket(PongPacket.class);
        eLib.getInstance().getPidginHandler().registerPacket(LoadGamePacket.class);
        eLib.getInstance().getPidginHandler().registerPacket(StartGamePacket.class);
        eLib.getInstance().getPidginHandler().registerPacket(VoidGamePacket.class);
        eLib.getInstance().getPidginHandler().registerPacket(WinGamePacket.class);
    }
}
