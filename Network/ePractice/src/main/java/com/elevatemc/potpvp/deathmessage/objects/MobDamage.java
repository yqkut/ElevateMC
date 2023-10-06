package com.elevatemc.potpvp.deathmessage.objects;

import com.elevatemc.potpvp.deathmessage.objects.Damage;
import org.bukkit.entity.EntityType;

public abstract class MobDamage extends Damage {
    private final EntityType mobType;

    public MobDamage(String damaged, double damage, EntityType mobType) {
        super(damaged, damage);
        this.mobType = mobType;
    }

    public EntityType getMobType() {
        return this.mobType;
    }
}
