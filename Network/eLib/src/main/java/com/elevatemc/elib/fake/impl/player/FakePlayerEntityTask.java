package com.elevatemc.elib.fake.impl.player;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.elevatemc.elib.fake.FakeEntity;
import com.elevatemc.elib.fake.FakeEntityHandler;

import java.util.UUID;

@AllArgsConstructor
public class FakePlayerEntityTask implements Runnable {
    private FakeEntityHandler fakeEntityHandler;
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Location playerLocation = player.getLocation();
            UUID worldId = player.getWorld().getUID();

            for (FakeEntity fakeEntity : this.fakeEntityHandler.getAllEntitiesPlayerCanSee(uuid)) {
                if (!fakeEntity.getWorld().getUID().equals(worldId)) {
                    if (fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.hide(player);
                    }
                    continue;
                }

                if (fakeEntity.getLocation().distanceSquared(playerLocation) < fakeEntity.range()) {
                    if (!fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.show(player);
                    }
                } else {
                    if (fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.hide(player);
                    }
                }
            }
        }
    }
}
