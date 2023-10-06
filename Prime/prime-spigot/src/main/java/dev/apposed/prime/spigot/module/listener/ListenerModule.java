package dev.apposed.prime.spigot.module.listener;

import dev.apposed.prime.spigot.module.Module;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ListenerModule extends Module implements Listener {

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }
}