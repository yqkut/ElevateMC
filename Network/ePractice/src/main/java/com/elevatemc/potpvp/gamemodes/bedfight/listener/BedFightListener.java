package com.elevatemc.potpvp.gamemodes.bedfight.listener;


import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.spigot.event.BlockDropItemsEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class BedFightListener implements Listener {
    private List<Location> blocksPlaced = new ArrayList<>();
    private List<Material> allowBreak = Arrays.asList(Material.WOOD, Material.ENDER_STONE, Material.WOOL);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        System.out.println("Block break");
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null || !match.getGameMode().equals(GameModes.BED_FIGHT)) {
            return;
        }

        System.out.println("Broke bedfight item");

        final Block block = event.getBlock();

        if (event.getBlock().getType().name().contains("BED")) {
            System.out.println("Broke bedfight bed");

            final Block belowBlock = block.getRelative(BlockFace.DOWN);

            if (!belowBlock.getType().equals(Material.STAINED_CLAY)) {
                System.out.println("Broke bedfight not clay thouhg");
                return;
            }

            System.out.println("Broke bedfight yeeee");

            byte data = belowBlock.getData();

            boolean blue = data == DyeColor.BLUE.getWoolData();

            for (Player allPlayer : match.findAllPlayers()) {
                allPlayer.sendMessage("");
                allPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lBED DETECTION > " + (blue ? "&9Blue Bed" : "&cRed Bed") + " &ewas destroyed by " + (blue ? "&9" : "&c") + event.getPlayer().getName() + "&e!"));
                allPlayer.sendMessage("");

                allPlayer.playSound(allPlayer.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
            }
            return;
        }

        if (allowBreak.stream().noneMatch(it -> block.getType().equals(it))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPickup(PlayerPickupItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null || !match.getGameMode().equals(GameModes.BED_FIGHT)) {
            return;
        }

        if (event.getItem().getItemStack().getType().equals(Material.BED)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null || !match.getGameMode().equals(GameModes.BED_FIGHT)) {
            return;
        }

        if (!event.getBlock().getType().equals(Material.WOOL)) {
            event.setCancelled(true);
            return;
        }

        blocksPlaced.add(event.getBlockPlaced().getLocation());
    }
}