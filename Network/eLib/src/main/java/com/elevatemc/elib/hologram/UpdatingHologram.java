package com.elevatemc.elib.hologram;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdatingHologram extends Hologram {

    private final ScheduledExecutorService executorService;

    public UpdatingHologram(Location location, int delayMillis) {
        super(location);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(new UpdatingHologramTask(this), delayMillis, delayMillis, TimeUnit.MILLISECONDS);
    }

    public void stopTask() {
        this.executorService.shutdown();
    }

    @RequiredArgsConstructor
    public static class UpdatingHologramTask implements Runnable {

        private final UpdatingHologram hologram;

        @Override
        public void run() {
            for (UUID uuid : this.hologram.getSetup()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                this.hologram.update(player);
            }
        }
    }
}
