package com.elevatemc.elib.fake;

import com.elevatemc.elib.fake.impl.player.FakePlayerEntityTask;
import com.elevatemc.elib.fake.impl.player.FakePlayerLookTask;
import com.elevatemc.elib.fake.impl.player.FakePlayerPacketHandler;
import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.spigot.eSpigot;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ImHacking
 */
public class FakeEntityHandler {
    private final Int2ObjectMap<FakeEntity> entityMap = new Int2ObjectOpenHashMap<>();

    @Getter(AccessLevel.PROTECTED)
    private final Executor entityExecutor = Executors.newFixedThreadPool(2);

    public FakeEntityHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new FakeEntityListener(this), plugin);
        TaskUtil.scheduleAtFixedRateOnPool(new FakePlayerEntityTask(this), 50, 50, TimeUnit.MILLISECONDS);
        TaskUtil.scheduleAtFixedRateOnPool(new FakePlayerLookTask(this), 50, 50, TimeUnit.MILLISECONDS);
        System.out.println("FakeEntityHandler Created: " + System.currentTimeMillis());
        eSpigot.
                getInstance()
                .addPacketHandler(
                        new FakePlayerPacketHandler(this));
    }

    public Collection<FakeEntity> getEntities() {
        return Collections.unmodifiableCollection(this.entityMap.values());
    }

    public void registerFakeEntity(FakeEntity entity) {
        this.entityMap.put(entity.getEntityId(), entity);
    }

    public void removeFakeEntity(int id) {
        this.entityMap.remove(id);
    }

    public FakeEntity getEntityById(int id) {
        for (FakeEntity entity : this.getEntities()) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    public FakeEntity getEntityByEntityId(int id) {
        return this.entityMap.getOrDefault(id, null);
    }

    public Set<FakeEntity> getFakeEntitiesByType(Class<? extends FakeEntity> type) {
        Set<FakeEntity> entities = new HashSet<>();

        this.getEntities().forEach(fakeEntity -> {
            if (fakeEntity.getClass().getSimpleName().equals(type.getSimpleName())) {
                entities.add(fakeEntity);
            }
        });

        return entities;
    }

    public Set<FakeEntity> getAllEntitiesPlayerCanSee(UUID uuid) {
        Set<FakeEntity> entities = new HashSet<>();

        this.getEntities().forEach(fakeEntity -> {
            if (fakeEntity.isShownToPlayer(uuid)) {
                entities.add(fakeEntity);
            }
        });

        return entities;
    }

    protected void handleMovement(Player player, Location from, Location to) {
        if (from.getBlockZ() == to.getBlockZ() && from.getBlockX() == to.getBlockX()) {
            return;
        }

        this.entityExecutor.execute(() -> {
            UUID uuid = player.getUniqueId();

            for (FakeEntity fakeEntity : this.getAllEntitiesPlayerCanSee(uuid)) {
                if (!fakeEntity.getWorld().getUID().equals(to.getWorld().getUID())) {
                    if (fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.hide(player);
                    }
                    continue;
                }

                if (fakeEntity.getLocation().distanceSquared(to) < fakeEntity.range()) {
                    if (!fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.show(player);
                    }
                } else {
                    if (fakeEntity.getCurrentlyViewing().contains(uuid)) {
                        fakeEntity.hide(player);
                    }
                }
            }
        });
    }
}
