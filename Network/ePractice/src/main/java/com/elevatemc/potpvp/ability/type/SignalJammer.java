package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SignalJammer extends Ability {
    public static Map<Location, UUID> signalJammers = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.COMMAND;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Signal Jammer";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Place this down and no one within");
        toReturn.add(ChatColor.GRAY + "16 blocks can use any partner/ability items!");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return TimeUnit.MINUTES.toMillis(1L) + TimeUnit.SECONDS.toMillis(30);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (event.getItemInHand() == null) {
            return;
        }

        if (block.getType() == Material.COMMAND && !player.isOp() && !this.isSimilar(event.getItemInHand())) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not place that block!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlace(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getItem() == null || event.getClickedBlock() == null || !this.isSimilar(event.getItem()) || !event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();
        final Block block = clickedBlock.getRelative(BlockFace.UP);

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, block.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            player.updateInventory();
            return;
        }

        if (clickedBlock.getType() == Material.STATIONARY_WATER || clickedBlock.getType() == Material.STATIONARY_LAVA || clickedBlock.getType() == Material.LAVA || clickedBlock.getType() == Material.WATER) {
            player.updateInventory();
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not place " + this.getDisplayName() + ChatColor.RED + " ontop of " + clickedBlock.getType().name().replace("_", "").toLowerCase());
            return;
        }

        if (block.getType() != Material.AIR) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can only place this on the ground!");
            return;
        }

        final ItemStack itemStack = event.getItem();

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }
        player.updateInventory();

        for (Player onlinePlayer : PotPvPSI.getInstance().getServer().getOnlinePlayers()) {
            if (!onlinePlayer.getWorld().getName().equalsIgnoreCase(block.getWorld().getName())) {
                continue;
            }

            if (onlinePlayer.getLocation().distance(block.getLocation()) > 16) {
                continue;
            }

            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(Color.translate(player.getName() + " &chas placed a " + this.getDisplayName() + "&c!"));
            onlinePlayer.sendMessage(Color.translate("&7All players within a 16 block radius may not use any ability items!"));
            onlinePlayer.sendMessage("");
        }

        if (block.getType().isSolid() && block.getType().isBlock()) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not place a " + this.getDisplayName() + ChatColor.RED + " here!");
            return;
        }

        player.updateInventory();

        signalJammers.put(block.getLocation(), player.getUniqueId());

        block.setMetadata("SIGNAL_JAMMER", new FixedMetadataValue(PotPvPSI.getInstance(), true));
        block.setType(Material.COMMAND);

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            signalJammers.remove(block.getLocation());

            block.setType(Material.AIR);

            if (player.isOnline()) {
                player.sendMessage("");
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired!");
                player.sendMessage("");
            }
        }, 20*30);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.COMMAND || !signalJammers.containsKey(block.getLocation())) {
            return;
        }

        if (event.getAction().name().contains("RIGHT")) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not right click the " + this.getDisplayName() + ChatColor.RED + "!");
            event.setCancelled(true);
            return;
        }

        int value = block.hasMetadata("SIGNAL_JAMMER") ? (block.getMetadata("SIGNAL_JAMMER").get(0).asInt() + 1) : 1;

        block.setMetadata("SIGNAL_JAMMER", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (value != 20) {
            player.sendMessage(Color.translate("&cYou have to hit the &f" + this.getDisplayName() + " &c" + (20 - value) + " more time" + (20 - value == 1 ? "" : "s") + "!"));
            return;
        }

        block.setType(Material.AIR);
        block.removeMetadata("SIGNAL_JAMMER", PotPvPSI.getInstance());

        final Player owner = PotPvPSI.getInstance().getServer().getPlayer(signalJammers.remove(block.getLocation()));

        if (owner == null || !owner.isOnline()) {
            return;
        }

        owner.sendMessage("");
        owner.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has been broken by " + ChatColor.WHITE + player.getName() + ChatColor.RED + "!");
        owner.sendMessage("");
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onAbilityUse(AbilityUseEvent event) {
        final Player player = event.getPlayer();
        final Ability ability = event.getAbility();
        final Location chosenLocation = event.getChosenLocation().clone();

        if (signalJammers.keySet().stream().anyMatch(it -> player.getWorld().getName().equalsIgnoreCase(it.getWorld().getName()) && chosenLocation.distance(it) <= 16)) {
            event.setCancelled(true);

            player.sendMessage("");
            player.sendMessage(Color.translate("&cYou may not use &f" + ability.getDisplayName() + " &cas you are within 16 blocks of a &f" + this.getDisplayName() + "&c!"));
            player.sendMessage("");
        }
    }

}