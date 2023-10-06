package com.elevatemc.ehub.listener;

import com.elevatemc.ehub.eHub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MusicListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasMetadata("MUSIC_DISABLED")) {
            eHub.getInstance().getRadioSongPlayer().addPlayer(player);
        }
    }
}
