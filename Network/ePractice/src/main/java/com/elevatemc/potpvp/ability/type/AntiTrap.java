package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.eLib;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.elevatemc.potpvp.util.PotionUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.POTION;

public class AntiTrap extends Ability {

    public static Map<Location, Material> cache = new HashMap<>();
    private List<Material> disallowedMaterial = Arrays.asList(
            Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.SIGN, Material.SIGN_POST, Material.HOPPER,
            Material.BEDROCK, Material.ENCHANTMENT_TABLE, Material.AIR, Material.DROPPER, Material.DISPENSER);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_ROD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Anti-Trap Rod";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Right click to activate and for 15 seconds");
        toReturn.add(ChatColor.GRAY + "a 3x3 of gold blocks appear under you.");
        toReturn.add(ChatColor.GRAY + "You cannot break/place/interact with.");
        toReturn.add(ChatColor.GRAY + "blocks on top of these gold blocks.");
        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand()) || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (eLib.getInstance().getAutoRebootHandler().isRebooting() && eLib.getInstance().getAutoRebootHandler().getRebootSecondsRemaining() <= TimeUnit.MINUTES.toSeconds(1)) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use this item whilst the server is rebooting!");
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        player.updateInventory();

        player.setMetadata("ANTI_TRAP", new FixedMetadataValue(PotPvPSI.getInstance(), true));

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            if (player.isOnline()) {
                player.removeMetadata("ANTI_TRAP", PotPvPSI.getInstance());
            }
        }, 20 * 12);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("ANTI_TRAP")) {
            player.removeMetadata("ANTI_TRAP", PotPvPSI.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("ANTI_TRAP")) {
            player.removeMetadata("ANTI_TRAP", PotPvPSI.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        if (event.isCancelled() || !player.hasMetadata("ANTI_TRAP")) {
            return;
        }

        final Block firstBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);

        if (firstBlock.getType() == Material.AIR) {
            return;
        }

        final Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);

        if (match == null) {
            return;
        }

        final Block secondBlock = firstBlock.getRelative(BlockFace.SOUTH);
        final Block thirdBlock = firstBlock.getRelative(BlockFace.WEST);
        final Block fourthBlock = thirdBlock.getRelative(BlockFace.SOUTH);
        final Block fifthBlock = firstBlock.getRelative(BlockFace.NORTH);
        final Block sixthBlock = fifthBlock.getRelative(BlockFace.WEST);
        final Block seventhBlock = fifthBlock.getRelative(BlockFace.EAST);
        final Block eighthBlock = secondBlock.getRelative(BlockFace.EAST);
        final Block ninthBlock = seventhBlock.getRelative(BlockFace.SOUTH);

        setAntiTrapBlock(firstBlock, player, match);
        setAntiTrapBlock(secondBlock, player, match);
        setAntiTrapBlock(thirdBlock, player, match);
        setAntiTrapBlock(fourthBlock, player, match);
        setAntiTrapBlock(fifthBlock, player, match);
        setAntiTrapBlock(sixthBlock, player, match);
        setAntiTrapBlock(seventhBlock, player, match);
        setAntiTrapBlock(eighthBlock, player, match);
        setAntiTrapBlock(ninthBlock, player, match);
    }

    public boolean isAntiTrapBlock(Block block) {
        return block.hasMetadata("ANTI_TRAP") && block.getType() == GOLD_BLOCK;
    }

    public void setAntiTrapBlock(Block block, Player player, Match match) {

        final Material type = block.getType();

        if (type == Material.GOLD_BLOCK && block.hasMetadata("ANTI_TRAP")) {
            return;
        }

        if (!type.isSolid() || this.disallowedMaterial.contains(type)) {
            return;
        }

        if (!PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
            return;
        }

        if (!match.get_id().equalsIgnoreCase(PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player).get_id())) {
            return;
        }

        cache.put(block.getLocation(), type);

        block.setType(Material.GOLD_BLOCK);
        block.setMetadata("ANTI_TRAP", new FixedMetadataValue(PotPvPSI.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                if (!PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
                    return;
                }

                if (!match.get_id().equalsIgnoreCase(PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player).get_id())) {
                    return;
                }

                final Material oldType = cache.remove(block.getLocation());

                block.removeMetadata("ANTI_TRAP", PotPvPSI.getInstance());

                if (!block.getType().equals(oldType)) {
                    block.setType(oldType);
                }
            }
        }.runTaskLater(PotPvPSI.getInstance(), 20 * 5);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        final Block downBlock = event.getBlockPlaced().getRelative(BlockFace.DOWN);
        final Block downTwoBlock = downBlock.getRelative(BlockFace.DOWN);

        if (!this.isAntiTrapBlock(downBlock) && !this.isAntiTrapBlock(downTwoBlock)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not place blocks on top of a " + ChatColor.GOLD + ChatColor.BOLD.toString() + "Anti-Trap Rod" + ChatColor.RED + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (this.isAntiTrapBlock(block) && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not break a anti-trap rod block!");
            return;
        }

        final Block downBlock = block.getRelative(BlockFace.DOWN);
        final Block downTwoBlock = downBlock.getRelative(BlockFace.DOWN);

        if (this.isAntiTrapBlock(downBlock) || this.isAntiTrapBlock(downTwoBlock)) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not break blocks as there is a " + ChatColor.GOLD + ChatColor.BOLD.toString() + "Anti-Drop Rod" + ChatColor.RED + " block below.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlate(PlayerInteractEvent event) {
        final Block clickedBlock = event.getClickedBlock();

        if ((event.getAction() != Action.PHYSICAL || clickedBlock == null || !clickedBlock.getType().name().contains("PLATE"))) {
            return;
        }

        final Block downBlock = event.getClickedBlock().getRelative(BlockFace.DOWN);

        if (!this.isAntiTrapBlock(downBlock)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || !AntiBlockup.NO_INTERACT.contains(event.getClickedBlock().getType()))) {
            return;
        }

        final Player player = event.getPlayer();

        final Block downBlock = event.getClickedBlock().getRelative(BlockFace.DOWN);
        final Block downTwoBlock = downBlock.getRelative(BlockFace.DOWN);
        final Block downThreeBlock = downTwoBlock.getRelative(BlockFace.DOWN);

        if (!this.isAntiTrapBlock(downBlock) && !this.isAntiTrapBlock(downTwoBlock) && !this.isAntiTrapBlock(downThreeBlock)) {
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
        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not open or close " + event.getClickedBlock().getType().name().toLowerCase().replace("_", " ") + "s as there is a " + ChatColor.GOLD + ChatColor.BOLD.toString() + "Anti-Trap Rod" + ChatColor.RED + " under it.");
    }
}