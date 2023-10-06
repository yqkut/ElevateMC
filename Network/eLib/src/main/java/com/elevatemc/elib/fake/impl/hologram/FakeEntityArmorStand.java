package com.elevatemc.elib.fake.impl.hologram;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.elevatemc.elib.fake.FakeEntity;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FakeEntityArmorStand implements FakeEntity {

    private final int id;
    private final EntityArmorStand entityArmorStand;

    private Location location;

    private PacketPlayOutSpawnEntityLiving spawnEntity;
    private PacketPlayOutEntityDestroy destroyEntity;
    private PacketPlayOutEntityMetadata metadata;

    private final Set<UUID> shouldBeAbleToView;
    private final Set<UUID> currentlyViewing;

    public FakeEntityArmorStand(int id, Location location) {
        this.id = id;
        this.entityArmorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
        this.entityArmorStand.setPosition(location.getX(), location.getY(), location.getZ());
        this.location = location;

        this.spawnEntity = new PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
        this.destroyEntity = new PacketPlayOutEntityDestroy(this.getEntityId());

        this.shouldBeAbleToView = new HashSet<>();
        this.currentlyViewing = new HashSet<>();

        this.setupPackets();
    }

    public void setInvisible(boolean b) {
        this.entityArmorStand.setInvisible(b);
        this.setupPackets();
    }

    public void setGravity(boolean b) {
        this.entityArmorStand.setGravity(b);
        this.setupPackets();
    }

    public void setSmall(boolean b) {
        this.entityArmorStand.setSmall(b);
        this.setupPackets();
    }

    public void n(boolean b) {
        this.entityArmorStand.n(b);
        this.setupPackets();
    }

    public void setName(String name) {
        this.entityArmorStand.setCustomName(name);
        this.entityArmorStand.setCustomNameVisible(true);
        this.updateMetadata();
    }

    public void update(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.metadata);
    }

    public PacketPlayOutEntityMetadata updateMetadata() {
        return this.metadata = new PacketPlayOutEntityMetadata(this.getEntityId(), this.entityArmorStand.getDataWatcher(), true);
    }

    public void setupPackets() {
        this.spawnEntity = new PacketPlayOutSpawnEntityLiving(this.entityArmorStand);
        this.destroyEntity = new PacketPlayOutEntityDestroy(this.getEntityId());
        this.updateMetadata();
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
        this.entityArmorStand.setPosition(location.getX(), location.getY(), location.getZ());
        this.entityArmorStand.world = ((CraftWorld) location.getWorld()).getHandle();
        this.location = location;

        this.setupPackets();

        PacketPlayOutEntityTeleport packetPlayOutEntityTeleport = new PacketPlayOutEntityTeleport(this.entityArmorStand);
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
        return this.entityArmorStand.getName();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getEntityId() {
        return this.entityArmorStand.getId();
    }

    @Override
    public UUID getUUID() {
        return this.entityArmorStand.getUniqueID();
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
    public org.bukkit.World getWorld() {
        return this.location.getWorld();
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
}
