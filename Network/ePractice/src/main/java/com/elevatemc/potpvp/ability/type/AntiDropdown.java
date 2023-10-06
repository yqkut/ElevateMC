package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AntiDropdown extends Ability {

    public AntiDropdown() {
        this.hassanStack = ItemBuilder.copyOf(this.hassanStack).data((byte)1).build();
    }

    public static Map<Location, Material> cache = new HashMap<>();
    private List<Material> disallowedMaterial = Arrays.asList(
            Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.SIGN, Material.SIGN_POST, Material.HOPPER,
            Material.BEDROCK, Material.ENCHANTMENT_TABLE, Material.AIR, Material.DROPPER, Material.DISPENSER
    );

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.INK_SACK;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Anti-Dropdown";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hit a player to activate and for");
        toReturn.add(ChatColor.GRAY + "the next 15 seconds all blocks under");
        toReturn.add(ChatColor.GRAY + "them will be replaced with red glass");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (damager.getItemInHand() == null || !this.isSimilar(damager.getItemInHand())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        int value = target.hasMetadata("DROPDOWN_COUNT") ? (target.getMetadata("DROPDOWN_COUNT").get(0).asInt() + 1) : 1;

        if (target.hasMetadata("ANTI_DROPDOWN")) {
            damager.sendMessage(Color.translate("&c" + target.getName() + " already has " + this.getDisplayName() + " activated!"));
            return;
        }

        if (eLib.getInstance().getAutoRebootHandler().isRebooting() && eLib.getInstance().getAutoRebootHandler().getRebootSecondsRemaining() <= TimeUnit.MINUTES.toSeconds(1)) {
            damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use this item whilst the server is rebooting!");
            return;
        }

        target.setMetadata("DROPDOWN_COUNT", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(Color.translate("&6You have to hit &f" + target.getName() + " &6" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("DROPDOWN_COUNT", PotPvPSI.getInstance());
        target.setMetadata("ANTI_DROPDOWN", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }

        damager.sendMessage("");
        damager.sendMessage(Color.translate("&6You have hit &f" + target.getName() + " &6with the " + this.getDisplayName() + "&6."));
        damager.sendMessage(Color.translate("&7Every time they walk over air or water it will be replaced with Red Stained Glass!"));
        damager.sendMessage("");

        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + "!");

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            if (target.isOnline()) {
                target.removeMetadata("ANTI_DROPDOWN", PotPvPSI.getInstance());
            }
        }, 20 * 15);

        this.applyCooldown(damager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("ANTI_DROPDOWN")) {
            player.removeMetadata("ANTI_DROPDOWN", PotPvPSI.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("ANTI_DROPDOWN")) {
            player.removeMetadata("ANTI_DROPDOWN", PotPvPSI.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        if (event.isCancelled() || !player.hasMetadata("ANTI_DROPDOWN")) {
            return;
        }

        if (!PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
            return;
        }

        final Block block = event.getTo().getBlock();
        final Block firstBlock = block.getRelative(BlockFace.DOWN);

        if (firstBlock.getType() != Material.AIR) {
            return;
        }

        final Block secondBlock = firstBlock.getRelative(BlockFace.DOWN);

        if (secondBlock.getType() != Material.AIR) {
            return;
        }

        final Block thirdBlock = secondBlock.getRelative(BlockFace.DOWN);

        if (thirdBlock.getType() != Material.AIR) {
            return;
        }

        setStabilizingShock(firstBlock);
    }

    public boolean isStabilizingShock(Block block) {
        return block.hasMetadata("ANTI_DROPDOWN") && block.getType() == Material.STAINED_GLASS && block.getData() == 14;
    }

    public void setStabilizingShock(Block block) {
        final Material type = block.getType();

        if (this.isStabilizingShock(block)) {
            return;
        }

        cache.put(block.getLocation(), type);

        block.setType(Material.STAINED_GLASS);
        block.setData((byte)14);
        block.setMetadata("ANTI_DROPDOWN", new FixedMetadataValue(PotPvPSI.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                final Material oldType = cache.remove(block.getLocation());

                block.removeMetadata("ANTI_DROPDOWN", PotPvPSI.getInstance());

                if (!block.getType().equals(oldType)) {
                    block.setType(oldType);
                }
            }
        }.runTaskLater(PotPvPSI.getInstance(), 20 * 5);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (this.isStabilizingShock(block) && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not break a " + this.getDisplayName() + ChatColor.RED + " block!");
        }
    }
}