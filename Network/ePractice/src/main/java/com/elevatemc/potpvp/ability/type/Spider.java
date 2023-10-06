package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Spider extends Ability {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.SPIDER_EYE;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Spider Ability";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Click to activate and for the");
        toReturn.add(ChatColor.GRAY + "next 15 seconds, all hits dealt");
        toReturn.add(ChatColor.GRAY + "have a 10% chance of putting a");
        toReturn.add(ChatColor.GRAY + "cobweb underneath the enemy.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        final Location blockAt = player.getLocation();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        player.setMetadata("SPIDER", new FixedMetadataValue(PotPvPSI.getInstance(), true));

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> player.removeMetadata("SPIDER", PotPvPSI.getInstance()), 20*15);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (!damager.hasMetadata("SPIDER")) {
            return;
        }

        if (ThreadLocalRandom.current().nextInt(100) > 10) {
            return;
        }

        final Block blockAt = target.getLocation().getBlock();

        if (blockAt.getType() == Material.WEB) {
            return;
        }

        final Block block2 = blockAt.getRelative(BlockFace.SOUTH);
        final Block block3 = blockAt.getRelative(BlockFace.WEST);
        final Block block4 = block3.getRelative(BlockFace.SOUTH);

        if (blockAt.getType() == Material.AIR) {
            blockAt.setType(Material.WEB);
        }

        if (block2.getType() == Material.AIR) {
            block2.setType(Material.WEB);
        }

        if (block3.getType() == Material.AIR) {
            block3.setType(Material.WEB);
        }

        if (block4.getType() == Material.AIR) {
            block4.setType(Material.WEB);
        }

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            if (blockAt.getType() == Material.WEB) {
                blockAt.setType(Material.AIR);
            }

            if (block2.getType() == Material.WEB) {
                block2.setType(Material.AIR);
            }

            if (block3.getType() == Material.WEB) {
                block3.setType(Material.AIR);
            }

            if (block4.getType() == Material.WEB) {
                block4.setType(Material.AIR);
            }
        }, 20*15);

        damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1, 1);
        damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have put " + PatchedPlayerUtils.getFormattedName(target.getUniqueId()) + ChatColor.RED + " in a cobweb!");
        damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " has been put in a cobweb due to the " + this.getDisplayName() + "!");

        target.playSound(target.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You were placed in a cobweb due to the " + this.getDisplayName() + ChatColor.RED + "!");
    }

}
