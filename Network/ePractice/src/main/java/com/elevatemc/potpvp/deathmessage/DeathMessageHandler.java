package com.elevatemc.potpvp.deathmessage;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.deathmessage.listeners.DamageListener;
import com.elevatemc.potpvp.deathmessage.objects.Damage;
import com.elevatemc.potpvp.deathmessage.trackers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeathMessageHandler {
    private final Map<String, List<Damage>> damage = new HashMap<>();

    public DeathMessageHandler () {
        Bukkit.getPluginManager().registerEvents(new DamageListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new GeneralTracker(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new PVPTracker(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new EntityTracker(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new FallTracker(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new ArrowTracker(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new VoidTracker(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new BurnTracker(), PotPvPSI.getInstance());
    }

    public List<Damage> getDamage(Player player) {
        return damage.get(player.getName());
    }

    public void addDamage(Player player, Damage addedDamage) {
        if (!damage.containsKey(player.getName()))
            damage.put(player.getName(), new ArrayList<>());
        List<Damage> damageList = damage.get(player.getName());
        while (damageList.size() > 30)
            damageList.remove(0);
        damageList.add(addedDamage);
    }

    public void clearDamage(Player player) {
        damage.remove(player.getName());
    }
}
