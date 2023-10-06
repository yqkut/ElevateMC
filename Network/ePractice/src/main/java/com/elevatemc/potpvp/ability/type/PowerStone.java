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
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PowerStone extends Ability {
    public PowerStone() {
        this.hassanStack.setDurability((byte)5);
    }

    private List<UUID> powerStone = new ArrayList<>();

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
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Power Stone";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Right Click to receive Strength 2, Resistance 3");
        toReturn.add(ChatColor.GRAY + "Regeneration 3 for 12 seconds. Within those");
        toReturn.add(ChatColor.GRAY + "12 seconds you may not use any sort of potions.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return 120_000L;
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

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        PotPvPSI.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        final Block belowBlock = blockAt.getBlock().getRelative(BlockFace.DOWN);

        if (!player.isOnGround() && belowBlock.getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use the " + this.getDisplayName() + ChatColor.RED + " in the air!");
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);

        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 12, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 12, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 12, 1), true);

        final UUID uuid = player.getUniqueId();
        powerStone.add(uuid);

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired! You may now splash potions!");
            player.sendMessage("");
            powerStone.remove(uuid);
        }, 20 * 12);

        this.applyCooldown(player);
    }

    @EventHandler
    private void onSplash(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.getType() != Material.POTION) {
            return;
        }

        if (itemStack.getDurability() == 0) { // Water bottle
            return;
        }

        final Potion potion = Potion.fromItemStack(itemStack);

        if (potion.getType() != PotionType.INSTANT_HEAL) {
            return;
        }

        if (!powerStone.contains(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not splash health potions whilst using " + this.getDisplayName() + ChatColor.RED + ".");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSplash(PotionSplashEvent event) {
        final ThrownPotion thrownPotion = event.getPotion();

        if (!(thrownPotion.getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getPotion().getShooter();

        if (thrownPotion.getEffects().stream().noneMatch(it -> it.getType().getName().contains("HEAL"))) {
            return;
        }

        if (!powerStone.contains(shooter.getUniqueId())) {
            return;
        }

        shooter.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not splash health potions whilst using " + this.getDisplayName() + ChatColor.RED + ".");
        event.setCancelled(true);
    }
}