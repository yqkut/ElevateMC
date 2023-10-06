package com.elevatemc.potpvp.deathmessage.objects;

import com.elevatemc.potpvp.nametag.PotPvPNametagProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Damage {
    private final String damaged;

    private final double damage;

    private final long time;

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public String getDamaged() {
        return this.damaged;
    }

    public double getDamage() {
        return this.damage;
    }

    public long getTime() {
        return this.time;
    }

    public String wrapName(String playerName, Player viewer) {
        Player player = Bukkit.getPlayer(playerName);
        ChatColor color = ChatColor.GRAY;
        if (player != null) {
            color = PotPvPNametagProvider.getNameColor(player, viewer);
        }
        return color.toString() + playerName + ChatColor.GRAY;
    }

    public long getTimeDifference() {
        return System.currentTimeMillis() - this.time;
    }

    public abstract String getDeathMessage(Player viewer);
}
