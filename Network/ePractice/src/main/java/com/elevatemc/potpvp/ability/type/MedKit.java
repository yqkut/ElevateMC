package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MedKit extends Ability {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.PAPER;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Med Kit";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Right Click to receive Resistance 3,");
        toReturn.add(ChatColor.GRAY + "Regeneration 3, and 4 Absorption");
        toReturn.add(ChatColor.GRAY + "Hearts for 5 seconds!");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        final Location blockAt = player.getLocation();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final Block belowBlock = blockAt.getBlock().getRelative(BlockFace.DOWN);

        if (!player.isOnGround() && belowBlock.getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use the " + this.getDisplayName() + ChatColor.RED + " in the air!");
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

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 30*20, 1), true);

        this.applyCooldown(player);
    }
}