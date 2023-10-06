package com.elevatemc.potpvp.gamemodes.pearlfight.listener;


import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.spigot.event.BlockDropItemsEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class MatchPearlfightListener implements Listener {
    private final List<BlockRestore> restores = new ArrayList<>();

    public MatchPearlfightListener() {
        new BlockRestoreTask().runTaskTimer(PotPvPSI.getInstance(), 0L, 20L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match != null && match.getGameMode().equals(GameModes.PEARL_FIGHT) && event.getBlock().getType().equals(Material.WOOL)) {
            Block block = event.getBlock();
            restores.add(new BlockRestore(block.getLocation(), event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match != null && match.getGameMode().equals(GameModes.PEARL_FIGHT) && event.getBlock().getType().equals(Material.WOOL)) {
            restores.removeIf(restore -> restore.getBlockLocation().equals(event.getBlock().getLocation()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockDrop(BlockDropItemsEvent event) {
        Player recipient = event.getPlayer();
        if (recipient == null) return;

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match != null && match.getGameMode().equals(GameModes.PEARL_FIGHT) && event.getBlock().getType().equals(Material.WOOL)) {
            restores.removeIf(restore -> restore.getBlockLocation().equals(event.getBlock().getLocation()));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getGameMode().equals(GameModes.PEARL_FIGHT)) {
            event.setFoodLevel(20);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getGameMode().equals(GameModes.PEARL_FIGHT)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            } else {
                event.setDamage(0);
            }
        }
    }

    private class BlockRestoreTask extends BukkitRunnable {
        @Override
        public void run() {
            Iterator<BlockRestore> itr = restores.iterator();
            while(itr.hasNext()) {
                BlockRestore restore = itr.next();
                if(System.currentTimeMillis() >= restore.getExpireAt()) {
                    final Block block = restore.getBlockLocation().getWorld().getBlockAt(restore.getBlockLocation());
                    if (block.getType().equals(Material.WOOL)) {
                        Player player = Bukkit.getPlayer(restore.getPlayer());
                        if (player != null) {
                            Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);

                            if (match != null && match.getGameMode().equals(GameModes.PEARL_FIGHT)) {
                                ItemStack wool = new ItemStack(Material.WOOL);
                                if (match.getTeams().size() == 2) {
                                    if (match.getTeams().get(0) == match.getTeam(player.getUniqueId())) {
                                        wool.setDurability(DyeColor.BLUE.getWoolData());
                                    } else {
                                        wool.setDurability(DyeColor.RED.getWoolData());
                                    }
                                }
                                player.getInventory().addItem(wool);
                            }
                        }
                    }
                    block.setType(Material.AIR);
                    itr.remove();
                }
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class BlockRestore {
        private final Location blockLocation;
        private final UUID player;
        private final long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
    }
}