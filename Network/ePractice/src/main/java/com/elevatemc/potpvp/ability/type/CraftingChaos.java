package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.util.Color;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CraftingChaos extends Ability {

    @Getter
    public static Map<UUID, Long> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.WORKBENCH;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Crafting Chaos";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hit an enemy and for 10 seconds, every");
        toReturn.add(ChatColor.GRAY + "hit you deal has a 10% chance of");
        toReturn.add(ChatColor.GRAY + "putting them in a crafting table.");

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

        int value = target.hasMetadata("CHAOS") ? (target.getMetadata("CHAOS").get(0).asInt()+1) : 1;

        if (cache.containsKey(target.getUniqueId())) {
            damager.sendMessage(Color.translate("&c" + target.getName() +  " is already in the crafting chaos for &l" + TimeUtils.formatIntoMMSS((int) (cache.get(target.getUniqueId())-System.currentTimeMillis())/1000) + "&c."));
            return;
        }

        target.setMetadata("CHAOS", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(Color.translate("&6You have to hit &f" + target.getName() + " &6" + (3 - value) + " more times!"));
            return;
        }

        target.removeMetadata("CHAOS", PotPvPSI.getInstance());

        cache.put(target.getUniqueId(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(10));

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount()-1);
        }

        damager.sendMessage("");
        damager.sendMessage(Color.translate("&6You have hit &f" + target.getName() + " &6with " + this.getDisplayName() + "&6."));
        damager.sendMessage(Color.translate("&7For the next 10 seconds, every hit you deal has a 10% chance of putting them in a crafting table."));
        damager.sendMessage("");

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + ".");
        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "For the next 10 seconds, every hit you take has a 10% chance of putting you in a crafting table.");
        target.sendMessage("");

        this.applyCooldown(damager);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (!cache.containsKey(target.getUniqueId()) || cache.get(target.getUniqueId()) < System.currentTimeMillis()) {
            cache.remove(target.getUniqueId());
            return;
        }

        if (ThreadLocalRandom.current().nextInt(100) <= 10) {
            damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1, 1);
            damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + target.getName() + " has been put in a crafting table due to the " + this.getDisplayName() + "!");

            target.playSound(target.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
            target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You were placed in a crafting table due to the " + this.getDisplayName() + ChatColor.RED + "!");
            target.openWorkbench(target.getLocation(), true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (this.isSimilar(event.getItemInHand())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't place partner items!");
        }

    }

}
