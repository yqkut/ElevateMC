package com.elevatemc.elib.npc;


import com.elevatemc.elib.eLib;
import com.elevatemc.elib.fake.FakeEntity;

public class NPCUpdateArrowTask implements Runnable {
    @Override
    public void run() {
        for (FakeEntity entity : eLib.getInstance().getFakeEntityHandler().getEntities()) {
            if (!(entity instanceof NPC)) {
                continue;
            }
            NPC npc = (NPC) entity;

            if (!npc.isSitting()) {
                continue;
            }

            npc.setEntityArrow(null);
            npc.setAttachPacket(null);
            npc.updateRideForViewers();
        }
    }
}
