package com.elevatemc.ehub.type.particle;


import com.elevatemc.ehub.type.particle.impl.ParticleCallable;
import com.elevatemc.ehub.type.particle.impl.ParticleMeta;
import com.elevatemc.ehub.utils.CC;
import com.elevatemc.ehub.utils.ParticleUtil;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

import static com.elevatemc.elib.util.ParticleMath.cos;
import static com.elevatemc.elib.util.ParticleMath.sin;

@Getter
public enum ParticleType {

    VIP("VIP Particle", CC.GREEN, 32, Color.LIME, (location, player) -> {
        double angle = (double) ParticleType.VIP.ticks * 0.19634954084936207;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        Location topRingLocation = location.clone().add(0.1, 0, 0.1).add(0.8 * cos, 1.4, 0.8 * sin);

        for (int i = 0; i < 5; ++i) {
            ParticleUtil.sendsParticleToAll(new ParticleMeta(topRingLocation, Effect.HAPPY_VILLAGER));
        }
    }),
    MVP("MVP Particle", CC.BLUE, 32, Color.BLUE, (location, player) -> {
        Location location2 = location.clone().add(0.1, 0.0, 0.1);

        double angle = (double) ParticleType.MVP.ticks * 0.19634954084936207;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        Location bottomRingLocation = location2.clone().add(0.8 * cos, 0.6, 0.8 * sin);
        Location topRingLocation = location2.clone().add(0.8 * cos, 1.4, 0.8 * sin);

        for (int i = 0; i < 5; ++i) {
            ParticleUtil.sendsParticleToAll(new ParticleMeta(bottomRingLocation, Effect.LAVADRIP));
            ParticleUtil.sendsParticleToAll(new ParticleMeta(topRingLocation, Effect.LAVADRIP));
        }
    }),

    PRO("Pro Particle", CC.GOLD, 40, Color.ORANGE, (location, player) -> {
        Location location2 = location.clone().add(0.1, 0.0, 0.1);

        double angle = (double) ParticleType.PRO.ticks * 0.15707963267948966;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        ArrayList<ParticleMeta> particleMetaList = new ArrayList<>();
        particleMetaList.add(new ParticleMeta(location2.clone().add(1.0 * cos, 0.5 + 1.0 * cos, 1.0 * sin), Effect.WATERDRIP));
        particleMetaList.add(new ParticleMeta(location2.clone().add(1.0 * cos, 1.0 + 1.0 * cos, 1.0 * sin), Effect.WATERDRIP));
        particleMetaList.add(new ParticleMeta(location2.clone().add(1.0 * cos, 1.5 + 1.0 * cos, 1.0 * sin), Effect.WATERDRIP));

        ParticleUtil.sendsParticleToAll(particleMetaList.toArray(new ParticleMeta[0]));
    }),
    ELEVATE("Elevate Particle", CC.DARK_AQUA, 32, Color.TEAL, (location, player) -> {

        double angle = (double) ParticleType.ELEVATE.ticks * 0.19634954084;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 8) {
            for (double i = 0; i <= 1; i += 1) {

                Location cone = location.clone().add(0.4 * (2 * Math.PI - t) * 0.5 * cos(t + angle + i * Math.PI), 0.5 * t, 0.4 * (2 * Math.PI - t) * 0.5 * sin(t + angle + i * Math.PI));

                ParticleUtil.sendsParticleToAll(new ParticleMeta(cone, Effect.COLOURED_DUST));
            }
        }
    });
        private final String name, displayColor;
        private final int frequency;
        private final Color color;
        private final ParticleCallable callable;

        @Setter
        private int ticks;

        public static ParticleType getByName(String input) {
            return Arrays.stream(values()).filter((type) -> type.name().equalsIgnoreCase(input) || type.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
        }

        public boolean hasPermission(Player player) {
            return player.hasPermission(getPermissionForAll()) || player.hasPermission(getPermission());
        }

        public String getPermissionForAll() {
            return "core.cosmetics.particle.*";
        }

        public String getPermission() {
            return "core.cosmetics.particle." + name().toLowerCase();
        }

        ParticleType(String name, String displayColor, int frequency, Color color, ParticleCallable callable) {
            this.name = name;
            this.callable = callable;
            this.displayColor = displayColor;
            this.frequency = frequency;
            this.color = color;
            this.ticks = 0;
        }
    }
