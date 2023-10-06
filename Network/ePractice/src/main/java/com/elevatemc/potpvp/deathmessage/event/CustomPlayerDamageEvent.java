package com.elevatemc.potpvp.deathmessage.event;

import com.elevatemc.potpvp.deathmessage.objects.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import java.beans.ConstructorProperties;

public class CustomPlayerDamageEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final EntityDamageEvent cause;
    private Damage trackerDamage;

    @ConstructorProperties({"cause", "trackerDamage"})
    public CustomPlayerDamageEvent(EntityDamageEvent cause, Damage trackerDamage) {
        this.cause = cause;
        this.trackerDamage = trackerDamage;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public EntityDamageEvent getCause() {
        return this.cause;
    }

    public Damage getTrackerDamage() {
        return this.trackerDamage;
    }

    public void setTrackerDamage(Damage trackerDamage) {
        this.trackerDamage = trackerDamage;
    }

    public Player getPlayer() {
        return (Player) this.cause.getEntity();
    }

    public double getDamage() {
        return this.cause.getDamage();
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}
