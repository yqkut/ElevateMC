package com.elevatemc.potpvp.util;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.spigot.eSpigot;
import com.elevatemc.spigot.handler.PacketHandler;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClickTracker implements PacketHandler, Listener {

    private final PotPvPSI plugin;

    // This is concurrent to make sure that scoreboard won't throw a CME
    private static final Map<UUID, Integer> cpsCount = new ConcurrentHashMap<>();
    private static final Set<UUID> mining = new HashSet<>();

    private static final Set<UUID> cancelNextSwing = new HashSet<>();

    public ClickTracker(PotPvPSI plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        eSpigot.getInstance().addPacketHandler(this);
    }

    @Override
    public void handleReceivedPacket(PlayerConnection connection, Packet<?> packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            UUID uuid = connection.getPlayer().getUniqueId();
            if (mining.contains(uuid)) {
                return;
            }
            if (cancelNextSwing.contains(uuid)) {
                cancelNextSwing.remove(uuid);
                return;
            }

            cpsCount.put(uuid, cpsCount.get(uuid) + 1);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (cpsCount.containsKey(uuid)) {
                    cpsCount.put(uuid, Math.max(0, cpsCount.get(uuid) - 1));
                }
            }, 20L);
        }
        if (packet instanceof PacketPlayInBlockDig) {
            UUID uuid = connection.getPlayer().getUniqueId();
            PacketPlayInBlockDig digPacket = ((PacketPlayInBlockDig) packet);
            BlockPosition blockPosition = digPacket.a();
            PacketPlayInBlockDig.EnumPlayerDigType digType = digPacket.c();
            TaskUtil.runSync(() -> {
                switch (digType) {
                    case START_DESTROY_BLOCK:
                        // Checks for hardness -> the abort/stop won't be sent to the player when it's a block like tnt
                        float hardness = CraftMagicNumbers.getBlock(connection.getPlayer().getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())).g(null, null);
                        if (hardness > 0.0F) {
                            mining.add(uuid);
                        } else {
                            cancelNextSwing.add(uuid);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                cancelNextSwing.remove(uuid);
                            }, 1l);
                        }
                        break;
                    case ABORT_DESTROY_BLOCK:
                        mining.remove(uuid);
                        break;
                    case STOP_DESTROY_BLOCK:
                        // Some delay remove to prevent swing packets from the time between the end of the mining and the start of mining for the same block
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            mining.remove(uuid);
                        }, 6l); // 6 ticks some magic value which probs isn't that good for laggy connects
                        break;
                }
            });
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cpsCount.putIfAbsent(event.getPlayer().getUniqueId(), 0);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cpsCount.remove(event.getPlayer().getUniqueId());
    }

    public static int getCPS(Player player) {
        return cpsCount.get(player.getUniqueId());
    }
}

