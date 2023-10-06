package com.elevatemc.potpvp.deathmessage.util;

import com.elevatemc.potpvp.deathmessage.objects.Damage;
import org.bukkit.entity.Player;

public class UnknownDamage extends Damage {
    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    public String getDeathMessage(Player viewer) {
        return wrapName(getDamaged(), viewer) + " died.";
    }
}
