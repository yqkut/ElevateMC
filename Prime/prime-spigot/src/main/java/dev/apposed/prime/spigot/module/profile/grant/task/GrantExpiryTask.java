package dev.apposed.prime.spigot.module.profile.grant.task;

import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class GrantExpiryTask extends BukkitRunnable {

    private final ProfileHandler profileHandler;

    public GrantExpiryTask(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Override
    public void run() {
        this.profileHandler.getProfiles().forEach(profile -> {
            boolean updated = profile.checkGrants();
            if (updated) this.profileHandler.setupPlayer(profile);
        });
    }
}
