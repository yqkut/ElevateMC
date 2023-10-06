package com.elevatemc.potpvp.deathmessage.trackers;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.deathmessage.DeathMessageHandler;
import com.elevatemc.potpvp.deathmessage.event.CustomPlayerDamageEvent;
import com.elevatemc.potpvp.deathmessage.objects.Damage;
import com.elevatemc.potpvp.deathmessage.objects.PlayerDamage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VoidTracker implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.VOID)
            return;
        List<Damage> record = PotPvPSI.getInstance().getDeathMessageHandler().getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        if (record != null)
            for (Damage damage : record) {
                if (damage instanceof VoidDamage || damage instanceof VoidDamageByPlayer)
                    continue;
                if (damage instanceof PlayerDamage && (knocker == null || damage.getTime() > knockerTime)) {
                    knocker = damage;
                    knockerTime = damage.getTime();
                }
            }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new VoidDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage) knocker).getDamager()));
        } else {
            event.setTrackerDamage(new VoidDamage(event.getPlayer().getName(), event.getDamage()));
        }
    }

    public static class VoidDamage extends Damage {
        public VoidDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        public String getDeathMessage(Player viewer) {
            return wrapName(getDamaged(), viewer) + " fell into the void.";
        }
    }

    public static class VoidDamageByPlayer extends PlayerDamage {
        public VoidDamageByPlayer(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        public String getDeathMessage(Player viewer) {
            return wrapName(getDamaged(), viewer) + " fell into the void thanks to " + wrapName(getDamager(), viewer) + ".";
        }
    }
}
