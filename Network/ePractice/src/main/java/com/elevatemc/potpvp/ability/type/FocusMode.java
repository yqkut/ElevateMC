package com.elevatemc.potpvp.ability.type;

import com.elevatemc.potpvp.util.Color;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class FocusMode extends Ability {

    @Getter
    public static Map<UUID, UUID> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.LEVER;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Focus Mode";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "When you hit a player with this 3 times");
        toReturn.add(ChatColor.GRAY + "you will now deal 20% more damage towards them.");
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
        
        int value = target.hasMetadata("FOCUS_MODE") ? (target.getMetadata("FOCUS_MODE").get(0).asInt() + 1) : 1;

        target.setMetadata("FOCUS_MODE", new FixedMetadataValue(PotPvPSI.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(Color.translate("&6You have to hit &f" + target.getName() + " &6" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("FOCUS_MODE", PotPvPSI.getInstance());

        cache.put(target.getUniqueId(), damager.getUniqueId());

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }

        damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1, 1);
        damager.sendMessage("");
        damager.sendMessage(Color.translate("&6You have hit &f" + target.getName() + " &6with the " + this.getDisplayName() + "&6."));
        damager.sendMessage(Color.translate("&7For the next 10 seconds you now deal 20% more damage towards them!"));
        damager.sendMessage("");

        target.playSound(target.getLocation(), Sound.ANVIL_BREAK, 1, 1);
        target.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + "!");

        this.applyCooldown(damager);
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (!cache.containsKey(player.getUniqueId()) || !cache.get(player.getUniqueId()).toString().equalsIgnoreCase(damager.getUniqueId().toString())) {
            return;
        }

        event.setDamage(event.getDamage()*1.2D);
    }

}