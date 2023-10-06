package com.elevatemc.potpvp.deathmessage.trackers;

import com.elevatemc.elib.util.EntityUtils;
import com.elevatemc.potpvp.deathmessage.event.CustomPlayerDamageEvent;
import com.elevatemc.potpvp.deathmessage.objects.MobDamage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityTracker implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();
            if (!(e.getDamager() instanceof org.bukkit.entity.Player) && !(e.getDamager() instanceof org.bukkit.entity.Arrow))
                event.setTrackerDamage(new EntityDamage(event.getPlayer().getName(), event.getDamage(), e.getDamager()));
        }
    }

    public static class EntityDamage extends MobDamage {
        public EntityDamage(String damaged, double damage, Entity entity) {
            super(damaged, damage, entity.getType());
        }

        public String getDeathMessage(Player viewer) {
            return wrapName(getDamaged(), viewer) + " was slain by a " + ChatColor.RED + EntityUtils.getName(getMobType()) + ChatColor.GRAY + ".";
        }
    }
}
