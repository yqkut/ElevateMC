package com.elevatemc.potpvp.deathmessage.listeners;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.deathmessage.DeathMessageHandler;
import com.elevatemc.potpvp.deathmessage.event.CustomPlayerDamageEvent;
import com.elevatemc.potpvp.deathmessage.util.UnknownDamage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DamageListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));
            Bukkit.getPluginManager().callEvent(customEvent);
            PotPvPSI.getInstance().getDeathMessageHandler().addDamage(player, customEvent.getTrackerDamage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PotPvPSI.getInstance().getDeathMessageHandler().clearDamage(event.getPlayer());
    }
}
