package com.elevatemc.potpvp.pvpclasses.pvpclasses.bard;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class BardEffect {

    @Getter private final PotionEffect potionEffect;
    @Getter private final int energy;
    @Getter private final String name;

    // For the message we send when you select the (de)buff in your hotbar.
    @Getter private final Map<String, Long> lastMessageSent = new HashMap<>();

    private BardEffect(PotionEffect potionEffect, int energy, String name) {
        this.potionEffect = potionEffect;
        this.energy = energy;
        this.name = name;
    }

    public static BardEffect fromPotion(PotionEffect potionEffect) {
        return (new BardEffect(potionEffect, -1, ""));
    }

    public static BardEffect fromPotionAndEnergy(PotionEffect potionEffect, int energy) {
        return (new BardEffect(potionEffect, energy, ""));
    }

    public static BardEffect fromPotionAndEnergyAndName(PotionEffect potionEffect, int energy, String name) {
        return (new BardEffect(potionEffect, energy, name));
    }

    public static BardEffect fromEnergy(int energy) {
        return (new BardEffect(null, energy, ""));
    }

    public static BardEffect fromEnergyAndName(int energy, String name) {
        return (new BardEffect(null, energy, name));
    }

}