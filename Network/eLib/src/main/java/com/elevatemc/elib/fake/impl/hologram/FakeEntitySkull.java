package com.elevatemc.elib.fake.impl.hologram;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.elevatemc.elib.fake.FakeEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class FakeEntitySkull implements FakeEntity {

    private final int id;
    private final EntityWitherSkull entityWitherSkull;

    private final Set<UUID> shouldBeAbleToView;
    private final Set<UUID> currentlyViewing;

    private PacketPlayOutSpawnEntity spawnEntity;
    private PacketPlayOutEntityDestroy destroyEntity;
    private PacketPlayOutEntityMetadata metadata;

    private Location location;

    public FakeEntitySkull(int id, Location location) {
        this.id = id;
        this.entityWitherSkull = new EntityWitherSkull(((CraftWorld) location.getWorld()).getHandle());
        this.entityWitherSkull.setPosition(location.getX(), location.getY(), location.getZ());
        this.location = location;

        this.shouldBeAbleToView = new HashSet<>();
        this.currentlyViewing = new HashSet<>();

        this.setupPackets();
    }

    public void updateMetadata() {
        this.metadata = new PacketPlayOutEntityMetadata(this.getEntityId(), this.entityWitherSkull.getDataWatcher(), true);
    }

    public void update(Player player) {
        if (!this.shouldBeAbleToView.contains(player.getUniqueId())) {
            return;
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.metadata);
    }

    @Override
    public boolean show(Player player) {
        if (!player.getWorld().getUID().equals(this.location.getWorld().getUID())) {
            return false;
        }

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(this.destroyEntity);
        playerConnection.sendPacket(this.spawnEntity);
        playerConnection.sendPacket(this.metadata);

        this.currentlyViewing.add(player.getUniqueId());
        this.shouldBeAbleToView.add(player.getUniqueId());
        return true;
    }

    @Override
    public boolean showToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.show(player);
        }
        return false;
    }

    @Override
    public boolean hide(Player player) {
        if (!this.currentlyViewing.contains(player.getUniqueId())) {
            return false;
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.destroyEntity);
        this.currentlyViewing.remove(player.getUniqueId());
        return true;
    }

    @Override
    public boolean hideFromAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.hide(player);
        }
        return true;
    }

    @Override
    public void teleport(Location location) {
        this.entityWitherSkull.setPosition(location.getX(), location.getY(), location.getZ());
        this.entityWitherSkull.world = ((CraftWorld) location.getWorld()).getHandle();
        this.location = location;

        this.setupPackets();

        PacketPlayOutEntityTeleport packetPlayOutEntityTeleport = new PacketPlayOutEntityTeleport(this.entityWitherSkull);
        for (UUID uuid : this.getCurrentlyViewing()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            if (!player.getWorld().getName().equals(this.location.getWorld().getName())) {
                this.hide(player);
                continue;
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityTeleport);
        }
    }

    @Override
    public boolean isShownToPlayer(UUID uuid) {
        return this.shouldBeAbleToView.contains(uuid);
    }

    @Override
    public String getName() {
        return this.entityWitherSkull.getName();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getEntityId() {
        return this.entityWitherSkull.getId();
    }

    @Override
    public UUID getUUID() {
        return this.entityWitherSkull.getUniqueID();
    }

    @Override
    public void addToTeam(Player player) {
        //ignore
    }

    @Override
    public void handleDisconnect(UUID uuid) {
        this.currentlyViewing.remove(uuid);
        this.shouldBeAbleToView.remove(uuid);
    }

    @Override
    public Set<UUID> getCurrentlyViewing() {
        return this.currentlyViewing;
    }

    @Override
    public Set<UUID> getShouldBeAbleToView() {
        return this.shouldBeAbleToView;
    }

    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    public void setupPackets() {
        this.spawnEntity = new PacketPlayOutSpawnEntity(this.entityWitherSkull, 66);
        this.destroyEntity = new PacketPlayOutEntityDestroy(this.getEntityId());
        this.updateMetadata();
    }
}
