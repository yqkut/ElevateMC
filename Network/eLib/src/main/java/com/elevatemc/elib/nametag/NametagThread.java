package com.elevatemc.elib.nametag;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.nametag.construct.NameTagUpdate;
import lombok.Getter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class NameTagThread extends Thread {

    @Getter private static final Map<NameTagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    NameTagThread() {
        super("eLib - NameTag Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {

            final Iterator<NameTagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while (pendingUpdatesIterator.hasNext()) {

                final NameTagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    eLib.getInstance().getNameTagHandler().applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(eLib.getInstance().getNameTagHandler().getUpdateInterval() * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}