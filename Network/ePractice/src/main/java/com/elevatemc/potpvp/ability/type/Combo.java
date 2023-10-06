package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Combo extends Ability {

    public static Map<UUID, Integer> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.RED_ROSE;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Combo";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Get a second of strength II for the");
        toReturn.add(ChatColor.GRAY + "amount of hits dealt within 10 seconds.");

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

        event.setCancelled(true);
        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }
        player.updateInventory();

        cache.put(player.getUniqueId(), 0);

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "Successfully used the " + this.getDisplayName() + ChatColor.GOLD + " item.");
        player.sendMessage(ChatColor.GRAY + "For the next 10 seconds, every hit you deal will get you a second of Strength 2.");
        player.sendMessage("");

        this.applyCooldown(event.getPlayer());

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {

            int seconds = cache.remove(player.getUniqueId());

            if (!player.isOnline()) {
                return;
            }

            player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*seconds, 1), true);

            player.sendMessage("");
            player.sendMessage(this.getDisplayName() + ChatColor.GOLD + " has been activated. You now have Strength 2 for " + seconds + " seconds.");
            player.sendMessage("");
        }, 20*10L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();

        if (!cache.containsKey(damager.getUniqueId())) {
            return;
        }

        int amount = cache.getOrDefault(damager.getUniqueId(), 0)+1;

        if (amount > 10) {
            return;
        }

        damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Found another hit, you will get " + amount + " seconds of Strength II.");

        cache.put(damager.getUniqueId(), amount);
    }
}