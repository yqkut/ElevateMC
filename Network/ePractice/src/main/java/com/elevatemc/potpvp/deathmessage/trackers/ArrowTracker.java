package com.elevatemc.potpvp.deathmessage.trackers;

import com.elevatemc.elib.util.EntityUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.deathmessage.event.CustomPlayerDamageEvent;
import com.elevatemc.potpvp.deathmessage.objects.Damage;
import com.elevatemc.potpvp.deathmessage.objects.MobDamage;
import com.elevatemc.potpvp.deathmessage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ArrowTracker implements Listener {
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player)
            event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(PotPvPSI.getInstance(), event.getProjectile().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();
            if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();
                    for (MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
                        Location shotFrom = (Location) value.value();
                        double distance = shotFrom.distance(event.getPlayer().getLocation());
                        event.setTrackerDamage(new ArrowDamageByPlayer(event.getPlayer().getName(), event.getDamage(), shooter.getName(), shotFrom, distance));
                    }
                } else if (arrow.getShooter() instanceof Entity) {
                    event.setTrackerDamage(new ArrowDamageByMob(event.getPlayer().getName(), event.getDamage(), (Entity) arrow.getShooter()));
                } else {
                    event.setTrackerDamage(new ArrowDamage(event.getPlayer().getName(), event.getDamage()));
                }
            }
        }
    }

    public static class ArrowDamage extends Damage {
        public ArrowDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        public String getDeathMessage(Player viewer) {
            return wrapName(getDamaged(), viewer) + " was shot.";
        }
    }

    public static class ArrowDamageByPlayer extends PlayerDamage {
        private final Location shotFrom;

        private final double distance;

        public ArrowDamageByPlayer(String damaged, double damage, String damager, Location shotFrom, double distance) {
            super(damaged, damage, damager);
            this.shotFrom = shotFrom;
            this.distance = distance;
        }

        public Location getShotFrom() {
            return this.shotFrom;
        }

        public double getDistance() {
            return this.distance;
        }

        public String getDeathMessage(Player viewer) {
            return wrapName(getDamaged(), viewer) + " was shot by " + wrapName(getDamager(), viewer) + " from " + ChatColor.BLUE + (int) this.distance + " blocks" + ChatColor.GRAY + ".";
        }
    }

    public static class ArrowDamageByMob extends MobDamage {
        public ArrowDamageByMob(String damaged, double damage, Entity damager) {
            super(damaged, damage, damager.getType());
        }

        public String getDeathMessage(Player viewer) {
            return wrapName(getDamaged(), viewer) + " was shot by a " + ChatColor.RED + EntityUtils.getName(getMobType()) + ChatColor.GRAY + ".";
        }
    }
}
