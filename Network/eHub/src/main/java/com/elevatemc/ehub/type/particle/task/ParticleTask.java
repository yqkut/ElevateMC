package com.elevatemc.ehub.type.particle.task;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.particle.ParticleType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTask extends BukkitRunnable {
    private final eHub plugin = eHub.getInstance();

    public ParticleTask() {
        runTaskTimerAsynchronously(eHub.getInstance(), 40L, 1L);
    }

    @Override
    public void run() {
        for (ParticleType particle : ParticleType.values()) {
            if (particle.getTicks() >= particle.getFrequency()) {
                particle.setTicks(-1);
            }

            particle.setTicks(particle.getTicks() + 1);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = plugin.getProfileManager().getByUuid(player.getUniqueId());
            ParticleType type = profile.getParticleType();

            if (type == null) {
                continue;
            }

            type.getCallable().call(player.getLocation(), player);
        }
    }
}
