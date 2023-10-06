package com.elevatemc.elib.util.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class BaseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public BaseEvent() {
        this(false);
    }

    public BaseEvent(boolean async) {
        super(async);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean call(JavaPlugin javaPlugin) {
        javaPlugin.getServer().getPluginManager().callEvent(this);
        return this instanceof Cancellable && ((Cancellable) this).isCancelled();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
