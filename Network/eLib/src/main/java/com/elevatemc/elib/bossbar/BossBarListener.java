package com.elevatemc.elib.bossbar;

import com.elevatemc.elib.eLib;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BossBarListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        eLib.getInstance().getBossBarHandler().removeBossBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Player player = event.getPlayer();

        if (!eLib.getInstance().getBossBarHandler().getDisplaying().containsKey(player.getUniqueId())) {
            return;
        }

        final BossBarData data = eLib.getInstance().getBossBarHandler().getDisplaying().get(player.getUniqueId());

        final String message = data.getMessage();
        final float health = data.getHealth();

        eLib.getInstance().getBossBarHandler().removeBossBar(player);
        eLib.getInstance().getBossBarHandler().setBossBar(player,message,health);

    }
}
