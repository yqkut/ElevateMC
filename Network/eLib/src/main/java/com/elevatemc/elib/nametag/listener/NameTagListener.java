package com.elevatemc.elib.nametag.listener;

import com.elevatemc.elib.eLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class NameTagListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("eLibNametag-LoggedIn", new FixedMetadataValue(eLib.getInstance(), true));

        eLib.getInstance().getNameTagHandler().initiatePlayer(event.getPlayer());
        eLib.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        eLib.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("eLibNametag-LoggedIn", eLib.getInstance());
        eLib.getInstance().getNameTagHandler().getTeamMap().remove(event.getPlayer().getName());
    }

}