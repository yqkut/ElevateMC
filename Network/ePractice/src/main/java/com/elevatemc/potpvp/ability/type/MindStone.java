package com.elevatemc.potpvp.ability.type;

import com.elevatemc.elib.util.ItemBuilder;
import com.elevatemc.potpvp.pvpclasses.PvPClassHandler;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.listener.events.AbilityUseEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MindStone extends Ability {
    public MindStone() {
        this.hassanStack = ItemBuilder.copyOf(hassanStack.clone()).data((byte)14).build();
    }

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
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Mind Stone";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hit a player to rotate their");
        toReturn.add(ChatColor.GRAY + "head 180 degrees and give them");
        toReturn.add(ChatColor.GRAY + "Blindness X, Slowness III and");
        toReturn.add(ChatColor.GRAY + "Nausea III for 8 seconds.");

        return toReturn;
    }

    @Override
    public long getCooldown() {
        return TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(15);
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

        if (PvPClassHandler.getPvPClass(target) != null) {
            damager.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You may not use a " + this.getDisplayName() + ChatColor.RED + " on a " + ChatColor.WHITE + Objects.requireNonNull(PvPClassHandler.getPvPClass(target)).getName() + ChatColor.RED + ".");
            return;
        }

        final Location location = target.getLocation();
        location.setYaw(location.getYaw()+180.0F);

        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*8, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*8, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*8, 9));
        target.teleport(location);

        target.sendMessage(damager.getName() + ChatColor.RED + " has used the " + this.getDisplayName() + ChatColor.RED + " on you!");

        final ItemStack itemStack = damager.getItemInHand();

        if (itemStack.getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        this.applyCooldown(damager);
    }
}