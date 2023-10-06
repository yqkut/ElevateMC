package com.elevatemc.elib.uuid.listener;

import com.elevatemc.elib.eLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public final class UUIDListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        if (eLib.getInstance().getUuidCache().cached(event.getUniqueId())) {
            eLib.getInstance().getUuidCache().update(event.getUniqueId(),event.getName());
        } else {
            eLib.getInstance().getUuidCache().updateAll(event.getUniqueId(),event.getName());
        }

    }



}