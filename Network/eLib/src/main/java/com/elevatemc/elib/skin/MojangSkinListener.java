package com.elevatemc.elib.skin;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class MojangSkinListener implements Listener {
    private MojangSkinHandler skinHandler;
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.skinHandler.removeTemporarySkinEntry(event.getPlayer().getUniqueId());
    }
}
