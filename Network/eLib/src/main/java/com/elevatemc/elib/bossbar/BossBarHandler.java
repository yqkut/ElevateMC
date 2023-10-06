package com.elevatemc.elib.bossbar;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.EntityUtils;
import com.elevatemc.elib.util.PlayerUtils;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gnu.trove.map.hash.TObjectIntHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BossBarHandler {

    @Getter private final Map<UUID,BossBarData> displaying = new HashMap<>();
    @Getter private final Map<UUID,Integer> lastUpdatedPosition = new HashMap<>();


    private Object2IntMap classToIdMap = null;

    public BossBarHandler() {

        eLib.getInstance().getServer().getPluginManager().registerEvents(new BossBarListener(), eLib.getInstance());

        try {
            final Field dataWatcherClassToIdField = DataWatcher.class.getDeclaredField("classToId");

            dataWatcherClassToIdField.setAccessible(true);

            this.classToIdMap = (Object2IntMap)dataWatcherClassToIdField.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        eLib.getInstance().getServer().getScheduler().runTaskTimer(eLib.getInstance(),() -> {

            for (UUID uuid : this.displaying.keySet()) {

                final Player player = eLib.getInstance().getServer().getPlayer(uuid);

                if (player == null) {
                    continue;
                }

                final int updateTicks = PlayerUtils.getProtocol(player) >= 47 ? 60 : 3;

                if (this.lastUpdatedPosition.containsKey(player.getUniqueId()) && MinecraftServer.currentTick - 
                        lastUpdatedPosition.get(player.getUniqueId()) < updateTicks) {
                    return;
                }

                this.updatePosition(player);

                this.lastUpdatedPosition.put(player.getUniqueId(),MinecraftServer.currentTick);
            }

        },1,1);

    }


    public void setBossBar(Player player, String message, float health) {

        try {

            if (message == null) {
                removeBossBar(player);
                return;
            }

            Preconditions.checkArgument(health >= 0.0F && health <= 1.0F, "Health must be between 0 and 1");

            if (message.length() > 64) {
                message = message.substring(0, 64);
            }

            message = ChatColor.translateAlternateColorCodes('&', message);

            if (!this.displaying.containsKey(player.getUniqueId())) {
                this.sendSpawnPacket(player, message, health);
            } else {
                this.sendUpdatePacket(player, message, health);
            }

            final BossBarData bossBarData = this.displaying.get(player.getUniqueId());

            bossBarData.setMessage(message);
            bossBarData.setHealth(health);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void removeBossBar(Player player) {

        if (!this.displaying.containsKey(player.getUniqueId())) {
            return;
        }

        final int entityId = this.displaying.get(player.getUniqueId()).getEntityId();

        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));

        this.displaying.remove(player.getUniqueId());
        this.lastUpdatedPosition.remove(player.getUniqueId());
    }

    private void sendSpawnPacket(Player bukkitPlayer, String message, float health) throws Exception {

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final int version = PlayerUtils.getProtocol(bukkitPlayer);

        this.displaying.put(bukkitPlayer.getUniqueId(), new BossBarData(EntityUtils.getFakeEntityId(), message, health));

        final BossBarData stored = this.displaying.get(bukkitPlayer.getUniqueId());

        final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        packet.id = (stored.getEntityId()); //this.spawnPacketAField.set(packet,stored.getEntityId());

        final DataWatcher watcher = new DataWatcher((Entity)null);

        if (version < 47) {
            packet.type = ((byte)EntityType.ENDER_DRAGON.getTypeId());

            watcher.a(6, health * 200.0F);
            packet.x = ((int)(player.locX * 32.0D));
            packet.y = (-6400);
            packet.z = ((int)(player.locZ * 32.0D));

        } else {
            packet.type = ((byte)EntityType.WITHER.getTypeId());

            watcher.a(6, health * 300.0F);
            watcher.a(20, 880);

            final double pitch = Math.toRadians(player.pitch);
            final double yaw = Math.toRadians(player.yaw);
            packet.x = ((int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D));
            packet.y = ((int)((player.locY - Math.sin(pitch) * 32.0D) * 32.0D));
            packet.z = ( (int)((player.locZ + Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D));
        }

        watcher.a(version < 47 ? 10 : 2, message);
        packet.l = (watcher);

        player.playerConnection.sendPacket(packet);
    }

    private void sendUpdatePacket(Player bukkitPlayer, String message, float health) {

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final int version = PlayerUtils.getProtocol(bukkitPlayer);
        final BossBarData stored = this.displaying.get(bukkitPlayer.getUniqueId());
        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        packet.a = (stored.getEntityId());

        final List<DataWatcher.WatchableObject> objects = new ArrayList<>();

        if (health != stored.getHealth()) {

            if (version != 47) {
                objects.add(createWatchableObject(6, health * 200.0F));
            } else {
                objects.add(createWatchableObject(6, health * 300.0F));
            }

        }

        if (!message.equals(stored.getMessage())) {
            objects.add(createWatchableObject(version != 47 ? 10 : 2, message));
        }
        packet.b = objects;
        player.playerConnection.sendPacket(packet);
    }

    private DataWatcher.WatchableObject createWatchableObject(int id, Object object) {
        return new DataWatcher.WatchableObject(this.classToIdMap.get(object.getClass()), id, object);
    }

    private void updatePosition(Player bukkitPlayer) {

        if (!this.displaying.containsKey(bukkitPlayer.getUniqueId())) {
            return;
        }

        final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        final int version = PlayerUtils.getProtocol(bukkitPlayer);

        int x;
        int y;
        int z;

        if (version != 47) {
            x = (int)(player.locX * 32.0D);
            y = -6400;
            z = (int)(player.locZ * 32.0D);
        } else {
            final double pitch = Math.toRadians((double)player.pitch);
            final double yaw = Math.toRadians((double)player.yaw);
            x = (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0D) * 32.0D);
            y = (int)((player.locY - Math.sin(pitch) * 32.0D) * 32.0D);
            z = (int)((player.locZ + Math.cos(yaw) * Math.cos(pitch) * 32.0D) * 32.0D);
        }

        player.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(
                this.displaying.get(bukkitPlayer.getUniqueId()).getEntityId(),x,y,z,(byte)0,(byte)0, true));
    }
}
