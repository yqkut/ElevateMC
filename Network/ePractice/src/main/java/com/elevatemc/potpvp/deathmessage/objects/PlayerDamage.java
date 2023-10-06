package com.elevatemc.potpvp.deathmessage.objects;

public abstract class PlayerDamage extends Damage {
    private final String damager;

    public PlayerDamage(String damaged, double damage, String damager) {
        super(damaged, damage);
        this.damager = damager;
    }

    public String getDamager() {
        return this.damager;
    }
}
