package com.elevatemc.elib.hologram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class HologramTask implements Runnable {

    private final HologramHandler hologramHandler;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            World world = player.getWorld();

            for (Hologram hologram : this.hologramHandler.getHolograms()) {
                Location location = hologram.getLocation();

                if (world.getUID() != location.getWorld().getUID()) {
                    hologram.hide(player);
                    continue;
                }

                Location playerLocation = player.getLocation();
                if (playerLocation.distanceSquared(location) <= 1600D) {
                    if (hologram.isSetup(player.getUniqueId())) {
                        continue;
                    }
                    hologram.setup(player);
                } else {
                    hologram.hide(player);
                }
            }
        }
    }
}
