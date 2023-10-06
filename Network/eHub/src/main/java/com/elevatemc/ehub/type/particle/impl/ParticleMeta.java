package com.elevatemc.ehub.type.particle.impl;

import java.beans.ConstructorProperties;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Effect;
import org.bukkit.Location;

@Getter
@Setter
public final class ParticleMeta {
    private final Location location;
    private final Effect effect;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private float offsetZ = 0.0f;
    private float speed = 1.0f;
    private int amount = 1;

    @ConstructorProperties(value={"location", "effect"})
    public ParticleMeta(Location location, Effect effect) {
        this.location = location;
        this.effect = effect;
    }
}
