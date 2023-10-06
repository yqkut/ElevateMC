package com.elevatemc.ehub.type.particle.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ParticleCallable {
    void call(Location location, Player player);
}
