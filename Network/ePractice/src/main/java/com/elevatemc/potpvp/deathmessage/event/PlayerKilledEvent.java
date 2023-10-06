package com.elevatemc.potpvp.deathmessage.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.beans.ConstructorProperties;

public class PlayerKilledEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Player killer;
    private final Player victim;

    @ConstructorProperties({"killer", "victim"})
    public PlayerKilledEvent(Player killer, Player victim) {
        this.killer = killer;
        this.victim = victim;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getKiller() {
        return this.killer;
    }

    public Player getVictim() {
        return this.victim;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}
