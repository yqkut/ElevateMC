package dev.apposed.prime.spigot.module.profile.punishment.task;

import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class PunishmentCheckerTask extends BukkitRunnable {

    private final ProfileHandler profileHandler;

    public PunishmentCheckerTask(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;

    }

    @Override
    public void run() {
        this.profileHandler.getProfiles().forEach(Profile::checkPunishments);
    }
}
