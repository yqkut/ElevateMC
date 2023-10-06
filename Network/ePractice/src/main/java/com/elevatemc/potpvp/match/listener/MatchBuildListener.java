package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.type.AntiBlockup;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.elib.cuboid.Cuboid;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.PotionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.Potion;

import static org.bukkit.Material.POTION;

public final class MatchBuildListener implements Listener {

    private static final int SEARCH_RADIUS = 3;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            // BasicPreventionListener handles this
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (!match.getGameMode().getBuildingAllowed() || match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        }

        if (match.getGameMode().getBuildingAllowed() && match.getGameMode().equals(GameModes.TRAPPING) && !player.hasMetadata("TRAPPER")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            // BasicPreventionListener handles this
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (match.getGameMode().equals(GameModes.TRAPPING) && player.hasMetadata("TRAPPER")) {
            return;
        }

        if (!match.getGameMode().getBuildingAllowed()) {
            event.setCancelled(true);
            return;
        }

        if (match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        if (!canBePlaced(event.getBlock(), match)) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't build here.");
            event.setCancelled(true);
            player.teleport(player.getLocation()); // teleport them back so they can't block-glitch
            return;
        }

        // apparently this is a problem
        if (event.getPlayer().getItemInHand().getType() == Material.FLINT_AND_STEEL && event.getBlockAgainst().getType() == Material.GLASS) {
            event.setCancelled(true);
            return;
        }

        match.recordPlacedBlock(event.getBlock());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();

        if (!AntiBlockup.NO_INTERACT.contains(clickedBlock.getType()) && !clickedBlock.getType().name().contains("SIGN")) {
            return;
        }

        final Player player = event.getPlayer();

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (matchHandler.isSpectatingMatch(player)) {
            event.setCancelled(true);
            return;
        }

        if (!matchHandler.isPlayingMatch(player)) {
            // BasicPreventionListener handles this
            return;
        }

        final Match match = matchHandler.getMatchPlaying(player);

        if (match.getGameMode() != GameModes.TRAPPING) {
            return;
        }

        if (player.hasMetadata("TRAPPER")) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
            Potion potion = Potion.fromItemStack(event.getItem());

            if (potion.isSplash()) {
                PotionUtil.splashPotion(player, event.getItem());
                if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                    player.setItemInHand(null);
                    player.updateInventory();
                } else {
                    InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (!match.getGameMode().getBuildingAllowed() || match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        if (!canBePlaced(event.getBlockClicked(), match)) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't build here.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            if (!match.getArena().getBounds().contains(event.getBlock()) || !match.getGameMode().getBuildingAllowed()) {
                continue;
            }

            match.recordPlacedBlock(event.getBlock());
            break;
        }
    }

    private boolean canBePlaced(Block placedBlock, Match match) {
        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    Block current = placedBlock.getRelative(x, y, z);

                    if (current.isEmpty()) {
                        continue;
                    }

                    if (isBlacklistedBlock(current)) {
                        continue;
                    }

                    if (isBorderGlass(current, match)) {
                        continue;
                    }

                    if (!match.canBeBroken(current)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isBlacklistedBlock(Block block) {
        return block.isLiquid() || block.getType().name().contains("LOG") || block.getType().name().contains("LEAVES");
    }

    private boolean isBorderGlass(Block block, Match match) {
        if (block.getType() != Material.GLASS) {
            return false;
        }

        Cuboid cuboid = match.getArena().getBounds();

        // the reason we do a buffer of 3 blocks here is because sometimes
        // schematics aren't perfectly copied and the glass isn't exactly on the
        // limit of the arena.
        return (getDistanceBetween(block.getX(), cuboid.getLowerX()) <= 3 || getDistanceBetween(block.getX(), cuboid.getUpperX()) <= 3) || (getDistanceBetween(block.getZ(), cuboid.getLowerZ()) <= 3 || getDistanceBetween(block.getZ(), cuboid.getUpperZ()) <= 3);
    }

    private int getDistanceBetween(int x, int z) {
        return Math.abs(x - z);
    }

}