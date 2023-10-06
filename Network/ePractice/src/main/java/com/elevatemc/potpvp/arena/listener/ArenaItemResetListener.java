package com.elevatemc.potpvp.arena.listener;

import com.elevatemc.elib.cuboid.Cuboid;
import com.elevatemc.potpvp.arena.event.ArenaReleasedEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Removes projectiles & items from the chunk when it unloads in the arena world
 */
public final class ArenaItemResetListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        if (chunk.getWorld().getName().equals("arenas")) {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Item || entity instanceof Projectile) {
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onArenaReleased(ArenaReleasedEvent event) {
        Set<Chunk> coveredChunks = new HashSet<>();
        Cuboid bounds = event.getArena().getBounds();

        Location minPoint = bounds.getLowerNE();
        Location maxPoint = bounds.getUpperSW();
        World world = minPoint.getWorld();

        // definitely a better way to increment than += 1 but arenas
        // are small enough this doesn't matter
        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                // getChunkAt wants chunk x/z coords, not block coords
                int xChunk = x >> 4;
                int zChunk = z >> 4;
                if (world.isChunkLoaded(xChunk, zChunk)) {
                    coveredChunks.add(world.getChunkAt(xChunk, zChunk));
                }
            }
        }

        coveredChunks.stream().forEach(chunk -> {
            for (Entity entity : chunk.getEntities()) {
                // if we remove all entities we might call .remove()
                // on a player (breaks a lot of things)
                if ((entity instanceof Item || entity instanceof Projectile) && bounds.contains(entity.getLocation())) {
                    entity.remove();
                }
            }
        });
    }

}