package com.elevatemc.potpvp.cosmetic;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.cosmetic.type.CosmeticType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Getter @RequiredArgsConstructor
public abstract class Cosmetic implements Listener {

    private final String name;

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, PotPvPSI.getInstance());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public abstract void onEnable(Player player);
    public abstract void onDisable(Player player);

    public CosmeticType getType() {
        return CosmeticType.MISC;
    }
}
